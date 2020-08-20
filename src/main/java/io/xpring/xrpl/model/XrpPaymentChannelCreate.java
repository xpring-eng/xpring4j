package io.xpring.xrpl.model;

import org.immutables.value.Value;

import java.util.Optional;

/**
 * Represents a PaymentChannelCreate transaction on the XRP Ledger.
 * <p>
 * A PaymentChannelCreate transaction creates a unidirectional channel and funds it with XRP.
 * The address sending this transaction becomes the "source address" of the payment channel.
 * </p>
 *
 * @see "https://xrpl.org/paymentchannelcreate.html"
 */
@Value.Immutable
public interface XrpPaymentChannelCreate {
  static ImmutableXrpPaymentChannelCreate.Builder builder() {
    return ImmutableXrpPaymentChannelCreate.builder();
  }

  XrpCurrencyAmount amount();

  Optional<Integer> cancelAfter();

  String destinationXAddress();

  String publicKey();

  Integer settleDelay();
}
