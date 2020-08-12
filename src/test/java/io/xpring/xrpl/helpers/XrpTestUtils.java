package io.xpring.xrpl.helpers;

import io.xpring.common.XrplNetwork;
import io.xpring.xrpl.model.MemoField;
import io.xpring.xrpl.model.XrpMemo;
import io.xpring.xrpl.model.XrpTransaction;
import org.xrpl.rpc.v1.GetAccountTransactionHistoryResponse;
import org.xrpl.rpc.v1.GetTransactionResponse;
import org.xrpl.rpc.v1.Memo;
import org.xrpl.rpc.v1.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Convenience class for utility functions used in test cases for XrpClient infrastructure.
 */
public class XrpTestUtils {
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
