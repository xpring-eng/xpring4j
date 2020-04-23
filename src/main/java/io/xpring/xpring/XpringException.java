package io.xpring.xpring;

/**
 * Represents exceptions thrown by Xpring components of the Xpring SDK.
 */
public class XpringException extends Exception {
  /**
   * Input entities given to a Xpring component were attached to different networks.
   * <p>
   * For instance, this error may be thrown if a XpringClient was constructed with
   * a PayIDClient attached to Testnet and a XRPClient attached to Mainnet.
   * </p>
   */
  public static XpringException MISMATCHED_NETWORKS =
      new XpringException(
          XpringExceptionType.MISMATCHED_NETWORKS,
          "Components are not connecting to the same network."
      );

  /**
   * The type of exception.
   */
  private XpringExceptionType type;

  /**
   * Create a new exception.
   *
   * @param type    The type of exception.
   * @param message The message to to include in the exception
   */
  public XpringException(XpringExceptionType type, String message) {
    super(message);

    this.type = type;
  }

  /**
   * The exception type.
   */
  public XpringExceptionType getType() {
    return this.type;
  }
}
