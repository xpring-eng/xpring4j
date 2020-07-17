package io.xpring.xrpl.model;

/**
 * Flags used in AccountSet transactions.
 *
 * @see <a href="https://xrpl.org/accountset.html#accountset-flags">AccountSet Flags</a>
 */
public enum AccountSetFlag {
  ASF_REQUIRE_DEST(1),
  ASF_REQUIRE_AUTH(2),
  ASF_DISALLOW_XRP(3),
  ASF_DISABLE_MASTER(4),
  ASF_ACCOUNT_TXN_ID(5),
  ASF_NO_FREEZE(6),
  ASF_GLOBAL_FREEZE(7),
  ASF_DEFAULT_RIPPLE(8),
  ASF_DEPOSIT_AUTH (9);

  /**
   * The value of the flag.
   */
  public final int value;

  AccountSetFlag(int value) {
    this.value = value;
  }
}
