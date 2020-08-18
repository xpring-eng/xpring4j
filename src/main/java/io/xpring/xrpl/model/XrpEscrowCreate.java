package io.xpring.xrpl.model;

import io.xpring.common.XrplNetwork;
import io.xpring.xrpl.ClassicAddress;
import io.xpring.xrpl.ImmutableClassicAddress;
import io.xpring.xrpl.Utils;
import org.immutables.value.Value;
import org.xrpl.rpc.v1.EscrowCreate;

import java.util.Optional;

/**
 * Represents an EscrowCreate transaction on the XRP Ledger.
 * <p>
 * An EscrowCreate transaction sequesters XRP until the escrow process either finishes or is canceled.
 * </p>
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
   * @return An {@link XrpCurrencyAmount} containing the amount of XRP, in drops, to deduct from the sender's balance
   *         and escrow.
   */
  XrpCurrencyAmount amount();

  /**
   * (Optional) The time, in seconds since the Ripple Epoch, when this escrow expires.
   * <p>
   * This value is immutable; the funds can only be returned the sender after this time.
   * </p>
   *
   * @return An {@link Integer} containing the time, in seconds since the Ripple Epoch, when this escrow expires.
   */
  Optional<Integer> cancelAfter();

  /**
   * (Optional) Hex value representing a PREIMAGE-SHA-256 crypto-condition.
   * <p>
   * The funds can only be delivered to the recipient if this condition is fulfilled.
   * </p>
   *
   * @return A {@link String} containing a hex value representing a PREIMAGE-SHA-256 crypto-condition.
   */
  Optional<String> condition();

  /**
   * Address and (optional) destination tag to receive escrowed XRP, encoded as an X-address.
   * <p>
   * (See https://xrpaddress.info/)
   * </p>
   *
   * @return A {@link String} containing the address and (optional) destination tag to receive escrowed XRP,
   *         encoded as an X-address.
   */
  String destinationXAddress();

  /**
   * (Optional) The time, in seconds since the Ripple Epoch, when the escrowed XRP can be released to the recipient.
   * <p>
   * This value is immutable; the funds cannot move until this time is reached.
   * </p>
   *
   * @return An {@link Integer} containing the time, in seconds since the Ripple Epoch, when the escrowed XRP can be
   *         released to the recipient.
   */
  Optional<Integer> finishAfter();

  /**
   * Constructs an XrpEscrowCreate from an EscrowCreate protocol buffer.
   *
   * @param escrowCreate An {@link EscrowCreate} (protobuf object) whose field values will be used to construct an
   *                     XrpEscrowCreate.
   * @param xrplNetwork The {@link XrplNetwork} that this transaction should occur on.
   * @return an XrpEscrowCreate with its fields set via the analogous protobuf fields.
   * @see "https://github.com/ripple/rippled/blob/3d86b49dae8173344b39deb75e53170a9b6c5284/src/ripple/proto/org/xrpl/rpc/v1/transaction.proto#L178"
   */
  static XrpEscrowCreate from(EscrowCreate escrowCreate, XrplNetwork xrplNetwork) {
    if (!escrowCreate.hasAmount()) {
      return null;
    }

    XrpCurrencyAmount amount = XrpCurrencyAmount.from(escrowCreate.getAmount().getValue());
    if (amount == null) {
      return null;
    }

    if (!escrowCreate.hasDestination() || escrowCreate.getDestination().getValue().getAddress().isEmpty()) {
      return null;
    }

    final String destination = escrowCreate.getDestination().getValue().getAddress();
    Optional<Integer> destinationTag = escrowCreate.hasDestinationTag()
        ? Optional.of(escrowCreate.getDestinationTag().getValue())
        : Optional.empty();

    ClassicAddress classicAddress = ImmutableClassicAddress.builder()
        .address(destination)
        .tag(destinationTag)
        .isTest(Utils.isTestNetwork(xrplNetwork))
        .build();

    final String destinationXAddress = Utils.encodeXAddress(classicAddress);
    if (destinationXAddress == null) {
      return null;
    }

    final Optional<Integer> cancelAfter =  escrowCreate.hasCancelAfter()
        ? Optional.of(escrowCreate.getCancelAfter().getValue())
        : Optional.empty();

    final Optional<String> condition = escrowCreate.hasCondition()
        ? Optional.of(escrowCreate.getCondition().getValue().toString())
        : Optional.empty();

    final Optional<Integer> finishAfter =  escrowCreate.hasFinishAfter()
        ? Optional.of(escrowCreate.getFinishAfter().getValue())
        : Optional.empty();

    return XrpEscrowCreate.builder()
        .amount(amount)
        .cancelAfter(cancelAfter)
        .condition(condition)
        .destinationXAddress(destinationXAddress)
        .finishAfter(finishAfter)
        .build();
  }
}
