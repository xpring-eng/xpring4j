package io.xpring.xrpl;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.xpring.xrpl.model.XRPTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xrpl.rpc.v1.*;
import org.xrpl.rpc.v1.Common.*;
import org.xrpl.rpc.v1.Common.Account;
import org.xrpl.rpc.v1.Common.Amount;
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
public class DefaultXRPClient implements XRPClientDecorator {
    // A margin to pad the current ledger sequence with when submitting transactions.
    private static final int MAX_LEDGER_VERSION_OFFSET = 10;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // Channel is the abstraction to connect to a service endpoint
    private final XRPLedgerAPIServiceBlockingStub stub;

    /**
     * No-args Constructor.
     */
    public DefaultXRPClient(String grpcURL) {
        this(ManagedChannelBuilder
                .forTarget(grpcURL)
                .usePlaintext()
                .build()
        );
    }

    /**
     * Required-args Constructor, currently for testing.
     *
     * @param channel A {@link ManagedChannel}.
     */
    DefaultXRPClient(final ManagedChannel channel) {
        // It is up to the client to determine whether to block the call. Here we create a blocking stub, but an async
        // stub, or an async stub with Future are always possible.
        this.stub = XRPLedgerAPIServiceGrpc.newBlockingStub(channel);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            channel.shutdown();
            try {
                channel.awaitTermination(5, TimeUnit.SECONDS);
            }
            catch (Exception timedOutException) {
                try {
                    channel.shutdownNow();
                }
                catch (Exception e) {
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
     * @throws XpringException If the given inputs were invalid.
     */
    public BigInteger getBalance(final String xrplAccountAddress) throws XpringException {
        if (!Utils.isValidXAddress(xrplAccountAddress)) {
            throw XpringException.xAddressRequiredException;
        }
        ClassicAddress classicAddress = Utils.decodeXAddress(xrplAccountAddress);

        AccountRoot accountData = this.getAccountData(classicAddress.address());

        return BigInteger.valueOf(accountData.getBalance().getValue().getXrpAmount().getDrops());
    }

    /**
     * Retrieve the transaction status for a Payment given transaction hash.
     *
     * Note: This method will only work for Payment type transactions which do not have the tf_partial_payment attribute set.
     * See: https://xrpl.org/payment.html#payment-flags
     *
     * @param transactionHash The hash of the transaction.
     * @return The status of the given transaction.
     */
    public TransactionStatus getPaymentStatus(String transactionHash) throws XpringException {
        Objects.requireNonNull(transactionHash);

        RawTransactionStatus transactionStatus = getRawTransactionStatus(transactionHash);

        // Return PENDING if the transaction is not validated.
        if (!transactionStatus.getValidated()) {
            return TransactionStatus.PENDING;
        }

        return transactionStatus.getTransactionStatusCode().startsWith("tes") ? TransactionStatus.SUCCEEDED : TransactionStatus.FAILED;
    }

    /**
     * Transact XRP between two accounts on the ledger.
     *
     * @param drops The number of drops of XRP to send.
     * @param destinationAddress The X-Address to send the XRP to.
     * @param sourceWallet The {@link Wallet} which holds the XRP.
     * @return A transaction hash for the payment.
     * @throws XpringException If the given inputs were invalid.
     */
    public String send(
            final BigInteger drops,
            final String destinationAddress,
            final Wallet sourceWallet
    ) throws XpringException {
        Objects.requireNonNull(drops);
        Objects.requireNonNull(destinationAddress);
        Objects.requireNonNull(sourceWallet);

        if (!Utils.isValidXAddress(destinationAddress)) {
            throw XpringException.xAddressRequiredException;
        }

        ClassicAddress destinationClassicAddress = Utils.decodeXAddress(destinationAddress);
        ClassicAddress sourceClassicAddress = Utils.decodeXAddress(sourceWallet.getAddress());

        AccountRoot accountData = this.getAccountData(sourceClassicAddress.address());
        XRPDropsAmount fee = this.getMinimumFee();
        int lastValidatedLedgerSequence = this.getLatestValidatedLedgerSequence();

        AccountAddress destinationAccountAddress = AccountAddress.newBuilder()
                .setAddress(destinationClassicAddress.address())
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
        if (destinationClassicAddress.tag().isPresent()) {
            DestinationTag destinationTag = DestinationTag.newBuilder()
                    .setValue(destinationClassicAddress.tag().get())
                    .build();
            paymentBuilder.setDestinationTag(destinationTag);
        }

        Payment payment = paymentBuilder.build();

        byte [] signingPublicKeyBytes = Utils.hexStringToByteArray(sourceWallet.getPublicKey());
        int lastLedgerSequenceInt = lastValidatedLedgerSequence + MAX_LEDGER_VERSION_OFFSET;

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

        byte [] signedTransaction = Signer.signTransaction(transaction, sourceWallet);

        SubmitTransactionRequest request = SubmitTransactionRequest.newBuilder()
                .setSignedTransaction(ByteString.copyFrom(signedTransaction))
                .build();

        SubmitTransactionResponse response = this.stub.submitTransaction(request);

        byte [] hashBytes = response.getHash().toByteArray();
        return Utils.byteArrayToHex(hashBytes);
    }


    /**
     * Return the history of payments for the given account.
     *
     * Note: This method only works for payment type transactions.
     * @see "https://xrpl.org/payment.html"
     * Note: This method only returns the history that is contained on the remote node,
     *       which may not contain a full history of the network.
     *
     * @param address: The address (account) for which to retrieve payment history.
     * @throws XpringException If there was a problem communicating with the XRP Ledger.
     * @returns An array of transactions associated with the account.
     */
    public List<XRPTransaction> paymentHistory(String address) throws XpringException {
        if (!Utils.isValidXAddress(address)) {
            throw XpringException.xAddressRequiredException;
        }
        ClassicAddress classicAddress = Utils.decodeXAddress(address);

        AccountAddress account = AccountAddress.newBuilder().setAddress(classicAddress.address()).build();

        GetAccountTransactionHistoryRequest request = GetAccountTransactionHistoryRequest.newBuilder()
                                                                                        .setAccount(account)
                                                                                        .build();
        GetAccountTransactionHistoryResponse transactionHistory = stub.getAccountTransactionHistory(request);

        List<GetTransactionResponse> getTransactionResponses = transactionHistory.getTransactionsList();

        // Filter transactions to payments only and convert them to XRPTransactions.
        // If a payment transaction fails conversion, throw an error.
        List<XRPTransaction> payments = new ArrayList<XRPTransaction>();
        for (GetTransactionResponse transactionResponse : getTransactionResponses) {
            Transaction transaction = transactionResponse.getTransaction();
            switch (transaction.getTransactionDataCase()) {
                case PAYMENT: {
                    XRPTransaction xrpTransaction = XRPTransaction.from(transaction);
                    if (xrpTransaction == null) {
                        throw XpringException.paymentConversionFailure;
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

    @Override
    public int getLatestValidatedLedgerSequence() throws XpringException {
        return this.getFeeResponse().getLedgerCurrentIndex();
    }

    @Override
    public RawTransactionStatus getRawTransactionStatus(String transactionHash) throws XpringException {
        Objects.requireNonNull(transactionHash);

        byte [] transactionHashBytes = Utils.hexStringToByteArray(transactionHash);
        ByteString transactionHashByteString = ByteString.copyFrom(transactionHashBytes);
        GetTransactionRequest request = GetTransactionRequest.newBuilder().setHash(transactionHashByteString).build();

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
        GetAccountInfoRequest request = GetAccountInfoRequest.newBuilder().setAccount(account).build();

        GetAccountInfoResponse response = this.stub.getAccountInfo(request);

        return response.getAccountData();
    }
}
