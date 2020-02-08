package org.interledger.spsp.server.client;

import org.interledger.connector.accounts.AccountId;
import org.interledger.core.InterledgerAddress;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.primitives.UnsignedLong;
import org.immutables.value.Value;

import java.math.BigInteger;

@JsonSerialize(as = ImmutableConnectorAccountBalance.class)
@JsonDeserialize(as = ImmutableConnectorAccountBalance.class)
@Value.Immutable
public interface ConnectorAccountBalanceWrapper {

  static ImmutableConnectorAccountBalanceWrapper.Builder builder() {
    return ImmutableConnectorAccountBalanceWrapper.builder();
  }

  /**
   * Currency code or other asset identifier that will be used to select the correct rate for this account.
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

  ConnectorAccountBalance accountBalance();

}
