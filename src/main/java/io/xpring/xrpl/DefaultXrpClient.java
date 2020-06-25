package io.xpring.xrpl;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.xpring.common.XrplNetwork;
import io.xpring.xrpl.model.XrpTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xrpl.rpc.v1.AccountAddress;
import org.xrpl.rpc.v1.AccountRoot;
import org.xrpl.rpc.v1.Common.Account;
import org.xrpl.rpc.v1.Common.Amount;
import org.xrpl.rpc.v1.Common.Destination;
import org.xrpl.rpc.v1.Common.LastLedgerSequence;
import org.xrpl.rpc.v1.Common.SigningPublicKey;
import org.xrpl.rpc.v1.CurrencyAmount;
import org.xrpl.rpc.v1.GetAccountInfoRequest;
import org.xrpl.rpc.v1.GetAccountInfoResponse;
import org.xrpl.rpc.v1.GetAccountTransactionHistoryRequest;
import org.xrpl.rpc.v1.GetAccountTransactionHistoryResponse;
import org.xrpl.rpc.v1.GetFeeRequest;
import org.xrpl.rpc.v1.GetFeeResponse;
import org.xrpl.rpc.v1.GetTransactionRequest;
import org.xrpl.rpc.v1.GetTransactionResponse;
import org.xrpl.rpc.v1.LedgerSpecifier;
import org.xrpl.rpc.v1.Payment;
import org.xrpl.rpc.v1.SubmitTransactionRequest;
import org.xrpl.rpc.v1.SubmitTransactionResponse;
import org.xrpl.rpc.v1.Transaction;
import org.xrpl.rpc.v1.XRPDropsAmount;
import org.xrpl.rpc.v1.XRPLedgerAPIServiceGrpc;
import org.xrpl.rpc.v1.XRPLedgerAPIServiceGrpc.XRPLedgerAPIServiceBlockingStub;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * A client that can submit transactions to the XRP Ledger.
 *
 * @see "https://xrpl.org"
 */
public class DefaultXrpClient implements XrpClientDecorator {
  // A margin to pad the current ledger sequence with when submitting transactions.
  private static final int MAX_LEDGER_VERSION_OFFSET = 10;

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  // Channel is the abstraction to connect to a service endpoint
  private final XRPLedgerAPIServiceBlockingStub stub;
  private final XrplNetwork xrplNetwork;

  /**
   * No-args Constructor.
   */
  DefaultXrpClient(String grpcUrl, XrplNetwork xrplNetwork) {
    this(ManagedChannelBuilder
        .forTarget(grpcUrl)
        .usePlaintext()
        .build(),
        xrplNetwork
    );
  }

