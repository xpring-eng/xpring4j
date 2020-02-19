package io.xpring.ilp;

import org.interledger.spsp.server.grpc.CreateAccountResponse;
import org.interledger.spsp.server.grpc.GetAccountResponse;
import org.interledger.spsp.server.grpc.GetBalanceResponse;
import org.interledger.spsp.server.grpc.SendPaymentResponse;

import io.xpring.xrpl.XpringKitException;

import java.math.BigInteger;

/**
 * An common interface shared between XpringClient and the internal hierarchy of decorators.
 */
public interface IlpClientDecorator {

    /**
     * Create an account on the connector.  Because no settings or auth were passed in, an account
     * with default settings, a generated account ID, and a generated SIMPLE auth token will be generated for you
     *
     * @return A {@link CreateAccountResponse} containing the account settings that were created on the connector
     * @throws XpringKitException If account creation failed.
     */
    CreateAccountResponse createAccount() throws XpringKitException;

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
                                        String description) throws XpringKitException;

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
                                        String description) throws XpringKitException;

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
                                        Integer assetScale) throws XpringKitException;

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
                                        Integer assetScale) throws XpringKitException;

    /**
     * Create an account on the connector.
     *
     * @param bearerToken : Optional auth token.  If empty, a simple auth token will be generated for you
     *
     * @param accountId : Optional account Id.  If empty, an account ID will be generated for you
     * @param assetCode : Optional asset code.  If empty, defaults to "XRP"
     * @param assetScale : Optional asset scale.  If empty, defaults to 9
     * @param description : Optional description.
     * @return A {@link CreateAccountResponse} containing the account settings that were created on the connector
     * @throws XpringKitException If the given inputs were invalid or account creation failed.
     */
    CreateAccountResponse createAccount(String bearerToken,
                                        String accountId,
                                        String assetCode,
                                        Integer assetScale,
                                        String description) throws XpringKitException;

    GetAccountResponse getAccount(String accountId, String bearerToken) throws XpringKitException;

    /**
     * Get the balance of the specified account on the connector.
     *
     * @param accountId The account ID to get the balance for.
     * @param bearerToken Authentication bearer token.
     * @return A {@link BigInteger} with the number of drops in this account.
     * @throws XpringKitException If the given inputs were invalid.
     */
    GetBalanceResponse getBalance(final String accountId, final String bearerToken) throws XpringKitException;

    SendPaymentResponse sendPayment(String destinationPaymentPointer,
                                    long amount,
                                    String accountId,
                                    String bearerToken) throws XpringKitException;
}
