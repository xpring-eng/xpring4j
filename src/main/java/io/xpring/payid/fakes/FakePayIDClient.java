package io.xpring.payid;

public class FakePayIDClient implements PayIDClientInterface {
    public FakePayIDClient() {
    }

    @Override
    public String xrpAddressForPayID(String payID) throws PayIDException {
        return null;
    }
}