package io.xpring.xrpl.model;

import org.immutables.value.Value;
import org.xrpl.rpc.v1.CheckCash;

import java.util.Optional;

/**
 * Represents a CheckCash transaction on the XRP Ledger.
 * <p>
 * A CheckCash transaction attempts to redeem a Check object in the ledger to receive up to the amount
 * authorized by the corresponding CheckCreate transaction.
 * </p>
 *
 * @see "https://xrpl.org/checkcash.html"
 */
@Value.Immutable
public interface XrpCheckCash {
  static ImmutableXrpCheckCash.Builder builder() {
    return ImmutableXrpCheckCash.builder();
  }

  /**
   * The ID of the Check ledger object to cash, as a 64-character hexadecimal string.
   *
   * @return A {@link String} representing the ID of the Check ledger object to cash, as a 64-character hexadecimal
   *         string.
   */
  String checkId();

  /**
   * (Optional) Redeem the Check for exactly this amount, if possible.
   * <p>
   * The currency must match that of the SendMax of the corresponding CheckCreate transaction.
   * You must provide either this field or deliverMin.
   * </p>
   *
   * @return A {@link XrpCurrencyAmount} representing the amount to redeem the Check for, if possible.
   */
  Optional<XrpCurrencyAmount> amount();

  /**
   * (Optional) Redeem the Check for at least this amount and for as much as possible.
   * <p>
   * The currency must match that of the SendMax of the corresponding CheckCreate transaction.
   * You must provide either this field or amount.
   * </p>
   *
   * @return A {@link XrpCurrencyAmount} representing the minimum amount to redeem the Check for, and for as much as
   *         possible.
   */
  Optional<XrpCurrencyAmount> deliverMin();

  /**
   * Constructs an XrpCheckCash from a CheckCash protocol buffer.
   *
   * @param checkCash A {@link CheckCash} (protobuf object) whose field values will be used to construct an XrpCheckCash
   * @return an XrpCheckCash with its fields set via the analogous protobuf fields.
   * @see <a href="https://github.com/ripple/rippled/blob/3d86b49dae8173344b39deb75e53170a9b6c5284/src/ripple/proto/org/xrpl/rpc/v1/transaction.proto#L132">
   * CheckCash protocol buffer</a>
   */
  static XrpCheckCash from(CheckCash checkCash) {
    // If the amount field is set, it must be able to be transformed into an XrpCurrencyAmount.
    Optional<XrpCurrencyAmount> amount = Optional.empty();
    if (checkCash.hasAmount()) {
      amount = Optional.ofNullable(XrpCurrencyAmount.from(checkCash.getAmount().getValue()));
      if (!amount.isPresent()) {
        return null;
      }
    }

    if (!checkCash.hasCheckId() || checkCash.getCheckId().getValue().isEmpty()) {
      return null;
    }
    final String checkId = checkCash.getCheckId().getValue().toString();

    // If the deliverMin field is set, it must be able to be transformed into an XrpCurrencyAmount.
    Optional<XrpCurrencyAmount> deliverMin = Optional.empty();
    if (checkCash.hasDeliverMin()) {
      deliverMin = Optional.ofNullable(XrpCurrencyAmount.from(checkCash.getDeliverMin().getValue()));
      if (!deliverMin.isPresent()) {
        return null;
      }
    }

    return builder()
        .amount(amount)
        .checkId(checkId)
        .deliverMin(deliverMin)
        .build();
  }
}
