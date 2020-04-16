package io.xpring.xrpl.javascript;

import io.xpring.xrpl.ClassicAddress;
import io.xpring.xrpl.ImmutableClassicAddress;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import java.util.Objects;
import java.util.Optional;


/**
 * Provides JavaScript based Utils functionality.
 */
public class JavaScriptUtils {
  /**
   * An reference to the underlying JavaScript Utils object.
   */
  private Value javaScriptUtils;

  /**
   * Initialize a new JavaScriptUtils.
   *
   * @throws JavaScriptLoaderException If the underlying JavaScript was missing or malformed.
   */
  public JavaScriptUtils() throws JavaScriptLoaderException {
    Context context = JavaScriptLoader.getContext();
    Value javaScriptUtils = JavaScriptLoader.loadResource("Utils", context);

    this.javaScriptUtils = javaScriptUtils;
  }

  /**
   * Check if the given string is a valid address on the XRP Ledger.
   *
   * @param address A string to validate.
   * @return A boolean indicating whether this was a valid address.
   */
  public boolean isValidAddress(String address) {
    Objects.requireNonNull(address);

    Value isValidAddressFunction = javaScriptUtils.getMember("isValidAddress");
    return isValidAddressFunction.execute(address).asBoolean();
  }

  /**
   * Encode the given {@link ClassicAddress} and tag into an X-Address.
   *
   * @param classicAddress A {@link ClassicAddress} to encode
   * @return A new X-Address if inputs were valid, otherwise null.
   * @see <a href="https://xrpaddress.info/">https://xrpaddress.info/</a>
   */
  public String encodeXAddress(ClassicAddress classicAddress) {
    Objects.requireNonNull(classicAddress);

    Value encodeXAddressFunction = javaScriptUtils.getMember("encodeXAddress");
    Value result = classicAddress.tag().isPresent()
        ? encodeXAddressFunction.execute(classicAddress.address(), classicAddress.tag().get(), classicAddress.isTest())
        : encodeXAddressFunction.execute(classicAddress.address(), classicAddress.isTest());
    return result.asString();
  }

  /**
   * Decode a {@link ClassicAddress} from a given X-Address.
   *
   * @param xAddress The xAddress to decode.
   * @return A {@link ClassicAddress} if the inputs were valid, otherwise null.
   * @see <a href="https://xrpaddress.info/">https://xrpaddress.info/</a>
   */
  @SuppressWarnings("checkstyle:ParameterName")
  public ClassicAddress decodeXAddress(String xAddress) {
    Objects.requireNonNull(xAddress);

    Value decodeXAddressFunction = javaScriptUtils.getMember("decodeXAddress");
    Value result = decodeXAddressFunction.execute(xAddress);

    if (result.isNull()) {
      return null;
    }

    String address = result.getMember("address").asString();
    Integer tag = result.getMember("tag").isNull() ? null : result.getMember("tag").asInt();
    boolean isTest = result.getMember("test").asBoolean();

    return ImmutableClassicAddress.builder().address(address).tag(Optional.ofNullable(tag)).isTest(isTest).build();
  }

  /**
   * Check if the given string is a valid X-Address on the XRP Ledger.
   *
   * @param address A string to validate.
   * @return A boolean indicating whether this was a valid X-Address.
   */
  public boolean isValidXAddress(String address) {
    Objects.requireNonNull(address);

    Value isValidXAddressFunction = javaScriptUtils.getMember("isValidXAddress");
    return isValidXAddressFunction.execute(address).asBoolean();
  }

  /**
   * Check if the given string is a valid classic address on the XRP Ledger.
   *
   * @param address A string to validate.
   * @return A boolean indicating whether this was a valid clssic address.
   */
  public boolean isValidClassicAddress(String address) {
    Objects.requireNonNull(address);

    Value isValidClassicAddressFunction = javaScriptUtils.getMember("isValidClassicAddress");
    return isValidClassicAddressFunction.execute(address).asBoolean();
  }

  /**
   * Convert the given transaction blob to a transaction hash.
   *
   * @param transactionBlobHex A hexadecimal encoded transaction blob.
   * @return A hex encoded hash if the input was valid, otherwise null.
   */
  public String toTransactionHash(String transactionBlobHex) {
    Objects.requireNonNull(transactionBlobHex);

    Value transactionBlobToTransactionHashFunction = javaScriptUtils.getMember("transactionBlobToTransactionHash");
    Value hash = transactionBlobToTransactionHashFunction.execute(transactionBlobHex);
    return hash.isNull() ? null : hash.toString();
  }

  /**
   * Convert the given byte array to a hexadecimal string.
   *
   * @param bytes An array of bytes
   * @return An encoded hexadecimal string.
   */
  public String toHex(byte[] bytes) {
    final char[] hexArray = "0123456789ABCDEF".toCharArray();
    char[] hexChars = new char[bytes.length * 2];
    for (int j = 0; j < bytes.length; j++) {
      int next = bytes[j] & 0xFF;
      hexChars[j * 2] = hexArray[next >>> 4];
      hexChars[j * 2 + 1] = hexArray[next & 0x0F];
    }
    return new String(hexChars);
  }
}
