package io.xpring.xrpl.model;

import org.immutables.value.Value;

import java.util.Optional;

/**
 * Represents a PaymentChannelFund transaction on the XRP Ledger.
 * <p>
 * A PaymentChannelFund transaction adds additional XRP to an open payment channel, updates the expiration
 * time of the channel, or both. Only the source address of the channel can use this transaction.
 * (Transactions from other addresses fail with the error tecNO_PERMISSION.)
 * </p>
 *
 * @see "https://xrpl.org/paymentchannelfund.html"
 */
@Value.Immutable
public interface XrpPaymentChannelFund {
  static ImmutableXrpPaymentChannelFund.Builder builder() {
    return ImmutableXrpPaymentChannelFund.builder();
  }

  XrpCurrencyAmount amount();

  String channel();

  Optional<Integer> expiration();
}
