package io.xpring.xrpl;

/** Exceptions which occur when working with xpring4j. */
public class XpringException extends Exception {
    /** Static exception for when a classic address is passed to an X-Address API. */
    public static XpringException xAddressRequiredException = new XpringException("Please use the X-Address format. See: https://xrpaddress.info/.");

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
