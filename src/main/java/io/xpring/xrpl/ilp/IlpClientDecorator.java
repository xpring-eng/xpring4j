package io.xpring.xrpl.ilp;

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
     * @return A {@link BigInteger} with the number of drops in this account.
     * @throws XpringKitException If the given inputs were invalid.
     */
    // TODO: change return value to the generated client AccountBalance entity
    BigInteger getBalance(final String accountId) throws XpringKitException;

}
