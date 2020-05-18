package io.xpring.payid.idiomatic;

/**
 * Represents an exception thrown from PayID Components.
 */
public class PayIdException extends Exception {
  /**
   * Static exception for when a classic address is passed to an X-Address API.
   */
  public static PayIdException invalidPaymentPointerException =
      new PayIdException(PayIdExceptionType.INVALID_PAYMENT_POINTER, "Invalid Payment Pointer");

  /**
   * The type of exception.
   */
  private PayIdExceptionType type;

  /**
   * Create a new exception.
   *
   * @param type    The type of exception.
   * @param message The message to to include in the exception
   */
  public PayIdException(PayIdExceptionType type, String message) {
    super(message);

    this.type = type;
  }

  /**
   * The exception type of this {@link PayIdException}.
   *
   * @return A {@link PayIdExceptionType} representing the exception type.
   */
  public PayIdExceptionType getType() {
    return this.type;
  }
}