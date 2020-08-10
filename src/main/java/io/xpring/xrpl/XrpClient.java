package io.xpring.xrpl;

import io.xpring.common.XrplNetwork;
import io.xpring.xrpl.model.SendXrpDetails;
import io.xpring.xrpl.model.TransactionResult;
import io.xpring.xrpl.model.XrpTransaction;

import java.math.BigInteger;
import java.util.List;

/**
 * A client that can submit transactions to the XRP Ledger.
 *
 * @see "https://xrpl.org"
 */
public class XrpClient implements XrpClientInterface {
  private XrpClientDecorator decoratedClient;

  /**
   * The XRPL Network of the node that this client is communicating with.
   */
  private XrplNetwork network;

  /**
   * Initialize a new client with the given options.
   *
   * @param grpcUrl The remote URL to use for gRPC calls.
   * @param network The network this XRPClient is connecting to.
   */
  public XrpClient(String grpcUrl, XrplNetwork network) {
    XrpClientDecorator defaultXrpClient = new DefaultXrpClient(grpcUrl, network);
    this.decoratedClient = new ReliableSubmissionXrpClient(defaultXrpClient);

    this.network = network;
  }

  /**
   * Retrieve the network that this XrpClient connects to.
   */
  public XrplNetwork getNetwork() {
    return this.network;
  }

  /**
   * Get the balance of the specified account on the XRP Ledger.
   * *
   *
   * @param xrplAccountAddress The X-Address to retrieve the balance for.
   * @return A {@link BigInteger} with the number of drops in this account.
   * @throws XrpException If the given inputs were invalid.
   */
  public BigInteger getBalance(final String xrplAccountAddress) throws XrpException {
    return decoratedClient.getBalance(xrplAccountAddress);
  }

  /**
   * Retrieve the transaction status for a Payment given transaction hash.
   * <p>
   * Note: This method will only work for Payment type transactions which do not have the tf_partial_payment attribute
   * set.
   * See: https://xrpl.org/payment.html#payment-flags
   * </p>
   * @param transactionHash The hash of the transaction.
   * @return The status of the given transaction.
   */
  public TransactionStatus getPaymentStatus(String transactionHash) throws XrpException {
    return decoratedClient.getPaymentStatus(transactionHash);
  }

  /**
   * Send the given amount of XRP from the source wallet to the destination address.
   *
   * @param amount             The number of drops of XRP to send.
   * @param destinationAddress The X-Address to send the XRP to.
   * @param sourceWallet       The {@link Wallet} which holds the XRP.
   * @return A string representing the hash of the submitted transaction.
   * @throws XrpException If the given inputs were invalid.
   */
  public String send(
      final BigInteger amount,
      final String destinationAddress,
      final Wallet sourceWallet
  ) throws XrpException {
    SendXrpDetails sendXrpDetails = SendXrpDetails.builder()
            .amount(amount)
            .destination(destinationAddress)
            .sender(sourceWallet)
            .build();
    return decoratedClient.sendWithDetails(sendXrpDetails);
  }

  /**
   * Send the given amount of XRP from the source wallet to the destination address, allowing
   * for additional details to be specified for use with supplementary features of the XRP ledger.
   *
   * @param sendXrpDetails a {@link SendXrpDetails} wrapper object containing details for constructing a transaction.
   * @return A string representing the hash of the submitted transaction.
   * @throws XrpException If the given inputs were invalid.
   */
  public String sendWithDetails(final SendXrpDetails sendXrpDetails) throws XrpException {
    return decoratedClient.sendWithDetails(sendXrpDetails);
  }

  /**
   * Check if an address exists on the XRP Ledger.
   *
   * @param xrplAccountAddress The address to check the existence of.
   * @return A boolean if the account is on the XRP Ledger.
   * @throws XrpException If the given inputs were invalid.
   */
  public boolean accountExists(final String xrplAccountAddress) throws XrpException {
    return decoratedClient.accountExists(xrplAccountAddress);
  }

  /**
   * Return the history of payments for the given account.
   * <p>
   * Note: This method only works for payment type transactions. See "https://xrpl.org/payment.html".
   * Note: This method only returns the history that is contained on the remote node,
   * which may not contain a full history of the network.
   * </p>
   * @param xrplAccountAddress The address (account) for which to retrieve payment history.
   * @return An array of transactions associated with the account.
   * @throws XrpException If there was a problem communicating with the XRP Ledger.
   */
  public List<XrpTransaction> paymentHistory(String xrplAccountAddress) throws XrpException {
    return decoratedClient.paymentHistory(xrplAccountAddress);
  }

  /**
   * Retrieve the payment transaction corresponding to the given transaction hash.
   * <p>
   * Note: This method can return transactions that are not included in a fully validated ledger.
   *       See the `validated` field to make this distinction.
   * </p>
   * @param transactionHash The hash of the transaction to retrieve.
   * @return An XrpTransaction object representing an XRP Ledger transaction.
   * @throws io.grpc.StatusRuntimeException If the transaction hash was invalid.
   */
  public XrpTransaction getPayment(String transactionHash) throws XrpException {
    return decoratedClient.getPayment(transactionHash);
  }

  /**
   * Enable Deposit Authorization for this XRPL account.
   *
   * <p>@see <a href="https://xrpl.org/depositauth.html">Deposit Authorization</a>
   * </p>
   * @param wallet The wallet associated with the XRPL account enabling Deposit Authorization and that will sign the
   *               request.
   * @return A TransactionResult object that contains the hash of the submitted AccountSet transaction and the
   *          final status of the transaction.
   * @throws XrpException If there was a problem communicating with the XRP Ledger.
   */
  public TransactionResult enableDepositAuth(Wallet wallet) throws XrpException {
    return decoratedClient.enableDepositAuth(wallet);
  }
}
