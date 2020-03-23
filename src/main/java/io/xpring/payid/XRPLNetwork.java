package io.xpring.payid;

/**
 * Possible networks to resolve.
 */
public enum XRPLNetwork {
    Dev("devnet"),
    TEST("testnet"),
    MAIN("mainnet");

    private String contentType;

    XRPLNetwork(String contentType) {
        this.contentType = contentType;
    }

    public String getContentType() {
        return contentType;
    }
}