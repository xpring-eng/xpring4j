package io.xpring.xrpl.model;

import io.xpring.xrpl.TransactionStatus;
import org.immutables.value.Value;

/**
 * Represents the outcome of submitting an XRPL transaction.
 */
@Value.Immutable
public interface TransactionResult {
  static ImmutableTransactionResult.Builder builder() {
    return ImmutableTransactionResult.builder();
  }

  /**
   * The identifying hash of the transaction.
   *
   * @return A {@link String}, the identifying hash of the transaction.
   */
  String hash();

  /**
   * The {@link TransactionStatus} representing the outcome of this transaction.
   *
   * @return A {@link TransactionStatus} representing the outcome of this transaction.
   */
  TransactionStatus status();

  /**
   * Whether this transaction is included in a validated ledger.
   * The transactions status is only final if this field is true.
   *
   * @return A {@link Boolean} indicating whether this transaction is included in a validated ledger.
   */
  Boolean validated();
}
