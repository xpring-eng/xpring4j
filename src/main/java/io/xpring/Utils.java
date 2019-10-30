package io.xpring;

import io.xpring.javascript.JavaScriptLoaderException;
import io.xpring.javascript.JavaScriptUtils;

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
     * @param address: A string to validate
     *
     * @return A boolean indicating whether this was a valid address.
     */
    public static boolean isValidAddress(String address) {
        try {
            return javaScriptUtils.isValidAddress(address);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Convert bytes to a hex string.
     *
     * @param bytes The bytes to convert.
     * @return Hex from bytes.
     */
    public static String byteArrayToHex(byte [] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for(byte b: bytes)
            sb.append(String.format("%02x", b));
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
                    + Character.digit(hex.charAt(i+1), 16));
        }
        return data;
    }

    /**
     * Check if the given string is a valid X-Address on the XRP Ledger.
     *
     * @param address: A string to validate
     * @return A boolean indicating whether this was a valid X-Address.
     */
    public static boolean isValidXAddress(String address) {
        try {
            return javaScriptUtils.isValidXAddress(address);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Check if the given string is a valid classic address on the XRP Ledger.
     *
     * @param address: A string to validate
     * @return A boolean indicating whether this was a valid clssic address.
     */
    public static boolean isValidClassicAddress(String address) {
        try {
            return javaScriptUtils.isValidClassicAddress(address);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}
