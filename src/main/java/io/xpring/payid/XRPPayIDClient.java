package io.xpring.payid;

import io.xpring.common.XRPLNetwork;
import io.xpring.payid.generated.model.CryptoAddressDetails;

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

  /**
   * Resolve the given PayID to an XRP Address.
   *
   * @param payID The payID to resolve for an address.
   * @return An XRP address representing the given PayID.
   * @throws PayIDException if the inputs were invalid.
   */
  public String xrpAddressForPayID(String payID) throws PayIDException {
    // TODO(keefertaylor): Ensure the address is in X-Address format.
    CryptoAddressDetails addressDetails = super.addressForPayID(payID);
    return addressDetails.getAddress();
  }

}
