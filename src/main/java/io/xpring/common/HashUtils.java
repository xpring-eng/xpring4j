package io.xpring.common;

import static com.ripple.utils.HashUtils.halfSha512;

import com.google.common.io.BaseEncoding;

public class HashUtils {

  /**
   * A prefix applied when hashing a signed transaction blob.
   * @see <a href="https://xrpl.org/basic-data-types.html#hashes">hashes</a>
   */
  private static final String signedTransactionPrefixHex = "54584E00";

  /**
   * Convert the given transaction blob to a transaction hash.
   *
   * @param transactionBlobHex - A hexadecimal encoded transaction blob.
   * @return A hex encoded hash if the input was valid, otherwise undefined.
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
