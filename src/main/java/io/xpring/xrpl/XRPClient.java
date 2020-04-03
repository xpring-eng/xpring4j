package io.xpring.xrpl;

import java.math.BigInteger;

/**
 * A client that can submit transactions to the XRP Ledger.
 *
 * @see "https://xrpl.org"
 */
public class XRPClient implements XRPClientInterface {
    private XRPClientDecorator decoratedClient;

    /**
     * Initialize a new client with the given options.
     *
     * @param grpcURL The remote URL to use for gRPC calls.
     */
    public XRPClient(String grpcURL) {
        XRPClientDecorator defaultXRPClient =  new DefaultXRPClient(grpcURL);
        this.decoratedClient = new ReliableSubmissionXRPClient(defaultXRPClient);
    }

    /**
     * Get the balance of the specified account on the XRP Ledger.
     **
     * @param xrplAccountAddress The X-Address to retrieve the balance for.
     * @return A {@link BigInteger} with the number of drops in this account.
     * @throws XRPException If the given inputs were invalid.
     */
    public BigInteger getBalance(final String xrplAccountAddress) throws XRPException {
        return decoratedClient.getBalance(xrplAccountAddress);
    }

    /**
     * Retrieve the transaction status for a Payment given transaction hash.
     *
     * Note: This method will only work for Payment type transactions which do not have the tf_partial_payment attribute set.
     * See: https://xrpl.org/payment.html#payment-flags
     *
     * @param transactionHash The hash of the transaction.
     * @return The status of the given transaction.
     */
    public TransactionStatus getPaymentStatus(String transactionHash) throws XRPException {
        return decoratedClient.getPaymentStatus(transactionHash);
    }

    /**
     * Transact XRP between two accounts on the ledger.
     *
     * @param amount The number of drops of XRP to send.
     * @param destinationAddress The X-Address to send the XRP to.
     * @param sourceWallet The {@link Wallet} which holds the XRP.
     * @return A transaction hash for the payment.
     * @throws XRPException If the given inputs were invalid.
     * */
    public String send(
            final BigInteger amount,
            final String destinationAddress,
            final Wallet sourceWallet
    ) throws XRPException {
        return decoratedClient.send(amount, destinationAddress, sourceWallet);
    }

    /**
     * Check if an address exists on the XRP Ledger.
     *
     * @param xrplAccountAddress The address to check the existence of.
     * @return A boolean if the account is on the XRP Ledger.
     */
    public boolean accountExists(final String xrplAccountAddress) throws XRPException {
        return decoratedClient.accountExists(xrplAccountAddress);
    }
}
