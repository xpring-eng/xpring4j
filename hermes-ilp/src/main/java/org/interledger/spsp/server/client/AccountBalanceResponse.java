package org.interledger.spsp.server.client;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

/**
 * API response for requests to get an account's balance
 */
@Value.Immutable
@JsonSerialize(as = ImmutableAccountBalanceResponse.class)
@JsonDeserialize(as = ImmutableAccountBalanceResponse.class)
public interface AccountBalanceResponse {

  static ImmutableAccountBalanceResponse.Builder builder() {
    return ImmutableAccountBalanceResponse.builder();
  }

  String assetCode();

  int assetScale();

  /**
   * Account balance
   *
   * @return
   */
  AccountBalance accountBalance();

}
