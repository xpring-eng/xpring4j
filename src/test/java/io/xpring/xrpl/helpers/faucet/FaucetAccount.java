package io.xpring.xrpl.helpers.faucet;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.xpring.xrpl.helpers.faucet.ImmutableFaucetAccount;
import org.immutables.value.Value;

/**
 * Faucet account details returned as part of a request to the /accounts API.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableFaucetAccount.class)
@JsonDeserialize(as = ImmutableFaucetAccount.class)
public interface FaucetAccount {

  @SuppressWarnings("checkstyle:MethodName")
  /**
   * X-address of the created account.
   *
   * @return
   */
  String xAddress();

  /**
   * Classic address of the created account.
   *
   * @return
   */
  String classicAddress();

  /**
   * Same value as classicAddress.
   *
   * @return
   */
  String address();

  /**
   * Private secret/seed for the address.
   *
   * @return
   */
  String secret();

}
