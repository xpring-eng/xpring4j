package io.xpring.xrpl;

/**
 * Flags used in ripppled transactions.
 *
 * Note: These are only flags which are utilized in Xpring SDK. For a complete list of flags, see:
 * https://xrpl.org/transaction-common-fields.html#flags-field.
 */
public enum RippledFlags {
    TF_PARTIAL_PAYMENT(131072);

    /** The value of the flag. */
    public final Integer value;

    RippledFlags(Integer value) {
        this.value = value;
    }

    /**
     * Check if the given flag is present in the given flags.
     *
     * @param flag: The flag to check the presence of.
     * @param flags: The flags to check
     */
    public static boolean check(RippledFlags flag, Integer flags) {
        return (flag.value & flags) == flag.value;
    }
}
