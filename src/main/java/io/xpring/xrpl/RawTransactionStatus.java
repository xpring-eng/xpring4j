package io.xpring.xrpl;

import org.xrpl.rpc.v1.*;

/** Encapsulates fields of a raw transaction status which is returned by the XRP Ledger. */
// TODO(keefertaylor): This class is now defunct. Refactor and remove.
public class RawTransactionStatus {
    private boolean validated;
    private String transactionStatusCode;
    private int lastLedgerSequence;
    private boolean isFullPayment;

    /**
     * Create a new RawTransactionStatus from a {@link GetTransactionResponse} protocol buffer.
     *
     * @param getTransactionResponse The {@link GetTransactionResponse} to encapsulate.
     */
    public RawTransactionStatus(GetTransactionResponse getTransactionResponse) {
        Transaction transaction = getTransactionResponse.getTransaction();

        this.validated = getTransactionResponse.getValidated();
        this.transactionStatusCode = getTransactionResponse.getMeta().getTransactionResult().getResult();
        this.lastLedgerSequence = transaction.getLastLedgerSequence().getValue();

        boolean isPayment = transaction.hasPayment();
        int flags = transaction.getFlags().getValue();

        boolean isPartialPayment = RippledFlags.check(RippledFlags.TF_PARTIAL_PAYMENT, flags);

        this.isFullPayment = isPayment && !isPartialPayment;
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

    /**
     * Returns whether the transaction represnented by this status is a full payment.
     */
    public boolean isFullPayment() { return this.isFullPayment; }
}
