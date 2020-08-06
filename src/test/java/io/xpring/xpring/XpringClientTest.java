package io.xpring.xpring;

import static org.junit.Assert.assertEquals;

import io.xpring.common.Result;
import io.xpring.common.XrplNetwork;
import io.xpring.payid.PayIdException;
import io.xpring.payid.PayIdExceptionType;
import io.xpring.payid.XrpPayIdClientInterface;
import io.xpring.payid.fakes.FakeXrpPayIdClient;
import io.xpring.xrpl.FakeXrpClient;
import io.xpring.xrpl.FakeXrpProtobufs;
import io.xpring.xrpl.RawTransactionStatus;
import io.xpring.xrpl.TransactionStatus;
import io.xpring.xrpl.Wallet;
import io.xpring.xrpl.XrpClientInterface;
import io.xpring.xrpl.XrpException;
import io.xpring.xrpl.XrpExceptionType;
import io.xpring.xrpl.fakes.FakeWallet;
import io.xpring.xrpl.model.TransactionResult;
import io.xpring.xrpl.model.XrpTransaction;
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
   * Default values for the {@link FakeXrpClient}. These values must be provided but are not varied in testing.
   */
  public static final BigInteger FAKE_BALANCE_VALUE = new BigInteger("10");
  public static final TransactionStatus FAKE_TRANSACTION_STATUS_VALUE = TransactionStatus.SUCCEEDED;
  public static final int FAKE_LAST_LEDGER_SEQUENCE_VALUE = 10;
  private static final RawTransactionStatus DEFAULT_RAW_TRANSACTION_STATUS_VALUE = new RawTransactionStatus(
      GetTransactionResponse.newBuilder().build()
  );
  public static final List<XrpTransaction> FAKE_PAYMENT_HISTORY_VALUE = new LinkedList<XrpTransaction>();
  public static final Boolean FAKE_ACCOUNT_EXISTS_VALUE = true;
  public static final XrpTransaction FAKE_GET_TRANSACTION_VALUE = XrpTransaction.from(
          FakeXrpProtobufs.getTransactionResponsePaymentAllFields,
          XrplNetwork.TEST
  );
  public static final TransactionResult FAKE_ENABLE_DEPOSIT_AUTH_VALUE =
          TransactionResult.builder()
                            .hash("faketransactionhash")
                            .status(TransactionStatus.SUCCEEDED)
                            .validated(true)
                            .build();

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
  public static final PayIdException PAY_ID_EXCEPTION =
      new PayIdException(PayIdExceptionType.UNKNOWN, "Test PayID error");
  public static final XrpException XRP_EXCEPTION = new XrpException(XrpExceptionType.UNKNOWN, "Test XRP Error");

  public XpringClientTest() throws XrpException {
  }

  @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
  @Test
  public void testSendSuccess() throws PayIdException, XrpException {
    // GIVEN a XpringClient composed of a fake PayIDClient and a fake XRPClient which will both succeed.
    String expectedTransactionHash = "deadbeefdeadbeefdeadbeef";
    XrpClientInterface xrpClient = new FakeXrpClient(
        XrplNetwork.TEST,
        Result.ok(FAKE_BALANCE_VALUE),
        Result.ok(FAKE_TRANSACTION_STATUS_VALUE),
        Result.ok(expectedTransactionHash),
        Result.ok(FAKE_LAST_LEDGER_SEQUENCE_VALUE),
        Result.ok(DEFAULT_RAW_TRANSACTION_STATUS_VALUE),
        Result.ok(FAKE_PAYMENT_HISTORY_VALUE),
        Result.ok(FAKE_ACCOUNT_EXISTS_VALUE),
        Result.ok(FAKE_GET_TRANSACTION_VALUE),
        Result.ok(FAKE_ENABLE_DEPOSIT_AUTH_VALUE)
    );

    String fakeResolvedPayID = "r123";
    XrpPayIdClientInterface payIDClient = new FakeXrpPayIdClient(XrplNetwork.TEST, Result.ok(fakeResolvedPayID));

    XpringClient xpringClient = new XpringClient(payIDClient, xrpClient);

    // WHEN XRP is sent to the Pay ID.
    String transactionHash = xpringClient.send(AMOUNT, PAY_ID, this.wallet);

    // THEN the returned hash is correct and no error was thrown.
    assertEquals(transactionHash, expectedTransactionHash);
  }

  @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
  @Test
  public void testSendFailureInPayID() throws PayIdException, XrpException {
    // GIVEN a XpringClient composed of a PayIDClient which will throw an error.
    String expectedTransactionHash = "deadbeefdeadbeefdeadbeef";
    XrpClientInterface xrpClient = new FakeXrpClient(
        XrplNetwork.TEST,
        Result.ok(FAKE_BALANCE_VALUE),
        Result.ok(FAKE_TRANSACTION_STATUS_VALUE),
        Result.ok(expectedTransactionHash),
        Result.ok(FAKE_LAST_LEDGER_SEQUENCE_VALUE),
        Result.ok(DEFAULT_RAW_TRANSACTION_STATUS_VALUE),
        Result.ok(FAKE_PAYMENT_HISTORY_VALUE),
        Result.ok(FAKE_ACCOUNT_EXISTS_VALUE),
        Result.ok(FAKE_GET_TRANSACTION_VALUE),
        Result.ok(FAKE_ENABLE_DEPOSIT_AUTH_VALUE)
    );

    XrpPayIdClientInterface payIDClient = new FakeXrpPayIdClient(XrplNetwork.TEST, Result.error(PAY_ID_EXCEPTION));

    XpringClient xpringClient = new XpringClient(payIDClient, xrpClient);

    // WHEN XRP is sent to the Pay ID THEN the exception thrown is from Pay ID.
    expectedException.expect(PayIdException.class);
    xpringClient.send(AMOUNT, PAY_ID, this.wallet);
  }

  @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
  @Test
  public void testSendFailureInXRP() throws PayIdException, XrpException {
    // GIVEN a XpringClient composed of a XRPClient which will throw an error.
    XrpClientInterface xrpClient = new FakeXrpClient(
        XrplNetwork.TEST,
        Result.ok(FAKE_BALANCE_VALUE),
        Result.ok(FAKE_TRANSACTION_STATUS_VALUE),
        Result.error(XRP_EXCEPTION),
        Result.ok(FAKE_LAST_LEDGER_SEQUENCE_VALUE),
        Result.ok(DEFAULT_RAW_TRANSACTION_STATUS_VALUE),
        Result.ok(FAKE_PAYMENT_HISTORY_VALUE),
        Result.ok(FAKE_ACCOUNT_EXISTS_VALUE),
        Result.ok(FAKE_GET_TRANSACTION_VALUE),
        Result.ok(FAKE_ENABLE_DEPOSIT_AUTH_VALUE)
    );

    String fakeResolvedPayID = "r123";
    XrpPayIdClientInterface payIDClient = new FakeXrpPayIdClient(XrplNetwork.TEST, Result.ok(fakeResolvedPayID));

    XpringClient xpringClient = new XpringClient(payIDClient, xrpClient);

    // WHEN XRP is sent to the Pay ID THEN the exception thrown is from XRP.
    expectedException.expect(XrpException.class);
    xpringClient.send(AMOUNT, PAY_ID, this.wallet);
  }

  @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
  @Test
  public void testSendFailureInBoth() throws PayIdException, XrpException {
    // GIVEN a XpringClient composed of an XRPClient and a PayID client which both throw errors.
    XrpClientInterface xrpClient = new FakeXrpClient(
        XrplNetwork.TEST,
        Result.ok(FAKE_BALANCE_VALUE),
        Result.ok(FAKE_TRANSACTION_STATUS_VALUE),
        Result.error(XRP_EXCEPTION),
        Result.ok(FAKE_LAST_LEDGER_SEQUENCE_VALUE),
        Result.ok(DEFAULT_RAW_TRANSACTION_STATUS_VALUE),
        Result.ok(FAKE_PAYMENT_HISTORY_VALUE),
        Result.ok(FAKE_ACCOUNT_EXISTS_VALUE),
        Result.ok(FAKE_GET_TRANSACTION_VALUE),
        Result.ok(FAKE_ENABLE_DEPOSIT_AUTH_VALUE)
    );

    XrpPayIdClientInterface payIDClient = new FakeXrpPayIdClient(XrplNetwork.TEST, Result.error(PAY_ID_EXCEPTION));

    XpringClient xpringClient = new XpringClient(payIDClient, xrpClient);

    // WHEN XRP is sent to the Pay ID THEN the exception thrown is from Pay ID.
    expectedException.expect(PayIdException.class);
    xpringClient.send(AMOUNT, PAY_ID, this.wallet);
  }
}
