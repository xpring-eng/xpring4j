package io.xpring.xrpl.model;

import io.xpring.common.XrplNetwork;
import io.xpring.xrpl.ClassicAddress;
import io.xpring.xrpl.ImmutableClassicAddress;
import io.xpring.xrpl.Utils;
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

  /**
   * Constructs an XrpPaymentChannelCreate from a PaymentChannelCreate protocol buffer.
   *
   * @param paymentChannelCreate A {@link PaymentChannelCreate} (protobuf object) whose field values will be used
   *                             to construct an XrpPaymentChannelCreate
   * @return An {@link XrpPaymentChannelCreate} with its fields set via the analogous protobuf fields.
   * @see "https://github.com/ripple/rippled/blob/3d86b49dae8173344b39deb75e53170a9b6c5284/src/ripple/proto/org/xrpl/rpc/v1/transaction.proto#L272"
   */
  static XrpPaymentChannelCreate from(PaymentChannelCreate paymentChannelCreate, XrplNetwork xrplNetwork) {
    if (
        !paymentChannelCreate.hasAmount()
        || !paymentChannelCreate.hasDestination()
        || !paymentChannelCreate.hasPublicKey()
        || !paymentChannelCreate.hasSettleDelay()
    ) {
      return null;
    }

    final XrpCurrencyAmount amount = XrpCurrencyAmount.from(paymentChannelCreate.getAmount().getValue());
    if (amount == null) {
      return null;
    }

    final String destination = paymentChannelCreate.getDestination().getValue().getAddress();
    Optional<Integer> destinationTag = paymentChannelCreate.hasDestinationTag()
        ? Optional.of(paymentChannelCreate.getDestinationTag().getValue())
        : Optional.empty();

    ClassicAddress classicAddress = ImmutableClassicAddress.builder()
        .address(destination)
        .tag(destinationTag)
        .isTest(Utils.isTestNetwork(xrplNetwork))
        .build();

    final String destinationXAddress = Utils.encodeXAddress(classicAddress);

    Optional<Integer> cancelAfter = paymentChannelCreate.hasCancelAfter()
        ? Optional.of(paymentChannelCreate.getCancelAfter().getValue())
        : Optional.empty();

    final String publicKey = paymentChannelCreate.getPublicKey().toString();

    final Integer settleDelay = paymentChannelCreate.getSettleDelay().getValue();

    return XrpPaymentChannelCreate.builder()
      .amount(amount)
      .cancelAfter(cancelAfter)
      .destinationXAddress(destinationXAddress)
      .publicKey(publicKey)
      .settleDelay(settleDelay)
      .build();
  }
}
