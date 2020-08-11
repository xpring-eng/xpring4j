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

  /**
   * (Optional) Time after which the Check is no longer valid, in seconds since the Ripple Epoch.
   *
   * @return An {@link Integer} representing the time after which the Check is no longer valid, in seconds
   *         since the Ripple Epoch.
   */
  Optional<Integer> expiration();

  /**
   * (Optional) Arbitrary 256-bit hash representing a specific reason or identifier for this Check.
   *
   * @return A {@link String} containing an arbitrary 256-bit hash representing a specific reason or identifier for
   *         this Check.
   */
  Optional<String> invoiceID();

  /**
   * Maximum amount of source currency the Check is allowed to debit the sender, including transfer fees on non-XRP
   * currencies.
   * <p>
   * The Check can only credit the destination with the same currency (from the same issuer, for non-XRP currencies).
   * For non-XRP amounts, the nested field names MUST be lower-case.
   * </p>
   * @return A {@link XrpCurrencyAmount} representing the maximum amount of source currency the Check is allowed
   *         to debit the sender, including transfer fees on non-XRP currencies.
   */
  Optional<XrpCurrencyAmount> sendMax();
}
