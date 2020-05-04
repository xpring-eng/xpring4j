package io.xpring.xpring;

import static org.junit.Assert.assertEquals;

import io.xpring.common.Result;
import io.xpring.common.XRPLNetwork;
import io.xpring.payid.XRPPayIDClientInterface;
import io.xpring.payid.PayIDException;
import io.xpring.payid.PayIDExceptionType;
import io.xpring.payid.fakes.FakeXRPPayIDClient;
import io.xpring.xrpl.FakeXRPClient;
import io.xpring.xrpl.RawTransactionStatus;
import io.xpring.xrpl.TransactionStatus;
import io.xpring.xrpl.Wallet;
import io.xpring.xrpl.XRPClientInterface;
import io.xpring.xrpl.XRPException;
import io.xpring.xrpl.XRPExceptionType;
import io.xpring.xrpl.fakes.FakeWallet;
import io.xpring.xrpl.model.XRPTransaction;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.xrpl.rpc.v1.GetTransactionResponse;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

public class XpringClientTest {
  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  /**
   * Default values for the {@link FakeXRPClient}. These values must be provided but are not varied in testing.
   */
  public static final BigInteger FAKE_BALANCE_VALUE = new BigInteger("10");
  public static final TransactionStatus FAKE_TRANSACTION_STATUS_VALUE = TransactionStatus.SUCCEEDED;
  public static final int FAKE_LAST_LEDGER_SEQUENCE_VALUE = 10;
  private static final RawTransactionStatus DEFAULT_RAW_TRANSACTION_STATUS_VALUE = new RawTransactionStatus(
      GetTransactionResponse.newBuilder().build()
  );
  public static final List<XRPTransaction> FAKE_PAYMENT_HISTORY_VALUE = new LinkedList<XRPTransaction>();
  public static final Boolean FAKE_ACCOUNT_EXISTS_VALUE = true;

  /**
   * An amount to send.
   */
  public static final BigInteger AMOUNT = new BigInteger("10");

  /**
   * A Pay ID to resolve.
   */
  public static final String PAY_ID = "$xpring.money/georgewashington";

  /**
   * A {@link Wallet}.
   */
  public Wallet wallet = new FakeWallet("01234567890");

  /**
   * Exceptions to throw.
   */
  public static final PayIDException PAY_ID_EXCEPTION =
      new PayIDException(PayIDExceptionType.UNKNOWN, "Test PayID error");
  public static final XRPException XRP_EXCEPTION = new XRPException(XRPExceptionType.UNKNOWN, "Test XRP Error");

  public XpringClientTest() throws XRPException {
  }

  @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
  @Test
  public void testSendSuccess() throws PayIDException, XRPException {
    // GIVEN a XpringClient composed of a fake PayIDClient and a fake XRPClient which will both succeed.
    String expectedTransactionHash = "deadbeefdeadbeefdeadbeef";
    XRPClientInterface xrpClient = new FakeXRPClient(
        XRPLNetwork.TEST,
        Result.ok(FAKE_BALANCE_VALUE),
        Result.ok(FAKE_TRANSACTION_STATUS_VALUE),
        Result.ok(expectedTransactionHash),
        Result.ok(FAKE_LAST_LEDGER_SEQUENCE_VALUE),
        Result.ok(DEFAULT_RAW_TRANSACTION_STATUS_VALUE),
        Result.ok(FAKE_PAYMENT_HISTORY_VALUE),
        Result.ok(FAKE_ACCOUNT_EXISTS_VALUE)
    );

    String fakeResolvedPayID = "r123";
    XRPPayIDClientInterface payIDClient = new FakeXRPPayIDClient(XRPLNetwork.TEST, Result.ok(fakeResolvedPayID));

    XpringClient xpringClient = new XpringClient(payIDClient, xrpClient);

    // WHEN XRP is sent to the Pay ID.
    String transactionHash = xpringClient.send(AMOUNT, PAY_ID, this.wallet);

    // THEN the returned hash is correct and no error was thrown.
    assertEquals(transactionHash, expectedTransactionHash);
  }

