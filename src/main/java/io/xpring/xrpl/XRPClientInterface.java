package io.xpring.xrpl;

import io.xpring.common.XRPLNetwork;

import java.math.BigInteger;

public interface XRPClientInterface {
    /**
     * Retrieve the network that this XRPClient connects to.
     */
    public XRPLNetwork getNetwork();

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
    TransactionStatus getPaymentStatus(String transactionHash) throws XRPException;

    /**
     * Transact XRP between two accounts on the ledger.
     *
     * @param amount The number of drops of XRP to send.
     * @param destinationAddress The X-Address to send the XRP to.
     * @param sourceWallet The {@link Wallet} which holds the XRP.
     * @return A transaction hash for the payment.
     * @throws XRPException If the given inputs were invalid.
     */
    public String send(BigInteger amount, String destinationAddress,  Wallet sourceWallet) throws XRPException;
}
