package io.xpring.xrpl;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import org.junit.Test;
import java.io.IOException;
import java.math.BigInteger;

import io.xpring.xrpl.model.XRPCurrency;

public class ProtobufConversionTest {
    @Test
    public void convertCurrencyTest() {
        XRPCurrency xrpCurrency = XRPCurrency.from(FakeXRPProtobufs.currency);
    }
}