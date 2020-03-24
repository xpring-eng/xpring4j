package io.xpring.xrpl;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import org.junit.Test;

import org.xrpl.rpc.v1.*;
import io.xpring.xrpl.model.XRPCurrency;

public class ProtocolBufferConversionTest {

    // Currency

    @Test
    public void convertCurrencyTest() {
        // GIVEN a Currency protocol buffer with a code and a name.
        Currency currencyProto = FakeXRPProtobufs.currency;

        // WHEN the protocol buffer is converted to a native Typescript type.
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

    }

    it('Convert PathElement protobuf with all fields set to XRPPathElement', function(): void {
        // GIVEN a PathElement protocol buffer with all fields set.
    const pathElementProto = testPathElement

        // WHEN the protocol buffer is converted to a native TypeScript type.
    const pathElement = XRPPathElement.from(pathElementProto)

        // THEN the currency converted as expected.
        assert.equal(
                pathElement?.account,
                pathElementProto.getAccount()!.getAddress(),
    )
        assert.deepEqual(
                pathElement?.currency,
                XRPCurrency.from(pathElementProto.getCurrency()!),
    )
        assert.equal(
                pathElement?.issuer,
                pathElementProto.getIssuer()!.getAddress(),
    )
    })

    it('Convert PathElement protobuf with no fields set to XRPPathElement', function(): void {
        // GIVEN a PathElement protocol buffer with no fields set.
    const pathElementProto = new Payment.PathElement()

        // WHEN the protocol buffer is converted to a native TypeScript type.
    const pathElement = XRPPathElement.from(pathElementProto)

        // THEN the currency converted as expected.
        assert.isUndefined(pathElement?.account)
        assert.isUndefined(pathElement?.currency)
        assert.isUndefined(pathElement?.issuer)
    })

    // Path

    it('Convert Path protobuf with no paths to XRPPath', function(): void {
        // GIVEN a set of paths with zero path elements.
    const pathProto = new Payment.Path()

        // WHEN the protocol buffer is converted to a native TypeScript type.
    const path = XRPPath.from(pathProto)

        // THEN there are zero paths in the output.
        assert.equal(path?.pathElements.length, 0)
    })

    it('Convert Path protobuf with one Path to XRPPath', function(): void {
        // GIVEN a set of paths with one path element.
    const pathProto = new Payment.Path()
        pathProto.addElements(testPathElement)

        // WHEN the protocol buffer is converted to a native TypeScript type.
    const path = XRPPath.from(pathProto)

        // THEN there is one path in the output.
        assert.equal(path?.pathElements.length, 1)
    })

    it('Convert Path protobuf with many Paths to XRPPath', function(): void {
        // GIVEN a set of paths with three path elements.
    const pathProto = new Payment.Path()
        pathProto.setElementsList([
                testPathElement,
                testPathElement,
                testPathElement,
    ])

        // WHEN the protocol buffer is converted to a native TypeScript type.
    const path = XRPPath.from(pathProto)

        // THEN there are multiple paths in the output.
        assert.equal(path?.pathElements.length, 3)
    })
}