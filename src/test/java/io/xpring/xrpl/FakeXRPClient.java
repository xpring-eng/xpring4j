package io.xpring.xrpl;

import io.xpring.common.Result;
import io.xpring.common.XRPLNetwork;
import io.xpring.xrpl.model.XRPTransaction;

import java.math.BigInteger;
import java.util.List;

/**
 * A fake XRPClient which returns the given iVars as results from XRPClientDecorator calls.
 * <p>
 * Note: Since this class is passed by reference and the iVars are mutable, outputs of this class can be changed after
 * it is injected.
 * </p>
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public class FakeXRPClient implements XRPClientDecorator, XRPClientInterface {
  private XRPLNetwork network;

  public Result<BigInteger, XRPException> getBalanceResult;
  public Result<TransactionStatus, XRPException> paymentStatusResult;
  public Result<String, XRPException> sendResult;
  public Result<Integer, XRPException> latestValidatedLedgerResult;
  public Result<RawTransactionStatus, XRPException> rawTransactionStatusResult;
  public Result<List<XRPTransaction>, XRPException> paymentHistoryResult;
  public Result<Boolean, XRPException> accountExistsResult;
  public Result<XRPTransaction, XRPException> getTransactionResult;

  /**
   * Create a new FakeXRPClient.
   *
   * @param network The network the client is attached to.
   * @param getBalanceResult The result of a call to get a balance of an account.
   * @param paymentStatusResult The result of a call to get a payment status.
   * @param sendResult The result of sending a payment.
   * @param latestValidatedLedgerResult The result of requesting the latest validated ledger sequence number.
   * @param rawTransactionStatusResult The result of requesting a raw transaction status.
   * @param paymentHistoryResult The result of requesting payment history.
   * @param accountExistsResult The result of checking existence of an account.
   * @param getTransactionResult The result of a call to get a transaction by hash.
   */
  public FakeXRPClient(
      XRPLNetwork network,
      Result<BigInteger, XRPException> getBalanceResult,
      Result<TransactionStatus, XRPException> paymentStatusResult,
      Result<String, XRPException> sendResult,
      Result<Integer, XRPException> latestValidatedLedgerResult,
      Result<RawTransactionStatus, XRPException> rawTransactionStatusResult,
      Result<List<XRPTransaction>, XRPException> paymentHistoryResult,
      Result<Boolean, XRPException> accountExistsResult,
      Result<XRPTransaction, XRPException> getTransactionResult
  ) {
    this.network = network;
    this.getBalanceResult = getBalanceResult;
    this.paymentStatusResult = paymentStatusResult;
    this.sendResult = sendResult;
    this.latestValidatedLedgerResult = latestValidatedLedgerResult;
    this.rawTransactionStatusResult = rawTransactionStatusResult;
    this.paymentHistoryResult = paymentHistoryResult;
    this.accountExistsResult = accountExistsResult;
    this.getTransactionResult = getTransactionResult;
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
  public int getLatestValidatedLedgerSequence(String address) throws XRPException {
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

  @Override
  public List<XRPTransaction> paymentHistory(String xrplAccountAddress) throws XRPException {
    if (this.paymentHistoryResult.isError()) {
      throw this.paymentHistoryResult.getError();
    } else {
      return paymentHistoryResult.getValue();
    }
  }

  @Override
  public XRPTransaction getTransaction(String transactionHash) throws XRPException {
    if (this.getTransactionResult.isError()) {
      throw this.getTransactionResult.getError();
    } else {
      return getTransactionResult.getValue();
    }
  }
}