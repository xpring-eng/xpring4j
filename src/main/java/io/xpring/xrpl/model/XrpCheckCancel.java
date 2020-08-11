package io.xpring.xrpl.model;

import org.immutables.value.Value;
import org.xrpl.rpc.v1.CheckCancel;

/**
 * Represents a CheckCancel transaction on the XRP Ledger.
 * <p>
 * A CheckCancel transaction cancels an unredeemed Check, removing it from the ledger without sending any money.
 * The source or the destination of the check can cancel a Check at any time using this transaction type.
 * If the Check has expired, any address can cancel it.
 * </p>
 *
 * @see "https://xrpl.org/checkcancel.html"
 */
@Value.Immutable
public interface XrpCheckCancel {
  static ImmutableXrpCheckCancel.Builder builder() {
    return ImmutableXrpCheckCancel.builder();
  }

  /**
   * The ID of the Check ledger object to cancel, as a 64-character hexadecimal string.
   *
   * @return A {@link String} representing the ID of the Check ledger object to cancel, as a 64-character hexadecimal
   *         string.
   */
  String checkId();

  /**
   * Constructs an XrpCheckCancel from a CheckCancel protocol buffer.
   *
   * @param checkCancel A {@link CheckCancel} (protobuf object) whose field values will be used to construct an
   *                    XrpCheckCancel
   * @return An {@link XrpCheckCancel} with its fields set via the analogous protobuf fields.
   * @see <a href="https://github.com/ripple/rippled/blob/3d86b49dae8173344b39deb75e53170a9b6c5284/src/ripple/proto/org/xrpl/rpc/v1/transaction.proto#L126">
   * CheckCancel protocol buffer</a>
   */
  static XrpCheckCancel from(CheckCancel checkCancel) {
    if (!checkCancel.hasCheckId() || checkCancel.getCheckId().getValue().isEmpty()) {
      return null;
    }

    return builder()
        .checkId(checkCancel.getCheckId().getValue().toString())
        .build();
  }
}
