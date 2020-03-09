package io.xpring.payid;

public class PayIDException extends Exception {
    /** Static exception for when a classic address is passed to an X-Address API. */
    public static PayIDException invalidPaymentPointerExpection = new PayIDException("Invalid Payment Pointer");


    /**
     * Create a new exception.
     *
     * @param message The message to to include in the exception
     */
    public PayIDException(String message) {
        super(message);
    }
}
