package io.xpring.payid;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Integration tests for PayID.
 */
public class PayIDIntegrationTest {
    /** The PayIDClient under test. */
    private PayIDClient payIDClient = new PayIDClient();

    /** A Pay ID to resolve. */
    private static final String PAY_ID = "$doug.purdy.im";

    @Test
    public void testResolveToXRP() throws PayIDException {
        String xrpAddress = payIDClient.resolveToXRPAddress(PAY_ID);
        assertEquals(xrpAddress, "r9wmZ8Ctfdcr9gctT7LresUve7vs14ADcz");
    }

    // TODO(keefertaylor): Add a test for a PayID mapping which doesn't exist. https://doug.purdy.im returns 403 errors
    // for paths which do not exist, rather than 404s.
}
