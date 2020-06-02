package io.xpring.xrpl.idiomatic;

/**
 * Exceptions which occur when working with xpring4j.
 */
public class XrpException extends Exception {
  /**
   * Static exception for when a classic address is passed to an X-Address API.
   */
  public static XrpException xAddressRequiredException = new XrpException(
      XrpExceptionType.X_ADDRESS_REQUIRED,
      "Please use the X-Address format. See: https://xrpaddress.info/."
  );

  /**
   * Static exception for when a payment transaction can't be converted to an XrpTransaction.
   */
  public static XrpException paymentConversionFailure = new XrpException(
      XrpExceptionType.UNKNOWN,
      "Could not convert payment transaction: (transaction). Please file a bug at https://github.com/xpring-eng/xpring4j/issues"
  );

  /**
   * Static exception for when functionality is unimplemented.
   */
  public static XrpException unimplemented = new XrpException(XrpExceptionType.UNIMPLEMENTED, "Unimplemented");

  /**
   * The type of exception.
   */
  private XrpExceptionType type;

  /**
   * Create a new exception.
   *
   * @param type    The type of exception.
   * @param message The message to to include in the exception
   */
  public XrpException(XrpExceptionType type, String message) {
    super(message);

    this.type = type;
  }

  /**
   * The exception type of this {@link XrpException}.
   *
   * @return A {@link XrpExceptionType} representing the exception type.
   */
  public XrpExceptionType getType() {
    return this.type;
  }
}
