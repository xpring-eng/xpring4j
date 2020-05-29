package io.xpring.xrpl;

import io.xpring.xrpl.idiomatic.XrpException;
import io.xpring.xrpl.idiomatic.XrpExceptionType;

/**
 * Types of {@link XrpException}s.
 *
 * @deprecated Please use the idiomatically named {@link XrpExceptionType} class instead.
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
@Deprecated
public enum XRPExceptionType {
    INVALID_INPUTS,
    SIGNING_ERROR,
    UNIMPLEMENTED,
    UNKNOWN,
    X_ADDRESS_REQUIRED
}
