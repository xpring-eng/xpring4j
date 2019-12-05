package io.xpring.xrpl;

import io.xpring.proto.GetLatestValidatedLedgerSequenceRequest;
import io.xpring.proto.LedgerSequence;

import java.math.BigInteger;

/**
 * An common interface shared between XpringClient and the internal hierarchy of decorators.
 */
public interface XpringClientDecorator {
    /**
     * Get the balance of the specified account on the XRP Ledger.
     *
     * @param xrplAccountAddress The X-Address to retrieve the balance for.
     * @return A {@link BigInteger} with the number of drops in this account.
     * @throws XpringKitException If the given inputs were invalid.
     */
    BigInteger getBalance(final String xrplAccountAddress) throws XpringKitException;


    /**
     * Retrieve the transaction status for a given transaction hash.
     *
     * @param transactionHash The hash of the transaction.
     * @return The status of the given transaction.
     */
    TransactionStatus getTransactionStatus(String transactionHash);

    /**
     * Transact XRP between two accounts on the ledger.
     *
     * @param amount The number of drops of XRP to send.
     * @param destinationAddress The X-Address to send the XRP to.
     * @param sourceWallet The {@link Wallet} which holds the XRP.
     * @return A transaction hash for the payment.
     * @throws XpringKitException If the given inputs were invalid.
     */
    String send(
            final BigInteger amount,
            final String destinationAddress,
            final Wallet sourceWallet
    ) throws XpringKitException;

    /**
     * Retrieve the latest validated ledger sequence on the XRP Ledger.
     *
     * @return A long representing the sequence of the most recently validated ledger.
     */
    int getLatestValidatedLedgerSequence();

    /**
     * Retrieve the raw transaction status for the given transaction hash.
     *
     * @param transactionHash: The hash of the transaction.
     * @return an {@link io.xpring.proto.TransactionStatus} containing the raw transaction status.
     */
    io.xpring.proto.TransactionStatus getRawTransactionStatus(String transactionHash);
}