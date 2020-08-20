package io.xpring.xrpl.model;

import io.xpring.common.XrplNetwork;
import org.immutables.value.Value;
import org.xrpl.rpc.v1.PaymentChannelCreate;

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
   * @return An {@link Integer} containing the time, in seconds since the Ripple Epoch, when this channel expires.
   */
  Optional<Integer> cancelAfter();

  /**
   * Address and (optional) destination tag to receive XRP claims against this channel,
   * encoded as an X-address (see https://xrpaddress.info/).
   * <p>
   * This is also known as the "destination address" for the channel.
   * Cannot be the same as the sender (Account)
   * </p>
   *
   * @return A {@link String} containing the address and (optional) destination tag to receive XRP claims against
   *         this channel, encoded as an X-address.
   */
  String destinationXAddress();

  /**
   * The public key of the key pair the source will use to sign claims against this channel, in hexadecimal.
   * <p>
   * This can be any secp256k1 or Ed25519 public key.
   * </p>
   *
   * @return A {@link String} containing the public key of the key pair the source will use to sign claims against
   *         this channel, in hexadecimal.
   */
  String publicKey();

  /**
   * Amount of time the source address must wait before closing the channel if it has unclaimed XRP.
   *
   * @return An {@link Integer} containing the amount of time the source address must wait before closing the channel
   *         if it has unclaimed XRP.
   */
  Integer settleDelay();

  static XrpPaymentChannelCreate from(PaymentChannelCreate paymentChannelCreate, XrplNetwork xrplNetwork) {
    return XrpPaymentChannelCreate.builder()
      .build();
  }
}
