package io.xpring.common;

import com.google.common.io.BaseEncoding;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtils {
  private static final MessageDigest digest;

  /**
   * A prefix applied when hashing a signed transaction blob.
   * {@see https://xrpl.org/basic-data-types.html#hashes).
   */
  private static final String signedTransactionPrefixHex = "54584E00";

  static {
    try {
      digest = MessageDigest.getInstance("SHA-256");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);  // Can't happen.
    }
  }

  /**
   * See {@link HashUtils#doubleDigest(byte[], int, int)}.
   */
  public static byte[] doubleDigest(byte[] input) {
    return doubleDigest(input, 0, input.length);
  }

  /**
   * Calculates the SHA-256 hash of the given byte range, and then hashes the resulting hash again. This is
   * standard procedure in Bitcoin. The resulting hash is in big endian form.
   */
  public static byte[] doubleDigest(byte[] input, int offset, int length) {
    synchronized (digest) {
      digest.reset();
      digest.update(input, offset, length);
      byte[] first = digest.digest();
      return digest.digest(first);
    }
  }

  public static byte[] halfSha512(byte[] bytes) {
    return new Sha512(bytes).finish256();
  }

  public static byte[] quarterSha512(byte[] bytes) {
    return new Sha512(bytes).finish128();
  }

  public static byte[] sha512(byte[] bytes) {
    return new Sha512(bytes).finish();
  }

  /**
   * Convert the given transaction blob to a transaction hash.
   *
   * @param transactionBlobHex - A hexadecimal encoded transaction blob.
   * @returns A hex encoded hash if the input was valid, otherwise undefined.
   */
  public static String transactionBlobToTransactionHash(String transactionBlobHex) {
    if (!BaseEncoding.base16().canDecode(transactionBlobHex)) {
      throw new IllegalArgumentException("not a valid hex value");
    }
    byte[] prefixedTransactionBlob = BaseEncoding.base16().decode(signedTransactionPrefixHex + transactionBlobHex);
    byte[] hash = halfSha512(prefixedTransactionBlob);
    return BaseEncoding.base16().encode(hash);
  }

}
