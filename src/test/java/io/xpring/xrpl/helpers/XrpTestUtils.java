package io.xpring.xrpl.helpers;

import io.xpring.common.XrplNetwork;
import io.xpring.xrpl.Utils;
import io.xpring.xrpl.Wallet;
import io.xpring.xrpl.XrpClient;
import io.xpring.xrpl.XrpException;
import io.xpring.xrpl.XrpExceptionType;
import io.xpring.xrpl.model.XrpTransaction;
import org.xrpl.rpc.v1.GetAccountTransactionHistoryResponse;
import org.xrpl.rpc.v1.GetTransactionResponse;
import org.xrpl.rpc.v1.Transaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
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

  /**
   * Generates a random wallet and funds using the XRPL Testnet faucet.
   */
  public static Wallet randomWalletFromFaucet() throws XrpException, IOException, InterruptedException {
    Integer timeoutInSeconds = 20;

    Wallet wallet = Wallet.generateRandomWallet(true).getWallet();
    String xAddress = wallet.getAddress();
    String classicAddress = Utils.decodeXAddress(xAddress).address();

    String rippledUrl = "test.xrp.xpring.io:50051";
    XrpClient xrpClient = new XrpClient(rippledUrl, XrplNetwork.TEST);

    // Balance prior to asking for more funds
    BigInteger startingBalance;
    try {
      startingBalance = xrpClient.getBalance(xAddress);
    } catch (Exception exception) {
      startingBalance = new BigInteger("0");
    }
    // Ask the faucet to send funds to the given address
    String faucetURL = "https://faucet.altnet.rippletest.net/accounts";
    URL url = new URL(faucetURL);
    HttpURLConnection con = (HttpURLConnection)url.openConnection();
    con.setRequestMethod("POST");
    con.setRequestProperty("Content-Type", "application/json");
    con.setRequestProperty("Accept", "application/json");
    con.setDoOutput(true);
    String jsonInputString = String.format("{\"destination\": \"%s\"}", classicAddress);

    try(OutputStream os = con.getOutputStream()) {
      byte[] input = jsonInputString.getBytes("utf-8");
      os.write(input, 0, input.length);
    }
    try(BufferedReader br = new BufferedReader(
            new InputStreamReader(con.getInputStream(), "utf-8"))) {
      StringBuilder response = new StringBuilder();
      String responseLine;
      while ((responseLine = br.readLine()) != null) {
        response.append(responseLine.trim());
      }
    }

    // Wait for the faucet to fund our account or until timeout
    // Waits one second checks if balance has changed
    // If balance doesn't change it will attempt again until timeoutInSeconds
    for (Integer balanceCheckCounter = 0; balanceCheckCounter < timeoutInSeconds; balanceCheckCounter++) {
      // Wait 1 second
      Thread.sleep(1000);

      // Request our current balance
      BigInteger currentBalance;
      try {
        currentBalance = xrpClient.getBalance(xAddress);
      } catch (Exception exception) {
        currentBalance = new BigInteger("0");
      }
      // If our current balance has changed then return
      if (!startingBalance.equals(currentBalance)) {
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
}
