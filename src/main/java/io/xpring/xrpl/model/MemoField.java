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

  String value();

  Boolean isHex();
}
