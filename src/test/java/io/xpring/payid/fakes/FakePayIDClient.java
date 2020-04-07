package io.xpring.payid.fakes;

import io.xpring.common.Result;
import io.xpring.payid.PayIDClientInterface;
import io.xpring.payid.PayIDException;

/**
 * Fakes a PayID client.
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public class FakePayIDClient implements PayIDClientInterface {
  /**
   * Results from method calls.
   */
  @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
  private Result<String, PayIDException> xrpAddressForPayIDResult;

  /**
   * Initialize a new fake Pay ID client.
   *
   * @param xrpAddressForPayIDResult The result that will be returned from `xrpAddressForPayID`.
   */
  @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
  public FakePayIDClient(Result<String, PayIDException> xrpAddressForPayIDResult) {
    this.xrpAddressForPayIDResult = xrpAddressForPayIDResult;
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