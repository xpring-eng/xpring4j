package io.xpring.xrpl.model;

import io.xpring.common.XrplNetwork;
import org.immutables.value.Value;
import org.xrpl.rpc.v1.EscrowFinish;

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
   * (Optional) Hex value matching the previously-supplied PREIMAGE-SHA-256 crypto-condition  of the held payment.
   *
   * @return A {@link String} containing the hex value matching the previously-supplied PREIMAGE-SHA-256
   *          crypto-condition  of the held payment.
   */
  Optional<String> condition();

  /**
   * (Optional) Hex value of the PREIMAGE-SHA-256 crypto-condition fulfillment  matching the held payment's Condition.
   *
   * @return A {@link String} containing the hex value of the PREIMAGE-SHA-256 crypto-condition fulfillment
   *         matching the held payment's Condition.
   */
  Optional<String> fulfillment();

  /**
   * Transaction sequence of EscrowCreate transaction that created the held payment to finish.
   *
   * @return An {@link Integer} transaction sequence of EscrowCreate transaction that created the held payment to
   *         finish.
   */
  Integer offerSequence();

  /**
   * Address of the source account that funded the held payment, encoded as an X-address (see https://xrpaddress.info/).
   *
   * @return A {@link String} containing the address of the source account that funded the held payment, encoded as
   *         an X-address.
   */
  String ownerXAddress();

  /**
   * Constructs an {@link XrpEscrowFinish} from an {@link EscrowFinish} protocol buffer.
   *
   * @param escrowFinish An {@link EscrowFinish} (protobuf object) whose field values will be used to construct an
   *                     {@link XrpEscrowFinish}.
   * @return An {@link XrpEscrowFinish} with its fields set via the analogous protobuf fields.
   * @see "https://github.com/ripple/rippled/blob/3d86b49dae8173344b39deb75e53170a9b6c5284/src/ripple/proto/org/xrpl/rpc/v1/transaction.proto#L194"
   */
  static XrpEscrowFinish from(EscrowFinish escrowFinish, XrplNetwork xrplNetwork) {
    if (!escrowFinish.hasOfferSequence() || !escrowFinish.hasOwner()) {
      return null;
    }

    final Integer offerSequence = escrowFinish.getOfferSequence().getValue();

    return XrpEscrowFinish.builder()
      .offerSequence(offerSequence)
      .build();
  }
}
