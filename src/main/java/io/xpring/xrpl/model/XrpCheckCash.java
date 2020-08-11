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

  Optional<XrpCurrencyAmount> amount();

  Optional<XrpCurrencyAmount> deliverMin();
}
