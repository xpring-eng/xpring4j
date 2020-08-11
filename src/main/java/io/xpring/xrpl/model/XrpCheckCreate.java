package io.xpring.xrpl.model;

import org.immutables.value.Value;

import java.util.Optional;

/**
 * Represents a CheckCreate transaction on the XRP Ledger.
 * <p>
 * A CheckCreate transaction creates a Check object in the ledger, which is a deferred payment that can be cashed
 * by its intended destination.  The sender of this transaction is the sender of the Check.
 * </p>
 *
 * @see "https://xrpl.org/checkcreate.html"
 */
@Value.Immutable
public interface XrpCheckCreate {
  static ImmutableXrpCheckCreate.Builder builder() {
    return ImmutableXrpCheckCreate.builder();
  }

  /**
   * The unique address and (optional) destination tag of the account that can cash the Check,
   * encoded as an X-address.
   *
   * @return A {@link String} representing the unique address and (optional) destination tag of the account that
   *         can cash the Check, encoded as an X-address.
   *
   * @see "https://xrpaddress.info/)"
   */
  String destinationXAddress();

  Optional<Integer> expiration();

  Optional<String> invoiceID();

  Optional<XrpCurrencyAmount> sendMax();
}
