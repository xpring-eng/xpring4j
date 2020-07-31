package io.xpring.xrpl.model;

/**
 * Flags used in payment transactions.
 * <p>
 * Note: These are only flags which are utilized in Xpring SDK.
 * For the full list of payment flags, @see https://xrpl.org/payment.html#payment-flags
 * </p>
 */
public enum PaymentFlag {
  TF_PARTIAL_PAYMENT(131072);

  /**
   * The value of the flag.
   */
  public final int value;

  PaymentFlag(int value) {
    this.value = value;
  }

  /**
   * Check if the given flag is present in the given flags.
   *
   * @param flag  The flag to check the presence of.
   * @param flags The flags to check
   * @return A boolean indicating if the flag was present.
   */
  public static boolean check(PaymentFlag flag, int flags) {
    return (flag.value & flags) == flag.value;
  }
}
