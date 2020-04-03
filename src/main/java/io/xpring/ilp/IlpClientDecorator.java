package io.xpring.ilp;

import io.xpring.ilp.model.AccountBalance;
import io.xpring.ilp.model.PaymentRequest;
import io.xpring.ilp.model.PaymentResult;

/**
 * A common interface shared between IlpClient and the internal hierarchy of decorators.
 */
public interface IlpClientDecorator {

    /**
     * Get the balance of the specified account on the connector.
     *
     * @param accountId The accountId to get the balance for.
     * @param accessToken Access token used for authentication.
     * @return An {@link AccountBalance} with account balances and denomination.
     * @throws IlpException If the given inputs were invalid, the account doesn't exist, or authentication failed.
     */
    AccountBalance getBalance(final String accountId, final String accessToken) throws IlpException;

    /**
     * Send a payment from the given accountId to the destinationPaymentPointer payment pointer
     *
     * @param paymentRequest a {@link PaymentRequest} with parameters used to send a payment
     * @param accessToken Access token of the sender
     * @return A {@link PaymentResult} with details about the payment. Note that this method will not
     *          necessarily throw an exception if the payment failed. Payment status can be checked in
     *          {@link PaymentResult#successfulPayment()}
     * @throws IlpException If the given inputs were invalid.
     */
    PaymentResult sendPayment(final PaymentRequest paymentRequest,
                              final String accessToken) throws IlpException;
}
