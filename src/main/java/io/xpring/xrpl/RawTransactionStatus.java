package io.xpring.xrpl;

import io.xpring.proto.TransactionStatus;
import org.xrpl.org.xrpl.rpc.v1.GetTransactionResponse;

/** Encapsulates fields of a raw transaction status which is returned by the XRP Ledger. */
public class RawTransactionStatus {
    private boolean validated;
    private String transactionStatusCode;
    private int lastLedgerSequence;

    /**
     * Create a new RawTransactionStatus from a {@link TransactionStatus} protocol buffer.
     *
     * @param transactionStatus The {@link TransactionStatus} to encapsulate.
     */
    public RawTransactionStatus(TransactionStatus transactionStatus) {
        this.validated = transactionStatus.getValidated();
        this.transactionStatusCode = transactionStatus.getTransactionStatusCode();
        this.lastLedgerSequence = transactionStatus.getLastLedgerSequence();
    }

    /**
     * Create a new RawTransactionStatus from a {@link GetTransactionResponse} protocol buffer.
     *
     * @param GetTransactionResponse The {@link GetTransactionResponse} to encapsulate.
     */
    public RawTransactionStatus(GetTransactionResponse GetTransactionResponse) {
        this.validated = GetTransactionResponse.getValidated();
        this.transactionStatusCode = GetTransactionResponse.getMeta().getTransactionResult().getResult();
        this.lastLedgerSequence = GetTransactionResponse.getTransaction().getLastLedgerSequence().getValue();
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
