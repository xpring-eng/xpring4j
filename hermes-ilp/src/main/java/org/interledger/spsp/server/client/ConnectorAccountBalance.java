package org.interledger.spsp.server.client;

import org.interledger.connector.accounts.AccountId;
import org.interledger.core.InterledgerAddress;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.primitives.UnsignedLong;
import org.immutables.value.Value;

import java.math.BigInteger;

@Value.Immutable
@JsonSerialize(as = ImmutableConnectorAccountBalance.class)
@JsonDeserialize(as = ImmutableConnectorAccountBalance.class)
public interface ConnectorAccountBalance {

  static ImmutableConnectorAccountBalance.Builder builder() {
    return ImmutableConnectorAccountBalance.builder();
  }

  /**
   * <p>A unique identifier for this account. For example, <tt>alice</tt> or <tt>123456789</tt>.
   * Note that this is not an {@link InterledgerAddress} because an account's address is assigned when a connection is
   * made, generally using information from the client and this identifier.</p>
   *
   */
  AccountId accountId();

  /**
   * The amount of units representing the aggregate position this Connector operator holds with the account owner. A
   * positive balance indicates the Connector operator has an outstanding liability (i.e., owes money) to the account
   * holder. A negative balance represents an asset (i.e., the account holder owes money to the operator). This value is
   * the sum of the clearing balance and the prepaid amount.
   *
   * @return An {@link BigInteger} representing the net clearingBalance of this account.
   */
  BigInteger netBalance();

  /**
   * The number of units that the account holder has prepaid. This value is factored into the value returned by {@link
   * #netBalance()}, and is generally never negative.
   *
   * @return An {@link UnsignedLong} representing the number of units the counterparty (i.e., owner of this account) has
   * prepaid with this Connector.
   */
  UnsignedLong prepaidAmount();

  /**
   * The amount of units representing the clearing position this Connector operator holds with the account owner. A
   * positive clearing balance indicates the Connector operator has an outstanding liability (i.e., owes money) to the
   * account holder. A negative clearing balance represents an asset (i.e., the account holder owes money to the
   * operator).
   *
   * @return An {@link UnsignedLong} representing the net clearing balance of this account.
   */
  UnsignedLong clearingBalance();
}
