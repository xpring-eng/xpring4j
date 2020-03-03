package io.xpring.xrpl;

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
     * @throws XpringException If the given inputs were invalid.
     */
    BigInteger getBalance(final String xrplAccountAddress) throws XpringException;


    /**
     * Retrieve the transaction status for a given transaction hash.
     *
     * @param transactionHash The hash of the transaction.
     * @return The status of the given transaction.
     */
    TransactionStatus getTransactionStatus(String transactionHash) throws XpringException;

    /**
     * Transact XRP between two accounts on the ledger.
     *
     * @param amount The number of drops of XRP to send.
     * @param destinationAddress The X-Address to send the XRP to.
     * @param sourceWallet The {@link Wallet} which holds the XRP.
     * @return A transaction hash for the payment.
     * @throws XpringException If the given inputs were invalid.
     */
    String send(
            final BigInteger amount,
            final String destinationAddress,
            final Wallet sourceWallet
    ) throws XpringException;

    /**
     * Retrieve the latest validated ledger sequence on the XRP Ledger.
     *
     * @return A long representing the sequence of the most recently validated ledger.
     */
    int getLatestValidatedLedgerSequence() throws XpringException;

    /**
     * Retrieve the raw transaction status for the given transaction hash.
     *
     * @param transactionHash: The hash of the transaction.
     * @return an {@link io.xpring.proto.TransactionStatus} containing the raw transaction status.
     */
    RawTransactionStatus getRawTransactionStatus(String transactionHash) throws XpringException;

    /**
     * Retrieve the transaction history for an address.
     *
     * @param address: The address to retrieve transaction history for.
     * @return: An array of {@link Transaction}s for the account.
     * @throws XpringException If the given inputs were invalid.
     */
    Transaction [] getTransactionHistory(String address) throws XpringException;
}