package io.xpring.xrpl.model;

import io.xpring.xrpl.TransactionStatus;

/**
 * Represents the outcome of submitting an XRPL transaction.
 */
public class TransactionResult {
  public String hash;
  public TransactionStatus status;
  public Boolean validated;

  /**
   *
   * @param hash The identifying hash of the transaction.
   * @param status The {@link TransactionStatus} indicating the outcome of this transaction.
   * @param validated Whether this transaction is included in a validated ledger.
   *                  The transactions status is only final if this field is true.
   */
  public TransactionResult(String hash, TransactionStatus status, Boolean validated) {
    this.hash = hash;
    this.status = status;
    this.validated = validated;
  }
}
