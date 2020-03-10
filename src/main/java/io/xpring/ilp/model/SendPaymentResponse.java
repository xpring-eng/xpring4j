package io.xpring.ilp.model;

import com.google.common.primitives.UnsignedLong;
import org.immutables.value.Value;

/**
 * A response object containing details about a requested payment
 */
@Value.Immutable
public interface SendPaymentResponse {

  static ImmutableSendPaymentResponse.Builder builder() {
    return ImmutableSendPaymentResponse.builder();
  }

  /**
   * The original amount that was requested to be sent.
   *
   * @return An {@link UnsignedLong} representing the original amount to be sent in a given payment.
   */
  UnsignedLong originalAmount();

  /**
   * The actual amount, in the receivers units, that was delivered to the receiver. Any currency conversion and/or
   * connector fees may cause this to be different than the amount sent.
   *
   * @return An {@link UnsignedLong} representing the amount delivered.
   */
  UnsignedLong amountDelivered();

  /**
   * The actual amount, in the senders units, that was sent to the receiver. In the case of a timeout or rejected
   * packets this amount may be less than the requested amount to be sent.
   *
   * @return An {@link UnsignedLong} representing the amount sent.
   */
  UnsignedLong amountSent();

  /**
   * Indicates if the payment was completed successfully.
   *
   * @return true if payment was successful
   */
  boolean successfulPayment();
}
