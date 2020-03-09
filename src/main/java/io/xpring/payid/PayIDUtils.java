package io.xpring.payid;

import org.interledger.spsp.PaymentPointer;

/**
 * Provides utilities for PayID.
 */
public class PayIDUtils {

    /**
     * Parse a PayID to a payment pointer object
     *
     * @param payID The input payID..
     * @returns A PaymentPointer object if the input was valid, otherwise undefined.
     */
    public static PaymentPointer parsePayID(String payID) {
        try {
            return PaymentPointer.of(payID);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Please do not instantiate this static utility class.
     */
    private PayIDUtils() {}
}