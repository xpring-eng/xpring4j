package io.xpring.xrpl.model;

import org.immutables.value.Value;
import org.xrpl.rpc.v1.Payment;

import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

/**
 * A payment on the XRP Ledger.
 *
 * @see "https://xrpl.org/payment.html"
 */
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

  /**
   * The unique address of the account receiving the payment.
   *
   * @return A {@link String} containing the unique address of the account receiving the payment.
   */
  String destination();

  /**
   * (Optional) Arbitrary tag that identifies the reason for the payment.
   *
   * @return An {@link Integer} containing the tag that identifies the reason for the payment.
   */
  @Nullable
  Integer destinationTag();

  /**
   * (Optional) Minimum amount of destination currency this transaction should deliver.
   *
   * @return An {@link XRPCurrencyAmount} representing the minimum amount of destination currency this
   *          transaction should deliver.
   */
  @Nullable
  XRPCurrencyAmount deliverMin();

  /**
   * (Optional) Arbitrary 256-bit hash representing a specific reason or identifier for this payment.
   *
   * @return A byte array containing a 256-bit hash representing a specific reason or identifier for this payment.
   */
  @Nullable
  byte[] invoiceID();

  /**
   * Array of payment paths to be used for this transaction.
   * <p>
   * Must be omitted for XRP-to-XRP transactions.
   * </p>
   *
   * @return A {@link List} of {@link XRPPath}s containing the paths to be used for this transaction.
   */
  @Nullable
  List<XRPPath> paths();

  /**
   * (Optional) Highest amount of source currency this transaction is allowed to cost.
   *
   * @return An {@link XRPCurrencyAmount} representing the highest amount of source currency this
   *          transaction is allowed to cost.
   */
  @Nullable
  XRPCurrencyAmount sendMax();

  /**
   * Constructs an {@link XRPPayment} from a {@link org.xrpl.rpc.v1.Payment}.
   *
   * @param payment a {@link org.xrpl.rpc.v1.Payment} (protobuf object) whose field values will be used
   *                to construct an {@link XRPPayment}
   * @return an {@link XRPPayment} with its fields set via the analogous protobuf fields.
   * @see <a href="https://github.com/ripple/rippled/blob/develop/src/ripple/proto/org/xrpl/rpc/v1/transaction.proto#L224">
   * Payment protocol buffer</a>
   */
  static XRPPayment from(Payment payment) {
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

    Integer destinationTag;
    if (payment.hasDestinationTag()) {
      destinationTag = payment.getDestinationTag().getValue();
    } else {
      destinationTag = null;
    }

    // If the deliverMin field is set, it must be able to be transformed into an XRPCurrencyAmount.
    XRPCurrencyAmount deliverMin;
    if (payment.hasDeliverMin()) {
      deliverMin = XRPCurrencyAmount.from(payment.getDeliverMin().getValue());
      if (deliverMin == null) {
        return null;
      }
    } else {
      deliverMin = null;
    }

    byte[] invoiceID;
    if (payment.hasInvoiceId()) {
      invoiceID = payment.getInvoiceId().getValue().toByteArray();
    } else {
      invoiceID = null;
    }

    List<XRPPath> paths = payment.getPathsList()
        .stream()
        .map(XRPPath::from)
        .collect(Collectors.toList());
    if (paths.isEmpty()) {
      paths = null;
    }

    // If the sendMax field is set, it must be able to be transformed into an XRPCurrencyAmount.
    XRPCurrencyAmount sendMax;
    if (payment.hasSendMax()) {
      sendMax = XRPCurrencyAmount.from(payment.getSendMax().getValue());
      if (sendMax == null) {
        return null;
      }
    } else {
      sendMax = null;
    }

    return builder()
        .amount(amount)
        .destination(destination)
        .destinationTag(destinationTag)
        .deliverMin(deliverMin)
        .invoiceID(invoiceID)
        .paths(paths)
        .sendMax(sendMax)
        .build();
  }
}
