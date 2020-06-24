package io.xpring.xrpl.helpers;

import io.xpring.xrpl.model.XRPTransaction;
import org.xrpl.rpc.v1.GetAccountTransactionHistoryResponse;
import org.xrpl.rpc.v1.GetTransactionResponse;
import org.xrpl.rpc.v1.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Convenience class for utility functions used in test cases for XRPClient infrastructure.
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public class XRPTestUtils {
  /**
   * Converts a GetAccountTransactionHistoryResponse protocol buffer object into a list of XRPTransaction objects,
   * filtered only for PAYMENT type transactions.
   *
   * @param transactionHistoryResponse protocol buffer object containing an array of GetTransactionResponse objects
   */
  public static List<XRPTransaction> transactionHistoryToPaymentsList(
      GetAccountTransactionHistoryResponse transactionHistoryResponse) {
    List<GetTransactionResponse> getTransactionResponses = transactionHistoryResponse.getTransactionsList();

    // Filter transactions to payments only and convert them to XRPTransactions.
    // If a payment transaction fails conversion, throw an error.
    List<XRPTransaction> payments = new ArrayList<XRPTransaction>();
    for (GetTransactionResponse transactionResponse : getTransactionResponses) {
      Transaction transaction = transactionResponse.getTransaction();
      switch (transaction.getTransactionDataCase()) {
        case PAYMENT: {
          XRPTransaction xrpTransaction = XRPTransaction.from(transactionResponse, XRPLNetwork.TEST);
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
