package io.xpring.common;

import io.xpring.xrpl.Utils;

public class CommonUtils {
  /**
   * Converts a string such that each of its characters are represented as hex.
   *
   * @param value - the string to convert to hex.
   * @return the hex encoded version of the string.
   */
  public static String stringToHex(String value) {
    StringBuffer hex = new StringBuffer();
    for (char ch : value.toCharArray()) {
      hex.append(Integer.toHexString(ch));
    }
    return hex.toString();
  }

  /**
   * Converts a string that is optionally in hex format into a byte[].
   *
   * @param value - the string to convert to a byte[].
   * @param isHex - flag to indicate if it's a hex string or not.
   * @return The byte[] value.
   */
  public static byte[] stringToByteArray(String value, Boolean isHex) {
    if (value.length() == 0) {
      return new byte[0];
    }

    return !isHex ? Utils.hexStringToByteArray(stringToHex(value))
            : Utils.hexStringToByteArray(value);
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
}