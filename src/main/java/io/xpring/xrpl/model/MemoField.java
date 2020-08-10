package io.xpring.xrpl.model;

import org.immutables.value.Value;

/**
 * Provides a means of passing a string to a memo that allows for user
 * specification as to whether or not the string is already a hex string.
 */
@Value.Immutable
public interface MemoField {
  static ImmutableMemoField.Builder builder() {
    return ImmutableMemoField.builder();
  }

  /**
   * The string to be passed to the Memo.
   *
   * @return A {@link String} to be passed to a memo.
   */
  String value();

  /**
   * Whether or not the String in value is already hex-encoded.
   *
   * @return A {@link Boolean} indicating whether or not the value is already hex-encoded.
   */
  Boolean isHex();
}
