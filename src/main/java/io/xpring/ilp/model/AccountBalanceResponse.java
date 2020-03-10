package io.xpring.ilp.model;

import org.immutables.value.Value;

/**
 * Response object for requests to get an account's balance
 */
@Value.Immutable
public interface AccountBalanceResponse {

  static ImmutableAccountBalanceResponse.Builder builder() {
    return ImmutableAccountBalanceResponse.builder();
  }

  /**
   * Currency code or other asset identifier that this account's balances will be denominated in
   */
  String assetCode();

  /**
   * Interledger amounts are integers, but most currencies are typically represented as # fractional units, e.g. cents.
   * This property defines how many Interledger units make # up one regular unit. For dollars, this would usually be set
   * to 9, so that Interledger # amounts are expressed in nano-dollars.
   *
   * @return an int representing this account's asset scale.
   */
  int assetScale();

  /**
   * Contains the balance of an account on a connector, denominated in the account's assetCode and assetScale
   *
   * @return an {@link AccountBalance} with net, clearing, and prepaid balance of an account.
   */
  AccountBalance accountBalance();

}
