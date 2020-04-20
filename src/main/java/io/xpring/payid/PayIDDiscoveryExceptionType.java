package io.xpring.payid;

/**
 * Types of {@link PayIDDiscoveryException}
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public enum PayIDDiscoveryExceptionType {
  INVALID_PAY_ID,
  URL_NOT_FOUND,
  MAPPING_NOT_FOUND,
  UNKNOWN
}
