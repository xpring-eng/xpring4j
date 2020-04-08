package io.xpring.ilp;

import io.xpring.ilp.model.AccountBalance;
import io.xpring.ilp.model.PaymentRequest;
import io.xpring.ilp.model.PaymentResult;

import java.util.Objects;

/**
 * A client that can get balances and send ILP payments on a connector.
 */
public class IlpClient {

  private IlpClientDecorator decoratedClient;

  /**
   * Initialize a new client with a configured URL.
   *
   * @param grpcUrl The gRPC URL exposed by Hermes.
   */
  public IlpClient(String grpcUrl) {
    Objects.requireNonNull(grpcUrl, "grpcUrl must not be null");
    this.decoratedClient = new DefaultIlpClient(grpcUrl);
  }

  /**
   * Get the balance of the specified account on the connector.
   *
   * @param accountId   The accountId to get the balance for.
   * @param accessToken Access token used for authentication.
   * @return An {@link AccountBalance} with account balances and denomination.
   * @throws IlpException If the given inputs were invalid, the account doesn't exist, or authentication failed.
   */
  public AccountBalance getBalance(final String accountId, final String accessToken) throws IlpException {
    return decoratedClient.getBalance(accountId, accessToken);
  }

  /**
   * Send a payment from the given accountId to the destinationPaymentPointer payment pointer.
   *
   * @param paymentRequest a {@link PaymentRequest} with parameters used to send a payment
   * @param accessToken    Access token of the sender
   * @return A {@link PaymentResult} with details about the payment. Note that this method will not
   *     necessarily throw an exception if the payment failed. Payment status can be checked in.
   * {@link PaymentResult#successfulPayment()}
   * @throws IlpException If the given inputs were invalid.
   */
  public PaymentResult sendPayment(final PaymentRequest paymentRequest,
                                   final String accessToken) throws IlpException {
    return decoratedClient.sendPayment(paymentRequest, accessToken);
  }
}
