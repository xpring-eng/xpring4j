package io.xpring.payid.javascript;

import com.eclipsesource.v8.V8Object;
import io.xpring.payid.ImmutablePayIdComponents;
import io.xpring.payid.PayIdComponents;
import io.xpring.xrpl.javascript.JavaScriptLoader;
import io.xpring.xrpl.javascript.JavaScriptLoaderException;

import java.util.Objects;

/**
 * Provides JavaScript based Utils functionality.
 */
public class JavaScriptPayIdUtils {
  /**
   * An reference to the underlying JavaScriptPayIdUtils object.
   */
  V8Object javaScriptPayIdUtils;

  /**
   * Initialize a new JavaScriptPayIdUtils.
   *
   * @throws JavaScriptLoaderException If the underlying JavaScript was missing or malformed.
   */
  public JavaScriptPayIdUtils() throws JavaScriptLoaderException {
    V8Object context = JavaScriptLoader.getContext();
    V8Object javaScriptUtils = JavaScriptLoader.loadResource("PayIdUtils", context);

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

    V8Object javaScriptComponents =
        ((V8Object) javaScriptPayIdUtils.executeJSFunction("parsePayId", payId));
    if (javaScriptComponents.isUndefined()) {
      return null;
    }

    String host = javaScriptComponents.getString("host");
    String path = javaScriptComponents.getString("path");

    return ImmutablePayIdComponents.builder().host(host).path(path).build();
  }
}
