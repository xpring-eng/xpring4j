package io.xpring.xrpl;


import java.math.BigInteger;

/**
 * A fake XRPClient which returns the given iVars as results from XRPClientDecorator calls.
 * @Note: Since this class is passed by reference and the iVars are mutable, outputs of this class can be changed after it is injected.
 */
public class FakeXRPClient implements  XRPClientDecorator {
    public BigInteger getBalanceValue;
    public TransactionStatus transactionStatusValue;
    public String sendValue;
    public int latestValidatedLedgerValue;
    public RawTransactionStatus rawTransactionStatusValue;

    public FakeXRPClient(
            BigInteger getBalanceValue,
            TransactionStatus transactionStatusValue,
            String sendValue,
            int latestValidatedLedgerValue,
            RawTransactionStatus rawTransactionStatusValue
    ) {
        this.getBalanceValue = getBalanceValue;
        this.transactionStatusValue = transactionStatusValue;
        this.sendValue = sendValue;
        this.latestValidatedLedgerValue = latestValidatedLedgerValue;
        this.rawTransactionStatusValue = rawTransactionStatusValue;
    }

    @Override
    public BigInteger getBalance(String xrplAccountAddress) throws XpringException {
        return this.getBalanceValue;
    }

    @Override
    public TransactionStatus getTransactionStatus(String transactionHash) {
        return this.transactionStatusValue;
    }

    @Override
    public String send(BigInteger amount, String destinationAddress, Wallet sourceWallet) throws XpringException {
        return this.sendValue;
    }

    @Override
    public int getLatestValidatedLedgerSequence() {
        return this.latestValidatedLedgerValue;
    }

    @Override
    public RawTransactionStatus getRawTransactionStatus(String transactionHash) {
        return this.rawTransactionStatusValue;
    }
}