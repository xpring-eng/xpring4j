package io.xpring.xrpl.model;

import org.xrpl.rpc.v1.Payment;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A payment on the XRP Ledger.
 *
 * @see "https://xrpl.org/payment.html"
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public class XRPPayment {
  private final XRPCurrencyAmount amount;
  private final String destination;
  private final Integer destinationTag;
  private final XRPCurrencyAmount deliverMin;
  private final byte[] invoiceID;
  private final List<XRPPath> paths;
  private final XRPCurrencyAmount sendMax;

  /**
   * The amount of currency to deliver.
   *
   * @return An {@link XRPCurrencyAmount} representing the amount of currency to deliver.
   */
  public XRPCurrencyAmount amount() {
    return amount;
  }

  /**
   * The unique address of the account receiving the payment.
   *
   * @return A {@link String} containing the unique address of the account receiving the payment.
   */
  public String destination() {
    return destination;
  }

  /**
   * (Optional) Arbitrary tag that identifies the reason for the payment.
   *
   * @return An {@link Integer} containing the tag that identifies the reason for the payment.
   */
  public Optional<Integer> destinationTag() {
    return Optional.ofNullable(destinationTag);
  }

  /**
   * (Optional) Minimum amount of destination currency this transaction should deliver.
   *
   * @return An {@link XRPCurrencyAmount} representing the minimum amount of destination currency this
   *          transaction should deliver.
   */
  public Optional<XRPCurrencyAmount> deliverMin() {
    return Optional.ofNullable(deliverMin);
  }

  /**
   * (Optional) Arbitrary 256-bit hash representing a specific reason or identifier for this payment.
   *
   * @return A byte array containing a 256-bit hash representing a specific reason or identifier for this payment.
   */
  public Optional<byte[]> invoiceID() {
    return Optional.ofNullable(invoiceID);
  }

  /**
   * (Optional) Array of payment paths to be used for this transaction.
   * Must be omitted for XRP-to-XRP transactions.
   *
   * @return A {@link List} of {@link XRPPath}s containing the paths to be used for this transaction.
   */
  public Optional<List<XRPPath>> paths() {
    return Optional.ofNullable(paths);
  }

  /**
   * (Optional) Highest amount of source currency this transaction is allowed to cost.
   *
   * @return An {@link XRPCurrencyAmount} representing the highest amount of source currency this
   *          transaction is allowed to cost.
   */
  public Optional<XRPCurrencyAmount> sendMax() {
    return Optional.ofNullable(sendMax);
  }

  /**
   * Constructs an {@link XRPPayment} from a {@link org.xrpl.rpc.v1.Payment}.
   *
   * @param payment a {@link org.xrpl.rpc.v1.Payment} (protobuf object) whose field values will be used
   *                to construct an {@link XRPPayment}
   * @return an {@link XRPPayment} with its fields set via the analogous protobuf fields.
   * @see <a href="https://github.com/ripple/rippled/blob/develop/src/ripple/proto/org/xrpl/rpc/v1/transaction.proto#L224">
   * Payment protocol buffer</a>
   */
  public static XRPPayment from(Payment payment) {
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

    return new XRPPaymentBuilder()
        .amount(amount)
        .destination(destination)
        .destinationTag(destinationTag)
        .deliverMin(deliverMin)
        .invoiceID(invoiceID)
        .paths(paths)
        .sendMax(sendMax)
        .build();
  }

  private XRPPayment(XRPPayment.XRPPaymentBuilder builder) {
    this.amount = builder.amount;
    this.destination = builder.destination;
    this.destinationTag = builder.destinationTag;
    this.deliverMin = builder.deliverMin;
    this.invoiceID = builder.invoiceID;
    this.paths = builder.paths;
    this.sendMax = builder.sendMax;
  }

  //Builder class
  public static class XRPPaymentBuilder {
    private XRPCurrencyAmount amount;
    private String destination;
    private Integer destinationTag;
    private XRPCurrencyAmount deliverMin;
    private byte[] invoiceID;
    private List<XRPPath> paths;
    private XRPCurrencyAmount sendMax;

    public XRPPaymentBuilder() {}

    public XRPPayment.XRPPaymentBuilder amount(XRPCurrencyAmount amount) {
      this.amount = amount;
      return this;
    }

    public XRPPayment.XRPPaymentBuilder destination(String destination) {
      this.destination = destination;
      return this;
    }

    public XRPPayment.XRPPaymentBuilder destinationTag(Integer destinationTag) {
      this.destinationTag = destinationTag;
      return this;
    }

    public XRPPayment.XRPPaymentBuilder deliverMin(XRPCurrencyAmount deliverMin) {
      this.deliverMin = deliverMin;
      return this;
    }

    public XRPPayment.XRPPaymentBuilder invoiceID(byte[] invoiceID) {
      this.invoiceID = invoiceID;
      return this;
    }

    public XRPPayment.XRPPaymentBuilder paths(List<XRPPath> paths) {
      this.paths = paths;
      return this;
    }

    public XRPPayment.XRPPaymentBuilder sendMax(XRPCurrencyAmount sendMax) {
      this.sendMax = sendMax;
      return this;
    }

    public XRPPayment build() {
      return new XRPPayment(this);
    }
  }
}
