package io.xpring.payid.fakes;

import io.xpring.common.Result;
import io.xpring.common.XrplNetwork;
import io.xpring.payid.PayIdException;
import io.xpring.payid.XrpPayIdClientInterface;

/**
 * Fakes a PayID client.
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public class FakeXRPPayIDClient implements XrpPayIdClientInterface {
  /**
   * The network this PayID client resolves on.
   */
  private XrplNetwork xrplNetwork;

  /**
   * Results from method calls.
   */
  private Result<String, PayIdException> xrpAddressForPayIDResult;

  /**
   * Initialize a new fake Pay ID client.
   *
   * @param xrplNetwork                  The network that addresses will be resolved on.
   * @param xrpAddressForPayIDResult The result that will be returned from `xrpAddressForPayID`.
   */
  public FakeXRPPayIDClient(XrplNetwork xrplNetwork, Result<String, PayIdException> xrpAddressForPayIDResult) {
    this.xrplNetwork = xrplNetwork;
    this.xrpAddressForPayIDResult = xrpAddressForPayIDResult;
  }

  @Override
  public XrplNetwork getXrplNetwork() {
    return this.xrplNetwork;
  }

  @Override
  public String xrpAddressForPayId(String payID) throws PayIdException {
    if (this.xrpAddressForPayIDResult.isError()) {
      throw this.xrpAddressForPayIDResult.getError();
    } else {
      return xrpAddressForPayIDResult.getValue();
    }
  }
}