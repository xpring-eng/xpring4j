package io.xpring.ilp.model;

import org.interledger.spsp.server.grpc.GetBalanceResponse;

import org.immutables.value.Value;

import java.math.BigInteger;

/**
 * Response object for requests to get an account's balance
 */
@Value.Immutable
public interface AccountBalanceResponse {

  static ImmutableAccountBalanceResponse.Builder builder() {
    return ImmutableAccountBalanceResponse.builder();
  }

  /**
   * @return The accountId for this account balance.
   */
  String accountId();

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

  /**
   * Constructs an {@link AccountBalanceResponse} from a {@link GetBalanceResponse}
   *
   * @param getBalanceResponse a {@link GetBalanceResponse} (protobuf object) whose field values will be used
   *                           to construct an {@link AccountBalanceResponse}
   * @return an {@link AccountBalanceResponse} with its fields set via the analogous protobuf fields.
   */
  static AccountBalanceResponse from(GetBalanceResponse getBalanceResponse) {
    return builder()
      .assetCode(getBalanceResponse.getAssetCode())
      .assetScale(getBalanceResponse.getAssetScale())
      .accountId(getBalanceResponse.getAccountId())
      .clearingBalance(BigInteger.valueOf(getBalanceResponse.getClearingBalance()))
      .prepaidAmount(BigInteger.valueOf(getBalanceResponse.getPrepaidAmount()))
      .build();
  }
}
