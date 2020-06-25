package io.xpring.payid;

import io.xpring.payid.javascript.JavaScriptPayIDUtils;
import io.xpring.xrpl.javascript.JavaScriptLoaderException;

/**
 * Provides utilities for PayID.
 *
 * @deprecated Use the idiomatically named `PayIdUtils` class instead.
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
@Deprecated
public class PayIDUtils {

  private static final JavaScriptPayIDUtils javaScriptPayIDUtils;

  static {
    try {
      javaScriptPayIDUtils = new JavaScriptPayIDUtils();
    } catch (JavaScriptLoaderException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Please do not instantiate this static utility class.
   */
  private PayIDUtils() {
  }

  /**
   * Parse the given Pay ID to a set of components.
   *
   * @param payID A PayID to parse.
   * @return A set of components parsed from the PayID.
   */
  public static PayIDComponents parsePayID(String payID) {
    return javaScriptPayIDUtils.parsePayID(payID);
  }
}
