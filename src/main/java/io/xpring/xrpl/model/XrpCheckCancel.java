package io.xpring.xrpl.model;

import org.immutables.value.Value;

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
}
