package io.xpring.xrpl.model;

import io.xpring.xrpl.ClassicAddress;
import io.xpring.xrpl.ImmutableClassicAddress;
import io.xpring.xrpl.Utils;
import io.xpring.xrpl.model.idiomatic.XrpPayment;
import org.immutables.value.Value;
import org.xrpl.rpc.v1.Payment;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A payment on the XRP Ledger.
 *
 * @deprecated Please use the idiomatically named {@link XrpPayment} instead.
 *
 * @see "https://xrpl.org/payment.html"
 */
@Deprecated
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
@Value.Immutable
public interface XRPPayment {
  static ImmutableXRPPayment.Builder builder() {
    return ImmutableXRPPayment.builder();
  }

  /**
   * The amount of currency to deliver.
   *
   * @return An {@link XRPCurrencyAmount} representing the amount of currency to deliver.
   */
  XRPCurrencyAmount amount();

  @Deprecated
  /**
   * @deprecated Please use destinationXAddress, which encodes both the destination and the destinationTag.
   *
   * The unique address of the account receiving the payment.
   *
   * @return A {@link String} containing the unique address of the account receiving the payment.
   */
  String destination();

  @Deprecated
  /**
   * @deprecated Please use destinationXAddress, which encodes both the destination and destinationTag.
   *
   * (Optional) Arbitrary tag that identifies a hosted recipient to pay, or the reason for the payment.
   *
   * @return An {@link Integer} containing the tag that identifies the reason for the payment.
   */
  Optional<Integer> destinationTag();

  /**
   * The address and (optional) destination tag of the account receiving the payment, encoded in X-address format.
   * @see "https://xrpaddress.info/"
   *
   * @return An {@link String} representing the X-address encoding of the address and destination tag receiving the
   *         payment.
   */
  String destinationXAddress();

  /**
   * (Optional) Minimum amount of destination currency this transaction should deliver.
   *
   * @return An {@link XRPCurrencyAmount} representing the minimum amount of destination currency this
   *          transaction should deliver.
   */
  Optional<XRPCurrencyAmount> deliverMin();

  /**
   * (Optional) Arbitrary 256-bit hash representing a specific reason or identifier for this payment.
   *
   * @return A byte array containing a 256-bit hash representing a specific reason or identifier for this payment.
   */
  @Value.Default
  default byte[] invoiceID() {
    return new byte[0];
  }

  /**
   * (Optional) Array of payment paths to be used for this transaction.
   * Must be omitted for XRP-to-XRP transactions.
   *
   * @return A {@link List} of {@link XRPPath}s containing the paths to be used for this transaction.
   */
  @Value.Default
  default List<XRPPath> paths() {
    return new ArrayList<>();
  }

  /**
   * (Optional) Highest amount of source currency this transaction is allowed to cost.
   *
   * @return An {@link XRPCurrencyAmount} representing the highest amount of source currency this
   *          transaction is allowed to cost.
   */
  Optional<XRPCurrencyAmount> sendMax();

  /**
   * Constructs an {@link XRPPayment} from a {@link org.xrpl.rpc.v1.Payment}.
   *
   * @param payment a {@link org.xrpl.rpc.v1.Payment} (protobuf object) whose field values will be used
   *                to construct an {@link XRPPayment}
   * @param xrplNetwork The XRPL network from which this object was retrieved.
   * @return an {@link XRPPayment} with its fields set via the analogous protobuf fields.
   * @see <a href="https://github.com/ripple/rippled/blob/develop/src/ripple/proto/org/xrpl/rpc/v1/transaction.proto#L224">
   * Payment protocol buffer</a>
   */
  static XRPPayment from(Payment payment, XRPLNetwork xrplNetwork) {
    // amount is required
    XRPCurrencyAmount amount = XRPCurrencyAmount.from(payment.getAmount().getValue());
    if (amount == null) {
      return null;
    }

    // destination is required
    if (!payment.hasDestination() || payment.getDestination().getValue().getAddress().isEmpty()) {
      return null;
    }
    final String destination = payment.getDestination().getValue().getAddress();

    Optional<Integer> destinationTag = Optional.empty();
    if (payment.hasDestinationTag()) {
      destinationTag = Optional.of(payment.getDestinationTag().getValue());
    }

    ClassicAddress classicAddress = ImmutableClassicAddress.builder()
            .address(destination)
            .tag(destinationTag)
            .isTest(xrplNetwork == XRPLNetwork.TEST)
            .build();

    final String destinationXAddress = Utils.encodeXAddress(classicAddress);

    // If the deliverMin field is set, it must be able to be transformed into an XRPCurrencyAmount.
    Optional<XRPCurrencyAmount> deliverMin = Optional.empty();
    if (payment.hasDeliverMin()) {
      deliverMin = Optional.ofNullable(XRPCurrencyAmount.from(payment.getDeliverMin().getValue()));
      if (!deliverMin.isPresent()) {
        return null;
      }
    }

    byte[] invoiceID = payment.getInvoiceId().getValue().toByteArray();

    List<XRPPath> paths = payment.getPathsList()
        .stream()
        .map(XRPPath::from)
        .collect(Collectors.toList());

    // If the sendMax field is set, it must be able to be transformed into an XRPCurrencyAmount.
    Optional<XRPCurrencyAmount> sendMax = Optional.empty();
    if (payment.hasSendMax()) {
      sendMax = Optional.ofNullable(XRPCurrencyAmount.from(payment.getSendMax().getValue()));
      if (!sendMax.isPresent()) {
        return null;
      }
    }

    return builder()
        .amount(amount)
        .destination(destination)
        .destinationTag(destinationTag)
        .destinationXAddress(destinationXAddress)
        .deliverMin(deliverMin)
        .invoiceID(invoiceID)
        .paths(paths)
        .sendMax(sendMax)
        .build();
  }
}
