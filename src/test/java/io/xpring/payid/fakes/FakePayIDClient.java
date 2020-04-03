package io.xpring.payid.fakes;

import io.xpring.payid.PayIDClientInterface;
import io.xpring.payid.PayIDException;
import io.xpring.common.Result;

/**
 * Fakes a PayID client.
 */
public class FakePayIDClient implements PayIDClientInterface {
    /** Results from method calls. */
    private Result<String, PayIDException> xrpAddressForPayIDResult;

    /**
     * Initialize a new fake Pay ID client.
     *
     * @param xrpAddressForPayIDResult The result that will be returned from `xrpAddressForPayID`.
     */
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