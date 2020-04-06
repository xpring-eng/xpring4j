package io.xpring.payid;

import org.interledger.spsp.PaymentPointer;
import org.junit.Test;

import static org.junit.Assert.*;

public class PayIDUtilsTest {
    @Test
    public void testParsePaymentPointerHostAndPath() {
        // GIVEN a payment pointer with a host and a path.
        String rawPaymentPointer = "$example.com/foo";

        // WHEN it is parsed to a PaymentPointer object
        PaymentPointer paymentPointer = PayIDUtils.parsePayID(rawPaymentPointer);

        // THEN the host and path are set correctly.
        assertEquals(paymentPointer.host(), "example.com");
        assertEquals(paymentPointer.path(), "/foo");
    }

    @Test
    public void testParsePaymentPointerWithWellKnownPath() {
        // GIVEN a payment pointer with a well known path.
        String rawPaymentPointer = "$example.com";

        // WHEN it is parsed to a PaymentPointer object
        PaymentPointer paymentPointer = PayIDUtils.parsePayID(rawPaymentPointer);

        // THEN the host and path are set correctly.
        assertEquals(paymentPointer.host(), "example.com");
        assertEquals(paymentPointer.path(), PaymentPointer.WELL_KNOWN);
    }

    @Test
    public void testParsePaymentPointerWithWellKnownPathAndTrailingSlash() {
        // GIVEN a payment pointer with a well known path and a trailing slash.
        String rawPaymentPointer = "$example.com";

        // WHEN it is parsed to a PaymentPointer object
        PaymentPointer paymentPointer = PayIDUtils.parsePayID(rawPaymentPointer);

        // THEN the host and path are set correctly.
        assertEquals(paymentPointer.host(), "example.com");
        assertEquals(paymentPointer.path(), PaymentPointer.WELL_KNOWN);
    }

    @Test
    public void testParsePaymentPointerIncorrectPrefix() {
        // GIVEN a payment pointer without a '$' prefix
        String rawPaymentPointer = "example.com/";

        // WHEN it is parsed to a PaymentPointer object
        PaymentPointer paymentPointer = PayIDUtils.parsePayID(rawPaymentPointer);

        // THEN the result is null
        assertNull(paymentPointer);
    }

    @Test
    public void testParsePaymentPointerEmptyHost() {
        // GIVEN a payment pointer without a host.
        String rawPaymentPointer = "$";

        // WHEN it is parsed to a PaymentPointer object
        PaymentPointer paymentPointer = PayIDUtils.parsePayID(rawPaymentPointer);

        // THEN the result is null
        assertNull(paymentPointer);
    }

    @Test
    public void testParsePaymentPointerNonAscii() {
        // GIVEN a payment pointer with non-ascii characters.
        String rawPaymentPointer = "$ZA̡͊͠͝LGΌ IS̯͈͕̹̘̱ͮ TO͇̹̺ͅƝ̴ȳ̳ TH̘Ë͖́̉ ͠P̯͍̭O̚N̐Y̡";

        // WHEN it is parsed to a PaymentPointer object
        PaymentPointer paymentPointer = PayIDUtils.parsePayID(rawPaymentPointer);

        // THEN the result is null
        assertNull(paymentPointer);
    }
}
