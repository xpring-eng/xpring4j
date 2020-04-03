package io.xpring.xpring;

import io.xpring.common.Result;
import io.xpring.common.XRPLNetwork;
import io.xpring.xrpl.fakes.FakeWallet;
import io.xpring.payid.*;
import io.xpring.payid.fakes.FakePayIDClient;
import io.xpring.xrpl.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.xrpl.rpc.v1.*;

import java.math.BigInteger;

import static org.junit.Assert.*;

public class XpringClientTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    /** Default values for the {@link FakeXRPClient}. These values must be provided but are not varied in testing. */
    public static final XRPLNetwork NETWORK = XRPLNetwork.TEST;
    public static final BigInteger FAKE_BALANCE_VALUE = new BigInteger("10");
    public static final TransactionStatus FAKE_TRANSACTION_STATUS_VALUE = TransactionStatus.SUCCEEDED;
    public static final int FAKE_LAST_LEDGER_SEQUENCE_VALUE = 10;
    private static final RawTransactionStatus DEFAULT_RAW_TRANSACTION_STATUS_VALUE = new RawTransactionStatus(
            GetTransactionResponse.newBuilder().build()
    );
    public static final Boolean FAKE_ACCOUNT_EXISTS_VALUE = true;

    /** An amount to send. */
    public static final BigInteger AMOUNT = new BigInteger("10");

    /** A Pay ID to resolve. */
    public static final String PAY_ID = "$xpring.money/georgewashington";

    /** A {@link Wallet}. */
    public Wallet wallet = new FakeWallet("01234567890");

    /** Exceptions to throw. */
    public static final PayIDException PAY_ID_EXCEPTION = new PayIDException(PayIDExceptionType.UNKNOWN, "Test PayID error");
    public static final XpringException XRP_EXCEPTION =  new XpringException("Test XRP Error");

    public XpringClientTest() throws XpringException {
    }

    @Test
    public void testSendSuccess() throws PayIDException, XpringException {
        // GIVEN a XpringClient composed of a fake PayIDClient and a fake XRPClient which will both succeed.
        String expectedTransactionHash = "deadbeefdeadbeefdeadbeef";
        XRPClientInterface xrpClient = new FakeXRPClient(
                NETWORK,
                Result.ok(FAKE_BALANCE_VALUE),
                Result.ok(FAKE_TRANSACTION_STATUS_VALUE),
                Result.ok(expectedTransactionHash),
                Result.ok(FAKE_LAST_LEDGER_SEQUENCE_VALUE),
                Result.ok(DEFAULT_RAW_TRANSACTION_STATUS_VALUE),
                Result.ok(FAKE_ACCOUNT_EXISTS_VALUE)
        );

        String fakeResolvedPayID = "r123";
        PayIDClientInterface payIDClient = new FakePayIDClient(NETWORK, Result.ok(fakeResolvedPayID));

        XpringClient xpringClient = new XpringClient(payIDClient, xrpClient);

        // WHEN XRP is sent to the Pay ID.
        String transactionHash = xpringClient.send(AMOUNT, PAY_ID, this.wallet);

        // THEN the returned hash is correct and no error was thrown.
        assertEquals(transactionHash, expectedTransactionHash);
    }

    @Test
    public void testSendFailureInPayID() throws PayIDException, XpringException {
        // GIVEN a XpringClient composed of a PayIDClient which will throw an error.
        String expectedTransactionHash = "deadbeefdeadbeefdeadbeef";
        XRPClientInterface xrpClient = new FakeXRPClient(
                NETWORK,
                Result.ok(FAKE_BALANCE_VALUE),
                Result.ok(FAKE_TRANSACTION_STATUS_VALUE),
                Result.ok(expectedTransactionHash),
                Result.ok(FAKE_LAST_LEDGER_SEQUENCE_VALUE),
                Result.ok(DEFAULT_RAW_TRANSACTION_STATUS_VALUE),
                Result.ok(FAKE_ACCOUNT_EXISTS_VALUE)
        );

        PayIDClientInterface payIDClient = new FakePayIDClient(NETWORK, Result.error(PAY_ID_EXCEPTION));

        XpringClient xpringClient = new XpringClient(payIDClient, xrpClient);

        // WHEN XRP is sent to the Pay ID THEN the exception thrown is from Pay ID.
        expectedException.expect(PayIDException.class);
        xpringClient.send(AMOUNT, PAY_ID, this.wallet);
    }

    @Test
    public void testSendFailureInXRP() throws PayIDException, XpringException {
        // GIVEN a XpringClient composed of a XRPClient which will throw an error.
        XRPClientInterface xrpClient = new FakeXRPClient(
                NETWORK,
                Result.ok(FAKE_BALANCE_VALUE),
                Result.ok(FAKE_TRANSACTION_STATUS_VALUE),
                Result.error(XRP_EXCEPTION),
                Result.ok(FAKE_LAST_LEDGER_SEQUENCE_VALUE),
                Result.ok(DEFAULT_RAW_TRANSACTION_STATUS_VALUE),
                Result.ok(FAKE_ACCOUNT_EXISTS_VALUE)
        );

        String fakeResolvedPayID = "r123";
        PayIDClientInterface payIDClient = new FakePayIDClient(NETWORK, Result.ok(fakeResolvedPayID));

        XpringClient xpringClient = new XpringClient(payIDClient, xrpClient);

        // WHEN XRP is sent to the Pay ID THEN the exception thrown is from XRP.
        expectedException.expect(XpringException.class);
        xpringClient.send(AMOUNT, PAY_ID, this.wallet);
    }

    @Test
    public void testSendFailureInBoth() throws PayIDException, XpringException {
        // GIVEN a XpringClient composed of an XRPClient and a PayID client which both throw errors.
        XRPClientInterface xrpClient = new FakeXRPClient(
                NETWORK,
                Result.ok(FAKE_BALANCE_VALUE),
                Result.ok(FAKE_TRANSACTION_STATUS_VALUE),
                Result.error(XRP_EXCEPTION),
                Result.ok(FAKE_LAST_LEDGER_SEQUENCE_VALUE),
                Result.ok(DEFAULT_RAW_TRANSACTION_STATUS_VALUE),
                Result.ok(FAKE_ACCOUNT_EXISTS_VALUE)
        );

        PayIDClientInterface payIDClient = new FakePayIDClient(NETWORK, Result.error(PAY_ID_EXCEPTION));

        XpringClient xpringClient = new XpringClient(payIDClient, xrpClient);

        // WHEN XRP is sent to the Pay ID THEN the exception thrown is from Pay ID.
        expectedException.expect(PayIDException.class);
        xpringClient.send(AMOUNT, PAY_ID, this.wallet);
    }
}