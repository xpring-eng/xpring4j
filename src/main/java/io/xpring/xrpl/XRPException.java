package io.xpring.xrpl;

/**
 * Exceptions which occur when working with xpring4j.
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public class XRPException extends Exception {
  /**
   * Static exception for when a classic address is passed to an X-Address API.
   */
  public static XRPException xAddressRequiredException = new XRPException(
      XRPExceptionType.X_ADDRESS_REQUIRED,
      "Please use the X-Address format. See: https://xrpaddress.info/."
  );

  /**
   * Static exception for when a payment transaction can't be converted to an XRPTransaction.
   */
  public static XRPException paymentConversionFailure = new XRPException(
      XRPExceptionType.UNKNOWN,
      "Could not convert payment transaction: (transaction). Please file a bug at https://github.com/xpring-eng/xpring4j/issues"
  );

  /**
   * Static exception for when functionality is unimplemented.
   */
  public static XRPException unimplemented = new XRPException(XRPExceptionType.UNIMPLEMENTED, "Unimplemented");

  /**
   * The type of exception.
   */
  private XRPExceptionType type;

  /**
   * Create a new exception.
   *
   * @param type    The type of exception.
   * @param message The message to to include in the exception
   */
  public XRPException(XRPExceptionType type, String message) {
    super(message);

    this.type = type;
  }

  /**
   * The exception type.
   */
  public XRPExceptionType getType() {
    return this.type;
  }
}
