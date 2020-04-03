package io.xpring.xpring;

import io.xpring.common.XRPLNetwork;
import io.xpring.payid.PayIDClient;
import io.xpring.payid.PayIDException;
import io.xpring.xrpl.Wallet;
import io.xpring.xrpl.XRPClient;
import io.xpring.xrpl.XRPException;
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
    public static final XRPClient XRP_CLIENT = new XRPClient("test.xrp.xpring.io:50051", XRPLNetwork.TEST);

    @Test
    public void testSendXRP() throws XpringException, XRPException, PayIDException {
        // GIVEN a Pay ID that will resolve and a wallet with a balance on TestNet.
        XpringClient xpringClient = new XpringClient(PAY_ID_CLIENT, XRP_CLIENT);
        String payID = "$dev.payid.xpring.money/hbergren";
        Wallet wallet = new Wallet("snYP7oArxKepd3GPDcrjMsJYiJeJB");

        // WHEN XRP is sent to the Pay ID.
        String transactionHash = xpringClient.send(new BigInteger("10"), payID, wallet);

        // THEN a transaction hash is returned.
        assertNotNull(transactionHash);
    }
}
