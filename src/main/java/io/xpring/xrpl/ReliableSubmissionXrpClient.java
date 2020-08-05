package io.xpring.xrpl;

import io.xpring.xrpl.model.SendXrpDetails;
import io.xpring.xrpl.model.XrpTransaction;

import java.math.BigInteger;
import java.util.List;

public class ReliableSubmissionXrpClient implements XrpClientDecorator {
  XrpClientDecorator decoratedClient;

  public ReliableSubmissionXrpClient(XrpClientDecorator decoratedClient) {
    this.decoratedClient = decoratedClient;
  }

  @Override
  public BigInteger getBalance(String xrplAccountAddress) throws XrpException {
    return this.decoratedClient.getBalance(xrplAccountAddress);
  }

  @Override
  public TransactionStatus getPaymentStatus(String transactionHash) throws XrpException {
    return this.decoratedClient.getPaymentStatus(transactionHash);
  }

  @Override
  public String send(BigInteger amount, String destinationAddress, Wallet sourceWallet) throws XrpException {
    String transactionHash = decoratedClient.send(amount, destinationAddress, sourceWallet);
    this.awaitFinalTransactionResult(transactionHash, sourceWallet);
    return transactionHash;
  }

  @Override
  public String sendWithDetails(SendXrpDetails sendXrpDetails) throws XrpException {
    return this.decoratedClient.sendWithDetails(sendXrpDetails);
  }

  @Override
  public int getLatestValidatedLedgerSequence(String address) throws XrpException {
    return this.decoratedClient.getLatestValidatedLedgerSequence(address);
  }

  @Override
  public RawTransactionStatus getRawTransactionStatus(String transactionHash) throws XrpException {
    return this.decoratedClient.getRawTransactionStatus(transactionHash);
  }

  @Override
  public List<XrpTransaction> paymentHistory(String address) throws XrpException {
    return this.decoratedClient.paymentHistory(address);
  }

  @Override
  public boolean accountExists(String address) throws XrpException {
    return this.decoratedClient.accountExists(address);
  }

  @Override
  public XrpTransaction getPayment(String transactionHash) throws XrpException {
    return this.decoratedClient.getPayment(transactionHash);
  }

  private RawTransactionStatus awaitFinalTransactionResult(String transactionHash, Wallet sender) throws XrpException {
    try {
      long ledgerCloseTime = 4 * 1000;
      Thread.sleep(ledgerCloseTime);

      // Get transaction status.
      RawTransactionStatus transactionStatus = this.getRawTransactionStatus(transactionHash);
      int lastLedgerSequence = transactionStatus.getLastLedgerSequence();
      if (lastLedgerSequence == 0) {
        throw new XrpException(
                XrpExceptionType.UNKNOWN,
                "The transaction did not have a lastLedgerSequence field so transaction status cannot be reliably "
                        + "determined."
        );
      }

      // Decode the sending address to a classic address for use in determining the last ledger sequence.
      // An invariant of `getLatestValidatedLedgerSequence` is that the given input address (1) exists when the method
      // is called and (2) is in a classic address form.
      //
      // The sending address should always exist, except in the case where it is deleted. A deletion would supersede the
      // transaction in flight, either by:
      // 1) Consuming the nonce sequence number of the transaction, which would effectively cancel the transaction
      // 2) Occur after the transaction has settled which is an unlikely enough case that we ignore it.
      //
      // This logic is brittle and should be replaced when we have an RPC that can give us this data.
      ClassicAddress classicAddress = Utils.decodeXAddress(sender.getAddress());
      if (classicAddress == null) {
        throw new XrpException(
                XrpExceptionType.UNKNOWN,
                "The source wallet reported an address which could not be decoded to a classic address"
        );
      }
      String sourceClassicAddress = classicAddress.address();

      // Retrieve the latest ledger index.
      int latestLedgerSequence = this.getLatestValidatedLedgerSequence(sourceClassicAddress);

      // Poll until the transaction is validated, or until the lastLedgerSequence has been passed.
      while (latestLedgerSequence <= lastLedgerSequence && !transactionStatus.getValidated()) {
        Thread.sleep(ledgerCloseTime);

        latestLedgerSequence = this.getLatestValidatedLedgerSequence(sourceClassicAddress);
        transactionStatus = this.getRawTransactionStatus(transactionHash);
      }

      return transactionStatus;
    } catch (InterruptedException e) {
      throw new XrpException(
              XrpExceptionType.UNKNOWN,
              "Reliable transaction submission project was interrupted."
      );
    }
  }
}