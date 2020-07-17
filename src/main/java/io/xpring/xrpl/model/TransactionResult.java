package io.xpring.xrpl.model;

import io.xpring.xrpl.TransactionStatus;

/**
 * Represents the outcome of submitting an XRPL transaction. The hash is the identifying hash of the transaction.
 * The status is the final result of the transaction.
 *
 * Note: This class is intended to be used with reliable submission logic, and the status represents the final
 * outcome of the transaction after inclusion in a validated ledger, or after the expiration of the transaction's
 * lastLedgerSequence.  For example, a successful status indicates that the transaction was applied in a validated
 * ledger with a successful result code, as opposed to success only on an open ledger in a local rippled environment.
 */
public class TransactionResult {
  String hash;
  TransactionStatus status;

  public TransactionResult(String hash, TransactionStatus status) {
    this.hash = hash;
    this.status = status;
  }
}
