package io.xpring.xrpl;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import io.xpring.xrpl.model.XRPPath;
import io.xpring.xrpl.model.XRPPathElement;
import org.junit.Test;

import org.xrpl.rpc.v1.*;
import io.xpring.xrpl.model.XRPCurrency;

public class ProtocolBufferConversionTest {

    // Currency

    @Test
    public void convertCurrencyTest() {
        // GIVEN a Currency protocol buffer with a code and a name.
        Currency currencyProto = FakeXRPProtobufs.currency;

        // WHEN the protocol buffer is converted to a native Java type.
        XRPCurrency xrpCurrency = XRPCurrency.from(currencyProto);

        // THEN the currency converted as expected.
        assertThat(xrpCurrency.name()).isEqualTo(currencyProto.getName());
        assertThat(xrpCurrency.code()).isEqualTo(currencyProto.getCode());
    }

    // PathElement

    @Test
    public void convertPathElementTest() {
        // GIVEN a PathElement protocol buffer with all fields set.
        Payment.PathElement testPathElementProto = FakeXRPProtobufs.pathElement;

        // WHEN the protocol buffer is converted to a native Java type.
        XRPPathElement xrpPathElement = XRPPathElement.from(testPathElementProto);

        // THEN the currency converted as expected.
        assertThat(xrpPathElement.account()).isEqualTo(testPathElementProto.getAccount().getAddress());
        assertThat(xrpPathElement.currency()).isEqualTo(XRPCurrency.from(testPathElementProto.getCurrency()));
        assertThat(xrpPathElement.issuer()).isEqualTo(testPathElementProto.getIssuer().getAddress());
    }

    @Test
    public void convertPathElementWithNoFieldsTest() {
        // GIVEN a PathElement protocol buffer with no fields set.
        Payment.PathElement emptyPathElementProto = Payment.PathElement.newBuilder().build();

        // WHEN the protocol buffer is converted to a native Java type.
        XRPPathElement xrpPathElement = XRPPathElement.from(emptyPathElementProto);

        // THEN the currency converted as expected.
        assertThat(xrpPathElement.account()).isEmpty();
        assertThat(xrpPathElement.currency()).isNull();
        assertThat(xrpPathElement.issuer()).isEmpty();
    }

    // Path

    @Test
    public void convertPathWithNoPathsTest() {
        // GIVEN a set of paths with zero path elements.
        // WHEN the protocol buffer is converted to a native Java type.
        XRPPath xrpPath = XRPPath.from(FakeXRPProtobufs.emptyPath);

        // Then there are zero paths in the output.
        assertThat(xrpPath.pathElements().size()).isZero();
    }

    @Test
    public void convertPathWithOnePathTest() {
        // GIVEN a set of paths with one path element.
        // WHEN the protocol buffer is converted to a native Java type.
        XRPPath xrpPath = XRPPath.from(FakeXRPProtobufs.pathWithOneElement);

        // THEN there is one path in the output.
        assertThat(xrpPath.pathElements().size()).isOne();
    }

    @Test
    public void convertPathWithManyPathsTest() {
        // GIVEN a set of paths with three path elements.
        // WHEN the protocol buffer is converted to a native Java type.
        XRPPath xrpPath = XRPPath.from(FakeXRPProtobufs.pathWithThreeElements);

        // THEN there are three paths in the output.
        assertThat(xrpPath.pathElements().size()).isEqualTo(3);
    }
}