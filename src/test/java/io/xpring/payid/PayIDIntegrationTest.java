package io.xpring.payid;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class PayIDIntegrationTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testXRPAddressForPayIDKnownAddressMainnet() throws PayIDException {
        // GIVEN a Pay ID that will resolve on Mainnet.
        PayIDClient payIDClient = new PayIDClient(XRPLNetwork.MAIN);
        String payID = "$dev.payid.xpring.money/hbergren";

        // WHEN it is resolved to an XRP address
        String xrpAddress = payIDClient.xrpAddressForPayID(payID);

        // THEN the address is the expected value.
        assertEquals(xrpAddress, "X7zmKiqEhMznSXgj9cirEnD5sWo3iZSbeFRexSFN1xZ8Ktn");
    }

    @Test
    public void testXRPAddressForPayIDKnownAddressTestnet() throws PayIDException {
        // GIVEN a Pay ID that will resolve on Testnet.
        PayIDClient payIDClient = new PayIDClient(XRPLNetwork.TEST);
        String payID = "$dev.payid.xpring.money/hbergren";

        // WHEN it is resolved to an XRP address
        String xrpAddress = payIDClient.xrpAddressForPayID(payID);

        // THEN the address is the expected value.
        assertEquals(xrpAddress, "TVacixsWrqyWCr98eTYP7FSzE9NwupESR4TrnijN7fccNiS");
    }

    @Test
    public void testXRPAddressForPayIDKnownAddressDevnet() throws PayIDException {
        // GIVEN a Pay ID that will not resolve on Devnet.
        PayIDClient payIDClient = new PayIDClient(XRPLNetwork.DEV);
        String payID = "$dev.payid.xpring.money/hbergren";

        // WHEN it is resolved to an XRP address THEN a PayID is thrown.
        // TODO(keefertaylor): Tighten this condition to verify the exception is as expected.
        expectedException.expect(PayIDException.class);
        payIDClient.xrpAddressForPayID(payID);
    }
}
