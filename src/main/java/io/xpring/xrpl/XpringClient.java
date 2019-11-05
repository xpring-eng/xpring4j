package io.xpring.xrpl;

import com.google.common.annotations.VisibleForTesting;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.xpring.AccountInfoOuterClass.AccountInfo;
import io.xpring.FeeOuterClass.Fee;
import io.xpring.GetAccountInfoRequestOuterClass.GetAccountInfoRequest;
import io.xpring.GetFeeRequestOuterClass.GetFeeRequest;
import io.xpring.PaymentOuterClass.Payment;
import io.xpring.SignedTransactionOuterClass.SignedTransaction;
import io.xpring.Signer;
import io.xpring.SubmitSignedTransactionRequestOuterClass.SubmitSignedTransactionRequest;
import io.xpring.SubmitSignedTransactionResponseOuterClass.SubmitSignedTransactionResponse;
import io.xpring.TransactionOuterClass.Transaction;
import io.xpring.Utils;
import io.xpring.Wallet;
import io.xpring.XrpAmount.XRPAmount;
import io.xpring.xrpl.XRPLedgerGrpc.XRPLedgerBlockingStub;
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
    private final XRPLedgerBlockingStub stub;

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
        this.stub = XRPLedgerGrpc.newBlockingStub(channel);
    }

    /**
     * Get the balance of the specified account on the XRP Ledger.
     *
     * @param xrplAccountAddress The address to retrieve the balance for.
     *
     * @return A {@link BigInteger} with the number of drops in this account.
     */
    public BigInteger getBalance(final String xrplAccountAddress) {
        Objects.requireNonNull(xrplAccountAddress, "xrplAccountAddress must not be null");

        AccountInfo result = stub
            .getAccountInfo(GetAccountInfoRequest.newBuilder().setAddress(xrplAccountAddress).build());

        logger.debug(
            "Account balance successfully retrieved. accountAddress={} balance={}",
            xrplAccountAddress, result.getBalance().toString()
        );

        return new BigInteger(result.getBalance().getDrops());
    }

    public String send(
        final BigInteger amount,
        final String destinationAddress,
        final Wallet sourceWallet
    ) {
        AccountInfo accountInfo = this.getAccountInfo(sourceWallet.getAddress());
        BigInteger currentFeeInDrops = this.getCurrentFeeInDrops();

        Transaction transaction = Transaction.newBuilder()
            .setAccount(sourceWallet.getAddress())
            .setFee(XRPAmount.newBuilder().setDrops(currentFeeInDrops.toString()).build())
            .setSequence(accountInfo.getSequence())
            .setPayment(Payment.newBuilder()
                .setDestination(destinationAddress)
                .setXRPAmount(XRPAmount.newBuilder().setDrops(amount.toString()).build())
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
    @VisibleForTesting
    BigInteger getCurrentFeeInDrops() {
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
    @VisibleForTesting
    AccountInfo getAccountInfo(final String xrplAddress) {
        return stub.getAccountInfo(
            GetAccountInfoRequest.newBuilder().setAddress(xrplAddress).build()
        );
    }
}
