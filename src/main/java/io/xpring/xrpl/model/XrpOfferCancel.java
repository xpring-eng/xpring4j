package io.xpring.xrpl.model;

import org.immutables.value.Value;

/**
 * Represents an OfferCancel transaction on the XRP Ledger.
 * <p>
 * An OfferCancel transaction removes an Offer object from the XRP Ledger.
 * </p>
 *
 * @see "https://xrpl.org/offercancel.html"
 */
@Value.Immutable
public interface XrpOfferCancel {
  static ImmutableXrpOfferCancel.Builder builder() {
    return ImmutableXrpOfferCancel.builder();
  }

  /**
   * The sequence number of a previous OfferCreate transaction.
   * <p>
   * If specified, cancel any offer object in the ledger that was created by that transaction.
   * It is not considered an error if the offer specified does not exist.
   * </p>
   *
   * @return A {@link Integer} containing the sequence number of a previous OfferCreate transaction.
   */
  public Integer offerSequence();
}
