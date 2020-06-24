package io.xpring.xpring;

import io.xpring.payid.PayIdException;
import io.xpring.payid.XrpPayIdClientInterface;
import io.xpring.xrpl.Wallet;
import io.xpring.xrpl.XrpClientInterface;
import io.xpring.xrpl.XrpException;

import java.math.BigInteger;

/**
 * Composes interactions of Xpring services.
 */
public class XpringClient {
  /**
   * A {@link XrpPayIdClientInterface} used to interact with the Pay ID protocol.
   */
  @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
  private XrpPayIdClientInterface payIDClient;

  /**
   * A {@link XrpClientInterface} used to interact with the XRP Ledger protocol.
   */
  private XrpClientInterface xrpClient;

  /**
   * Create a new XpringClient.
   *
   * @param payIDClient A Pay ID Client used to interact with the Pay ID protocol.
   * @param xrpClient   An XRP Client used to interact with the XRP Ledger protocol.
   */
  @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
  public XpringClient(XrpPayIdClientInterface payIDClient, XrpClientInterface xrpClient) {
    // TODO(keefertaylor): Verify that given inputs are on the same network.
    this.payIDClient = payIDClient;
    this.xrpClient = xrpClient;
  }

  /**
   * Transact XRP between two accounts on the ledger.
   *
   * @param amount           The number of drops of XRP to send.
   * @param destinationPayID A destination Pay ID to send the drops to.
   * @param sourceWallet     The {@link Wallet} which holds the XRP.
   * @return A transaction hash for the payment.
   * @throws PayIdException If there was a problem resolving the Pay ID.
   * @throws XrpException   If there was a problem sending XRP.
   */
  public String send(
      final BigInteger amount,
      final String destinationPayID,
      final Wallet sourceWallet
  ) throws PayIdException, XrpException {
    // Resolve the destination address to an XRP address.
    String destinationAddress = this.payIDClient.xrpAddressForPayId(destinationPayID);

    // Transact XRP to the resolved address.
    return this.xrpClient.send(amount, destinationAddress, sourceWallet);
  }
}
