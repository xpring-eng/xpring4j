package io.xpring.xpring;

import io.xpring.GRPCResult;
import io.xpring.payid.*;
import io.xpring.payid.fakes.FakePayIDClient;
import io.xpring.xrpl.*;
import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.*;

public class XpringClientTest {
    /** Default values for the {@link FakeXRPClient}. These values must be provided but are not varied in testing. */
    public static final BigInteger FAKE_BALANCE_VALUE = new BigInteger("10");
    public static final TransactionStatus FAKE_TRANSACTION_STATUS_VALUE = TransactionStatus.SUCCEEDED;
    public static final Integer FAKE_LAST_LEDGER_SEQUENCE_VALUE = 10;
    public static final Boolean FAKE_ACCOUNT_EXISTS_VALUE = true;

    /** An amount to send. */
    public static final BigInteger AMOUNT = new BigInteger("10");

    /** A Pay ID to resolve. */
    public static final String PAY_ID = "$xpring.money/georgewashington";

    /** A {@link Wallet}. */
    // TODO(keefertaylor): Break out to separate PR?
    public Wallet wallet = new FakeWallet("01234567890");

    /** Exceptions to throw. */
    // TODO(keefertaylor): Refactor result
    public static final PayIDException PAY_ID_EXCEPTION = new PayIDException(PayIDExceptionType.UNKNOWN, "Test PayID error");
    public static final XpringException XRP_EXCEPTION =  new XpringException("Test XRP Error");

    @Test
    public void testSendSuccess() throws PayIDException, XpringException {
        // GIVEN a XpringClient composed of a fake PayIDClient and a fake XRPClient which will both succeed.
        String expectedTransactionHash = "deadbeefdeadbeefdeadbeef";
        XRPClientInterface xrpClient = new FakeXRPClient(
                FAKE_BALANCE_VALUE,
                FAKE_TRANSACTION_STATUS_VALUE,
                expectedTransactionHash,
                FAKE_LAST_LEDGER_SEQUENCE_VALUE,
                FAKE_ACCOUNT_EXISTS_VALUE
        );

        String fakeResolvedPayID = "r123";
        PayIDClientInterface payIDClient = new FakePayIDClient(GRPCResult.ok(fakeResolvedPayID));

        XpringClient xpringClient = new XpringClient(payIDClient, xrpClient);

        // WHEN XRP is sent to the Pay ID.
        String transactionHash = xpringClient.send(AMOUNT, PAY_ID, this.wallet);

        // THEN the returned hash is correct and no error was thrown.
        assertEquals(transactionHash, expectedTransactionHash);
    }

    // TODO(keefertaylor): Add tests for error cases.
}