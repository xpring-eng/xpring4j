package io.xpring.xrpl.javascript;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import java.util.Objects;

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
    Value javaScriptUtils = JavaScriptLoader.loadResource("XrpUtils", context);

    this.javaScriptUtils = javaScriptUtils;
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
}
