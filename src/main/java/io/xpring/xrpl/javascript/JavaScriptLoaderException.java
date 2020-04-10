package io.xpring.xrpl.javascript;

// TODO(keefertaylor): This class is used across Pay ID and XRP. Refactor to Common.
/**
 * Exceptions which occur when working with JavaScript.
 */
public class JavaScriptLoaderException extends Exception {
  public JavaScriptLoaderException(String errorMessage) {
    super(errorMessage);
  }
}
