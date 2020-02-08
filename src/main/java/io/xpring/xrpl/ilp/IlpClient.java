package io.xpring.xrpl.ilp;

import org.interledger.spsp.server.grpc.GetBalanceResponse;

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
     * @param bearerToken Authentication bearer token. TODO: Probably change from string to some wrapped JWT class
     * @return A {@link BigInteger} with the number of drops in this account.
     * @throws XpringKitException If the given inputs were invalid.
     */
    public GetBalanceResponse getBalance(final String accountId, final String bearerToken) throws XpringKitException {
        return decoratedClient.getBalance(accountId, bearerToken);
    }
}
