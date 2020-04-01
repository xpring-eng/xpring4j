package io.xpring.xrpl;

/** Exceptions which occur when working with xpring4j. */
public class XRPException extends Exception {
    /** Static exception for when a classic address is passed to an X-Address API. */
    public static XRPException xAddressRequiredException = new XRPException("Please use the X-Address format. See: https://xrpaddress.info/.");

    /** Static exception for when functionality is unimplemented. */
    public static XRPException unimplemented = new XRPException("Unimplemented");

    /**
     * Create a new exception.
     *
     * @param message The message to to include in the exception
     */
    public XRPException(String message) {
        super(message);
    }
}
