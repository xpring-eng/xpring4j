package io.xpring.payid.idiomatic.javascript;

import io.xpring.payid.idiomatic.ImmutablePayIdComponents;
import io.xpring.payid.idiomatic.PayIdComponents;
import io.xpring.xrpl.javascript.JavaScriptLoader;
import io.xpring.xrpl.javascript.JavaScriptLoaderException;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import java.util.Objects;

/**
 * Provides JavaScript based Utils functionality.
 */
public class JavaScriptPayIdUtils {
  /**
   * An reference to the underlying JavaScriptPayIdUtils object.
   */
  private Value javaScriptPayIdUtils;

  /**
   * Initialize a new JavaScriptPayIdUtils.
   *
   * @throws JavaScriptLoaderException If the underlying JavaScript was missing or malformed.
   */
  public JavaScriptPayIdUtils() throws JavaScriptLoaderException {
    Context context = JavaScriptLoader.getContext();
    Value javaScriptUtils = JavaScriptLoader.loadResource("PayIdUtils", context);

    this.javaScriptPayIdUtils = javaScriptUtils;
  }

  /**
   * Parse the given Pay ID to a set of components.
   *
   * @param payId A PayID to parse.
   * @return A set of components parsed from the PayID.
   */
  public PayIdComponents parsePayId(String payId) {
    Objects.requireNonNull(payId);

    Value parsePayIDFunction = javaScriptPayIdUtils.getMember("parsePayId");
    Value javaScriptComponents = parsePayIDFunction.execute(payId);
    if (javaScriptComponents.isNull()) {
      return null;
    }

    String host = javaScriptComponents.getMember("host").asString();
    String path = javaScriptComponents.getMember("path").asString();

    return ImmutablePayIdComponents.builder().host(host).path(path).build();
  }
}
