package io.xpring.payid;

import io.xpring.common.XRPLNetwork;

/**
 * Provides functionality for XRP in the PayID protocol.
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public class XRPPayIDClient extends PayIDClient implements XRPPayIDClientInterface {
  /**
   * The XRP Ledger network that this client attaches to.
   */
  private XRPLNetwork xrplNetwork;

  /**
   * Get the XRP Ledger network that this client attaches to.
   *
   * @return The XRP Ledger network that this client attaches to.
   */
  public XRPLNetwork getXRPLNetwork() {
    return this.xrplNetwork;
  }

  /**
   * Construct a new XRPPayIDClient.
   *
   * @param xrplNetwork The XRP Ledger network that this client attaches to.
   */
  public XRPPayIDClient(XRPLNetwork xrplNetwork) {
    super("xrpl-" + xrplNetwork.getNetworkName());

    this.xrplNetwork = xrplNetwork;
  }
}