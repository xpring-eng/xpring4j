package io.xpring.xrpl.model;

import org.immutables.value.Value;
import org.xrpl.rpc.v1.PaymentChannelClaim;

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

  /**
   * (Optional) The amount of XRP, in drops, authorized by the Signature.
   * <p>
   * This must match the amount in the signed message.
   * </p>
   */
  Optional<XrpCurrencyAmount> amount();

  /**
   * (Optional) Total amount of XRP, in drops, delivered by this channel after processing this claim.
   * <p>
   * Required to deliver XRP. Must be more than the total amount delivered by the channel so far,
   * but not greater than the Amount of the signed claim. Must be provided except when closing the channel.
   * </p>
   *
   * @return An {@link XrpCurrencyAmount} containing the total amount of XRP, in drops, delivered by this channel
   *         after processing this claim.
   */
  Optional<XrpCurrencyAmount> balance();

  /**
   * The unique ID of the channel, as a 64-character hexadecimal string.
   *
   * @return A {@link String} containing the unique ID of the channel, as a 64-character hexadecimal string.
   */
  String channel();

  /**
   * (Optional) The public key used for the signature, as hexadecimal.
   * <p>
   * This must match the PublicKey stored in the ledger for the channel. Required unless the sender of the transaction
   * is the source address of the channel and the Signature field is omitted.
   * (The transaction includes the PubKey so that rippled can check the validity of the signature before trying to
   * apply the transaction to the ledger.)
   * </p>
   *
   * @return A {@link String} containing the public key used for the signature, as hexadecimal.
   */
  Optional<String> publicKey();

  /**
   * (Optional) The signature of this claim, as hexadecimal.
   * <p>
   * The signed message contains the channel ID and the amount of the claim.
   * Required unless the sender of the transaction is the source address of the channel.
   * </p>
   *
   * @return A {@link String} containing the signature of this claim, as hexadecimal.
   */
  Optional<String> signature();

  /**
   * Constructs an {@link XrpPaymentChannelClaim} from a {@link PaymentChannelClaim} protocol buffer.
   *
   * @param paymentChannelClaim A {@link PaymentChannelClaim} (protobuf object) whose field values will be used to
   *                             construct an {@link XrpPaymentChannelClaim}.
   * @return An {@link XrpPaymentChannelClaim} with its fields set via the analogous protobuf fields.
   * @see "https://github.com/ripple/rippled/blob/3d86b49dae8173344b39deb75e53170a9b6c5284/src/ripple/proto/org/xrpl/rpc/v1/transaction.proto#L258"
   */
  static XrpPaymentChannelClaim from(PaymentChannelClaim paymentChannelClaim) {
    if (!paymentChannelClaim.hasChannel()) {
      return null;
    }

    final String channel = paymentChannelClaim.getChannel().toString();

    // If the amount field is set, it must be able to be transformed into an XrpCurrencyAmount.
    Optional<XrpCurrencyAmount> amount = Optional.empty();
    if (paymentChannelClaim.hasAmount()) {
      amount = Optional.ofNullable(XrpCurrencyAmount.from(paymentChannelClaim.getAmount().getValue()));
      if (!amount.isPresent()) {
        return null;
      }
    }

    // If the balance field is set, it must be able to be transformed into an XrpCurrencyAmount.
    Optional<XrpCurrencyAmount> balance = Optional.empty();
    if (paymentChannelClaim.hasBalance()) {
      balance = Optional.ofNullable(XrpCurrencyAmount.from(paymentChannelClaim.getBalance().getValue()));
      if (!balance.isPresent()) {
        return null;
      }
    }

    final Optional<String> publicKey = paymentChannelClaim.hasPublicKey()
        ? Optional.of(paymentChannelClaim.getPublicKey().toString())
        : Optional.empty();

    final Optional<String> signature = paymentChannelClaim.hasPaymentChannelSignature()
        ? Optional.of(paymentChannelClaim.getPaymentChannelSignature().toString())
        : Optional.empty();

    return XrpPaymentChannelClaim.builder()
        .amount(amount)
        .balance(balance)
        .channel(channel)
        .publicKey(publicKey)
        .signature(signature)
        .build();
  }
}
