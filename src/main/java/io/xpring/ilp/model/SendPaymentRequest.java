package io.xpring.ilp.model;

import com.google.common.primitives.UnsignedLong;
import org.immutables.value.Value;

/**
 * An immutable interface which can be used to send a payment request to a connector
 */
@Value.Immutable
public interface SendPaymentRequest {

  static ImmutableSendPaymentRequest.Builder builder() {
    return ImmutableSendPaymentRequest.builder();
  }

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
   * The amount to send.  This amount is denominated in the asset code and asset scale of the sender's account
   * on the connector.  For example, if the account has an asset code of "USD" and an asset scale of 9,
   * a payment request of 100 units would send 100 nano-dollars.
   *
   * @return the amount to send to the recipient, denominated in the sender's account asset code and scale
   */
  UnsignedLong amount();

}
