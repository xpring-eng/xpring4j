package io.xpring.xpring;

import static org.junit.Assert.assertNotNull;

import io.xpring.common.XrplNetwork;
import io.xpring.payid.PayIdException;
import io.xpring.payid.XrpPayIdClient;
import io.xpring.xrpl.Wallet;
import io.xpring.xrpl.XrpClient;
import io.xpring.xrpl.XrpException;
import org.junit.Test;

import java.math.BigInteger;

public class XpringClientIntegrationTest {
  /**
   * The network to conduct tests on.
   */
  public static final XrplNetwork NETWORK = XrplNetwork.TEST;

  /**
   * A PayIDClient under test.
   */
  public static final XrpPayIdClient PAY_ID_CLIENT = new XrpPayIdClient(NETWORK);

  /**
   * An XrpClient under test.
   */
  public static final XrpClient XRP_CLIENT = new XrpClient("test.xrp.xpring.io:50051", XrplNetwork.TEST);

  /**
   * A XpringClient under test.
   */
  public static final XpringClient XPRING_CLIENT = new XpringClient(PAY_ID_CLIENT, XRP_CLIENT);

  @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
  @Test
  public void testSendXRP() throws XrpException, PayIdException {
    // GIVEN a Pay ID that will resolve and a wallet with a balance on TestNet.
    String payID = "alice$dev.payid.xpring.money";
    Wallet wallet = new Wallet("snYP7oArxKepd3GPDcrjMsJYiJeJB");

    // WHEN XRP is sent to the Pay ID.
    String transactionHash = XPRING_CLIENT.send(new BigInteger("10"), payID, wallet);

    // THEN a transaction hash is returned.
    assertNotNull(transactionHash);
  }
}
