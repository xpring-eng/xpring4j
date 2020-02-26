package io.xpring.ilp;

import org.interledger.spsp.server.grpc.CreateAccountResponse;
import org.interledger.spsp.server.grpc.GetAccountResponse;
import org.interledger.spsp.server.grpc.GetBalanceResponse;
import org.interledger.spsp.server.grpc.SendPaymentResponse;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.primitives.UnsignedLong;
import io.grpc.ExperimentalApi;
import io.xpring.xrpl.XpringException;

import java.math.BigInteger;
import java.util.Optional;

/**
 * A client that can create accounts, get accounts, get balances, and send ILP payments on a connector.
 */
public class IlpClient {
    private IlpClientDecorator decoratedClient;

    /**
     * Initialize a new client with default options.
     *
     */
    public IlpClient() {
        this.decoratedClient = new DefaultIlpClient();
    }

    /**
     * Initialize a new client with a configured URL
     * @param grpcUrl : The gRPC URL exposed by Hermes
     *
     */
    public IlpClient(String grpcUrl) {
        this.decoratedClient = new DefaultIlpClient(grpcUrl);
    }

    /**
     * Create an account on the connector.  Because no settings or auth were passed in, an account
     * with default settings, a generated account ID, and a generated SIMPLE auth token will be generated for you
     *
     * @return A {@link CreateAccountResponse} containing the account settings that were created on the connector
     * @throws XpringException If account creation failed.
     */
    public CreateAccountResponse createAccount() throws XpringException {
        return decoratedClient.createAccount();
    }

    /**
     * Create an account on the connector.
     *
     * @param createAccountRequest: A request object with specified account details. Note that {@link CreateAccountRequest#assetScale}
     *                           and {@link CreateAccountRequest#assetCode} MUST be specified, as guaranteed by {@link CreateAccountRequest#builder(String, Integer)}
     * @param bearerToken: An optionally present authentication token, for example a JWT. If no token is specified, a simple bearer
     *                   token will be generated.
     * @return A {@link CreateAccountResponse} containing the account settings that were created on the connector
     * @throws XpringException If the given inputs were invalid or account creation failed.
     */
    public CreateAccountResponse createAccount(CreateAccountRequest createAccountRequest, Optional<String> bearerToken) throws XpringException {
        return decoratedClient.createAccount(createAccountRequest, bearerToken);
    }

    /**
     * Gets account details for the given account ID
     *
     * @param accountId: Unique Identifier of this account.
     * @param bearerToken: Authentication token to access the account.
     * @return A {@link GetAccountResponse} with account details and settings.
     * @throws XpringException if the given inputs were invalid, the account doesn't exist, or something else went wrong
     */
    public GetAccountResponse getAccount(String accountId, String bearerToken) throws XpringException {
        return decoratedClient.getAccount(accountId, bearerToken);
    }

    /**
     * Get the balance of the specified account on the connector.
     *
     * @param accountId The account ID to get the balance for.
     * @param bearerToken Authentication bearer token.
     * @return A {@link BigInteger} with the number of drops in this account.
     * @throws XpringException If the given inputs were invalid, the account doesn't exist, or authentication failed.
     */
    public GetBalanceResponse getBalance(final String accountId, final String bearerToken) throws XpringException {
        return decoratedClient.getBalance(accountId, bearerToken);
    }

    /**
     * Send a payment from the given accountId to the destinationPaymentPointer payment pointer
     *
     * @param destinationPaymentPointer : payment pointer of the receiver
     * @param amount : Amount to send
     * @param accountId : accountId of the sender
     * @param bearerToken : auth token of the sender
     * @return A {@link SendPaymentResponse} with details about the payment. Note that this method will not
     *          necessarily throw an exception if the payment failed. Payment status can be checked in
     *          {@link SendPaymentResponse#getSuccessfulPayment()}
     * @throws XpringException If the given inputs were invalid.
     */
    public SendPaymentResponse sendPayment(final String destinationPaymentPointer,
                                           final UnsignedLong amount,
                                           final String accountId,
                                           final String bearerToken) throws XpringException {
        return decoratedClient.sendPayment(destinationPaymentPointer, amount, accountId, bearerToken);
    }
}