package io.xpring.xrpl.helpers;

import io.xpring.common.XrplNetwork;
import io.xpring.xrpl.Wallet;
import io.xpring.xrpl.XrpClient;
import io.xpring.xrpl.XrpException;
import io.xpring.xrpl.XrpExceptionType;
import io.xpring.xrpl.helpers.faucet.FaucetAccountResponse;
import io.xpring.xrpl.helpers.faucet.FaucetClient;
import io.xpring.xrpl.model.MemoField;
import io.xpring.xrpl.model.XrpMemo;
import io.xpring.xrpl.model.XrpTransaction;
import okhttp3.HttpUrl;
import org.awaitility.Awaitility;
import org.xrpl.rpc.v1.GetAccountTransactionHistoryResponse;
import org.xrpl.rpc.v1.GetTransactionResponse;
import org.xrpl.rpc.v1.Transaction;

import java.io.IOException;
import java.math.BigInteger;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Convenience class for utility functions used in test cases for XrpClient infrastructure.
 */
public class XrpTestUtils {

  public static final FaucetClient faucetClient =
      FaucetClient.construct(HttpUrl.parse("https://faucet.altnet.rippletest.net"));

  /**
   * Converts a GetAccountTransactionHistoryResponse protocol buffer object into a list of XrpTransaction objects,
   * filtered only for PAYMENT type transactions.
   *
   * @param transactionHistoryResponse protocol buffer object containing an array of GetTransactionResponse objects
   */
  public static List<XrpTransaction> transactionHistoryToPaymentsList(
      GetAccountTransactionHistoryResponse transactionHistoryResponse) {
    List<GetTransactionResponse> getTransactionResponses = transactionHistoryResponse.getTransactionsList();

    // Filter transactions to payments only and convert them to XrpTransactions.
    // If a payment transaction fails conversion, throw an error.
    List<XrpTransaction> payments = new ArrayList<XrpTransaction>();
    for (GetTransactionResponse transactionResponse : getTransactionResponses) {
      Transaction transaction = transactionResponse.getTransaction();
      switch (transaction.getTransactionDataCase()) {
        case PAYMENT: {
          XrpTransaction xrpTransaction = XrpTransaction.from(transactionResponse, XrplNetwork.TEST);
          if (xrpTransaction != null) {
            payments.add(xrpTransaction);
          }
          break;
        }
        default: {
          // Intentionally do nothing, non-payment type transactions are ignored.
        }
      }
    }
    return payments;
  }

  /**
   * Generates a random wallet and funds using the XRPL Testnet faucet.
   */
  public static Wallet randomWalletFromFaucet() throws XrpException, IOException, InterruptedException {
    final Integer timeoutInSeconds = 20;

    String rippledUrl = "test.xrp.xpring.io:50051";
    XrpClient xrpClient = new XrpClient(rippledUrl, XrplNetwork.TEST);

    FaucetAccountResponse response = faucetClient.generateFaucetAccount();
    Awaitility.await()
        .atMost(Duration.ofSeconds(20))
        .pollInterval(Duration.ofSeconds(1))
        .until(() -> xrpClient.accountExists(response.account().xAddress()));

    Wallet wallet = new Wallet(response.account().secret(), true);

    // Wait for the faucet to fund our account or until timeout
    // Waits one second checks if balance has changed
    // If balance doesn't change it will attempt again until timeoutInSeconds
    for (Integer balanceCheckCounter = 0; balanceCheckCounter < timeoutInSeconds; balanceCheckCounter++) {
      // Wait 1 second
      Thread.sleep(1000);

      // Request our current balance
      BigInteger currentBalance;
      try {
        currentBalance = xrpClient.getBalance(wallet.getAddress());
      } catch (Exception exception) {
        currentBalance = new BigInteger("0");
      }
      // If our current balance has changed then return
      if (!currentBalance.equals(BigInteger.ZERO)) {
        return wallet;
      }

      // In the future if we had a tx hash from the faucet
      // We should check the status of the tx which would be more accurate
    }

    // Balance did not update
    throw new XrpException(
            XrpExceptionType.UNKNOWN,
            String.format("Unable to fund address with faucet after waiting %d seconds", timeoutInSeconds)
    );
  }

  private static Optional<MemoField> memoField1 = Optional.of(
          MemoField.builder().value("I forgot to pick up Carl...").isHex(false).build()
  );
  private static Optional<MemoField> memoField2 = Optional.of(
          MemoField.builder().value("jaypeg").isHex(false).build()
  );
  private static Optional<MemoField> memoField3 = Optional.of(
          MemoField.builder().value("meme").isHex(false).build()
  );

  private static Optional<MemoField> blankMemoField = Optional.of(
          MemoField.builder().value("").isHex(false).build()
  );

  public static XrpMemo iForgotToPickUpCarlMemo = XrpMemo.fromMemoFields(memoField1, memoField2, memoField3);

  public static XrpMemo noDataMemo = XrpMemo.fromMemoFields(Optional.empty(), memoField2, memoField3);

  /**
   * Exists because ledger will store value as blank.
   */
  public static XrpMemo expectedNoDataMemo = XrpMemo.fromMemoFields(blankMemoField, memoField2, memoField3);

  public static XrpMemo noFormatMemo = XrpMemo.fromMemoFields(memoField1, Optional.empty(), memoField3);

  /**
   * Exists because ledger will stored value as blank.
   */
  public static XrpMemo expectedNoFormatMemo = XrpMemo.fromMemoFields(memoField1, blankMemoField, memoField3);

  public static XrpMemo noTypeMemo = XrpMemo.fromMemoFields(memoField1, memoField2, Optional.empty());

  /**
   * Exists because ledger will stored value as blank.
   */
  public static XrpMemo expectedNoTypeMemo = XrpMemo.fromMemoFields(memoField1, memoField2, blankMemoField);
}
