package io.xpring.xrpl;

import java.math.BigInteger;

/**
 * An common interface shared between XRPClient and the internal hierarchy of decorators.
 */
public interface XRPClientDecorator {
    /**
     * Get the balance of the specified account on the XRP Ledger.
     *
     * @param xrplAccountAddress The X-Address to retrieve the balance for.
     * @return A {@link BigInteger} with the number of drops in this account.
     * @throws XRPException If the given inputs were invalid.
     */
    BigInteger getBalance(final String xrplAccountAddress) throws XRPException;


    /**
     * Retrieve the transaction status for a Payment given transaction hash.
     *
     * Note: This method will only work for Payment type transactions which do not have the tf_partial_payment attribute set.
     * See: https://xrpl.org/payment.html#payment-flags
     *
     * @param transactionHash The hash of the transaction.
     * @return The status of the given transaction.
     */
    public TransactionStatus getPaymentStatus(String transactionHash) throws XRPException;

    /**
     * Transact XRP between two accounts on the ledger.
     *
     * @param amount The number of drops of XRP to send.
     * @param destinationAddress The X-Address to send the XRP to.
     * @param sourceWallet The {@link Wallet} which holds the XRP.
     * @return A transaction hash for the payment.
     * @throws XRPException If the given inputs were invalid.
     */
    String send(
            final BigInteger amount,
            final String destinationAddress,
            final Wallet sourceWallet
    ) throws XRPException;

    /**
     * Retrieve the latest validated ledger sequence on the XRP Ledger.
     *
     * @return A long representing the sequence of the most recently validated ledger.
     */
    int getLatestValidatedLedgerSequence() throws XRPException;

    /**
     * Retrieve the raw transaction status for the given transaction hash.
     *
     * @param transactionHash: The hash of the transaction.
     * @return an {@link io.xpring.proto.TransactionStatus} containing the raw transaction status.
     */
    RawTransactionStatus getRawTransactionStatus(String transactionHash) throws XRPException;
}