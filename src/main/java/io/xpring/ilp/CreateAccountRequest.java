package io.xpring.ilp;

import org.immutables.value.Value;

import javax.annotation.Nullable;

@Value.Immutable
public interface CreateAccountRequest {

  static ImmutableCreateAccountRequest.Builder builder(String assetCode, Integer assetScale) {
    return ImmutableCreateAccountRequest.builder().assetCode(assetCode).assetScale(assetScale);
  }

  @Value.Default
  default String accountId() { return ""; };

  String assetCode();

  Integer assetScale();

  @Value.Default
  default String description() { return ""; };

}
