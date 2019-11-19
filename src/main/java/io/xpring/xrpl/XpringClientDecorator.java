package io.xpring.xrpl;

import java.math.BigInteger;

public interface XpringClientDecorator {
    /**
     * Get the balance of the specified account on the XRP Ledger.
     *
     * @param xrplAccountAddress The X-Address to retrieve the balance for.
     * @return A {@link BigInteger} with the number of drops in this account.
     * @throws XpringKitException If the given inputs were invalid.
     */
    public BigInteger getBalance(final String xrplAccountAddress) throws XpringKitException;

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
    ) throws XpringKitException;
}