package io.xpring.ilp;

import org.immutables.value.Value;

/**
 * A proxy request class intended to be used by an SDK user when creating an account on a connector.
 *
 * In order to create an account with at least ONE specified field, the user MUST specify, at
 * a minimum, the assetCode and assetScale for the account. By requiring these fields to create a builder,
 * this class makes sure the client satisfies that contract.
 */
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
