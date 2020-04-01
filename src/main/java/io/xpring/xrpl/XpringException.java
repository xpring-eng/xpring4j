package io.xpring.xrpl;

/** Exceptions which occur when working with xpring4j. */
public class XpringException extends Exception {
    /** Static exception for when a classic address is passed to an X-Address API. */
    public static XpringException xAddressRequiredException = new XpringException("Please use the X-Address format. See: https://xrpaddress.info/.");

    /** Static exception for when a payment transaction can't be converted to an XRPTransaction. */
    public static XpringException paymentConversionFailure =
            new XpringException("Could not convert payment transaction: (transaction). Please file a bug at https://github.com/xpring-eng/xpring4j/issues");

    /** Static exception for when functionality is unimplemented. */
    public static XpringException unimplemented = new XpringException("Unimplemented");

    /**
     * Create a new exception.
     *
     * @param message The message to to include in the exception
     */
    public XpringException(String message) {
        super(message);
    }
}
