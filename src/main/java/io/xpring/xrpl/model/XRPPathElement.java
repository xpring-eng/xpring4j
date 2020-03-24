package io.xpring.xrpl.model;

import org.xrpl.rpc.v1.Payment.PathElement;
import org.immutables.value.Value;

import java.util.Optional;

/**
 * A path step in an XRP Ledger Path.
 * @see "https://xrpl.org/paths.html#path-steps"
 */
@Value.Immutable
public interface XRPPathElement {
    static ImmutableXRPPathElement.Builder builder() {
        return ImmutableXRPPathElement.builder();
    }

    String account();
    XRPCurrency currency();
    String issuer();

    static XRPPathElement from(PathElement pathElement) {
        if (pathElement == null) {
            return null;
        }
        if (XRPCurrency.from(pathElement.getCurrency()) == null) {
            return null;
        }
        return builder()
                .account(pathElement.getAccount().getAddress())
                .currency(XRPCurrency.from(pathElement.getCurrency()))
                .issuer(pathElement.getIssuer().getAddress())
                .build();
    }
}