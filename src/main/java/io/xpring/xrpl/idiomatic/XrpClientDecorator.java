package io.xpring.xrpl.idiomatic;

import io.xpring.xrpl.RawTransactionStatus;
import io.xpring.xrpl.TransactionStatus;
import io.xpring.xrpl.Wallet;
import io.xpring.xrpl.model.idiomatic.XrpTransaction;

import java.math.BigInteger;
import java.util.List;

/**
 * An common interface shared between XRPClient and the internal hierarchy of decorators.
 */
interface XrpClientDecorator {
  /**
   * Get the balance of the specified account on the XRP Ledger.
   *
   * @param xrplAccountAddress The X-Address to retrieve the balance for.
   * @return A {@link BigInteger} with the number of drops in this account.
   * @throws XrpException If the given inputs were invalid.
   */
  BigInteger getBalance(final String xrplAccountAddress) throws XrpException;

  /**
   * Retrieve the transaction status for a Payment given transaction hash.
   * <p>
   * Note: This method will only work for Payment type transactions which do not have the tf_partial_payment attribute
   * set.
   * See: https://xrpl.org/payment.html#payment-flags
   * </p>
   *
   * @param transactionHash The hash of the transaction.
   * @return The status of the given transaction.
   * @throws XrpException If the given inputs were invalid.
   */
  public TransactionStatus getPaymentStatus(String transactionHash) throws XrpException;

  /**
   * Transact XRP between two accounts on the ledger.
   *
   * @param amount             The number of drops of XRP to send.
   * @param destinationAddress The X-Address to send the XRP to.
   * @param sourceWallet       The {@link Wallet} which holds the XRP.
   * @return A transaction hash for the payment.
   * @throws XrpException If the given inputs were invalid.
   */
  String send(
      final BigInteger amount,
      final String destinationAddress,
      final Wallet sourceWallet
  ) throws XrpException;

  /**
   * Retrieve the latest validated ledger sequence on the XRP Ledger.
   * <p>
   * Note: This call will throw if the given account does not exist on the ledger at the current time. It is the
   * *caller's responsibility* to ensure this invariant is met.
   * </p><p>
   * Note: The input address *must* be in a classic address form. Inputs are not checked to this internal method.
   * </p><p>
   * TODO(keefertaylor): The above requirements are onerous, difficult to reason about and the logic of this method is
   * brittle. Replace this method's implementation when rippled supports a `ledger` RPC via gRPC.
   * </p>
   * @param address An address that exists at the current time. The address is unchecked and must be a classic address.
   * @return The index of the latest validated ledger.
   * @throws XrpException If there was a problem communicating with the XRP Ledger.
   */
  int getLatestValidatedLedgerSequence(String address) throws XrpException;

  /**
   * Retrieve the raw transaction status for the given transaction hash.
   *
   * @param transactionHash The hash of the transaction.
   * @return an {@link RawTransactionStatus} containing the raw transaction status.
   * @throws XrpException If the given inputs were invalid.
   */
  RawTransactionStatus getRawTransactionStatus(String transactionHash) throws XrpException;

  /**
   * Return the history of payments for the given account.
   * <p>
   * Note: This method only works for payment type transactions. See "https://xrpl.org/payment.html"
   * Note: This method only returns the history that is contained on the remote node,
   * which may not contain a full history of the network.
   * </p>
   * @param address The address (account) for which to retrieve payment history.
   * @return An array of transactions associated with the account.
   * @throws XrpException If there was a problem communicating with the XRP Ledger.
   */
  List<XrpTransaction> paymentHistory(String address) throws XrpException;

  /**
   * Check if an address exists on the XRP Ledger.
   *
   * @param address The address to check the existence of.
   * @return A boolean if the account is on the XRPLedger.
   * @throws XrpException If the given inputs were invalid.
   */
  boolean accountExists(String address) throws XrpException;

  /**
   * Retrieve the payment transaction corresponding to the given transaction hash.
   * <p>
   * Note: This method can return transactions that are not included in a fully validated ledger.
   *       See the `validated` field to make this distinction.
   * </p>
   * @param transactionHash The hash of the transaction to retrieve.
   * @return An XRPTransaction object representing an XRP Ledger transaction.
   * @throws io.grpc.StatusRuntimeException If the transaction hash was invalid.
   */
  XrpTransaction getPayment(String transactionHash) throws XrpException;
}
