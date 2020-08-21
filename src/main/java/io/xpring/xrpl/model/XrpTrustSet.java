package io.xpring.xrpl.model;

import org.immutables.value.Value;
import org.xrpl.rpc.v1.TrustSet;

import java.util.Optional;

/**
 * Represents a {@link TrustSet} transaction on the XRP Ledger.
 * <p>
 * A {@link TrustSet} transaction creates or modifies a trust line linking two accounts.
 * </p>
 *
 * @see "https://xrpl.org/trustset.html"
 */
public interface XrpTrustSet {
  /**
   * Object defining the trust line to create or modify, in the format of an XrpCurrencyAmount.
   * <p>
   * limitAmount.currency: The currency this trust line applies to, as a three-letter ISO 4217 Currency Code,
   *   or a 160-bit hex value according to currency format. "XRP" is invalid.
   * limitAmount.value: Quoted decimal representation of the limit to set on this trust line.
   * limitAmount.issuer: The address of the account to extend trust to.
   * </p>
   *
   * @return An {@link XrpCurrencyAmount} object defining the trust line to create or modify, in the format of
   *         an {@link XrpCurrencyAmount}.
   */
  XrpCurrencyAmount limitAmount();

  /**
   * (Optional) Value incoming balances on this trust line at the ratio of this number per 1,000,000,000 units.
   * <p>
   * A value of 0 is shorthand for treating balances at face value.
   * </p>
   *
   * @return An {@link Integer} containing the value of incoming balances on this trust line at the ratio of this
   *         number per 1,000,000,000 units.
   */
  Optional<Integer> qualityIn();

  /**
   * (Optional) Value outgoing balances on this trust line at the ratio of this number per 1,000,000,000 units.
   * <p>
   * A value of 0 is shorthand for treating balances at face value.
   * </p>
   *
   * @return An {@link Integer} containing the value of outgoing balances on this trust line at the ratio of this
   *         number per 1,000,000,000 units.
   */
  Optional<Integer> qualityOut();
}
