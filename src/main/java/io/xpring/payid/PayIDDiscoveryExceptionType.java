package io.xpring.payid;

/**
 * Types of {@link PayIDDiscoveryException}.
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public enum PayIDDiscoveryExceptionType {
  UNKNOWN,
  INVALID_RESPONSE,
  ERROR_RESPONSE,
  REQUEST_FAILED
}
