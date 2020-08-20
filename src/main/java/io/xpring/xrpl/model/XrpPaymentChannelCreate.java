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

  /**
   *  Amount of XRP, in drops, to deduct from the sender's balance and set aside in this channel.
   *  <p>
   *  While the channel is open, the XRP can only go to the Destination address.
   *  When the channel closes, any unclaimed XRP is returned to the source address's balance.
   * </p>
   *
   * @return An {@link XrpCurrencyAmount} containing the amount of XRP, in drops, to deduct from the sender's balance
   *         and set aside in this channel.
   */
  XrpCurrencyAmount amount();

  /**
   * (Optional) The time, in seconds since the Ripple Epoch, when this channel expires.
   * <p>
   * Any transaction that would modify the channel after this time closes the channel without otherwise affecting it.
   * This value is immutable; the channel can be closed earlier than this time but cannot remain open after this time.)
   * </p>
   *
   * @return An Integer containing the time, in seconds since the Ripple Epoch, when this channel expires.
   */
  Optional<Integer> cancelAfter();

  String destinationXAddress();

  String publicKey();

  Integer settleDelay();
}
