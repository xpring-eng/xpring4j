package io.xpring.xrpl;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.xpring.proto.*;
import io.xpring.proto.XRPLedgerAPIGrpc.XRPLedgerAPIBlockingStub;
import io.xpring.xrpl.XpringClientDecorator;
import io.xpring.xrpl.XpringKitException;
import io.xpring.xrpl.legacy.LegacySigner;
import io.xpring.xrpl.TransactionStatus;
import io.xpring.xrpl.Wallet;
import io.xpring.xrpl.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final int LEDGER_SEQUENCE_MARGIN = 10;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // Channel is the abstraction to connect to a service endpoint
    private final XRPLedgerAPIBlockingStub stub;

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
        throw XpringKitException.unimplemented;
    }

    /**
     * Retrieve the transaction status for a given transaction hash.
     *
     * @param transactionHash The hash of the transaction.
     * @return The status of the given transaction.
     */
    public TransactionStatus getTransactionStatus(String transactionHash) throws XpringKitException {
        throw XpringKitException.unimplemented;
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
    public io.xpring.proto.TransactionStatus getRawTransactionStatus(String transactionHash) throws XpringKitException {
        throw XpringKitException.unimplemented;
    }
}
