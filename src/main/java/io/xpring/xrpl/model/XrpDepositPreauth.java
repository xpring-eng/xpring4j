package io.xpring.xrpl.model;

import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
public interface XrpDepositPreauth {
  static ImmutableXrpDepositPreauth.Builder builder() {
    return ImmutableXrpDepositPreauth.builder();
  }

  Optional<String> authorizeXAddress();

  Optional<String> unauthorizeXAddress();
}