  /**
   * Required-args Constructor, currently for testing.
   *
   * @param channel A {@link ManagedChannel}.
   */
  DefaultXrpClient(final ManagedChannel channel, XrplNetwork xrplNetwork) {
    this.xrplNetwork = xrplNetwork;

    // It is up to the client to determine whether to block the call. Here we create a blocking stub, but an async
    // stub, or an async stub with Future are always possible.
    this.stub = XRPLedgerAPIServiceGrpc.newBlockingStub(channel);

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      channel.shutdown();
      try {
        channel.awaitTermination(5, TimeUnit.SECONDS);
      } catch (Exception timedOutException) {
        try {
          channel.shutdownNow();
        } catch (Exception e) {
          // nothing more can be done
        }
      }
    }));
  }

  /**
   * Get the balance of the specified account on the XRP Ledger.
   *
   * @param xrplAccountAddress The X-Address to retrieve the balance for.
   * @return A {@link BigInteger} with the number of drops in this account.
   * @throws XrpException If the given inputs were invalid.
   */
  public BigInteger getBalance(final String xrplAccountAddress) throws XrpException {
    if (!Utils.isValidXAddress(xrplAccountAddress)) {
      throw XrpException.xAddressRequiredException;
    }
    ClassicAddress classicAddress = Utils.decodeXAddress(xrplAccountAddress);

    AccountRoot accountData = this.getAccountData(classicAddress.address());

    return BigInteger.valueOf(accountData.getBalance().getValue().getXrpAmount().getDrops());
  }

  /**
   * Retrieve the transaction status for a Payment given transaction hash.
   *
   * <p>
   * Note: This method will only work for Payment type transactions which do not have the tf_partial_payment attribute
   * set.
   *
   * See: https://xrpl.org/payment.html#payment-flags
   * </p>
   *
   * @param transactionHash The hash of the transaction.
   * @return The status of the given transaction.
   */
  public TransactionStatus getPaymentStatus(String transactionHash) throws XrpException {
    Objects.requireNonNull(transactionHash);

    RawTransactionStatus transactionStatus = getRawTransactionStatus(transactionHash);

    // Return PENDING if the transaction is not validated.
    if (!transactionStatus.getValidated()) {
      return TransactionStatus.PENDING;
    }

    return transactionStatus.getTransactionStatusCode().startsWith("tes")
        ? TransactionStatus.SUCCEEDED
        : TransactionStatus.FAILED;
  }

  /**
   * Transact XRP between two accounts on the ledger.
   *
   * @param drops              The number of drops of XRP to send.
   * @param destinationAddress The X-Address to send the XRP to.
   * @param sourceWallet       The {@link Wallet} which holds the XRP.
   * @return A transaction hash for the payment.
   * @throws XrpException If the given inputs were invalid.
   */
  public String send(
      final BigInteger drops,
      final String destinationAddress,
      final Wallet sourceWallet
  ) throws XrpException {
    Objects.requireNonNull(drops);
    Objects.requireNonNull(destinationAddress);
    Objects.requireNonNull(sourceWallet);

    if (!Utils.isValidXAddress(destinationAddress)) {
      throw XrpException.xAddressRequiredException;
    }

    ClassicAddress destinationClassicAddress = Utils.decodeXAddress(destinationAddress);
    ClassicAddress sourceClassicAddress = Utils.decodeXAddress(sourceWallet.getAddress());

    AccountRoot accountData = this.getAccountData(sourceClassicAddress.address());
    XRPDropsAmount fee = this.getMinimumFee();
    int openLedgerSequence = this.getOpenLedgerSequence();

    AccountAddress destinationAccountAddress = AccountAddress.newBuilder()
        .setAddress(destinationAddress)
        .build();
    AccountAddress sourceAccountAddress = AccountAddress.newBuilder()
        .setAddress(sourceClassicAddress.address())
        .build();

    XRPDropsAmount dropsAmount = XRPDropsAmount.newBuilder().setDrops(drops.longValue()).build();
    CurrencyAmount currencyAmount = CurrencyAmount.newBuilder().setXrpAmount(dropsAmount).build();
    Amount amount = Amount.newBuilder().setValue(currencyAmount).build();
    Destination destination = Destination.newBuilder().setValue(destinationAccountAddress).build();

    Payment.Builder paymentBuilder = Payment.newBuilder()
        .setAmount(amount)
        .setDestination(destination);

    Payment payment = paymentBuilder.build();

    byte[] signingPublicKeyBytes = Utils.hexStringToByteArray(sourceWallet.getPublicKey());
    int lastLedgerSequenceInt = openLedgerSequence + MAX_LEDGER_VERSION_OFFSET;

    Account sourceAccount = Account.newBuilder().setValue(sourceAccountAddress).build();
    LastLedgerSequence lastLedgerSequence = LastLedgerSequence.newBuilder().setValue(lastLedgerSequenceInt).build();
    SigningPublicKey signingPublicKey = SigningPublicKey.newBuilder()
        .setValue(ByteString.copyFrom(signingPublicKeyBytes))
        .build();

    Transaction transaction = Transaction.newBuilder()
        .setAccount(sourceAccount)
        .setFee(fee)
        .setSequence(accountData.getSequence())
        .setPayment(payment)
        .setLastLedgerSequence(lastLedgerSequence)
        .setSigningPublicKey(signingPublicKey)
        .build();

    byte[] signedTransaction = Signer.signTransaction(transaction, sourceWallet);

    SubmitTransactionRequest request = SubmitTransactionRequest.newBuilder()
        .setSignedTransaction(ByteString.copyFrom(signedTransaction))
        .build();

    SubmitTransactionResponse response = this.stub.submitTransaction(request);

    byte[] hashBytes = response.getHash().toByteArray();
    return Utils.byteArrayToHex(hashBytes);
  }

  /**
   * Return the history of payments for the given account.
   *
   * <p>
   * Note: This method only works for payment type transactions. See "https://xrpl.org/payment.html".
   * Note: This method only returns the history that is contained on the remote node,
   * which may not contain a full history of the network.
   * </p>
   *
   * @param address The address (account) for which to retrieve payment history.
   * @return An array of transactions associated with the account.
   * @throws XrpException If there was a problem communicating with the XRP Ledger.
   */
  public List<XrpTransaction> paymentHistory(String address) throws XrpException {
    if (!Utils.isValidXAddress(address)) {
      throw XrpException.xAddressRequiredException;
    }
    ClassicAddress classicAddress = Utils.decodeXAddress(address);

    AccountAddress account = AccountAddress.newBuilder().setAddress(classicAddress.address()).build();
    GetAccountTransactionHistoryRequest request = GetAccountTransactionHistoryRequest.newBuilder()
                                                                                .setAccount(account)
                                                                                .build();
    GetAccountTransactionHistoryResponse transactionHistory = stub.getAccountTransactionHistory(request);

    List<GetTransactionResponse> getTransactionResponses = transactionHistory.getTransactionsList();

    // Filter transactions to payments only and convert them to XrpTransactions.
    // If a payment transaction fails conversion, throw an error.
    List<XrpTransaction> payments = new ArrayList<XrpTransaction>();
    for (GetTransactionResponse transactionResponse : getTransactionResponses) {
      Transaction transaction = transactionResponse.getTransaction();
      switch (transaction.getTransactionDataCase()) {
        case PAYMENT: {
          XrpTransaction xrpTransaction = XrpTransaction.from(transactionResponse, this.xrplNetwork);
          if (xrpTransaction == null) {
            throw XrpException.paymentConversionFailure;
          } else {
            payments.add(xrpTransaction);
          }
          break;
        }
        default: {
          // Intentionally do nothing, non-payment type transactions are ignored.
        }
      }
    }
    return payments;
  }

  /**
   * Check if an address exists on the XRP Ledger.
   *
   * @param address The address to check the existence of.
   * @return A boolean if the account is on the XRP Ledger.
   */
  @Override
  public boolean accountExists(String address) throws XrpException {
    if (!Utils.isValidXAddress(address)) {
      throw XrpException.xAddressRequiredException;
    }
    try {
      this.getBalance(address);
      return true;
    } catch (StatusRuntimeException exception) {
      if (exception.getStatus().getCode() == io.grpc.Status.NOT_FOUND.getCode()) {
        return false;
      }
      throw exception; // re-throw if code other than NOT_FOUND
    } catch (Exception exception) {
      throw exception; // re-throw any other type of exception
    }
  }

  @Override
  /**
   * Retrieve the payment transaction corresponding to the given transaction hash.
   * <p>
   * Note: This method can return transactions that are not included in a fully validated ledger.
   *       See the `validated` field to make this distinction.
   * </p>
   * @param transactionHash The hash of the transaction to retrieve.
   * @return An XrpTransaction object representing an XRP Ledger transaction.
   * @throws XrpException If the transaction hash was invalid.
   */
  public XrpTransaction getPayment(String transactionHash) throws XrpException {
    Objects.requireNonNull(transactionHash);

    byte[] transactionHashBytes = Utils.hexStringToByteArray(transactionHash);
    ByteString transactionHashByteString = ByteString.copyFrom(transactionHashBytes);
    GetTransactionRequest request = GetTransactionRequest.newBuilder()
            .setHash(transactionHashByteString).build();
    GetTransactionResponse response = this.stub.getTransaction(request);
    return XrpTransaction.from(response, this.xrplNetwork);
  }

  public int getOpenLedgerSequence() throws XrpException {
    return this.getFeeResponse().getLedgerCurrentIndex();
  }

  /**
   * Retrieve the latest validated ledger sequence on the XRP Ledger.
   * <p>
   * Note: This call will throw if the given account does not exist on the ledger at the current time. It is the
   * *caller's responsibility* to ensure this invariant is met.
   * </p><p>
   * Note: The input address *must* be in a classic address form. Inputs are not checked to this internal method.
   * </p><p>
   * TODO(keefertaylor): The above requirements are onerous, difficult to reason about and the logic of this method is
   * brittle. Replace this method's implementation when rippled supports a `ledger` RPC via gRPC.
   * </p>
   * @param address An address that exists at the current time. The address is unchecked and must be a classic address.
   * @return The index of the latest validated ledger.
   * @throws io.grpc.StatusRuntimeException If there was a problem communicating with the XRP Ledger.
   */
  @Override
  public int getLatestValidatedLedgerSequence(String address) throws XrpException {
    // rippled doesn't support a gRPC call that tells us the latest validated ledger sequence. To get around this,
    // query the account info for an account which will exist, using a shortcut for the latest validated ledger. The
    // response will contain the ledger the information was retrieved at.
    AccountAddress accountAddress = AccountAddress.newBuilder().setAddress(address).build();
    LedgerSpecifier ledgerSpecifier = LedgerSpecifier.newBuilder()
        .setShortcut(LedgerSpecifier.Shortcut.SHORTCUT_VALIDATED)
        .build();
    GetAccountInfoRequest getAccountInfoRequest = GetAccountInfoRequest.newBuilder()
        .setAccount(accountAddress)
        .setLedger(ledgerSpecifier)
        .build();

    GetAccountInfoResponse getAccountInfoResponse = this.stub.getAccountInfo(getAccountInfoRequest);

    return getAccountInfoResponse.getLedgerIndex();
  }

  @Override
  public RawTransactionStatus getRawTransactionStatus(String transactionHash) throws XrpException {
    Objects.requireNonNull(transactionHash);

    byte[] transactionHashBytes = Utils.hexStringToByteArray(transactionHash);
    ByteString transactionHashByteString = ByteString.copyFrom(transactionHashBytes);
    GetTransactionRequest request = GetTransactionRequest.newBuilder()
                                              .setHash(transactionHashByteString).build();

    GetTransactionResponse response = this.stub.getTransaction(request);

    return new RawTransactionStatus(response);
  }

  private XRPDropsAmount getMinimumFee() {
    return this.getFeeResponse().getFee().getMinimumFee();
  }

  private GetFeeResponse getFeeResponse() {
    GetFeeRequest request = GetFeeRequest.newBuilder().build();
    return this.stub.getFee(request);
  }

  private AccountRoot getAccountData(String xrplAccountAddress) {
    AccountAddress account = AccountAddress.newBuilder().setAddress(xrplAccountAddress).build();

    LedgerSpecifier ledgerSpecifier = LedgerSpecifier.newBuilder()
                                                      .setShortcut(LedgerSpecifier.Shortcut.SHORTCUT_VALIDATED).build();

    GetAccountInfoRequest request = GetAccountInfoRequest.newBuilder()
                                                          .setAccount(account).setLedger(ledgerSpecifier).build();

    GetAccountInfoResponse response = this.stub.getAccountInfo(request);

    return response.getAccountData();
  }
}
