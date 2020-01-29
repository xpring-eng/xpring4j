package io.xpring.xrpl;

import io.xpring.xrpl.XpringClientDecorator;

import java.math.BigInteger;

public class ReliableSubmissionXpringClient implements XpringClientDecorator {
    XpringClientDecorator decoratedClient;

    public ReliableSubmissionXpringClient(XpringClientDecorator decoratedClient) {
        this.decoratedClient = decoratedClient;
    }

    @Override
    public BigInteger getBalance(String xrplAccountAddress) throws XpringKitException {
        return this.decoratedClient.getBalance(xrplAccountAddress);
    }

    @Override
    public TransactionStatus getTransactionStatus(String transactionHash) {
        return this.decoratedClient.getTransactionStatus(transactionHash);
    }

    public String send(BigInteger amount, String destinationAddress, Wallet sourceWallet) throws XpringKitException {
        try {
            long ledgerCloseTime = 4 * 1000;

            // Submit a transaction hash and wait for a ledger to close.
            String transactionHash = decoratedClient.send(amount, destinationAddress, sourceWallet);
            Thread.sleep(ledgerCloseTime);

            // Get transaction status.
            RawTransactionStatus transactionStatus = this.getRawTransactionStatus(transactionHash);
            int lastLedgerSequence = transactionStatus.getLastLedgerSequence();
            if (lastLedgerSequence == 0) {
                throw new XpringKitException("The transaction did not have a lastLedgerSequence field so transaction status cannot be reliably determined.");
            }

            // Retrieve the latest ledger index.
            int latestLedgerSequence = this.getLatestValidatedLedgerSequence();

            // Poll until the transaction is validated, or until the lastLedgerSequence has been passed.
            while (latestLedgerSequence <= lastLedgerSequence && !transactionStatus.getValidated()) {
                Thread.sleep(ledgerCloseTime);

                latestLedgerSequence = this.getLatestValidatedLedgerSequence();
                transactionStatus = this.getRawTransactionStatus(transactionHash);
            }

            return transactionHash;
        } catch (InterruptedException e) {
            throw new XpringKitException("Reliable transaction submission project was interrupted.");
        }
    }

    @Override
    public int getLatestValidatedLedgerSequence() {
        return this.decoratedClient.getLatestValidatedLedgerSequence();
    }

    @Override
    public RawTransactionStatus getRawTransactionStatus(String transactionHash) {
        return this.decoratedClient.getRawTransactionStatus(transactionHash);
    }
}