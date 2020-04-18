package io.xpring.xrpl.model;

import org.xrpl.rpc.v1.Payment.PathElement;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * A path step in an XRP Ledger Path.
 *
 * @see "https://xrpl.org/paths.html#path-steps"
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public class XRPPathElement {
  @Nullable
  private final String account;

  @Nullable
  private final XRPCurrency currency;

  @Nullable
  private final String issuer;

  /**
   * (Optional) If present, this path step represents rippling through the specified address.
   * MUST NOT be provided if this path element specifies the currency or issuer fields.
   *
   * @return A {@link String} representing the account of this {@link XRPPathElement}.
   */
  public Optional<String> account() {
    return Optional.ofNullable(account);
  }

  /**
   * (Optional) If present, this path element represents changing currencies through an order book.
   * The currency specified indicates the new currency.
   * MUST NOT be provided if this path element specifies the account field.
   *
   * @return The {@link XRPCurrency} of this {@link XRPPathElement}.
   */
  public Optional<XRPCurrency> currency() {
    return Optional.ofNullable(currency);
  }

  /**
   * (Optional) If present, this path element represents changing currencies and this address
   * defines the issuer of the new currency. If omitted in a path element with a non-XRP currency,
   * a previous element of the path defines the issuer. If present when currency is omitted,
   * indicates a path element that uses an order book between same-named currencies with different issuers.
   * MUST be omitted if the currency is XRP. MUST NOT be provided if this element specifies the account field.
   *
   * @return A {@link String} representing the issuer of a new currency.
   */
  public Optional<String> issuer() {
    return Optional.ofNullable(issuer);
  }

  /**
   * Constructs an {@link XRPPathElement} from a {@link org.xrpl.rpc.v1.Payment.PathElement}.
   *
   * @param pathElement a {@link org.xrpl.rpc.v1.Payment.PathElement} (protobuf object) whose field values will be used
   *                    to construct an {@link XRPPathElement}
   * @return an {@link XRPPathElement} with its fields set via the analogous protobuf fields.
   * @see <a href="https://github.com/ripple/rippled/blob/develop/src/ripple/proto/org/xrpl/rpc/v1/transaction.proto#L227">
   * PathElement protocol buffer</a>
   */
  public static XRPPathElement from(PathElement pathElement) {
    String account = null;
    if (pathElement.hasAccount()) {
      account = pathElement.getAccount().getAddress();
    }
    XRPCurrency currency = null;
    if (pathElement.hasCurrency()) {
      currency = XRPCurrency.from(pathElement.getCurrency());
    }
    String issuer = null;
    if (pathElement.hasIssuer()) {
      issuer = pathElement.getIssuer().getAddress();
    }
    return new XRPPathElementBuilder()
        .account(account)
        .currency(currency)
        .issuer(issuer)
        .build();
  }

  private XRPPathElement(XRPPathElementBuilder builder) {
    this.account = builder.account;
    this.currency = builder.currency;
    this.issuer = builder.issuer;
  }

  //Builder class
  public static class XRPPathElementBuilder {
    //optional fields
    private String account;
    private XRPCurrency currency;
    private String issuer;

    public XRPPathElementBuilder() {}

    public XRPPathElementBuilder account(String account) {
      this.account = account;
      return this;
    }

    public XRPPathElementBuilder currency(XRPCurrency currency) {
      this.currency = currency;
      return this;
    }

    public XRPPathElementBuilder issuer(String issuer) {
      this.issuer = issuer;
      return this;
    }

    public XRPPathElement build() {
      return new XRPPathElement(this);
    }
  }
}
