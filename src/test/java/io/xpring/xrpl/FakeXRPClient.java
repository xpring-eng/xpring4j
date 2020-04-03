package io.xpring.xrpl;


import com.sun.org.apache.xpath.internal.operations.Bool;
import io.xpring.GRPCResult;

import java.math.BigInteger;

/**
 * A fake XRPClient which returns the given iVars as results from XRPClientDecorator calls.
 * @Note: Since this class is passed by reference and the iVars are mutable, outputs of this class can be changed after it is injected.
 */
public class FakeXRPClient implements  XRPClientDecorator, XRPClientInterface {
    public GRPCResult<BigInteger, XpringException> getBalanceResult;
    public GRPCResult<TransactionStatus, XpringException> paymentStatusResult;
    public GRPCResult<String, XpringException> sendResult;
    public GRPCResult<Integer, XpringException> latestValidatedLedgerResult;
    public GRPCResult<RawTransactionStatus, XpringException> rawTransactionStatusResult;
    public GRPCResult<Boolean, XpringException> accountExistsResult;

    public FakeXRPClient(
            GRPCResult<BigInteger, XpringException> getBalanceResult,
            GRPCResult<TransactionStatus, XpringException> paymentStatusResult,
            GRPCResult<String, XpringException> sendResult,
            GRPCResult<Integer, XpringException> latestValidatedLedgerResult,
            GRPCResult<RawTransactionStatus, XpringException> rawTransactionStatusResult,
            GRPCResult<Boolean, XpringException> accountExistsResult
    ) {
        this.getBalanceResult = getBalanceResult;
        this.paymentStatusResult = paymentStatusResult;
        this.sendResult = sendResult;
        this.latestValidatedLedgerResult = latestValidatedLedgerResult;
        this.rawTransactionStatusResult = rawTransactionStatusResult;
        this.accountExistsResult = accountExistsResult;
    }

    @Override
    public BigInteger getBalance(String xrplAccountAddress) throws XpringException {
        if (this.getBalanceResult.isError()) {
            throw this.getBalanceResult.getError();
        } else {
            return getBalanceResult.getValue();
        }
    }

    @Override
    public TransactionStatus getPaymentStatus(String transactionHash) throws XpringException {
        if (this.paymentStatusResult.isError()) {
            throw this.paymentStatusResult.getError();
        } else {
            return paymentStatusResult.getValue();
        }
    }

    @Override
    public String send(BigInteger amount, String destinationAddress, Wallet sourceWallet) throws XpringException {
        if (this.sendResult.isError()) {
            throw this.sendResult.getError();
        } else {
            return sendResult.getValue();
        }
    }

    @Override
    public int getLatestValidatedLedgerSequence() throws XpringException {
        if (this.latestValidatedLedgerResult.isError()) {
            throw this.latestValidatedLedgerResult.getError();
        } else {
            return latestValidatedLedgerResult.getValue();
        }
    }

    @Override
    public RawTransactionStatus getRawTransactionStatus(String transactionHash) throws XpringException {
        if (this.rawTransactionStatusResult.isError()) {
            throw this.rawTransactionStatusResult.getError();
        } else {
            return rawTransactionStatusResult.getValue();
        }
    }

    @Override
    public boolean accountExists(String address) throws XpringException {
        if (this.accountExistsResult.isError()) {
            throw this.accountExistsResult.getError();
        } else {
            return accountExistsResult.getValue();
        }
    }
}