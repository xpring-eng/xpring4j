package io.xpring.xrpl.model;

import org.immutables.value.Value;
import org.xrpl.rpc.v1.PaymentChannelFund;

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

  /**
   * Amount of XRP, in drops, to add to the channel.
   * <p>
   * To set the expiration for a channel without adding more XRP, set this to "0".
   * </p>
   *
   * @return An {@link XrpCurrencyAmount} containing the amount of XRP, in drops, to add to the channel.
   */
  XrpCurrencyAmount amount();

  /**
   * The unique ID of the channel to fund, as a 64-character hexadecimal string.
   *
   * @return A {@link String} containing the unique ID of the channel to fund, as a 64-character hexadecimal string.
   */
  String channel();

  /**
   * (Optional) New Expiration time to set for the channel, in seconds since the Ripple Epoch.
   * <p>
   * This must be later than either the current time plus the SettleDelay of the channel,
   * or the existing Expiration of the channel. After the Expiration time, any transaction
   * that would access the channel closes the channel without taking its normal action.
   * Any unspent XRP is returned to the source address when the channel closes.
   * (Expiration is separate from the channel's immutable CancelAfter time.)
   * For more information, see the PayChannel ledger object type: https://xrpl.org/paychannel.html
   * </p>
   *
   * @return An {@link Integer} containing the new expiration time to set for the channel, in seconds since the
   *         Ripple Epoch.
   */
  Optional<Integer> expiration();

  /**
   * Constructs an {@link XrpPaymentChannelFund} from a {@link PaymentChannelFund} protocol buffer.
   *
   * @param paymentChannelFund A {@link PaymentChannelFund} (protobuf object) whose field values will be used
   *                           to construct an {@link XrpPaymentChannelFund}.
   * @return An {@link XrpPaymentChannelFund} with its fields set via the analogous protobuf fields.
   * @see "https://github.com/ripple/rippled/blob/3d86b49dae8173344b39deb75e53170a9b6c5284/src/ripple/proto/org/xrpl/rpc/v1/transaction.proto#L288"
   */
  static XrpPaymentChannelFund from(PaymentChannelFund paymentChannelFund) {
    if (!paymentChannelFund.hasAmount() || !paymentChannelFund.hasChannel()) {
      return null;
    }

    final XrpCurrencyAmount amount = XrpCurrencyAmount.from(paymentChannelFund.getAmount().getValue());
    if (amount == null) {
      return null;
    }

    final String channel = paymentChannelFund.getChannel().toString();

    Optional<Integer> expiration = paymentChannelFund.hasExpiration()
        ? Optional.of(paymentChannelFund.getExpiration().getValue())
        : Optional.empty();

    return XrpPaymentChannelFund.builder()
        .amount(amount)
        .channel(channel)
        .expiration(expiration)
        .build();
  }
}
