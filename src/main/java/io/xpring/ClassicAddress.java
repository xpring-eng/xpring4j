package io.xpring;

import io.xpring.javascript.JavaScriptWallet;
import io.xpring.javascript.JavaScriptWalletFactory;
import io.xpring.javascript.JavaScriptWalletGenerationResult;

/**
 * Represents classic address components on the XRP Ledger
 */
public class ClassicAddress {
    /** The address component of the classic address. */
    private String address;

    /** The tag component of the classic address. */
    private Long tag;

    /**
     * Initialize a new classic address.
     *
     * @param address The address component of a classic address.
     * @param tag The tag component of a classic address.
     */
    public ClassicAddress(String address, Long tag) {
        this.address = address;
        this.tag = tag;
    }

    /**
     * Returns the address of the classic address.
     *
     * @return The address component of the classic address.
     */
    public String getAddress() {
        return this.address;
    }

    /**
     * Returns the tag of the classic address.
     *
     * @return The tag component of the classic address.
     */
    public Long getTag() {
        return this.tag;
    }
}
