package io.xpring.xrpl;

import io.xpring.xrpl.model.XRPTransaction;

import io.xpring.common.XRPLNetwork;

import java.math.BigInteger;
import java.util.List;

/**
 * A fake XRPClient which returns the given iVars as results from XRPClientDecorator calls.
 * @Note: Since this class is passed by reference and the iVars are mutable, outputs of this class can be changed after it is injected.
 */
public class FakeXRPClient implements  XRPClientDecorator, XRPClientInterface {
    private XRPLNetwork network;

    public BigInteger getBalanceValue;
    public TransactionStatus paymentStatusValue;
    public String sendValue;
    public int latestValidatedLedgerValue;
    public RawTransactionStatus rawTransactionStatusValue;
    public List<XRPTransaction> paymentHistoryValue;
    public boolean accountExistsValue;

    public FakeXRPClient(
            XRPLNetwork network,
            BigInteger getBalanceValue,
            TransactionStatus paymentStatusValue,
            String sendValue,
            int latestValidatedLedgerValue,
            RawTransactionStatus rawTransactionStatusValue,
            List<XRPTransaction> paymentHistoryValue,
            boolean accountExistsValue
    ) {
        this.network = network;
        this.getBalanceValue = getBalanceValue;
        this.paymentStatusValue = paymentStatusValue;
        this.sendValue = sendValue;
        this.latestValidatedLedgerValue = latestValidatedLedgerValue;
        this.rawTransactionStatusValue = rawTransactionStatusValue;
        this.paymentHistoryValue = paymentHistoryValue;
        this.accountExistsValue = accountExistsValue;
    }

    @Override
    public XRPLNetwork getNetwork() {
        return this.network;
    }

    @Override
    public BigInteger getBalance(String xrplAccountAddress) throws XpringException {
        return this.getBalanceValue;
    }

    @Override
    public TransactionStatus getPaymentStatus(String transactionHash) {
        return this.paymentStatusValue;
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

    @Override
    public List<XRPTransaction> paymentHistory(String xrplAccountAddress) {
        return this.paymentHistoryValue;
    }

    @Override
    public boolean accountExists(String address) {
        return this.accountExistsValue;
    }
}