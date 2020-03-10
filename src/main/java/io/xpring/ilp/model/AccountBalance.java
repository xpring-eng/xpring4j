package io.xpring.ilp.model;

import org.immutables.value.Value;
import java.math.BigInteger;

/**
 * An immutable interface containing the different balances of an account on a connector.
 */
@Value.Immutable
public interface AccountBalance {

  static ImmutableAccountBalance.Builder builder() {
    return ImmutableAccountBalance.builder();
  }

  /**
   * @return The accountId for this account balance.
   */
  String accountId();

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
    return clearingBalance().add(prepaidAmount());
  }

  /**
   * The amount of units representing the clearing position this Connector operator holds with the account owner. A
   * positive clearing balance indicates the Connector operator has an outstanding liability (i.e., owes money) to the
   * account holder. A negative clearing balance represents an asset (i.e., the account holder owes money to the
   * operator).
   *
   * @return An {@link BigInteger} representing the net clearing balance of this account.
   */
  BigInteger clearingBalance();

  /**
   * The number of units that the account holder has prepaid. This value is factored into the value returned by {@link
   * #netBalance()}, and is generally never negative.
   *
   * @return An {@link BigInteger} representing the number of units the counterparty (i.e., owner of this account) has
   * prepaid with this Connector.
   */
  BigInteger prepaidAmount();
}
