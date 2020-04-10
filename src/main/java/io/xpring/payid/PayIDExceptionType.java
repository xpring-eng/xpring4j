package io.xpring.payid;

/**
 * Types of {@link PayIDException}s.
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public enum PayIDExceptionType {
    INVALID_PAYMENT_POINTER,
    MAPPING_NOT_FOUND,
    UNEXPECTED_RESPONSE,
    UNIMPLEMENTED,
    UNKNOWN;
}
