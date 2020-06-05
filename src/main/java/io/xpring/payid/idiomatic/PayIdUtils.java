package io.xpring.payid.idiomatic;

import io.xpring.payid.javascript.idiomatic.JavaScriptPayIdUtils;
import io.xpring.xrpl.javascript.JavaScriptLoaderException;

/**
 * Provides utilities for PayID.
 */
public class PayIdUtils {

  private static final JavaScriptPayIdUtils javaScriptPayIDUtils;

  static {
    try {
      javaScriptPayIDUtils = new JavaScriptPayIdUtils();
    } catch (JavaScriptLoaderException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Please do not instantiate this static utility class.
   */
  private PayIdUtils() {
  }

  /**
   * Parse the given Pay ID to a set of components.
   *
   * @param payID A PayID to parse.
   * @return A set of components parsed from the PayID.
   */
  public static PayIdComponents parsePayID(String payID) {
    return javaScriptPayIDUtils.parsePayId(payID);
  }
}
