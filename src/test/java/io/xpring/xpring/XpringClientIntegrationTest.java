package io.xpring.xpring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import io.xpring.common.XrplNetwork;
import io.xpring.payid.PayIdException;
import io.xpring.payid.XrpPayIdClient;
import io.xpring.xrpl.Wallet;
import io.xpring.xrpl.XrpClient;
import io.xpring.xrpl.XrpException;
import io.xpring.xrpl.helpers.XrpTestUtils;
import io.xpring.xrpl.model.SendXrpDetails;
import io.xpring.xrpl.model.XrpMemo;
import io.xpring.xrpl.model.XrpTransaction;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

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

  @Test(timeout = 20000)
  public void testSendWithDetailsWithMemos() throws XrpException, PayIdException {
    // GIVEN a Pay ID that will resolve and some memos.
    String payID = "alice$dev.payid.xpring.money";
    Wallet wallet = new Wallet("snYP7oArxKepd3GPDcrjMsJYiJeJB");
    BigInteger amount = new BigInteger("10");
    List<XrpMemo> memos = Arrays.asList(
            XrpTestUtils.iForgotToPickUpCarlMemo,
            XrpTestUtils.noDataMemo,
            XrpTestUtils.noFormatMemo,
            XrpTestUtils.noTypeMemo);

    // WHEN XRP is sent to the Pay ID, including a memo.
    SendXrpDetails sendXrpDetails = SendXrpDetails.builder()
                                                  .amount(amount)
                                                  .destination(payID)
                                                  .sender(wallet)
                                                  .memos(memos)
                                                  .build();
    String transactionHash = XPRING_CLIENT.sendWithDetails(sendXrpDetails);

    // THEN a transaction hash is returned and the memos are present and correct.
    assertNotNull(transactionHash);

    XrpTransaction transaction = XRP_CLIENT.getPayment(transactionHash);

    assertEquals(transaction.memos(), Arrays.asList(
            XrpTestUtils.iForgotToPickUpCarlMemo,
            XrpTestUtils.expectedNoDataMemo,
            XrpTestUtils.expectedNoFormatMemo,
            XrpTestUtils.expectedNoTypeMemo
          )
    );
  }
}
