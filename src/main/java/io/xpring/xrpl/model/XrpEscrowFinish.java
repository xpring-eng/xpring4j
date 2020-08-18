package io.xpring.xrpl.model;

import org.immutables.value.Value;

import java.util.Optional;

/**
 * Represents an EscrowFinish transaction on the XRP Ledger.
 * <p>
 * An EscrowFinish transaction delivers XRP from a held payment to the recipient.
 * </p>
 *
 * @see "https://xrpl.org/escrowfinish.html"
 */
@Value.Immutable
public interface XrpEscrowFinish {
  static ImmutableXrpEscrowFinish.Builder builder() {
    return ImmutableXrpEscrowFinish.builder();
  }

  /**
   * Address of the source account that funded the held payment, encoded as an X-address (see https://xrpaddress.info/).
   *
   * @return A {@link String} containing the address of the source account that funded the held payment, encoded as
   *         an X-address.
   */
  String ownerXAddress();

  /**
   * Transaction sequence of EscrowCreate transaction that created the held payment to finish.
   *
   * @return An {@link Integer} transaction sequence of EscrowCreate transaction that created the held payment to
   *         finish.
   */
  Integer offerSequence();

  Optional<String> condition();

  Optional<String> fulfillment();
}
