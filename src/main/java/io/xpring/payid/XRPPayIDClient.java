package io.xpring.payid;

import io.xpring.common.XRPLNetwork;
import io.xpring.payid.generated.model.CryptoAddressDetails;
import io.xpring.xrpl.ClassicAddress;
import io.xpring.xrpl.ImmutableClassicAddress;
import io.xpring.xrpl.Utils;

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
   * <p>
   * Note: The returned value will always be in an X-Address format.
   * </p>
   * @see "https://xrpaddress.info"
   *
   * @param payID The payID to resolve for an address.
   * @return An XRP address representing the given PayID.
   * @throws PayIDException if the inputs were invalid.
   */
  public String xrpAddressForPayID(String payID) throws PayIDException {
    CryptoAddressDetails addressDetails = super.addressForPayID(payID);

    // Return address immediately if it is an X-Address.
    String address = addressDetails.getAddress();
    if (Utils.isValidXAddress(address)) {
      return address;
    }

    // Otherwise, build a classic address.
    boolean isTest = this.xrplNetwork != XRPLNetwork.MAIN;
    ImmutableClassicAddress.Builder classicAddressBuilder = ImmutableClassicAddress.builder()
        .address(address)
        .isTest(isTest);
    if (addressDetails.getTag() != null) {
      Integer tag = Integer.valueOf(addressDetails.getTag());
      classicAddressBuilder.tag(tag);
    }
    ClassicAddress classicAddress = classicAddressBuilder.build();

    // Encode and return the classic address to an X-Address.
    String encodedXAddress = Utils.encodeXAddress(classicAddress);
    if (encodedXAddress == null) {
      throw new PayIDException(
          PayIDExceptionType.UNEXPECTED_RESPONSE,
          "The returned address was in an unexpected format"
      );
    }
    return encodedXAddress;
  }
}
