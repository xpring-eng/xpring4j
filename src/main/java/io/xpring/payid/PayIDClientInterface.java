package io.xpring.payid;

/**
 * An interface for a PayID client.
 */
public interface PayIDClientInterface {
    /**
     * Resolve the given PayID to an XRP Address.
     *
     * @param payID The payID to resolve for an address.
     * @return An XRP address representing the given PayID.
     */
    String xrpAddressForPayID(String payID) throws PayIDException;
}

