package io.xpring.xrpl.model;

import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
public interface XrpOfferCreate {
  static ImmutableXrpOfferCreate.Builder builder() {
    return ImmutableXrpOfferCreate.builder();
  }

  Optional<Integer> expiration();

  Optional<Integer> offerSequence();

  XrpCurrencyAmount takerGets();

  XrpCurrencyAmount takerPays();
}
