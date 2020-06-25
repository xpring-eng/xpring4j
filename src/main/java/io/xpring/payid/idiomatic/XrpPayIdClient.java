package io.xpring.payid.idiomatic;

import io.xpring.common.idiomatic.XrplNetwork;
import io.xpring.payid.PayIDException;
import io.xpring.payid.generated.model.CryptoAddressDetails;
import io.xpring.xrpl.ClassicAddress;
import io.xpring.xrpl.ImmutableClassicAddress;
import io.xpring.xrpl.Utils;

/**
 * Provides functionality for XRP in the PayID protocol.
 */
public class XrpPayIdClient extends PayIdClient implements XrpPayIdClientInterface {
  /**
   * The XRP Ledger network that this client attaches to.
   */
  private XrplNetwork xrplNetwork;

  /**
   * Get the XRP Ledger network that this client attaches to.
   *
   * @return The XRP Ledger network that this client attaches to.
   */
  public XrplNetwork getXrplNetwork() {
    return this.xrplNetwork;
  }

  /**
   * Construct a new XrpPayIdClient.
   *
   * @param xrplNetwork The XRP Ledger network that this client attaches to.
   */
  public XrpPayIdClient(XrplNetwork xrplNetwork) {
    super();

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
   * @param payId The PayID to resolve for an address.
   * @return An XRP address representing the given PayID.
   * @throws PayIdException if the inputs were invalid.
   */
  public String xrpAddressForPayId(String payId) throws PayIDException, PayIdException {
    CryptoAddressDetails addressDetails = super.cryptoAddressForPayId(
        payId,
        "xrpl-" + this.xrplNetwork.getNetworkName()
    );

    // Return address immediately if it is an X-Address.
    String address = addressDetails.getAddress();
    if (Utils.isValidXAddress(address)) {
      return address;
    }

    // Otherwise, build a classic address.
    boolean isTest = this.xrplNetwork != XrplNetwork.MAIN;
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
      throw new PayIdException(
          PayIdExceptionType.UNEXPECTED_RESPONSE,
          "The returned address was in an unexpected format"
      );
    }
    return encodedXAddress;
  }
}
