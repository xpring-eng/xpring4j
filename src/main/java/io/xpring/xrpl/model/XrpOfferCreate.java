package io.xpring.xrpl.model;

import org.immutables.value.Value;
import org.xrpl.rpc.v1.OfferCreate;

import java.util.Optional;

/**
 * Represents an OfferCreate transaction on the XRP Ledger.
 * <p>
 * An OfferCreate transaction is effectively a limit order.
 * It defines an intent to exchange currencies, and creates
 * an Offer object if not completely fulfilled when placed.
 * Offers can be partially fulfilled.
 * </p>
 *
 * @see "https://xrpl.org/offercreate.html"
 */
@Value.Immutable
public interface XrpOfferCreate {
  static ImmutableXrpOfferCreate.Builder builder() {
    return ImmutableXrpOfferCreate.builder();
  }

  /**
   * (Optional) Time after which the offer is no longer active, in seconds since the Ripple Epoch.
   *
   * @return An {@link Integer} containing the time after which the offer is no longer active,
   *         in seconds since the Ripple Epoch.
   */
  Optional<Integer> expiration();

  /**
   * (Optional) An offer to delete first, specified in the same way as OfferCancel.
   *
   * @return An {@link Integer} containing an offer to delete first, specified in the same way as OfferCancel.
   */
  Optional<Integer> offerSequence();

  /**
   * The amount and type of currency being provided by the offer creator.
   *
   * @return An {@link XrpCurrencyAmount} containing the amount and type of currency being provided by the offer
   *         creator.
   */
  XrpCurrencyAmount takerGets();

  /**
   * The amount and type of currency being requested by the offer creator.
   *
   * @return An {@link XrpCurrencyAmount} containing the amount and type of currency being requested by the offer
   *         creator.
   */
  XrpCurrencyAmount takerPays();

  /**
   * Constructs an {@link XrpOfferCreate} from an {@link OfferCreate} protocol buffer.
   *
   * @param offerCreate An {@link OfferCreate} (protobuf object) whose field values will be used to construct
   *                    an {@link XrpOfferCreate}.
   * @return An {@link XrpOfferCreate} with its fields set via the analogous protobuf fields.
   * @see "https://github.com/ripple/rippled/blob/3d86b49dae8173344b39deb75e53170a9b6c5284/src/ripple/proto/org/xrpl/rpc/v1/transaction.proto#L212"
   */
  static XrpOfferCreate from(OfferCreate offerCreate) {
    return XrpOfferCreate.builder()
        .build();
  }
}
