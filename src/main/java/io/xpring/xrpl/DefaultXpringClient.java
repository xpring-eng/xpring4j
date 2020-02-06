package io.xpring.xrpl;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.v1.AccountInfo;
import rpc.v1.Amount.AccountAddress;
import rpc.v1.AccountInfo.GetAccountInfoRequest;
import rpc.v1.AccountInfo.GetAccountInfoResponse;
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
    // A margin to pad the current ledger sequence with when submitting transactions.
    private static final int LEDGER_SEQUENCE_MARGIN = 10;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // Channel is the abstraction to connect to a service endpoint
    private final XRPLedgerAPIServiceBlockingStub stub;

    /**
     * No-args Constructor.
     */
    public DefaultXpringClient(String grpcURL) {
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

        AccountAddress account = AccountAddress.newBuilder().setAddress(xrplAccountAddress).build();
        GetAccountInfoRequest request = GetAccountInfoRequest.newBuilder().setAccount(account).build();

        GetAccountInfoResponse response = this.stub.getAccountInfo(request);

        long drops = response.getAccountData().getBalance().getDrops();
        return BigInteger.valueOf(drops);
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
        throw XpringKitException.unimplemented;
    }

    @Override
    public int getLatestValidatedLedgerSequence() throws XpringKitException {
        throw XpringKitException.unimplemented;
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
}
