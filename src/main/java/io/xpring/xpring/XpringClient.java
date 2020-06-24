package io.xpring.xpring;

import io.xpring.payid.PayIdException;
import io.xpring.payid.XrpPayIdClientInterface;
import io.xpring.xrpl.Wallet;
import io.xpring.xrpl.XRPClientInterface;
import io.xpring.xrpl.XRPException;

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
   * A {@link XRPClientInterface} used to interact with the XRP Ledger protocol.
   */
  private XRPClientInterface xrpClient;

  /**
   * Create a new XpringClient.
   *
   * @param payIDClient A Pay ID Client used to interact with the Pay ID protocol.
   * @param xrpClient   An XRP Client used to interact with the XRP Ledger protocol.
   */
  @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
  public XpringClient(XrpPayIdClientInterface payIDClient, XRPClientInterface xrpClient) {
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
   * @throws XRPException   If there was a problem sending XRP.
   */
  public String send(
      final BigInteger amount,
      final String destinationPayID,
      final Wallet sourceWallet
  ) throws PayIdException, XRPException {
    // Resolve the destination address to an XRP address.
    String destinationAddress = this.payIDClient.xrpAddressForPayId(destinationPayID);

    // Transact XRP to the resolved address.
    return this.xrpClient.send(amount, destinationAddress, sourceWallet);
  }
}
