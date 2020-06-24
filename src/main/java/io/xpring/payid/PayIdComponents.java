package io.xpring.payid;

import org.immutables.value.Value;

/**
 * Represents components of a Pay ID.
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
@Value.Immutable
public interface PayIdComponents {
  /**
   * The host component of the Pay ID.
   */
  String host();

  /**
   * The path component of the Pay ID. Starts with a leading '/'.
   */
  String path();
}
