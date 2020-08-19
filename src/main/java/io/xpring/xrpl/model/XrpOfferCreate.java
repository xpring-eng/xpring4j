package io.xpring.xrpl.model;

import org.immutables.value.Value;

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

  Optional<Integer> expiration();

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
}
