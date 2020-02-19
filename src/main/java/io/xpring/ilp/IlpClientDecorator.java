package io.xpring.ilp;

import org.interledger.spsp.server.grpc.CreateAccountResponse;
import org.interledger.spsp.server.grpc.GetAccountResponse;
import org.interledger.spsp.server.grpc.GetBalanceResponse;
import org.interledger.spsp.server.grpc.SendPaymentResponse;

import io.xpring.xrpl.XpringException;

import java.math.BigInteger;
import java.util.Optional;

/**
 * An common interface shared between IlpClient and the internal hierarchy of decorators.
 */
public interface IlpClientDecorator {

    /**
     * Create an account on the connector.  Because no settings or auth were passed in, an account
     * with default settings, a generated account ID, and a generated SIMPLE auth token will be generated for you
     *
     * @return A {@link CreateAccountResponse} containing the account settings that were created on the connector
     * @throws XpringException If account creation failed.
     */
    CreateAccountResponse createAccount() throws XpringException;


    /**
     * Create an account on the connector.
     *
     *
     * @param createAccountRequest @return A {@link CreateAccountResponse} containing the account settings that were created on the connector
     * @param bearerToken
     * @throws XpringException If the given inputs were invalid or account creation failed.
     */
    CreateAccountResponse createAccount(CreateAccountRequest createAccountRequest, Optional<String> bearerToken) throws XpringException;

    /**
     * Gets account details for the given account ID
     *
     * @param accountId
     * @param bearerToken
     * @return
     * @throws XpringException
     */
    GetAccountResponse getAccount(String accountId, String bearerToken) throws XpringException;

    /**
     * Get the balance of the specified account on the connector.
     *
     * @param accountId The account ID to get the balance for.
     * @param bearerToken Authentication bearer token.
     * @return A {@link BigInteger} with the number of drops in this account.
     * @throws XpringException If the given inputs were invalid.
     */
    GetBalanceResponse getBalance(final String accountId, final String bearerToken) throws XpringException;

    /**
     * Send a payment from the given accountId to the destinationPaymentPointer payment pointer
     * @param destinationPaymentPointer : payment pointer of the receiver
     * @param amount : Amount to send
     * @param accountId : accountId of the sender
     * @param bearerToken : auth token of the sender
     * @return
     * @throws XpringException
     */
    SendPaymentResponse sendPayment(String destinationPaymentPointer,
                                    long amount,
                                    String accountId,
                                    String bearerToken) throws XpringException;
}
