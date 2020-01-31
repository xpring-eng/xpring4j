package io.xpring.xrpl;

import io.xpring.proto.TransactionStatus;

/** Encapsulates fields of a raw transaction status which is returned by the XRP Ledger. */
public class RawTransactionStatus {
    private boolean validated;
    private String transactionStatusCode;
    private int lastLedgerSequence;

    /**
     * Create a new RawTransactionStatus from a {@link TransactionStatus} protocol buffer.
     *
     * @param transactionStatus The transaction status to encapsulate.
     */
    public RawTransactionStatus(TransactionStatus transactionStatus) {
        this.validated = transactionStatus.getValidated();
        this.transactionStatusCode = transactionStatus.getTransactionStatusCode();
        this.lastLedgerSequence = transactionStatus.getLastLedgerSequence();
    }

    /**
     * Retrieve whether or not the transaction has been validated.
     */
    public boolean getValidated() {
        return this.validated;
    }

    /**
     * Retrieve the transaction status code.
     */
    public String getTransactionStatusCode() {
        return this.transactionStatusCode;
    }

    /**
     * Retrieve the last ledger sequence this transaction is valid for.
     */
    public int getLastLedgerSequence() {
        return this.lastLedgerSequence;
    }
}
