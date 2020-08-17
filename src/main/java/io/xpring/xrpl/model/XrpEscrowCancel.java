package io.xpring.xrpl.model;

import io.xpring.common.XrplNetwork;
import io.xpring.xrpl.ClassicAddress;
import io.xpring.xrpl.ImmutableClassicAddress;
import io.xpring.xrpl.Utils;
import org.immutables.value.Value;
import org.xrpl.rpc.v1.EscrowCancel;

/**
 * Represents an EscrowCancel transaction on the XRP Ledger.
 * <p>
 * An EscrowCancel transaction returns escrowed XRP to the sender.
 * </p>
 *
 * @see "https://xrpl.org/escrowcancel.html"
 */
@Value.Immutable
public interface XrpEscrowCancel {
  static ImmutableXrpEscrowCancel.Builder builder() {
    return ImmutableXrpEscrowCancel.builder();
  }

  /**
   * Transaction sequence of EscrowCreate transaction that created the escrow to cancel.
   *
   * @return A {@link String} containing the transaction sequence of EscrowCreate transaction that created the escrow
   *         to cancel.
   */
  Integer offerSequence();

  /**
   * Address of the source account that funded the escrow payment, encoded as an X-address
   * (see https://xrpaddress.info/).
   *
   * @return A {@link String} containing the address of the source account that funded the escrow payment, encoded as
   *         an X-address
   */
  String ownerXAddress();

  /**
   * Constructs an XrpEscrowCancel from an EscrowCancel protocol buffer.
   *
   * @param escrowCancel An {@link EscrowCancel} (protobuf object) whose field values will be used to construct an
   *                     XrpEscrowCancel
   * @param xrplNetwork The network that this {@link EscrowCancel} should occur on.
   * @return An {@link XrpEscrowCancel} with its fields set via the analogous protobuf fields.
   * @see "https://github.com/ripple/rippled/blob/3d86b49dae8173344b39deb75e53170a9b6c5284/src/ripple/proto/org/xrpl/rpc/v1/transaction.proto#L170"
   */
  static XrpEscrowCancel from(EscrowCancel escrowCancel, XrplNetwork xrplNetwork) {
    if (!escrowCancel.hasOfferSequence() || !escrowCancel.hasOwner()) {
      return null;
    }

    final Integer offerSequence = escrowCancel.getOfferSequence().getValue();

    ClassicAddress ownerClassicAddress = ImmutableClassicAddress.builder()
        .address(escrowCancel.getOwner().getValue().getAddress())
        .isTest(xrplNetwork == XrplNetwork.TEST || xrplNetwork == XrplNetwork.DEV)
        .build();

    final String ownerXAddress = Utils.encodeXAddress(ownerClassicAddress);

    return XrpEscrowCancel.builder()
      .offerSequence(offerSequence)
      .ownerXAddress(ownerXAddress)
      .build();
  }
}
