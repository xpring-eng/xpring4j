package io.xpring.xrpl;

import io.xpring.common.Result;
import io.xpring.common.XrplNetwork;
import io.xpring.xrpl.model.TransactionResult;
import io.xpring.xrpl.model.XrpTransaction;

import java.math.BigInteger;
import java.util.List;

/**
 * A fake XRPClient which returns the given iVars as results from XrpClientDecorator calls.
 * <p>
 * Note: Since this class is passed by reference and the iVars are mutable, outputs of this class can be changed after
 * it is injected.
 * </p>
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public class FakeXrpClient implements XrpClientDecorator, XrpClientInterface {
  private XrplNetwork network;

  public Result<BigInteger, XrpException> getBalanceResult;
  public Result<TransactionStatus, XrpException> paymentStatusResult;
  public Result<String, XrpException> sendResult;
  public Result<Integer, XrpException> latestValidatedLedgerResult;
  public Result<RawTransactionStatus, XrpException> rawTransactionStatusResult;
  public Result<List<XrpTransaction>, XrpException> paymentHistoryResult;
  public Result<Boolean, XrpException> accountExistsResult;
  public Result<XrpTransaction, XrpException> getPaymentResult;
  public Result<TransactionResult, XrpException> enableDepositAuthResult;

  /**
   * Create a new FakeXrpClient.
   *
   * @param network The network the client is attached to.
   * @param getBalanceResult The result of a call to get a balance of an account.
   * @param paymentStatusResult The result of a call to get a payment status.
   * @param sendResult The result of sending a payment.
   * @param latestValidatedLedgerResult The result of requesting the latest validated ledger sequence number.
   * @param rawTransactionStatusResult The result of requesting a raw transaction status.
   * @param paymentHistoryResult The result of requesting payment history.
   * @param accountExistsResult The result of checking existence of an account.
   * @param getPaymentResult The result of a call to get a transaction by hash.
   * @param enableDepositAuthResult The result of submitting a transaction to enable deposit authorization.
   */
  public FakeXrpClient(
      XrplNetwork network,
      Result<BigInteger, XrpException> getBalanceResult,
      Result<TransactionStatus, XrpException> paymentStatusResult,
      Result<String, XrpException> sendResult,
      Result<Integer, XrpException> latestValidatedLedgerResult,
      Result<RawTransactionStatus, XrpException> rawTransactionStatusResult,
      Result<List<XrpTransaction>, XrpException> paymentHistoryResult,
      Result<Boolean, XrpException> accountExistsResult,
      Result<XrpTransaction, XrpException> getPaymentResult,
      Result<TransactionResult, XrpException> enableDepositAuthResult
  ) {
    this.network = network;
    this.getBalanceResult = getBalanceResult;
    this.paymentStatusResult = paymentStatusResult;
    this.sendResult = sendResult;
    this.latestValidatedLedgerResult = latestValidatedLedgerResult;
    this.rawTransactionStatusResult = rawTransactionStatusResult;
    this.paymentHistoryResult = paymentHistoryResult;
    this.accountExistsResult = accountExistsResult;
    this.getPaymentResult = getPaymentResult;
    this.enableDepositAuthResult = enableDepositAuthResult;
  }

  @Override
  public XrplNetwork getNetwork() {
    return this.network;
  }

  @Override
  public BigInteger getBalance(String xrplAccountAddress) throws XrpException {
    if (this.getBalanceResult.isError()) {
      throw this.getBalanceResult.getError();
    } else {
      return getBalanceResult.getValue();
    }
  }

  @Override
  public TransactionStatus getPaymentStatus(String transactionHash) throws XrpException {
    if (this.paymentStatusResult.isError()) {
      throw this.paymentStatusResult.getError();
    } else {
      return paymentStatusResult.getValue();
    }
  }

  @Override
  public String send(BigInteger amount, String destinationAddress, Wallet sourceWallet) throws XrpException {
    if (this.sendResult.isError()) {
      throw this.sendResult.getError();
    } else {
      return sendResult.getValue();
    }
  }

  @Override
  public int getLatestValidatedLedgerSequence(String address) throws XrpException {
    if (this.latestValidatedLedgerResult.isError()) {
      throw this.latestValidatedLedgerResult.getError();
    } else {
      return latestValidatedLedgerResult.getValue();
    }
  }

  @Override
  public RawTransactionStatus getRawTransactionStatus(String transactionHash) throws XrpException {
    if (this.rawTransactionStatusResult.isError()) {
      throw this.rawTransactionStatusResult.getError();
    } else {
      return rawTransactionStatusResult.getValue();
    }
  }

  @Override
  public boolean accountExists(String address) throws XrpException {
    if (this.accountExistsResult.isError()) {
      throw this.accountExistsResult.getError();
    } else {
      return accountExistsResult.getValue();
    }
  }

  @Override
  public List<XrpTransaction> paymentHistory(String xrplAccountAddress) throws XrpException {
    if (this.paymentHistoryResult.isError()) {
      throw this.paymentHistoryResult.getError();
    } else {
      return paymentHistoryResult.getValue();
    }
  }

  @Override
  public XrpTransaction getPayment(String transactionHash) throws XrpException {
    if (this.getPaymentResult.isError()) {
      throw this.getPaymentResult.getError();
    } else {
      return getPaymentResult.getValue();
    }
  }

  @Override
  public TransactionResult enableDepositAuth(Wallet wallet) throws XrpException {
    if (this.enableDepositAuthResult.isError()) {
      throw this.enableDepositAuthResult.getError();
    } else {
      return enableDepositAuthResult.getValue();
    }
  }
}