package io.xpring.xrpl.model;

import org.immutables.value.Value;

import java.util.Optional;

/**
 * Represents an EscrowCreate transaction on the XRP Ledger.
 *
 * An EscrowCreate transaction sequesters XRP until the escrow process either finishes or is canceled.
 *
 * @see "https://xrpl.org/escrowcreate.html"
 */
@Value.Immutable
public interface XrpEscrowCreate {
  static ImmutableXrpEscrowCreate.Builder builder() {
    return ImmutableXrpEscrowCreate.builder();
  }

  /**
   * Amount of XRP, in drops, to deduct from the sender's balance and escrow.
   * <p>
   * Once escrowed, the XRP can either go to the Destination address (after the FinishAfter time)
   * or returned to the sender (after the CancelAfter time).
   * </p>
   * @return An {@link XrpCurrencyAmount} containing the amount of XRP, in drops, to deduct from the sender's balance and escrow.
   *         Once escrowed, the XRP can either go to the Destination address (after the FinishAfter time)
   *         or returned to the sender (after the CancelAfter time).
   */
  XrpCurrencyAmount amount();

  Optional<Integer> cancelAfter();

  Optional<String> condition();

  String destinationXAddress();

  Optional<Integer> finishAfter();
}
