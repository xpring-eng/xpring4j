package io.xpring.xrpl;

import com.google.common.base.Preconditions;
import io.xpring.codec.addresses.ClassicAddressCodec;
import io.xpring.codec.addresses.XAddressCodec;
import io.xpring.common.CommonUtils;
import io.xpring.common.HashUtils;
import io.xpring.common.XrplNetwork;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides utility functions for working in the XRP Ecosystem.
 */
public class Utils {

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
    return isValidClassicAddress(address) || isValidXAddress(address);
  }

  /**
   * Convert bytes to a hex string.
   *
   * @deprecated Please use `CommonUtils.byteArrayToHex`
   * @param bytes The bytes to convert.
   * @return Hex from bytes.
   */
  @Deprecated
  public static String byteArrayToHex(byte[] bytes) {
    return CommonUtils.byteArrayToHex(bytes);
  }

  /**
   * Convert a hex string to bytes.
   *
   * @deprecated Please use `CommonUtils.hexStringToByteArray`
   *
   * @param hex The hex to convert to bytes.
   * @return Bytes from hex.
   */
  @Deprecated
  public static byte[] hexStringToByteArray(String hex) {
    return CommonUtils.hexStringToByteArray(hex);
  }

  /**
   * Encode the given {@link ClassicAddress} and tag into an X-Address.
   *
   * @param classicAddress A {@link ClassicAddress} to encode
   * @return A new X-Address if inputs were valid, otherwise undefined.
   * @see <a href="https://xrpaddress.info/">https://xrpaddress.info/</a>
   */
  public static String encodeXAddress(ClassicAddress classicAddress) {
    return XAddressCodec.encode(classicAddress);
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
    try {
      return XAddressCodec.decode(xAddress);
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Check if the given string is a valid X-Address on the XRP Ledger.
   *
   * @param address A string to validate
   * @return A boolean indicating whether this was a valid X-Address.
   */
  public static boolean isValidXAddress(String address) {
    try {
      return XAddressCodec.decode(address) != null;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Check if the given string is a valid classic address on the XRP Ledger.
   *
   * @param address A string to validate
   * @return A boolean indicating whether this was a valid clssic address.
   */
  public static boolean isValidClassicAddress(String address) {
    try {
      return ClassicAddressCodec.decodeAccountID(address) != null;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Convert the given transaction blob to a transaction hash.
   *
   * @param transactionBlobHex A hexadecimal encoded transaction blob.
   * @return A hex encoded hash if the input was valid, otherwise null.
   */
  public static String toTransactionHash(String transactionBlobHex) {
    try {
      return HashUtils.transactionBlobToTransactionHash(transactionBlobHex);
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  /**
   * Convert from units in drops to XRP.
   *
   * @param drops An amount of XRP expressed in units of drops.
   * @return A String representing the drops amount in units of XRP.
   * @throws XrpException if drops is in an invalid format.
   */
  public static String dropsToXrp(String drops) throws XrpException {
    Preconditions.checkNotNull(drops);

    String dropsRegex = "^-?[0-9]*['.']?[0-9]*$";
    Pattern dropsPattern = Pattern.compile(dropsRegex);
    Matcher dropsMatcher = dropsPattern.matcher(drops);

    if (!dropsMatcher.matches()) {
      throw new XrpException(XrpExceptionType.INVALID_INPUTS, String.format(
              "dropsToXrp: invalid value %s, should be a string-encoded number matching %s.", drops, dropsRegex));
    } else if (drops.equals(".")) {
      throw new XrpException(XrpExceptionType.INVALID_INPUTS, String.format(
              "dropsToXrp: invalid value %s, should be a string-encoded number.", drops));
    }

    // Converting with toBigIntegerExact() will throw an ArithmeticException if there is a fractional remainder.
    // Drops values can be expressed as a decimal (i.e. 2.00) but still must be whole numbers.
    // Important: specify base 10 to avoid exponential notation, e.g. '1e-7'
    try {
      drops = new BigDecimal(drops).toBigIntegerExact().toString(10);
    } catch (ArithmeticException exception) {     // drops are only whole units
      throw new XrpException(XrpExceptionType.INVALID_INPUTS,
              String.format("dropsToXrp: value %s must be a whole number.", drops)
      );
    }

    // This should never happen; the value has already been validated above.
    // This just ensures BigDecimal did not do something unexpected.
    if (!dropsMatcher.matches()) {
      throw new XrpException(XrpExceptionType.INVALID_INPUTS, String.format(
              "dropsToXrp: failed sanity check - value %s does not match %s.", drops, dropsRegex));
    }
    Integer dropsPerXrp = 1000000;
    return new BigDecimal(drops).divide(new BigDecimal(dropsPerXrp)).toPlainString();
  }

  /**
   * Convert from units in XRP to drops.
   *
   * @param xrp An amount of XRP expressed in units of XRP.
   * @return A String representing an amount of XRP expressed in units of drops.
   * @throws XrpException if xrp is in invalid format.
   */
  public static String xrpToDrops(String xrp) throws XrpException {
    Preconditions.checkNotNull(xrp);

    String xrpRegex = "^-?[0-9]*['.']?[0-9]*$";
    Pattern xrpPattern = Pattern.compile(xrpRegex);
    Matcher xrpMatcher = xrpPattern.matcher(xrp);

    if (!xrpMatcher.matches()) {
      throw new XrpException(XrpExceptionType.INVALID_INPUTS, String.format(
              "xrpToDrops: invalid value, %s should be a number matching %s.", xrp, xrpRegex));
    } else if (xrp.equals(".")) {
      throw new XrpException(XrpExceptionType.INVALID_INPUTS, String.format(
              "xrpToDrops: invalid value, %s should be a string-encoded number.", xrp));
    }

    // Remove any trailing zeroes and convert back to String.
    // Important: use toPlainString() to avoid exponential notation, e.g. '1e-7'.
    xrp = new BigDecimal(xrp).stripTrailingZeros().toPlainString();

    // This should never happen; the value has already been validated above.
    // This just ensures BigDecimal did not do something unexpected.
    if (!xrpMatcher.matches()) {
      throw new XrpException(XrpExceptionType.INVALID_INPUTS, String.format(
              "xrpToDrops: failed sanity check - value %s does not match %s.", xrp, xrpRegex));
    }

    String[] components = xrp.split("[.]");
    if (components.length > 2) {
      throw new XrpException(XrpExceptionType.INVALID_INPUTS, String.format(
              "xrpToDrops: failed sanity check - value %s has too many decimal points.", xrp));
    }
    String fraction = "0";
    if (components.length == 2) {
      fraction = components[1];
    }
    if (fraction.length() > 6) {
      throw new XrpException(XrpExceptionType.INVALID_INPUTS,
              String.format("xrpToDrops: value %s has too many decimal places.", xrp)
      );
    }
    Integer dropsPerXrp = 1000000;
    return new BigDecimal(xrp)
            .multiply(new BigDecimal(dropsPerXrp))
            .toBigInteger()
            .toString(10);
  }

  /**
   * Check if the XrplNetwork is a test network.
   *
   * @param xrplNetwork - The {@link XrplNetwork} to check.
   * @return A boolean representing whether or not the {@link XrplNetwork} is a test network.
   */
  public static boolean isTestNetwork(XrplNetwork xrplNetwork) {
    return xrplNetwork != XrplNetwork.MAIN;
  }
}
