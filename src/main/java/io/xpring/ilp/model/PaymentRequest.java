package io.xpring.ilp.model;

import com.google.common.primitives.UnsignedLong;
import org.immutables.value.Value;

/**
 * An immutable interface which can be used to send a payment request to a connector
 */
public interface PaymentRequest {

  static ImmutablePaymentRequest.Builder builder() {
    return ImmutablePaymentRequest.builder();
  }

  /**
   * The amount to send.  This amount is denominated in the asset code and asset scale of the sender's account
   * on the connector.  For example, if the account has an asset code of "USD" and an asset scale of 9,
   * a payment request of 100 units would send 100 nano-dollars.
   *
   * @return the amount to send to the recipient, denominated in the sender's account asset code and scale
   */
  UnsignedLong amount();

  /**
   * A payment pointer is a standardized identifier for payment accounts.
   * This payment pointer will be the identifier for the account of the recipient of this payment on the ILP
   * network.
   *
   * @see "https://github.com/interledger/rfcs/blob/master/0026-payment-pointers/0026-payment-pointers.md"
   *
   * @return A {@link String} conforming to the Payment Pointer RFC which identifies an account on the ILP network
   */
  String destinationPaymentPointer();

  /**
   * @return The accountID of the sender.
   */
  String senderAccountId();

  /**
   * Constructs a {@link PaymentRequest} (non-proto) from a {@link org.interledger.spsp.server.grpc.SendPaymentRequest}
   *
   * @return A {@link org.interledger.spsp.server.grpc.SendPaymentRequest} populated with the analogous fields in
   *          a {@link PaymentRequest}
   */
  org.interledger.spsp.server.grpc.SendPaymentRequest toProto();

  @Value.Immutable
  abstract class AbstractPaymentRequest implements PaymentRequest {

    @Override
    public org.interledger.spsp.server.grpc.SendPaymentRequest toProto() {
      return org.interledger.spsp.server.grpc.SendPaymentRequest.newBuilder()
        .setAmount(this.amount().longValue())
        .setDestinationPaymentPointer(this.destinationPaymentPointer())
        .setAccountId(this.senderAccountId())
        .build();
    }
  }
}
