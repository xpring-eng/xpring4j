package io.xpring.xrpl;

import org.junit.Test;

import io.xpring.xrpl.model.XRPCurrency;

public class ProtocolBufferConversionTest {
    @Test
    public void convertCurrencyTest() {
        XRPCurrency xrpCurrency = XRPCurrency.from(FakeXRPProtobufs.currency);
    }
}