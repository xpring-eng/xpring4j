package io.xpring.xrpl;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.xpring.proto.SubmitSignedTransactionRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.v1.Amount;
import rpc.v1.Amount.AccountAddress;
import rpc.v1.Amount.CurrencyAmount;
import rpc.v1.Amount.XRPDropsAmount;
import rpc.v1.AccountInfo.GetAccountInfoRequest;
import rpc.v1.AccountInfo.GetAccountInfoResponse;
import rpc.v1.FeeOuterClass.GetFeeRequest;
import rpc.v1.FeeOuterClass.GetFeeResponse;
import rpc.v1.LedgerObjects.AccountRoot;
import rpc.v1.Submit.SubmitTransactionRequest;
import rpc.v1.Submit.SubmitTransactionResponse;
import rpc.v1.TransactionOuterClass.Transaction;
import rpc.v1.TransactionOuterClass.Payment;
import rpc.v1.XRPLedgerAPIServiceGrpc;
import rpc.v1.XRPLedgerAPIServiceGrpc.XRPLedgerAPIServiceBlockingStub;
import rpc.v1.Tx.GetTxRequest;
import rpc.v1.Tx.GetTxResponse;

import java.math.BigInteger;
import java.util.Objects;

/**
 * A client that can submit transactions to the XRP Ledger.
 *
 * @see "https://xrpl.org"
 */
public class DefaultXpringClient implements XpringClientDecorator {
    // TODO: Use TLS!
    // TODO(keefertaylor): Make this configurable.
    public static final String XPRING_GRPC_URL = "3.14.64.116:50051";

    // A margin to pad the current ledger sequence with when submitting transactions.
    private static final int MAX_LEDGER_VERSION_OFFSET = 10;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // Channel is the abstraction to connect to a service endpoint
    private final XRPLedgerAPIServiceBlockingStub stub;

    /**
     * No-args Constructor.
     */
    public DefaultXpringClient() {
        this(ManagedChannelBuilder
                .forTarget(XPRING_GRPC_URL)
                // Let's use plaintext communication because we don't have certs
                // TODO: Use TLS!
                .usePlaintext()
                .build()
        );
    }

    /**
     * Required-args Constructor, currently for testing.
     *
     * @param channel A {@link ManagedChannel}.
     */
    DefaultXpringClient(final ManagedChannel channel) {
        // It is up to the client to determine whether to block the call. Here we create a blocking stub, but an async
        // stub, or an async stub with Future are always possible.
        this.stub = XRPLedgerAPIServiceGrpc.newBlockingStub(channel);
    }

    /**
     * Get the balance of the specified account on the XRP Ledger.
     *
     * @param xrplAccountAddress The X-Address to retrieve the balance for.
     * @return A {@link BigInteger} with the number of drops in this account.
     * @throws XpringKitException If the given inputs were invalid.
     */
    public BigInteger getBalance(final String xrplAccountAddress) throws XpringKitException {
        if (!Utils.isValidXAddress(xrplAccountAddress)) {
            throw XpringKitException.xAddressRequiredException;
        }
        ClassicAddress classicAddress = Utils.decodeXAddress(xrplAccountAddress);

        AccountRoot accountData = this.getAccountData(classicAddress.address());

      return BigInteger.valueOf(accountData.getBalance().getDrops());
    }

    /**
     * Retrieve the transaction status for a given transaction hash.
     *
     * @param transactionHash The hash of the transaction.
     * @return The status of the given transaction.
     */
    public TransactionStatus getTransactionStatus(String transactionHash) throws XpringKitException {
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
     * @param amount The number of drops of XRP to send.
     * @param destinationAddress The X-Address to send the XRP to.
     * @param sourceWallet The {@link Wallet} which holds the XRP.
     * @return A transaction hash for the payment.
     * @throws XpringKitException If the given inputs were invalid.
     */
    public String send(
            final BigInteger amount,
            final String destinationAddress,
            final Wallet sourceWallet
    ) throws XpringKitException {
        Objects.requireNonNull(amount);
        Objects.requireNonNull(destinationAddress);
        Objects.requireNonNull(sourceWallet);

        if (!Utils.isValidXAddress(destinationAddress)) {
            throw XpringKitException.xAddressRequiredException;
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

        XRPDropsAmount drops = XRPDropsAmount.newBuilder().setDrops(amount.longValue()).build();
        CurrencyAmount currencyAmount = CurrencyAmount.newBuilder().setXrpAmount(drops).build();

        Payment.Builder paymentBuilder = Payment.newBuilder()
                .setAmount(currencyAmount)
                .setDestination(destinationAccountAddress);
        if (destinationClassicAddress.tag().isPresent()) {
            paymentBuilder.setDestinationTag(destinationClassicAddress.tag().get());
        }

        Payment payment = paymentBuilder.build();

        byte [] signingPublicKeyBytes = Utils.hexStringToByteArray(sourceWallet.getPublicKey());
        int lastLedgerSequence = lastValidatedLedgerSequence + MAX_LEDGER_VERSION_OFFSET;

        Transaction transaction = Transaction.newBuilder()
                .setAccount(sourceAccountAddress)
                .setFee(fee)
                .setSequence(accountData.getSequence())
                .setPayment(payment)
                .setLastLedgerSequence(lastLedgerSequence)
                .setSigningPublicKey(ByteString.copyFrom(signingPublicKeyBytes))
                .build();

        byte [] signedTransaction = Signer.signTransaction(transaction, sourceWallet);

        SubmitTransactionRequest request = SubmitTransactionRequest.newBuilder()
                .setSignedTransaction(ByteString.copyFrom(signedTransaction))
                .build();

        SubmitTransactionResponse response = this.stub.submitTransaction(request);

        byte [] hashBytes = response.getHash().toByteArray();
        return Utils.byteArrayToHex(hashBytes);
    }

    @Override
    public int getLatestValidatedLedgerSequence() throws XpringKitException {
        return this.getFeeResponse().getLedgerCurrentIndex();
    }

    @Override
    public RawTransactionStatus getRawTransactionStatus(String transactionHash) throws XpringKitException {
        Objects.requireNonNull(transactionHash);

        byte [] transactionHashBytes = Utils.hexStringToByteArray(transactionHash);
        ByteString transactionHashByteString = ByteString.copyFrom(transactionHashBytes);
        GetTxRequest request = GetTxRequest.newBuilder().setHash(transactionHashByteString).build();

        GetTxResponse response = this.stub.getTx(request);

        return new RawTransactionStatus(response);
    }

    private XRPDropsAmount getMinimumFee() {
        return this.getFeeResponse().getDrops().getMinimumFee();
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
