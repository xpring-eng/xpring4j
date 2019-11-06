package io.xpring;

/** Exceptions which occur when working with XpringKit. */
public class XpringKitException extends Exception {
    /** Static exception for when a classic address is passed to an X-Address API. */
    public static XpringKitException xAddressRequiredException = new XpringKitException("Please use the X-Address format. See: https://xrpaddress.info/.");

    /**
     * Create a new exception.
     *
     * @param message The message to to include in the exception
     */
    public XpringKitException(String message) {
        super(message);
    }
}
