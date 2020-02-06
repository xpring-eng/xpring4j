package io.xpring.xrpl.ilp;

import io.xpring.xrpl.XpringKitException;

import java.math.BigInteger;

/**
 * A client that can get balances on a connector and send ILP payments.
 *
 */
public class IlpClient {
    private IlpClientDecorator decoratedClient;

    /**
     * Initialize a new client with the given options.
     *
     */
    public IlpClient() {
        this.decoratedClient = new DefaultIlpClient();
    }

    /**
     * Get the balance of the specified account on the connector.
     *
     * @param accountId The account ID to get the balance for.
     * @return A {@link BigInteger} with the number of drops in this account.
     * @throws XpringKitException If the given inputs were invalid.
     */
    // TODO: change return value to the generated client AccountBalance entity
    public BigInteger getBalance(final String accountId) throws XpringKitException {
        return decoratedClient.getBalance(accountId);
    }
}
