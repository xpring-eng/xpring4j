package io.xpring.ilp.model;

import com.google.common.primitives.UnsignedLong;
import org.immutables.value.Value;

/**
 * A response object containing details about a requested payment
 */
@Value.Immutable
public interface PaymentResult {

  static ImmutablePaymentResult.Builder builder() {
    return ImmutablePaymentResult.builder();
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

  /**
   * Constructs a {@link PaymentResult} from a protobuf {@link org.interledger.spsp.server.grpc.SendPaymentResponse}
   *
   * @param protoResponse a {@link org.interledger.spsp.server.grpc.SendPaymentResponse} to be converted
   * @return a {@link PaymentResult} with fields populated using the analogous fields in the proto object
   */
  static PaymentResult from(org.interledger.spsp.server.grpc.SendPaymentResponse protoResponse) {
    return PaymentResult.builder()
      .originalAmount(UnsignedLong.valueOf(protoResponse.getOriginalAmount()))
      .amountDelivered(UnsignedLong.valueOf(protoResponse.getAmountDelivered()))
      .amountSent(UnsignedLong.valueOf(protoResponse.getAmountSent()))
      .successfulPayment(protoResponse.getSuccessfulPayment())
      .build();
  }
}
