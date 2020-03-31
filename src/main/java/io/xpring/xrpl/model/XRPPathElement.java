package io.xpring.xrpl.model;

import org.xrpl.rpc.v1.IssuedCurrencyAmount;
import org.xrpl.rpc.v1.Payment.PathElement;
import org.immutables.value.Value;

/**
 * A path step in an XRP Ledger Path.
 * @see "https://xrpl.org/paths.html#path-steps"
 */
@Value.Immutable
public interface XRPPathElement {
    static ImmutableXRPPathElement.Builder builder() {
        return ImmutableXRPPathElement.builder();
    }

    /**
     * @return (Optional) If present, this path step represents rippling through the specified address.
     * MUST NOT be provided if this path element specifies the currency or issuer fields.
     */
    String account();

    /**
     * @return (Optional) If present, this path element represents changing currencies through an order book.
     * The currency specified indicates the new currency.
     * MUST NOT be provided if this path element specifies the account field.
     */
    XRPCurrency currency();

    /**
     *
     * @return (Optional) If present, this path element represents changing currencies and this address
     * defines the issuer of the new currency. If omitted in a path element with a non-XRP currency,
     * a previous element of the path defines the issuer. If present when currency is omitted,
     * indicates a path element that uses an order book between same-named currencies with different issuers.
     * MUST be omitted if the currency is XRP. MUST NOT be provided if this element specifies the account field.
     */
    String issuer();

    static XRPPathElement from(PathElement pathElement) {
        return builder()
                .account(pathElement.getAccount().getAddress())
                .currency(XRPCurrency.from(pathElement.getCurrency()))
                .issuer(pathElement.getIssuer().getAddress())
                .build();
    }
}