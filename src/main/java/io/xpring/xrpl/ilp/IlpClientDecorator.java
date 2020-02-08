package io.xpring.xrpl.ilp;

import org.interledger.spsp.server.grpc.GetBalanceResponse;

import io.xpring.xrpl.XpringKitException;

import java.math.BigInteger;

/**
 * An common interface shared between XpringClient and the internal hierarchy of decorators.
 */
public interface IlpClientDecorator {

    /**
     * Get the balance of the specified account on the connector.
     *
     * @param accountId The account ID to get the balance for.
     * @param bearerToken Authentication bearer token.
     * @return A {@link BigInteger} with the number of drops in this account.
     * @throws XpringKitException If the given inputs were invalid.
     */
    GetBalanceResponse getBalance(final String accountId, final String bearerToken) throws XpringKitException;

}
