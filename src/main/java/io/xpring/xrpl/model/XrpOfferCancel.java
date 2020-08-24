package io.xpring.xrpl.model;

import org.immutables.value.Value;
import org.xrpl.rpc.v1.OfferCancel;

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
   * @return An {@link Integer} containing the sequence number of a previous OfferCreate transaction.
   */
  Integer offerSequence();

  /**
   * Constructs an XrpOfferCancel from an OfferCancel protocol buffer.
   *
   * @param offerCancel An {@link OfferCancel} (protobuf object) whose field values will be used to construct
   *                    an XrpOfferCancel
   * @return An {@link XrpOfferCancel} with its fields set via the analogous protobuf fields.
   * @see "https://github.com/ripple/rippled/blob/3d86b49dae8173344b39deb75e53170a9b6c5284/src/ripple/proto/org/xrpl/rpc/v1/transaction.proto#L206"
   */
  static XrpOfferCancel from(OfferCancel offerCancel) {
    if (!offerCancel.hasOfferSequence()) {
      return null;
    }

    final Integer offerSequence = offerCancel.getOfferSequence().getValue();

    return XrpOfferCancel.builder()
      .offerSequence(offerSequence)
      .build();
  }
}
