package io.xpring.xrpl;

/** Represents statuses of transactions. */
public enum TransactionStatus {
    /** The transaction was included in a finalized ledger and failed. */
    FAILED,

    /** The transaction is not included in a finalized ledger. */
    PENDING,

    /** The transaction was included in a finalized ledger and succeeded. */
    SUCCEEDED,

    /** The transaction status is unknown. */
    UNKNOWN;
}
