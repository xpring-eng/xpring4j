package io.xpring.payid;

@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public class PayIDException extends Exception {
  /**
   * Static exception for when a classic address is passed to an X-Address API.
   */
  public static PayIDException invalidPayIDException =
      new PayIDException(PayIDExceptionType.INVALID_PAY_ID, "Invalid Payment Pointer");

  /**
   * The type of exception.
   */
  private PayIDExceptionType type;

  /**
   * Create a new exception.
   *
   * @param type    The type of exception.
   * @param message The message to to include in the exception
   */
  public PayIDException(PayIDExceptionType type, String message) {
    super(message);

    this.type = type;
  }

  /**
   * The exception type of this {@link PayIDException}.
   *
   * @return A {@link PayIDExceptionType} representing the exception type.
   */
  public PayIDExceptionType getType() {
    return this.type;
  }
}
