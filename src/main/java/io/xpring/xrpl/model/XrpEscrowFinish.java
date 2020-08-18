package io.xpring.xrpl.model;

import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
public interface XrpEscrowFinish {
  static ImmutableXrpEscrowFinish.Builder builder() {
    return ImmutableXrpEscrowFinish.builder();
  }

  String ownerXAddress();

  Integer offerSequence();

  Optional<String> condition();

  Optional<String> fulfillment();
}
