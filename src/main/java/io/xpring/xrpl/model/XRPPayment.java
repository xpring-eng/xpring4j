package io.xpring.xrpl.model;

import org.xrpl.rpc.v1.Payment;
import java.util.List;
import java.util.stream.Collectors;

import org.immutables.value.Value;
import javax.annotation.Nullable;

/**
 * A payment on the XRP Ledger.
 * @see "https://xrpl.org/payment.html"
 */
@Value.Immutable
public interface XRPPayment {
    static ImmutableXRPPayment.Builder builder() {
        return ImmutableXRPPayment.builder();
    }

    /**
     * @return The amount of currency to deliver.
     */
    XRPCurrencyAmount amount();

    /**
     * @return The unique address of the account receiving the payment.
     */
    String destination();

    /**
     * @return (Optional) Arbitrary tag that identifies the reason for the payment.
     */
    @Nullable Integer destinationTag();

    /**
     * @return (Optional) Minimum amount of destination currency this transaction should deliver.
     */
    @Nullable XRPCurrencyAmount deliverMin();

    /**
     * @return (Optional) Arbitrary 256-bit hash representing a specific reason or identifier for this payment.
     */
    @Nullable byte[] invoiceID();

    /**
     * @return (Optional) Array of payment paths to be used for this transaction.
     * Must be omitted for XRP-to-XRP transactions.
     */
    @Nullable List<XRPPath> paths();

    /**
     * @return (Optional) Highest amount of source currency this transaction is allowed to cost.
     */
    @Nullable XRPCurrencyAmount sendMax();

    static XRPPayment from(Payment payment) {
        // amount is required
        XRPCurrencyAmount amount = XRPCurrencyAmount.from(payment.getAmount().getValue());
        if (amount == null) { return null; }

        // destination is required
        if (!payment.hasDestination() || payment.getDestination().getValue().getAddress().isEmpty()) {
            return null;
        }
        String destination = payment.getDestination().getValue().getAddress();

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
            if (deliverMin == null) { return null; }
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
                                     .map(path -> XRPPath.from(path))
                                     .collect(Collectors.toList());
        if (paths.isEmpty()) {
            paths = null;
        }

        // If the sendMax field is set, it must be able to be transformed into an XRPCurrencyAmount.
        XRPCurrencyAmount sendMax;
        if (payment.hasSendMax()) {
            sendMax = XRPCurrencyAmount.from(payment.getSendMax().getValue());
            if (sendMax == null) { return null; }
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
