package io.xpring.xrpl;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.xpring.proto.AccountInfo;
import io.xpring.proto.Fee;
import io.xpring.proto.GetAccountInfoRequest;
import io.xpring.proto.GetFeeRequest;
import io.xpring.proto.Payment;
import io.xpring.proto.SignedTransaction;
import io.xpring.Signer;
import io.xpring.proto.SubmitSignedTransactionRequest;
import io.xpring.proto.SubmitSignedTransactionResponse;
import io.xpring.proto.Transaction;
import io.xpring.Utils;
import io.xpring.Wallet;
import io.xpring.XpringKitException;
import io.xpring.proto.XRPAmount;
import io.xpring.proto.XRPLedgerAPIGrpc;
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
        if (!Utils.isValidXAddress(destinationAddress)) {
            throw XpringKitException.xAddressRequiredException;
        }

        AccountInfo accountInfo = this.getAccountInfo(sourceWallet.getAddress());
        BigInteger currentFeeInDrops = this.getCurrentFeeInDrops();

        Transaction transaction = Transaction.newBuilder()
            .setAccount(sourceWallet.getAddress())
            .setFee(XRPAmount.newBuilder().setDrops(currentFeeInDrops.toString()).build())
            .setSequence(accountInfo.getSequence())
            .setPayment(Payment.newBuilder()
                .setDestination(destinationAddress)
                .setXrpAmount(XRPAmount.newBuilder().setDrops(amount.toString()).build())
                .build())
            .setSigningPublicKeyHex(sourceWallet.getPublicKey())
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
}
