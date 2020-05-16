package io.xpring.xrpl;

import io.xpring.common.XRPLNetwork;
import io.xpring.xrpl.model.XRPTransaction;

import java.math.BigInteger;
import java.util.List;

@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public interface XRPClientInterface {
  /**
   * Retrieve the network that this XRPClient connects to.
   *
   * @return The {@link XRPLNetwork} of an {@link XRPClientInterface}
   */
  public XRPLNetwork getNetwork();

  /**
   * Get the balance of the specified account on the XRP Ledger.
   *
   * @param xrplAccountAddress The X-Address to retrieve the balance for.
   * @return A {@link BigInteger} with the number of drops in this account.
   * @throws XRPException If the given inputs were invalid.
   */
  BigInteger getBalance(final String xrplAccountAddress) throws XRPException;

  /**
   * Retrieve the transaction status for a Payment given transaction hash.
   * <p>
   * Note: This method will only work for Payment type transactions which do not have the tf_partial_payment attribute
   * set.
   * See: https://xrpl.org/payment.html#payment-flags
   * </p>
   * @param transactionHash The hash of the transaction.
   * @return The status of the given transaction.
   * @throws XRPException If the given inputs were invalid.
   */
  TransactionStatus getPaymentStatus(String transactionHash) throws XRPException;

  /**
   * Transact XRP between two accounts on the ledger.
   *
   * @param amount             The number of drops of XRP to send.
   * @param destinationAddress The X-Address to send the XRP to.
   * @param sourceWallet       The {@link Wallet} which holds the XRP.
   * @return A transaction hash for the payment.
   * @throws XRPException If the given inputs were invalid.
   */
  public String send(BigInteger amount, String destinationAddress, Wallet sourceWallet) throws XRPException;

  /**
   * Return the history of payments for the given account.
   * <p>
   * Note: This method only works for payment type transactions. See "https://xrpl.org/payment.html".
   * Note: This method only returns the history that is contained on the remote node,
   * which may not contain a full history of the network.
   * </p>
   * @param address The address (account) for which to retrieve payment history.
   * @return An array of transactions associated with the account.
   * @throws XRPException If there was a problem communicating with the XRP Ledger.
   */
  public List<XRPTransaction> paymentHistory(String address) throws XRPException;

  /**
   * Retrieve the transaction corresponding to the given transaction hash.
   *
   * Note: This method can return transactions that are not included in a fully validated ledger.
   *       See the `validated` field to make this distinction.
   *
   * @param transactionHash The hash of the transaction to retrieve.
   * @return An XRPTransaction object representing an XRP Ledger transaction.
   * @throws XRPException If the transaction hash was invalid.
   */
  public XRPTransaction getTransaction(String transactionHash) throws XRPException;
}
