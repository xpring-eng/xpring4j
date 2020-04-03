package io.xpring.common;

/**
 * Possible networks to resolve.
 */
public enum XRPLNetwork {
    DEV("devnet"),
    TEST("testnet"),
    MAIN("mainnet");

    private String networkName;

    XRPLNetwork(String networkName) {
        this.networkName = networkName;
    }

    public String getNetworkName() {
        return networkName;
    }
}
