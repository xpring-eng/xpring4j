package io.xpring.payid.fakes;

import io.xpring.common.Result;
import io.xpring.common.XRPLNetwork;
import io.xpring.payid.PayIDClientInterface;
import io.xpring.payid.PayIDException;

/**
 * Fakes a PayID client.
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public class FakePayIDClient implements PayIDClientInterface {
  /**
   * The network this PayID client resolves on.
   */
  private XRPLNetwork network;

  /**
   * Results from method calls.
   */
  private Result<String, PayIDException> xrpAddressForPayIDResult;

  /**
   * Initialize a new fake Pay ID client.
   *
   * @param network                  The network that addresses will be resolved on.
   * @param xrpAddressForPayIDResult The result that will be returned from `xrpAddressForPayID`.
   */
  public FakePayIDClient(XRPLNetwork network, Result<String, PayIDException> xrpAddressForPayIDResult) {
    this.network = network;
    this.xrpAddressForPayIDResult = xrpAddressForPayIDResult;
  }

  @Override
  public XRPLNetwork getNetwork() {
    return this.network;
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