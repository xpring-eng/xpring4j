package io.xpring.payid;

/**
 * Types of {@link PayIDException}s.
 *
 * @deprecated Please use the idiomatically cased `PayIdExceptionType` enum instead.
 */
@Deprecated
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public enum PayIDExceptionType {
    INVALID_PAYMENT_POINTER,
    MAPPING_NOT_FOUND,
    UNEXPECTED_RESPONSE,
    UNIMPLEMENTED,
    UNKNOWN;
}