  @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
  @Test
  public void testSendFailureInPayID() throws PayIDException, XRPException {
    // GIVEN a XpringClient composed of a PayIDClient which will throw an error.
    String expectedTransactionHash = "deadbeefdeadbeefdeadbeef";
    XRPClientInterface xrpClient = new FakeXRPClient(
        XRPLNetwork.TEST,
        Result.ok(FAKE_BALANCE_VALUE),
        Result.ok(FAKE_TRANSACTION_STATUS_VALUE),
        Result.ok(expectedTransactionHash),
        Result.ok(FAKE_LAST_LEDGER_SEQUENCE_VALUE),
        Result.ok(DEFAULT_RAW_TRANSACTION_STATUS_VALUE),
        Result.ok(FAKE_PAYMENT_HISTORY_VALUE),
        Result.ok(FAKE_ACCOUNT_EXISTS_VALUE)
    );

    XRPPayIDClientInterface payIDClient = new FakeXRPPayIDClient(XRPLNetwork.TEST, Result.error(PAY_ID_EXCEPTION));

    XpringClient xpringClient = new XpringClient(payIDClient, xrpClient);

    // WHEN XRP is sent to the Pay ID THEN the exception thrown is from Pay ID.
    expectedException.expect(PayIDException.class);
    xpringClient.send(AMOUNT, PAY_ID, this.wallet);
  }

  @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
  @Test
  public void testSendFailureInXRP() throws PayIDException, XRPException {
    // GIVEN a XpringClient composed of a XRPClient which will throw an error.
    XRPClientInterface xrpClient = new FakeXRPClient(
        XRPLNetwork.TEST,
        Result.ok(FAKE_BALANCE_VALUE),
        Result.ok(FAKE_TRANSACTION_STATUS_VALUE),
        Result.error(XRP_EXCEPTION),
        Result.ok(FAKE_LAST_LEDGER_SEQUENCE_VALUE),
        Result.ok(DEFAULT_RAW_TRANSACTION_STATUS_VALUE),
        Result.ok(FAKE_PAYMENT_HISTORY_VALUE),
        Result.ok(FAKE_ACCOUNT_EXISTS_VALUE)
    );

    String fakeResolvedPayID = "r123";
    XRPPayIDClientInterface payIDClient = new FakeXRPPayIDClient(XRPLNetwork.TEST, Result.ok(fakeResolvedPayID));

    XpringClient xpringClient = new XpringClient(payIDClient, xrpClient);

    // WHEN XRP is sent to the Pay ID THEN the exception thrown is from XRP.
    expectedException.expect(XRPException.class);
    xpringClient.send(AMOUNT, PAY_ID, this.wallet);
  }

  @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
  @Test
  public void testSendFailureInBoth() throws PayIDException, XRPException {
    // GIVEN a XpringClient composed of an XRPClient and a PayID client which both throw errors.
    XRPClientInterface xrpClient = new FakeXRPClient(
        XRPLNetwork.TEST,
        Result.ok(FAKE_BALANCE_VALUE),
        Result.ok(FAKE_TRANSACTION_STATUS_VALUE),
        Result.error(XRP_EXCEPTION),
        Result.ok(FAKE_LAST_LEDGER_SEQUENCE_VALUE),
        Result.ok(DEFAULT_RAW_TRANSACTION_STATUS_VALUE),
        Result.ok(FAKE_PAYMENT_HISTORY_VALUE),
        Result.ok(FAKE_ACCOUNT_EXISTS_VALUE)
    );

    XRPPayIDClientInterface payIDClient = new FakeXRPPayIDClient(XRPLNetwork.TEST, Result.error(PAY_ID_EXCEPTION));

    XpringClient xpringClient = new XpringClient(payIDClient, xrpClient);

    // WHEN XRP is sent to the Pay ID THEN the exception thrown is from Pay ID.
    expectedException.expect(PayIDException.class);
    xpringClient.send(AMOUNT, PAY_ID, this.wallet);
  }
}
