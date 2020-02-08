package org.interledger.spsp.server.client;

import org.interledger.connector.accounts.AccountId;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.math.BigInteger;

@Value.Immutable
@JsonSerialize(as = ImmutableAccountBalance.class)
@JsonDeserialize(as = ImmutableAccountBalance.class)
public interface AccountBalance {

  static ImmutableAccountBalance.Builder builder() {
    return ImmutableAccountBalance.builder();
  }

  /**
   * The {@link AccountId} for this account balance.
   *
   * @return
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
  @Value.Derived
  default BigInteger netBalance() {
    final BigInteger netBalance = BigInteger.valueOf(clearingBalance());
    return netBalance.add(BigInteger.valueOf(prepaidAmount()));
  }

  /**
   * The amount of units representing the clearing position this Connector operator holds with the account owner. A
   * positive clearing balance indicates the Connector operator has an outstanding liability (i.e., owes money) to the
   * account holder. A negative clearing balance represents an asset (i.e., the account holder owes money to the
   * operator).
   *
   * @return An {@link BigInteger} representing the net clearing balance of this account.
   */
  // TODO: Use UnsignedLong
  long clearingBalance();

  /**
   * The number of units that the account holder has prepaid. This value is factored into the value returned by {@link
   * #netBalance()}, and is generally never negative.
   *
   * @return An {@link BigInteger} representing the number of units the counterparty (i.e., owner of this account) has
   * prepaid with this Connector.
   */
  // TODO: Use UnsignedLong
  long prepaidAmount();
}
