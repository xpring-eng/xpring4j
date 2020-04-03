package io.xpring.xrpl;

import io.xpring.common.Result;
import io.xpring.common.XRPLNetwork;

import java.math.BigInteger;

/**
 * A fake XRPClient which returns the given iVars as results from XRPClientDecorator calls.
 * @Note: Since this class is passed by reference and the iVars are mutable, outputs of this class can be changed after it is injected.
 */
public class FakeXRPClient implements  XRPClientDecorator, XRPClientInterface {
    private XRPLNetwork network;

    public Result<BigInteger, XRPException> getBalanceResult;
    public Result<TransactionStatus, XRPException> paymentStatusResult;
    public Result<String, XRPException> sendResult;
    public Result<Integer, XRPException> latestValidatedLedgerResult;
    public Result<RawTransactionStatus, XRPException> rawTransactionStatusResult;
    public Result<Boolean, XRPException> accountExistsResult;

    public FakeXRPClient(
            XRPLNetwork network,
            Result<BigInteger, XRPException> getBalanceResult,
            Result<TransactionStatus, XRPException> paymentStatusResult,
            Result<String, XRPException> sendResult,
            Result<Integer, XRPException> latestValidatedLedgerResult,
            Result<RawTransactionStatus, XRPException> rawTransactionStatusResult,
            Result<Boolean, XRPException> accountExistsResult
    ) {
        this.network = network;
        this.getBalanceResult = getBalanceResult;
        this.paymentStatusResult = paymentStatusResult;
        this.sendResult = sendResult;
        this.latestValidatedLedgerResult = latestValidatedLedgerResult;
        this.rawTransactionStatusResult = rawTransactionStatusResult;
        this.accountExistsResult = accountExistsResult;
    }

    @Override
    public XRPLNetwork getNetwork() {
        return this.network;
    }

    @Override
    public BigInteger getBalance(String xrplAccountAddress) throws XRPException {
        if (this.getBalanceResult.isError()) {
            throw this.getBalanceResult.getError();
        } else {
            return getBalanceResult.getValue();
        }
    }

    @Override
    public TransactionStatus getPaymentStatus(String transactionHash) throws XRPException {
        if (this.paymentStatusResult.isError()) {
            throw this.paymentStatusResult.getError();
        } else {
            return paymentStatusResult.getValue();
        }
    }

    @Override
    public String send(BigInteger amount, String destinationAddress, Wallet sourceWallet) throws XRPException {
        if (this.sendResult.isError()) {
            throw this.sendResult.getError();
        } else {
            return sendResult.getValue();
        }
    }

    @Override
    public int getLatestValidatedLedgerSequence() throws XRPException {
        if (this.latestValidatedLedgerResult.isError()) {
            throw this.latestValidatedLedgerResult.getError();
        } else {
            return latestValidatedLedgerResult.getValue();
        }
    }

    @Override
    public RawTransactionStatus getRawTransactionStatus(String transactionHash) throws XRPException {
        if (this.rawTransactionStatusResult.isError()) {
            throw this.rawTransactionStatusResult.getError();
        } else {
            return rawTransactionStatusResult.getValue();
        }
    }

    @Override
    public boolean accountExists(String address) throws XRPException {
        if (this.accountExistsResult.isError()) {
            throw this.accountExistsResult.getError();
        } else {
            return accountExistsResult.getValue();
        }
    }
}