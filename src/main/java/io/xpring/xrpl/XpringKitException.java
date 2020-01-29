package io.xpring.xrpl;

/** Exceptions which occur when working with xpring4j. */
// TODO(keefertaylor): Rename this class to be XpringException.
public class XpringKitException extends Exception {
    /** Static exception for when a classic address is passed to an X-Address API. */
    public static XpringKitException xAddressRequiredException = new XpringKitException("Please use the X-Address format. See: https://xrpaddress.info/.");

    /** Static exception for when functionality is unimplemented. */
    public static XpringKitException unimplemented = new XpringKitException("Unimplemented");

    /**
     * Create a new exception.
     *
     * @param message The message to to include in the exception
     */
    public XpringKitException(String message) {
        super(message);
    }
}
