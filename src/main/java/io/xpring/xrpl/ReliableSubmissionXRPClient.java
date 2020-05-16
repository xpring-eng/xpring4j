package io.xpring.xrpl;

import io.xpring.xrpl.model.XRPTransaction;

import java.math.BigInteger;
import java.util.List;

@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public class ReliableSubmissionXRPClient implements XRPClientDecorator {
  XRPClientDecorator decoratedClient;

  public ReliableSubmissionXRPClient(XRPClientDecorator decoratedClient) {
    this.decoratedClient = decoratedClient;
  }

  @Override
  public BigInteger getBalance(String xrplAccountAddress) throws XRPException {
    return this.decoratedClient.getBalance(xrplAccountAddress);
  }

  @Override
  public TransactionStatus getPaymentStatus(String transactionHash) throws XRPException {
    return this.decoratedClient.getPaymentStatus(transactionHash);
  }

  @Override
  public String send(BigInteger amount, String destinationAddress, Wallet sourceWallet) throws XRPException {
    try {
      long ledgerCloseTime = 4 * 1000;

      // Submit a transaction hash and wait for a ledger to close.
      String transactionHash = decoratedClient.send(amount, destinationAddress, sourceWallet);
      Thread.sleep(ledgerCloseTime);

      // Get transaction status.
      RawTransactionStatus transactionStatus = this.getRawTransactionStatus(transactionHash);
      int lastLedgerSequence = transactionStatus.getLastLedgerSequence();
      if (lastLedgerSequence == 0) {
        throw new XRPException(
            XRPExceptionType.UNKNOWN,
            "The transaction did not have a lastLedgerSequence field so transaction status cannot be reliably "
            + "determined."
        );
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
      throw new XRPException(
          XRPExceptionType.UNKNOWN,
          "Reliable transaction submission project was interrupted."
      );
    }
  }

  @Override
  public int getLatestValidatedLedgerSequence() throws XRPException {
    return this.decoratedClient.getLatestValidatedLedgerSequence();
  }

  @Override
  public RawTransactionStatus getRawTransactionStatus(String transactionHash) throws XRPException {
    return this.decoratedClient.getRawTransactionStatus(transactionHash);
  }

  @Override
  public List<XRPTransaction> paymentHistory(String address) throws XRPException {
    return this.decoratedClient.paymentHistory(address);
  }

  @Override
  public boolean accountExists(String address) throws XRPException {
    return this.decoratedClient.accountExists(address);
  }

  @Override
  public XRPTransaction getTransaction(String transactionHash) throws XRPException {
    return this.decoratedClient.getTransaction(transactionHash);
  }
}