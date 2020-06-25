package io.xpring.payid;

/**
 * Types of {@link PayIdException}s.
 */
public enum PayIdExceptionType {
    INVALID_PAYMENT_POINTER,
    MAPPING_NOT_FOUND,
    UNEXPECTED_RESPONSE,
    UNIMPLEMENTED,
    UNKNOWN;
}