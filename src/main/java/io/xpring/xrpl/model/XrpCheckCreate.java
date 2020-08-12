package io.xpring.xrpl.model;

import io.xpring.common.XrplNetwork;
import io.xpring.xrpl.ClassicAddress;
import io.xpring.xrpl.ImmutableClassicAddress;
import io.xpring.xrpl.Utils;
import org.immutables.value.Value;
import org.xrpl.rpc.v1.CheckCreate;

import java.util.Optional;

/**
 * Represents a CheckCreate transaction on the XRP Ledger.
 * <p>
 * A CheckCreate transaction creates a Check object in the ledger, which is a deferred payment that can be cashed
 * by its intended destination.  The sender of this transaction is the sender of the Check.
 * </p>
 *
 * @see "https://xrpl.org/checkcreate.html"
 */
@Value.Immutable
public interface XrpCheckCreate {
  static ImmutableXrpCheckCreate.Builder builder() {
    return ImmutableXrpCheckCreate.builder();
  }

  /**
   * The unique address and (optional) destination tag of the account that can cash the Check,
   * encoded as an X-address.
   *
   * @return A {@link String} representing the unique address and (optional) destination tag of the account that
   *         can cash the Check, encoded as an X-address.
   *
   * @see "https://xrpaddress.info/)"
   */
  String destinationXAddress();

  /**
   * (Optional) Time after which the Check is no longer valid, in seconds since the Ripple Epoch.
   *
   * @return An {@link Integer} representing the time after which the Check is no longer valid, in seconds
   *         since the Ripple Epoch.
   */
  Optional<Integer> expiration();

  /**
   * (Optional) Arbitrary 256-bit hash representing a specific reason or identifier for this Check.
   *
   * @return A {@link String} containing an arbitrary 256-bit hash representing a specific reason or identifier for
   *         this Check.
   */
  Optional<String> invoiceID();

  /**
   * Maximum amount of source currency the Check is allowed to debit the sender, including transfer fees on non-XRP
   * currencies.
   * <p>
   * The Check can only credit the destination with the same currency (from the same issuer, for non-XRP currencies).
   * For non-XRP amounts, the nested field names MUST be lower-case.
   * </p>
   * @return A {@link XrpCurrencyAmount} representing the maximum amount of source currency the Check is allowed
   *         to debit the sender, including transfer fees on non-XRP currencies.
   */
  Optional<XrpCurrencyAmount> sendMax();

  /**
   * Constructs an {@link XrpCheckCreate} from a CheckCreate protocol buffer.
   *
   * @param checkCreate A {@link CheckCreate} (protobuf object) whose field values will be used to construct an XrpCheckCreate
   * @return An {@link XrpCheckCreate} with its fields set via the analogous protobuf fields.
   * @see "https://github.com/ripple/rippled/blob/3d86b49dae8173344b39deb75e53170a9b6c5284/src/ripple/proto/org/xrpl/rpc/v1/transaction.proto#L145"
   */
  static XrpCheckCreate from(CheckCreate checkCreate, XrplNetwork xrplNetwork) {
    // Destination is required
    if (!checkCreate.hasDestination() || checkCreate.getDestination().getValue().getAddress().isEmpty()) {
      return null;
    }
    final String destination = checkCreate.getDestination().getValue().getAddress();

    Optional<Integer> destinationTag = checkCreate.hasDestinationTag()
        ? Optional.of(checkCreate.getDestinationTag().getValue())
        : Optional.empty();

    ClassicAddress classicAddress = ImmutableClassicAddress.builder()
        .address(destination)
        .tag(destinationTag)
        .isTest(xrplNetwork == XrplNetwork.TEST || xrplNetwork == XrplNetwork.DEV)
        .build();

    final String destinationXAddress = Utils.encodeXAddress(classicAddress);

    Optional<Integer> expiration = checkCreate.hasExpiration()
        ? Optional.of(checkCreate.getExpiration().getValue())
        : Optional.empty();

    Optional<String> invoiceID = checkCreate.hasInvoiceId()
        ? Optional.of(checkCreate.getInvoiceId().getValue().toString())
        : Optional.empty();

    // If the sendMax field is set, it must be able to be transformed into an XrpCurrencyAmount.
    Optional<XrpCurrencyAmount> sendMax = Optional.empty();
    if (checkCreate.hasSendMax()) {
      sendMax = Optional.ofNullable(XrpCurrencyAmount.from(checkCreate.getSendMax().getValue()));
      if (!sendMax.isPresent()) {
        return null;
      }
    }

    return builder()
        .destinationXAddress(destinationXAddress)
        .expiration(expiration)
        .invoiceID(invoiceID)
        .sendMax(sendMax)
        .build();
  }
}
