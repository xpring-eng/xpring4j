package io.xpring.xrpl;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import org.junit.Test;

import org.xrpl.rpc.v1.*;
import io.xpring.xrpl.model.XRPCurrency;

public class ProtocolBufferConversionTest {
    @Test
    public void convertCurrencyTest() {
        // GIVEN a Currency protocol buffer with a code and a name.
        Currency currencyProto = FakeXRPProtobufs.currency;

        // WHEN the protocol buffer is converted to a native Java type.
        XRPCurrency xrpCurrency = XRPCurrency.from(currencyProto);

        // THEN the currency converted as expected.
        assertThat(xrpCurrency.name()).isEqualTo(currencyProto.getName());
        assertThat(xrpCurrency.code()).isEqualTo(currencyProto.getCode().toByteArray());
    }
}