package io.xpring.xrpl.model;

import org.immutables.value.Value;

import java.util.Optional;

/**
 * Provides a means of passing a string to a memo that allows for user
 * specification as to whether or not the string is already a hex string.
 */
@Value.Immutable
public interface MemoFields {
  static ImmutableMemoFields.Builder builder() {
    return ImmutableMemoFields.builder();
  }

  Optional<String> value();

  Optional<Boolean> isHex();
}
