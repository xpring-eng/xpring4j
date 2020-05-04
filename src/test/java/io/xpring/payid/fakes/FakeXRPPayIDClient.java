package io.xpring.payid.fakes;

import io.xpring.common.Result;
import io.xpring.common.XRPLNetwork;
import io.xpring.payid.XRPPayIDClientInterface;
import io.xpring.payid.PayIDException;

/**
 * Fakes a PayID client.
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public class FakeXRPPayIDClient implements XRPPayIDClientInterface {
  /**
   * The network this PayID client resolves on.
   */
  private XRPLNetwork xrplNetwork;

  /**
   * Results from method calls.
   */
  private Result<String, PayIDException> xrpAddressForPayIDResult;

  /**
   * Initialize a new fake Pay ID client.
   *
   * @param xrplNetwork                  The network that addresses will be resolved on.
   * @param xrpAddressForPayIDResult The result that will be returned from `xrpAddressForPayID`.
   */
  public FakeXRPPayIDClient(XRPLNetwork xrplNetwork, Result<String, PayIDException> xrpAddressForPayIDResult) {
    this.xrplNetwork = xrplNetwork;
    this.xrpAddressForPayIDResult = xrpAddressForPayIDResult;
  }

  @Override
  public XRPLNetwork getXRPLNetwork() {
    return this.xrplNetwork;
  }

  @Override
  public String xrpAddressForPayID(String payID) throws PayIDException {
    if (this.xrpAddressForPayIDResult.isError()) {
      throw this.xrpAddressForPayIDResult.getError();
    } else {
      return xrpAddressForPayIDResult.getValue();
    }
  }
}