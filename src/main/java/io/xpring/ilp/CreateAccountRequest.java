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

  /**
   * A unique identifier for this account. For example, {@code alice} or {@code 123456789}.
   * If no accountId is specified here, an accountId will be generated automatically for this account.
   */
  @Value.Default
  default String accountId() { return ""; }

  /**
   * The currency that will be associated with the account that is created. For example, "XRP" or "ETH".
   * Balances and payments for this account will be denominated in this currency.
   */
  String assetCode();

  /**
   * Interledger amounts are integers, but most currencies are typically represented as # fractional units, e.g. cents.
   * This property defines how many Interledger units make # up one regular unit. For dollars, this would usually be set
   * to 9, so that Interledger # amounts are expressed in nano-dollars.
   *
   * @return an int representing this account's asset scale.
   */
  Integer assetScale();

  /**
   * An optional human-readable description of this account.
   */
  @Value.Default
  default String description() { return ""; };

}
