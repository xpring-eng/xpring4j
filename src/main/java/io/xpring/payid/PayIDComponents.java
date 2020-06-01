package io.xpring.payid;

import org.immutables.value.Value;

/**
 * Represents components of a PayID.
 *
 * @deprecated Please use the idiomatically named `PayIdComponents` interface instead.
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
@Deprecated
@Value.Immutable
public interface PayIDComponents {
  /**
   * The host component of the payID.
   */
  String host();

  /**
   * The path component of the payID. Starts with a leading '/'.
   */
  String path();
}
