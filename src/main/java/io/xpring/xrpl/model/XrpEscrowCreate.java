package io.xpring.xrpl.model;

import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
public interface XrpEscrowCreate {
  static ImmutableXrpEscrowCreate.Builder builder() {
    return ImmutableXrpEscrowCreate.builder();
  }

  XrpCurrencyAmount amount();

  Optional<Integer> cancelAfter();

  Optional<String> condition();

  String destinationXAddress();
  
  Optional<Integer> finishAfter();
}
