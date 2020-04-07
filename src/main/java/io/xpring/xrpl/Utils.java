package io.xpring.xrpl;

import io.xpring.xrpl.javascript.JavaScriptLoaderException;
import io.xpring.xrpl.javascript.JavaScriptUtils;

/**
 * Provides utility functions for working in the XRP Ecosystem.
 */
public class Utils {

  private static final JavaScriptUtils javaScriptUtils;

  static {
    try {
      javaScriptUtils = new JavaScriptUtils();
    } catch (JavaScriptLoaderException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Please do not instantiate this static utility class.
   */
  private Utils() {
  }

  /**
   * Check if the given string is a valid address on the XRP Ledger.
   *
   * @param address A string to validate
   * @return A boolean indicating whether this was a valid address.
   */
  public static boolean isValidAddress(String address) {
    return javaScriptUtils.isValidAddress(address);
  }

  /**
   * Convert bytes to a hex string.
   *
   * @param bytes The bytes to convert.
   * @return Hex from bytes.
   */
  public static String byteArrayToHex(byte[] bytes) {
    StringBuilder sb = new StringBuilder(bytes.length * 2);
    for (byte b : bytes) {
      sb.append(String.format("%02x", b));
    }
    return sb.toString();
  }

  /**
   * Convert a hex string to bytes.
   *
   * @param hex The hex to convert to bytes.
   * @return Bytes from hex.
   */
  public static byte[] hexStringToByteArray(String hex) {
    int len = hex.length();
    byte[] data = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
      data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
          + Character.digit(hex.charAt(i + 1), 16));
    }
    return data;
  }

  /**
   * Encode the given {@link ClassicAddress} and tag into an X-Address.
   *
   * @param classicAddress A {@link ClassicAddress} to encode
   * @return A new X-Address if inputs were valid, otherwise undefined.
   * @see <a href="https://xrpaddress.info/">https://xrpaddress.info/</a>
   */
  public static String encodeXAddress(ClassicAddress classicAddress) {
    return javaScriptUtils.encodeXAddress(classicAddress);
  }

  /**
   * Decode a {@link ClassicAddress} from a given X-Address.
   *
   * @param xAddress The xAddress to decode.
   * @return A {@link ClassicAddress} if the inputs were valid, otherwise null.
   * @see <a href="https://xrpaddress.info/">https://xrpaddress.info/</a>
   */
  @SuppressWarnings("checkstyle:ParameterName")
  public static ClassicAddress decodeXAddress(String xAddress) {
    return javaScriptUtils.decodeXAddress(xAddress);
  }

  /**
   * Check if the given string is a valid X-Address on the XRP Ledger.
   *
   * @param address A string to validate
   * @return A boolean indicating whether this was a valid X-Address.
   */
  public static boolean isValidXAddress(String address) {
    return javaScriptUtils.isValidXAddress(address);
  }

  /**
   * Check if the given string is a valid classic address on the XRP Ledger.
   *
   * @param address A string to validate
   * @return A boolean indicating whether this was a valid clssic address.
   */
  public static boolean isValidClassicAddress(String address) {
    return javaScriptUtils.isValidClassicAddress(address);
  }

  /**
   * Convert the given transaction blob to a transaction hash.
   *
   * @param transactionBlobHex A hexadecimal encoded transaction blob.
   * @return A hex encoded hash if the input was valid, otherwise null.
   */
  public static String toTransactionHash(String transactionBlobHex) {
    return javaScriptUtils.toTransactionHash(transactionBlobHex);
  }
}
