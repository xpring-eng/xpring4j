package io.xpring.xrpl;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import io.xpring.xrpl.model.XRPIssuedCurrency;
import io.xpring.xrpl.model.XRPPath;
import io.xpring.xrpl.model.XRPPathElement;

import org.junit.Test;

import org.xrpl.rpc.v1.*;
import io.xpring.xrpl.model.XRPCurrency;

import java.math.BigInteger;

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
        assertThat(xrpCurrency.code()).isEqualTo(currencyProto.getCode().toByteArray());
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
        assertThat(xrpPathElement.currency()).isEqualTo(XRPCurrency.from(Currency.newBuilder().build()));
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

    // IssuedCurrency

    @Test
    public void convertIssuedCurrencyTest() {
        // GIVEN an issued currency protocol buffer,
        // WHEN the protocol buffer is converted to a native Java type.
        XRPIssuedCurrency xrpIssuedCurrency = XRPIssuedCurrency.from(FakeXRPProtobufs.issuedCurrencyAmount);

        // THEN the issued currency converted as expected.
        assertThat(xrpIssuedCurrency.currency())
                .isEqualTo(XRPCurrency.from(FakeXRPProtobufs.issuedCurrencyAmount.getCurrency()));

        assertThat(xrpIssuedCurrency.issuer())
                .isEqualTo(FakeXRPProtobufs.issuedCurrencyAmount.getIssuer().getAddress());

        assertThat(xrpIssuedCurrency.value()).isEqualTo(new BigInteger(FakeXRPProtobufs.issuedCurrencyAmount.getValue()));
    }

    @Test
    public void convertIssuedCurrencyWithBadValueTest() {
        // GIVEN an issued currency protocol buffer with a non numeric value
        // WHEN the protocol buffer is converted to a native Java type.
        XRPIssuedCurrency xrpIssuedCurrency = XRPIssuedCurrency.from(FakeXRPProtobufs.invalidIssuedCurrencyAmount);

        // THEN the result is null
        assertThat(xrpIssuedCurrency).isNull();
    }


    // CurrencyAmount

    it('Convert CurrencyAmount with drops', function(): void {
        // GIVEN a currency amount protocol buffer with an XRP amount.
    const drops = '10'
    const currencyAmountProto = new CurrencyAmount()
    const xrpDropsAmountProto = new XRPDropsAmount()
        xrpDropsAmountProto.setDrops(drops)
        currencyAmountProto.setXrpAmount(xrpDropsAmountProto)

        // WHEN the protocol buffer is converted to a native TypeScript type.
    const currencyAmount = XRPCurrencyAmount.from(currencyAmountProto)

        // THEN the result has drops set and no issued amount.
        assert.isUndefined(currencyAmount?.issuedCurrency)
        assert.equal(currencyAmount?.drops, drops)
    })

    it('Convert CurrencyAmount with Issued Currency', function(): void {
        // GIVEN a currency amount protocol buffer with an issued currency amount.
    const currencyAmountProto = new CurrencyAmount()
        currencyAmountProto.setIssuedCurrencyAmount(testIssuedCurrency)

        // WHEN the protocol buffer is converted to a native TypeScript type.
    const currencyAmount = XRPCurrencyAmount.from(currencyAmountProto)

        // THEN the result has an issued currency set and no drops amount.
        assert.deepEqual(
                currencyAmount?.issuedCurrency,
                XRPIssuedCurrency.from(testIssuedCurrency),
    )
        assert.isUndefined(currencyAmount?.drops)
    })

    it('Convert CurrencyAmount with bad inputs', function(): void {
        // GIVEN a currency amount protocol buffer with no amounts
    const currencyAmountProto = new CurrencyAmount()
        currencyAmountProto.setIssuedCurrencyAmount(testInvalidIssuedCurrency)

        // WHEN the protocol buffer is converted to a native TypeScript type.
    const currencyAmount = XRPCurrencyAmount.from(currencyAmountProto)

        // THEN the result is empty
        assert.isUndefined(currencyAmount)
    })
}