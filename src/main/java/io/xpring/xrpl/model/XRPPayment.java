package io.xpring.xrpl.model;

import org.xrpl.rpc.v1.Payment;
import org.immutables.value.Value;

import javax.annotation.Nullable;

@Value.Immutable
public interface XRPPayment {
    static ImmutableXRPPayment.Builder builder() {
        return ImmutableXRPPayment.builder();
    }

    // note: all optional in JS
    /**
     * @return
     */
    XRPCurrencyAmount amount();

    /**
     * @return
     */
    String destination();

    /**
     * @return
     */
    Number destinationTag();

    /**
     * @return
     */
    XRPCurrencyAmount deliverMin();

    /**
     * @return
     */
    Uint8Array invoiceID();

    /**
     * @return
     */
    Array<XRPPath> paths();

    /**
     * @return
     */
    XRPCurrencyAmount sendMax();

    static XRPPayment from(Payment currencyAmount) {
        return builder().build();
    }
}