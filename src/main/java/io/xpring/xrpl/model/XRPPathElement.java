package io.xpring.xrpl.model;

import org.immutables.value.Value;
import org.xrpl.rpc.v1.Payment.PathElement;

/**
 * A path step in an XRP Ledger Path.
 *
 * @see "https://xrpl.org/paths.html#path-steps"
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
@Value.Immutable
public interface XRPPathElement {
  static ImmutableXRPPathElement.Builder builder() {
    return ImmutableXRPPathElement.builder();
  }

  /**
   * If present, this path step represents rippling through the specified address.
   * <p>
   * This field is optional. MUST NOT be provided if this path element specifies the currency or issuer fields.
   * </p>
   */
  String account();

  /**
   * If present, this path element represents changing currencies through an order book.
   * <p>
   * This field is optional. The currency specified indicates the new currency. MUST NOT be provided if this path
   * element specifies the account field.
   * </p>
   */
  XRPCurrency currency();

  /**
   * (Optional) If present, this path element represents changing currencies and this address
   * defines the issuer of the new currency. If omitted in a path element with a non-XRP currency,
   * a previous element of the path defines the issuer. If present when currency is omitted,
   * indicates a path element that uses an order book between same-named currencies with different issuers.
   * MUST be omitted if the currency is XRP. MUST NOT be provided if this element specifies the account field.
   */
  String issuer();

  /**
   * Constructs an {@link XRPPathElement} from a {@link org.xrpl.rpc.v1.Payment.PathElement}.
   *
   * @param pathElement a {@link org.xrpl.rpc.v1.Payment.PathElement} (protobuf object) whose field values will be used
   *                    to construct an {@link XRPPathElement}
   * @return an {@link XRPPathElement} with its fields set via the analogous protobuf fields.
   * @see <a href="https://github.com/ripple/rippled/blob/develop/src/ripple/proto/org/xrpl/rpc/v1/transaction.proto#L227">
   * PathElement protocol buffer</a>
   */
  static XRPPathElement from(PathElement pathElement) {
    return builder()
        .account(pathElement.getAccount().getAddress())
        .currency(XRPCurrency.from(pathElement.getCurrency()))
        .issuer(pathElement.getIssuer().getAddress())
        .build();
  }
}