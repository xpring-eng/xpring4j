package io.xpring.xrpl.model;

/**
 * There are several options which can be either enabled or disabled for an XRPL account.
 * These options can be changed with an AccountSet transaction. In the ledger,
 * flags are represented as binary values that can be combined with bitwise-or operations.
 * The bit values for the flags in the ledger are different than the values used to enable
 * or disable those flags in a transaction. Ledger flags have names that begin with lsf.
 *
 * @see <a href="https://xrpl.org/accountroot.html#accountroot-flags">AccountRoot Flags</a>
 */
public enum AccountRootFlag {
  LSF_DEPOSIT_AUTH(16777216);

  /**
   * The value of the flag.
   */
  public final int value;

  AccountRootFlag(int value) {
    this.value = value;
  }

  /**
   * Check if the given flag is present in the given flags.
   *
   * @param flag  The flag to check the presence of.
   * @param flags The flags to check
   * @return A boolean indicating if the flag was present.
   */
  public static boolean check(AccountRootFlag flag, int flags) {
    return (flag.value & flags) == flag.value;
  }
}
