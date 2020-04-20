package io.xpring.payid;

@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public class PayIDDiscoveryException extends RuntimeException {

  /**
   * The type of exception.
   */
  private PayIDDiscoveryExceptionType type;

  /**
   * Create a new exception.
   *
   * @param type    The type of exception.
   * @param message The message to to include in the exception
   */
  public PayIDDiscoveryException(PayIDDiscoveryExceptionType type, String message) {
    super(message);
    this.type = type;
  }

  /**
   * The exception type of this {@link PayIDDiscoveryException}.
   *
   * @return A {@link PayIDDiscoveryExceptionType} representing the exception type.
   */
  public PayIDDiscoveryExceptionType getType() {
    return this.type;
  }

}
