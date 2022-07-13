package io.xpring.codec.addresses;

import static com.google.common.io.BaseEncoding.base16;

import com.google.common.base.Preconditions;
import com.ripple.encodings.base58.B58;
import com.ripple.encodings.basex.IBaseX;
import io.xpring.xrpl.ClassicAddress;

import java.util.Optional;

/**
 * Encodes and decodes X-Address as defined by the spec: https://github.com/xrp-community/standards-drafts/issues/6.
 */
public class XAddressCodec {

  private static final int XADDRESS_EXPECTED_LENGTH = 30;
  private static final int ACCOUNT_ID_EXPECTED_LENGTH = 20;
  private static final int ACCOUNT_ID_VERSION = 0;
  private static final String TAG_PRESENT_FLAG = "01";
  private static final long MAX_TAG = (long) Math.pow(2, 32) - 1;
  // XRPL uses Base58 but with an alphabet in a peculiar order (see https://xrpl.org/base58-encodings.html)
  private static B58 codec = new B58("rpshnaf39wBUDNEGHJKLM4PQRST7VWXYZ2bcdeCg65jkm8oFqi1tuvAxyz");
  private static String RESERVED_SUFFIX = "00000000";

  /**
   * Encodes a classic address to an x-address.
   *
   * @param classicAddress the address to encode.
   * @return encoded x-address.
   */
  public static String encode(ClassicAddress classicAddress) {
    // To compute the X-Address, several hex values are computed and concatenated together, then encoded
    // using Base58 with a XRPL version
    try {
      Prefix prefix = classicAddress.isTest() ? Prefix.TESTNET : Prefix.MAINNET;
      String addressBytes = base16().encode(ClassicAddressCodec.decodeAccountID(classicAddress.address()));
      String tagFlag = classicAddress.tag().isPresent() ? TAG_PRESENT_FLAG : "00";
      String tagBytes = encodeTag(classicAddress.tag());
      byte [] xaddress = base16().decode(prefix.prefix + addressBytes + tagFlag + tagBytes + RESERVED_SUFFIX);
      return encodeAddress(prefix.version, xaddress, XADDRESS_EXPECTED_LENGTH);
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Decodes an x-address to a classic address.
   *
   * @param xAddress the address to encode.
   * @return encoded x-address.
   */
  @SuppressWarnings("checkstyle:ParameterName")
  public static ClassicAddress decode(String xAddress) {
    Preconditions.checkArgument(xAddress.length() == 47, "X-address must be exactly 47 characters long");
    boolean isMainnet = xAddress.startsWith("X");
    boolean isTestnet = xAddress.startsWith("T");
    Preconditions.checkArgument(isMainnet || isTestnet, "X-address must start with X or T");

    Prefix prefix = isMainnet ? Prefix.MAINNET : Prefix.TESTNET;
    IBaseX.Decoded decoded =
        codec.decodeVersioned(xAddress, new IBaseX.Version(prefix.version, "accountId", XADDRESS_EXPECTED_LENGTH));

    // encoded address is a hex string with 60 characters, with parts at the following indexes:
    // NETWORK [0-1] | ADDRESS [2-41] | TAG_FLAG [42-43] | TAG [44-51] | PADDING [52-59]
    // since the second argument to String.substring is exclusive, it will be 1 above these values.
    String encodedAddress = base16().encode(decoded.payload);
    String classicAddress = encodedAddress.substring(2, 42);
    String tagFlag = encodedAddress.substring(42, 44);
    String encodedTag = encodedAddress.substring(44, 52);
    return ClassicAddress.builder()
        .address(encodeAddress(ACCOUNT_ID_VERSION, base16().decode(classicAddress), ACCOUNT_ID_EXPECTED_LENGTH))
        .isTest(isTestnet)
        .tag(decodeTag(tagFlag, encodedTag))
        .build();
  }

  private static String encodeAddress(int version, byte[] decode, int expectedLength) {
    return codec.encodeVersioned(decode, new IBaseX.Version(version, "accountId", expectedLength));
  }

  private static String encodeTag(Optional<Integer> tag) {
    return base16().encode(tagToLittleEndian(tag.orElse(0)));
  }

  private static Optional<Integer> decodeTag(String tagFlag, String encodedTag) {
    if (!tagFlag.equals(TAG_PRESENT_FLAG)) {
      return Optional.empty();
    }
    return Optional.of(littleEndianToTag(encodedTag));
  }

  private static byte[] tagToLittleEndian(long value) {
    Preconditions.checkArgument(value <= MAX_TAG, "tag cannot be greater than " + MAX_TAG);
    Preconditions.checkArgument(value >= 0, "tag cannot be negative");
    byte[] bytes = new byte[4];
    bytes[0] = (byte) (value & 0xFF);
    bytes[1] = (byte) ((value >> 8) & 0xFF);
    bytes[2] = (byte) ((value >> 16) & 0xFF);
    bytes[3] = (byte) ((value >> 24) & 0xFF);
    return bytes;
  }

  private static int littleEndianToTag(String encodedTag) {
    byte [] bytes = base16().decode(encodedTag);
    return ((bytes[3] & 0xFF) << 24) | ((bytes[2] & 0xFF) << 16)
        | ((bytes[1] & 0xFF) << 8) | (bytes[0] & 0xFF);
  }

  /**
   * The X-Address spec embeds a prefix in the encoded X-address. The prefix consists of an XRPL object version
   * and a network prefix.
   */
  private enum Prefix {
    MAINNET(5, "44"),
    TESTNET(4, "93");

    private final int version;
    private final String prefix;

    Prefix(int version, String prefix) {
      this.version = version;
      this.prefix = prefix;
    }
  }

}
