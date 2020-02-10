package io.xpring.xrpl.ilp;

import org.interledger.spsp.server.grpc.CreateAccountRequest;
import org.interledger.spsp.server.grpc.CreateAccountResponse;
import org.interledger.spsp.server.grpc.GetBalanceResponse;

import io.xpring.xrpl.XpringKitException;

import java.math.BigInteger;
import java.util.Optional;

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

    /**
     * Create an account on the connector.  Because no settings or auth were passed in, Hermes will generate an account
     * with default settings, a generated account ID, and a generated SIMPLE auth token
     *
     * @return A {@link CreateAccountResponse} containing the account settings that were created on the connector
     * @throws XpringKitException If account creation failed.
     */
    public CreateAccountResponse createAccount() throws XpringKitException {
        return decoratedClient.createAccount();
    };

    /**
     * Create an account on the connector.
     *
     * @param createAccountRequest: Optional request object.  If createAccountRequest is empty, Hermes will generate an account for you
     * @param bearerToken: Optional auth token.  If empty, Hermes will generate a simple auth token for you
     *
     * @return A {@link CreateAccountResponse} containing the account settings that were created on the connector
     * @throws XpringKitException If the given inputs were invalid or account creation failed.
     */
    public CreateAccountResponse createAccount(Optional<CreateAccountRequest> createAccountRequest,
                                               Optional<String> bearerToken) throws XpringKitException {
        return decoratedClient.createAccount(createAccountRequest, bearerToken);
    }

    public CreateAccountResponse createAccount(CreateAccountRequest createAccountRequest,
                                               String bearerToken) throws XpringKitException {
        return decoratedClient.createAccount(createAccountRequest, bearerToken);
    }
}
