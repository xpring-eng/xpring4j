package io.xpring.xpring;

import io.xpring.payid.PayIDClient;
import io.xpring.payid.PayIDException;
import io.xpring.payid.XRPLNetwork;
import io.xpring.xrpl.Wallet;
import io.xpring.xrpl.XRPClient;
import io.xpring.xrpl.XpringException;
import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.*;

public class XpringClientIntegrationTest {
    /**
     * The network to conduct tests on.
     */
    public static final XRPLNetwork NETWORK = XRPLNetwork.TEST;

    /**
     * A PayIDClient under test.
     */
    public static final PayIDClient PAY_ID_CLIENT = new PayIDClient(NETWORK);

    /**
     * An XRPClient under test.
     */
    public static final XRPClient XRP_CLIENT = new XRPClient("test.xrp.xpring.io:50051");

    /**
     * A XpringClient under test.
     */
    public static final XpringClient XPRING_CLIENT = new XpringClient(PAY_ID_CLIENT, XRP_CLIENT);

    @Test
    public void testSendXRP() throws XpringException, PayIDException {
        // GIVEN a Pay ID that will resolve and a wallet with a balance on TestNet.
        String payID = "$dev.payid.xpring.money/hbergren";
        Wallet wallet = new Wallet("snYP7oArxKepd3GPDcrjMsJYiJeJB");

        // WHEN XRP is sent to the Pay ID.
        String transactionHash = XPRING_CLIENT.send(new BigInteger("10"), payID, wallet);

        // THEN a transaction hash is returned.
        assertNotNull(transactionHash);
    }
}
