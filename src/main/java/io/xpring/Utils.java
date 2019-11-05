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
     * @param address A string to validate
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
     * Encode the given classic address and tag into an x-address.
     *
     * @param classicAddress A classic address to encode.
     * @param tag            An optional tag to encode.
     * @return A new x-address if inputs were valid, otherwise undefined.
     * @see https://xrpaddress.info/
     */
    public static String encodeXAddress(String classicAddress, Long tag) {
        try {
            return javaScriptUtils.encodeXAddress(classicAddress, tag);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
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
            return javaScriptUtils.isValidXAddress(address);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    public static ClassicAddress decodeXAddress(String xAddress) {
        try {
            return javaScriptUtils.decodeXAddress(xAddress);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
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
            return javaScriptUtils.isValidClassicAddress(address);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }          
          
    /**
     * Convert the given transaction blob to a transaction hash.
     *
     * @param transactionBlobHex  A hexadecimal encoded transaction blob.
     * @return  A hex encoded hash if the input was valid, otherwise null.
     */
    public static String toTransactionHash(String transactionBlobHex) {
        try {
            return javaScriptUtils.toTransactionHash(transactionBlobHex);
>>>>>>> master
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}
