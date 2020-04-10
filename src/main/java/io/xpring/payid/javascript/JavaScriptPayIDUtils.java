package io.xpring.payid.javascript;

import io.xpring.payid.ImmutablePayIDComponents;
import io.xpring.payid.PayIDComponents;
import io.xpring.xrpl.javascript.JavaScriptLoader;
import io.xpring.xrpl.javascript.JavaScriptLoaderException;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import java.util.Objects;

/**
 * Provides JavaScript based Utils functionality.
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public class JavaScriptPayIDUtils {
  /**
   * An reference to the underlying JavaScript PayIDUtils object.
   */
  @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
  private Value javaScriptPayIDUtils;

  /**
   * Initialize a new JavaScriptPayIDUtils.
   *
   * @throws JavaScriptLoaderException If the underlying JavaScript was missing or malformed.
   */
  public JavaScriptPayIDUtils() throws JavaScriptLoaderException {
    Context context = JavaScriptLoader.getContext();
    Value javaScriptUtils = JavaScriptLoader.loadResource("PayIDUtils", context);

    this.javaScriptPayIDUtils = javaScriptUtils;
  }

  /**
   * Parse the given Pay ID to a set of components.
   *
   * @param payID A PayID to parse.
   * @return A set of components parsed from the PayID.
   */
  public PayIDComponents parsePayID(String payID) {
    Objects.requireNonNull(payID);

    Value isValidAddressFunction = javaScriptPayIDUtils.getMember("parsePaymentPointer");
    Value javaScriptComponents =  isValidAddressFunction.execute(payID);
    if (javaScriptComponents.isNull()) {
      return null;
    }

    String host = javaScriptComponents.getMember("host").asString();
    String path = javaScriptComponents.getMember("path").asString();

    return ImmutablePayIDComponents.builder().host(host).path(path).build();
  }

}
