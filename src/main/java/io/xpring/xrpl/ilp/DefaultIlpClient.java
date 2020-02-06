package io.xpring.xrpl.ilp;

import io.xpring.xrpl.XpringKitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.math.BigInteger;

/**
 * A client that can get balances on a connector and send ILP payments.
 *
 * NOTE: This is where we'll interact with the generated client from (maybe) Swagger.
 * {@link IlpClient} should use this as its decorated client so that it can hide the implementation details
 * of the client
 *
 */
public class DefaultIlpClient implements IlpClientDecorator {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * No-args Constructor.
     */
    public DefaultIlpClient() { }

    /**
     * Get the balance of the specified account on the connector.
     *
     * @param accountId The account ID to get the balance for.
     * @return A {@link BigInteger} with the number of drops in this account.
     * @throws XpringKitException If the given inputs were invalid.
     */
    // TODO: change return value to the generated client AccountBalance entity
    public BigInteger getBalance(final String accountId) throws XpringKitException {
        throw new NotImplementedException();
    }
}
