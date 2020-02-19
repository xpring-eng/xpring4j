package io.xpring.ilp;

import org.interledger.spsp.server.grpc.CreateAccountResponse;
import org.interledger.spsp.server.grpc.GetAccountResponse;
import org.interledger.spsp.server.grpc.GetBalanceResponse;
import org.interledger.spsp.server.grpc.SendPaymentResponse;

import com.google.common.annotations.VisibleForTesting;
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
     * Constructor meant only for integration testing, so that we can point the client at a docker container host
     * @param grpcUrl : Url of the Hermes host within a docker container
     *
     */
    @VisibleForTesting
    protected IlpClient(String grpcUrl) {
        this.decoratedClient = new DefaultIlpClient(grpcUrl);
    }

    /**
     * Create an account on the connector.  Because no settings or auth were passed in, an account
     * with default settings, a generated account ID, and a generated SIMPLE auth token will be generated for you
     *
     * @return A {@link CreateAccountResponse} containing the account settings that were created on the connector
     * @throws XpringKitException If account creation failed.
     */
    public CreateAccountResponse createAccount() throws XpringKitException {
        return decoratedClient.createAccount();
    };

    /**
     * Create an account on the connector.
     * bearerToken will be generated.
     *
     * @param accountId : Account Id of the created account
     * @param assetCode : Asset code of the account
     * @param assetScale : Asset scale of the account
     * @param description : Description of the account
     * @return A {@link CreateAccountResponse} containing the account settings that were created on the connector
     * @throws XpringKitException If the given inputs were invalid or account creation failed.
     */
    CreateAccountResponse createAccount(String accountId,
                                        String assetCode,
                                        Integer assetScale,
                                        String description) throws XpringKitException {
        return decoratedClient.createAccount(accountId, assetCode, assetScale, description);
    };

    /**
     * Create an account on the connector.
     * bearerToken and accountId will be generated.
     *
     * @param assetCode : Asset code of the account
     * @param assetScale : Asset scale of the account
     * @param description : Description of the account
     * @return A {@link CreateAccountResponse} containing the account settings that were created on the connector
     * @throws XpringKitException If the given inputs were invalid or account creation failed.
     */
    CreateAccountResponse createAccount(String assetCode,
                                        Integer assetScale,
                                        String description) throws XpringKitException {
        return decoratedClient.createAccount(assetCode, assetScale, description);
    };

    /**
     * Create an account on the connector.
     * bearerToken and accountId will be generated.
     * description will default to empty.
     *
     * @param assetCode : Asset code of the account
     * @param assetScale : Asset scale of the account
     * @return A {@link CreateAccountResponse} containing the account settings that were created on the connector
     * @throws XpringKitException If the given inputs were invalid or account creation failed.
     */
    CreateAccountResponse createAccount(String assetCode,
                                        Integer assetScale) throws XpringKitException {
        return decoratedClient.createAccount(assetCode, assetScale);
    };

    /**
     * Create an account on the connector.
     * Description will default to empty.
     *
     * @param bearerToken : Auth token.  If empty, a simple auth token will be generated for you
     *
     * @param accountId : Account Id of the created account
     * @param assetCode : Asset code of the account
     * @param assetScale : Asset scale of the account
     * @return A {@link CreateAccountResponse} containing the account settings that were created on the connector
     * @throws XpringKitException If the given inputs were invalid or account creation failed.
     */
    CreateAccountResponse createAccount(String bearerToken,
                                        String accountId,
                                        String assetCode,
                                        Integer assetScale) throws XpringKitException {
        return decoratedClient.createAccount(bearerToken, accountId, assetCode, assetScale);
    }

    /**
     * Create an account on the connector.
     *
     * @param bearerToken : Auth token.  If empty, a simple auth token will be generated for you
     *
     * @param accountId : Account Id of the created account
     * @param assetCode : Asset code of the account
     * @param assetScale : Asset scale of the account
     * @param description : Description of the account
     * @return A {@link CreateAccountResponse} containing the account settings that were created on the connector
     * @throws XpringKitException If the given inputs were invalid or account creation failed.
     */
    CreateAccountResponse createAccount(String bearerToken,
                                        String accountId,
                                        String assetCode,
                                        Integer assetScale,
                                        String description) throws XpringKitException {
        return decoratedClient.createAccount(bearerToken, accountId, assetCode, assetScale, description);
    };

    /**
     * Gets account details for the given account ID
     *
     * @param accountId
     * @param bearerToken
     * @return
     * @throws XpringKitException
     */
    public GetAccountResponse getAccount(String accountId, String bearerToken) throws XpringKitException {
        return decoratedClient.getAccount(accountId, bearerToken);
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
     * Send a payment from the given accountId to the destinationPaymentPointer payment pointer
     * @param destinationPaymentPointer : payment pointer of the receiver
     * @param amount : Amount to send
     * @param accountId : accountId of the sender
     * @param bearerToken : auth token of the sender
     * @return
     * @throws XpringKitException
     */
    public SendPaymentResponse sendPayment(final String destinationPaymentPointer,
                                           final long amount,
                                           final String accountId,
                                           final String bearerToken) throws XpringKitException {
        return decoratedClient.sendPayment(destinationPaymentPointer, amount, accountId, bearerToken);
    }
}
