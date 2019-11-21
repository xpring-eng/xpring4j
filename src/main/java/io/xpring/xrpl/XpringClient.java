package io.xpring.xrpl;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.xpring.proto.*;
import io.xpring.proto.XRPLedgerAPIGrpc.XRPLedgerAPIBlockingStub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.Objects;

/**
 * A client that can submit transactions to the XRP Ledger.
 *
 * @see "https://xrpl.org"
 */
public class XpringClient {
    // TODO: Use TLS!
    public static final String XPRING_TECH_GRPC_URL = "grpc.xpring.tech:80";

    // A margin to pad the current ledger sequence with when submitting transactions.
    private static final int LEDGER_SEQUENCE_MARGIN = 10;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // Channel is the abstraction to connect to a service endpoint
    private final XRPLedgerAPIBlockingStub stub;

    /**
     * No-args Constructor.
     */
    public XpringClient() {
        this(ManagedChannelBuilder
                .forTarget(XPRING_TECH_GRPC_URL)
                // Let's use plaintext communication because we don't have certs
                // TODO: Use TLS!
                .usePlaintext(true)
                .build()
        );
    }

    /**
     * Required-args Constructor, currently for testing.
     *
     * @param channel A {@link ManagedChannel}.
     */
    XpringClient(final ManagedChannel channel) {
        // It is up to the client to determine whether to block the call. Here we create a blocking stub, but an async
        // stub, or an async stub with Future are always possible.
        this.stub = XRPLedgerAPIGrpc.newBlockingStub(channel);
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

        Objects.requireNonNull(xrplAccountAddress, "xrplAccountAddress must not be null");

        AccountInfo result = stub
                .getAccountInfo(GetAccountInfoRequest.newBuilder().setAddress(xrplAccountAddress).build());

        logger.debug(
                "Account balance successfully retrieved. accountAddress={} balance={}",
                xrplAccountAddress, result.getBalance().toString()
        );

        return new BigInteger(result.getBalance().getDrops());
    }

    /**
     * Retrieve the transaction status for a given transaction hash.
     *
     * @param transactionHash The hash of the transaction.
     * @returns The status of the given transaction.
     */
    public TransactionStatus getTransactionStatus(String transactionHash) {
        GetTransactionStatusRequest transactionStatusRequest = GetTransactionStatusRequest.newBuilder().setTransactionHash(transactionHash).build();

        io.xpring.proto.TransactionStatus transactionStatus = stub.getTransactionStatus(transactionStatusRequest);

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
     * */
    public String send(
            final BigInteger amount,
            final String destinationAddress,
            final Wallet sourceWallet
    ) throws XpringKitException {
        if (!Utils.isValidXAddress(destinationAddress)) {
            throw XpringKitException.xAddressRequiredException;
        }

        AccountInfo accountInfo = this.getAccountInfo(sourceWallet.getAddress());
        BigInteger currentFeeInDrops = this.getCurrentFeeInDrops();
        int lastValidatedLedgerSequence = this.getLatestValidatedLedgerSequence();

        Transaction transaction = Transaction.newBuilder()
                .setAccount(sourceWallet.getAddress())
                .setFee(XRPAmount.newBuilder().setDrops(currentFeeInDrops.toString()).build())
                .setSequence(accountInfo.getSequence())
                .setPayment(Payment.newBuilder()
                        .setDestination(destinationAddress)
                        .setXrpAmount(XRPAmount.newBuilder().setDrops(amount.toString()).build()).build())
                .setSigningPublicKeyHex(sourceWallet.getPublicKey()).setLastLedgerSequence(lastValidatedLedgerSequence + LEDGER_SEQUENCE_MARGIN)
                .build();

        SignedTransaction signedTransaction = Signer.signTransaction(transaction, sourceWallet);

        SubmitSignedTransactionRequest submitSignedTransactionRequest = SubmitSignedTransactionRequest.newBuilder()
                .setSignedTransaction(signedTransaction).build();

        SubmitSignedTransactionResponse response = stub.submitSignedTransaction(submitSignedTransactionRequest);
        return Utils.toTransactionHash(response.getTransactionBlob());
    }

    /**
     * Retrieve the current fee to submit a transaction to the XRP Ledger.
     *
     * @return A {@link BigInteger} representing a `fee` for submitting a transaction to the ledger.
     */
    private BigInteger getCurrentFeeInDrops() {
        Fee getFeeResult = stub.getFee(GetFeeRequest.newBuilder().build());
        return new BigInteger(getFeeResult.getAmount().getDrops());
    }

    /**
     * Retrieve an `AccountInfo` for an address on the XRP Ledger.
     *
     * @param xrplAddress The address to retrieve information about.
     *
     * @return An {@link AccountInfo} containing data about the given address.
     */
    private AccountInfo getAccountInfo(final String xrplAddress) {
        return stub.getAccountInfo(
                GetAccountInfoRequest.newBuilder().setAddress(xrplAddress).build()
        );
    }

    /**
     * Retrieve the latest validated ledger sequence on the XRP Ledger.
     *
     * @return A long representing the sequence of the most recently validated ledger.
     */
    private int getLatestValidatedLedgerSequence() {
        GetLatestValidatedLedgerSequenceRequest request = GetLatestValidatedLedgerSequenceRequest.newBuilder().build();
        LedgerSequence ledgerSequence = stub.getLatestValidatedLedgerSequence(request);
        return ledgerSequence.getIndex();
    }
}
