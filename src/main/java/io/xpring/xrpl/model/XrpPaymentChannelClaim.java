package io.xpring.xrpl.model;

import org.immutables.value.Value;

import java.util.Optional;

/**
 * Represents a PaymentChannelClaim transaction on the XRP Ledger.
 * <p>
 * A PaymentChannelClaim transaction claims XRP from a payment channel, adjusts the payment channel's expiration,
 * or both.
 * </p>
 *
 * @see "https://xrpl.org/paymentchannelclaim.html"
 */
@Value.Immutable
public interface XrpPaymentChannelClaim {
  static ImmutableXrpPaymentChannelClaim.Builder builder() {
    return ImmutableXrpPaymentChannelClaim.builder();
  }

  Optional<XrpCurrencyAmount> amount();

  Optional<XrpCurrencyAmount> balance();

  /**
   * The unique ID of the channel, as a 64-character hexadecimal string.
   *
   * @return A {@link String} containing the unique ID of the channel, as a 64-character hexadecimal string.
   */
  String channel();

  Optional<String> publicKey();

  Optional<String> signature();
}
