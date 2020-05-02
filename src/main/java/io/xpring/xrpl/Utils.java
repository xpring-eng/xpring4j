package io.xpring.xrpl;

import io.xpring.xrpl.javascript.JavaScriptLoaderException;
import io.xpring.xrpl.javascript.JavaScriptUtils;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

  /**
   * Convert from units in drops to xrp.
   *
   * @param drops An amount of xrp expressed in units of drops.
   * @return A String representing the drops amount in units of xrp.
   * @throws Exception if drops is in an invalid format.
   */
  public static String dropsToXrp(String drops) throws Exception {
    if (drops == null) {
      return null;
    }
    String dropsRegex = "^-?[0-9]*['.']?[0-9]*$";
    Pattern dropsPattern = Pattern.compile(dropsRegex);

    Matcher dropsMatcher = dropsPattern.matcher(drops);
    if (!dropsMatcher.matches()) {
      throw new Exception(String.format(
              "dropsToXrp: invalid value %s, should be a number matching %s.", drops, dropsRegex));
    } else if (drops.equals(".")) {
      throw new Exception(String.format(
              "dropsToXrp: invalid value %s, should be a BigNumber or string-encoded number.", drops));
    }

    // Converting to BigInteger and then back to string should remove any
    // decimal point followed by zeros, e.g. '1.00', which is the only valid decimal
    // representation of drops, which must be whole numbers.
    // Important: specify base 10 to avoid exponential notation, e.g. '1e-7'
    drops = new BigDecimal(drops).toBigIntegerExact().toString(10);

    // drops are only whole units
    if (drops.contains(".")) {
      throw new Exception(String.format("dropsToXrp: value %s has too many decimal places.", drops));
    }

    // This should never happen; the value has already been
    // validated above. This just ensures BigNumber did not do
    // something unexpected.
    if (!dropsMatcher.matches()) {
      throw new Exception(String.format(
              "dropsToXrp: failed sanity check - value %s does not match %s.", drops, dropsRegex));
    }

    return new BigDecimal(drops).divide(new BigDecimal(1000000.0)).toPlainString();
  }

  /**
   * Convert from units in xrp to drops.
   *
   * @param xrp An amount of xrp expressed in units of xrp.
   * @return A String representing an amount of xrp expressed in units of drops.
   * @throws Exception if xrp is in invalid format.
   */
  public static String xrpToDrops(String xrp) throws Exception {
    if (xrp == null) {
      return null;
    }
    String xrpRegex = "^-?[0-9]*['.']?[0-9]*$";
    Pattern xrpPattern = Pattern.compile(xrpRegex);

    Matcher xrpMatcher = xrpPattern.matcher(xrp);
    if (!xrpMatcher.matches()) {
      throw new Exception(String.format(
              "xrpToDrops: invalid value, %s should be a number matching %s.", xrp, xrpRegex));
    } else if (xrp.equals(".")) {
      throw new Exception(String.format(
              "xrpToDrops: invalid value, %s should be a BigDecimal or string-encoded number.", xrp));
    }

    // Converting to BigDecimal and then back to string should remove any
    // decimal point followed by zeros, e.g. '1.00'.
    // Important: use toPlainString() to avoid exponential notation, e.g. '1e-7'.
    xrp = new BigDecimal(xrp).stripTrailingZeros().toPlainString();

    // This should never happen; the value has already been
    // validated above. This just ensures BigDecimal did not do
    // something unexpected.
    if (!xrpMatcher.matches()) {
      throw new Exception(String.format(
              "xrpToDrops: failed sanity check - value %s does not match %s.", xrp, xrpRegex));
    }

    String[] components = xrp.split("[.]");
    if (components.length > 2) {
      throw new Exception(String.format(
              "xrpToDrops: failed sanity check - value %s has too many decimal points.", xrp));
    }
    String fraction = "0";
    if (components.length == 2) {
      fraction = components[1];
    }
    if (fraction.length() > 6) {
      throw new Exception(String.format("xrpToDrops: value %s has too many decimal places.", xrp));
    }
    return new BigDecimal(xrp)
            .multiply(new BigDecimal(1000000.0))
            .toBigInteger()
            .toString(10);
  }
}
