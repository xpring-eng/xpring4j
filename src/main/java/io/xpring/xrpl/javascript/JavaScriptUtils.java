package io.xpring.xrpl.javascript;

import com.eclipsesource.v8.V8Object;
import io.xpring.xrpl.ClassicAddress;
import io.xpring.xrpl.ImmutableClassicAddress;

import java.util.Objects;
import java.util.Optional;

/**
 * Provides JavaScript based Utils functionality.
 */
public class JavaScriptUtils {
  /**
   * An reference to the underlying JavaScript Utils object.
   */
  private V8Object javaScriptUtils;

  /**
   * Initialize a new JavaScriptUtils.
   *
   * @throws JavaScriptLoaderException If the underlying JavaScript was missing or malformed.
   */
  public JavaScriptUtils() throws JavaScriptLoaderException {
    V8Object context = JavaScriptLoader.getContext();
    V8Object javaScriptUtils = JavaScriptLoader.loadResource("XrpUtils", context);

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

    return Boolean.valueOf(javaScriptUtils.executeJSFunction("isValidAddress", address).toString());
  }

  /**
   * Encode the given {@link ClassicAddress} and tag into an X-Address.
   *
   * @param classicAddress A {@link ClassicAddress} to encode
   * @return A new X-Address if inputs were valid, otherwise null.
   * @see <a href="https://xrpaddress.info/">https://xrpaddress.info/</a>
   */
  @SuppressWarnings("checkstyle:LocalVariableName")
  public String encodeXAddress(ClassicAddress classicAddress) {
    Objects.requireNonNull(classicAddress);

    if (classicAddress.tag().isPresent()) {
      Object xAddress = javaScriptUtils.executeJSFunction("encodeXAddress",
          classicAddress.address(),
          classicAddress.tag().get(),
          classicAddress.isTest()
      );
      return xAddress.toString();
    } else {
      Object result =
          javaScriptUtils.executeJSFunction("encodeXAddress", classicAddress.address(), null, classicAddress.isTest());
      return result instanceof String ? result.toString() : null;
    }
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

    V8Object result = (V8Object) javaScriptUtils.executeJSFunction("decodeXAddress", xAddress);

    if (result.isUndefined()) {
      return null;
    }

    String address = result.getString("address");
    Integer tag = (result.get("tag") instanceof V8Object) ? null : result.getInteger("tag");
    boolean isTest = result.getBoolean("test");

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

    return Boolean.valueOf(javaScriptUtils.executeJSFunction("isValidXAddress", address).toString());
  }

  /**
   * Check if the given string is a valid classic address on the XRP Ledger.
   *
   * @param address A string to validate.
   * @return A boolean indicating whether this was a valid clssic address.
   */
  public boolean isValidClassicAddress(String address) {
    Objects.requireNonNull(address);

    return Boolean.valueOf(javaScriptUtils.executeJSFunction("isValidClassicAddress", address).toString());
  }

  /**
   * Convert the given transaction blob to a transaction hash.
   *
   * @param transactionBlobHex A hexadecimal encoded transaction blob.
   * @return A hex encoded hash if the input was valid, otherwise null.
   */
  public String toTransactionHash(String transactionBlobHex) {
    Objects.requireNonNull(transactionBlobHex);

    Object hash = javaScriptUtils.executeJSFunction("transactionBlobToTransactionHash", transactionBlobHex);
    return hash instanceof String ? hash.toString() : null;
  }
}
