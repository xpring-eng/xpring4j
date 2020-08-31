package io.xpring.xrpl;

import static org.awaitility.Awaitility.await;

import io.xpring.xrpl.model.SendXrpDetails;
import io.xpring.xrpl.model.TransactionResult;
import io.xpring.xrpl.model.XrpTransaction;
import org.awaitility.core.ConditionFactory;
import org.awaitility.core.ConditionTimeoutException;
import org.hamcrest.Matchers;

import java.math.BigInteger;
import java.time.Duration;
import java.util.List;

public class ReliableSubmissionXrpClient implements XrpClientDecorator {
  // ledgers close every 4 seconds on average but are bucketed into 10s intervals.
  // wait up to 10s with a little wiggle room.
  public static final int MAX_TRX_STATUS_WAIT_SECONDS = 11;
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
    SendXrpDetails sendXrpDetails = SendXrpDetails.builder()
                                                  .amount(amount)
                                                  .destination(destinationAddress)
                                                  .sender(sourceWallet)
                                                  .build();
    return this.sendWithDetails(sendXrpDetails);
  }

  @Override
  public String sendWithDetails(SendXrpDetails sendXrpDetails) throws XrpException {
    String transactionHash = this.decoratedClient.sendWithDetails(sendXrpDetails);
    this.awaitFinalTransactionResult(transactionHash, sendXrpDetails.sender());
    return transactionHash;
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

  @Override
  public TransactionResult enableDepositAuth(Wallet wallet) throws XrpException {
    TransactionResult initialResult = this.decoratedClient.enableDepositAuth(wallet);
    String transactionHash = initialResult.hash();
    RawTransactionStatus finalStatus = this.awaitFinalTransactionResult(transactionHash, wallet);
    return TransactionResult.builder()
                            .hash(initialResult.hash())
                            .status(this.getPaymentStatus(transactionHash))
                            .validated(finalStatus.getValidated())
                            .build();
  }

  private RawTransactionStatus awaitFinalTransactionResult(String transactionHash, Wallet sender) throws XrpException {
    try {
      // Get transaction status.
      final RawTransactionStatus initialTransactionStatus = newTransactionPoller().until(
          () -> this.getRawTransactionStatus(transactionHash),
          Matchers.notNullValue());
      final int lastLedgerSequence = initialTransactionStatus.getLastLedgerSequence();
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

      // Poll until the transaction is validated, or until the lastLedgerSequence has been passed.
      final RawTransactionStatus finalTransactionStatus = newTransactionPoller().until(
          () -> {
            // Retrieve the latest ledger index.
            int latestLedgerSequence = this.getLatestValidatedLedgerSequence(sourceClassicAddress);
            RawTransactionStatus status = this.getRawTransactionStatus(transactionHash);
            if (latestLedgerSequence > lastLedgerSequence || status.getValidated()) {
              return status;
            }
            return null;
          },
          Matchers.notNullValue());
      return finalTransactionStatus;
    } catch (ConditionTimeoutException e) {
      throw new XrpException(
              XrpExceptionType.UNKNOWN,
              "Reliable transaction submission project was interrupted."
      );
    }
  }

  private ConditionFactory newTransactionPoller() {
    return await().atMost(Duration.ofSeconds(MAX_TRX_STATUS_WAIT_SECONDS)).pollInterval(Duration.ofSeconds(1));
  }
}