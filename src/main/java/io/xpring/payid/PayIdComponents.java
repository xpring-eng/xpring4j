package io.xpring.payid;

import org.immutables.value.Value;

/**
 * Represents components of a Pay ID.
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
@Value.Immutable
public interface PayIdComponents {
  /**
   * The host component of the PayID.
   *
   * @return A {@link String}, the host component of the PayID.
   */
  String host();

  /**
   * The path component of the PayID. Starts with a leading '/'.
   *
   * @return A {@link String}, the path component of the PayID.
   */
  String path();
}
