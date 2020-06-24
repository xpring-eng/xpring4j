package io.xpring.xrpl.helpers;

import io.xpring.common.XrplNetwork;
import io.xpring.xrpl.model.XrpTransaction;
import org.xrpl.rpc.v1.GetAccountTransactionHistoryResponse;
import org.xrpl.rpc.v1.GetTransactionResponse;
import org.xrpl.rpc.v1.Transaction;

import java.util.ArrayList;
import java.util.List;

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
}
