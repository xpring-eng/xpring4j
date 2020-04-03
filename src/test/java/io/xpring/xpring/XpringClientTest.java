package io.xpring.xpring;

import io.xpring.common.Result;
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
                FAKE_BALANCE_VALUE,
                FAKE_TRANSACTION_STATUS_VALUE,
                expectedTransactionHash,
                FAKE_LAST_LEDGER_SEQUENCE_VALUE,
                DEFAULT_RAW_TRANSACTION_STATUS_VALUE,
                FAKE_ACCOUNT_EXISTS_VALUE
        );

        String fakeResolvedPayID = "r123";
        PayIDClientInterface payIDClient = new FakePayIDClient(Result.ok(fakeResolvedPayID));

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
                FAKE_BALANCE_VALUE,
                FAKE_TRANSACTION_STATUS_VALUE,
                expectedTransactionHash,
                FAKE_LAST_LEDGER_SEQUENCE_VALUE,
                DEFAULT_RAW_TRANSACTION_STATUS_VALUE,
                FAKE_ACCOUNT_EXISTS_VALUE
        );

        PayIDClientInterface payIDClient = new FakePayIDClient(Result.error(PAY_ID_EXCEPTION));

        XpringClient xpringClient = new XpringClient(payIDClient, xrpClient);

        // WHEN XRP is sent to the Pay ID THEN an the error thrown is from PayID.
        expectedException.expect(PayIDException.class);
        xpringClient.send(AMOUNT, PAY_ID, this.wallet);
    }

//
//
//    it('send - failure in XRP', function(done): void {
//        // GIVEN a XpringClient composed of an XRPClient which will throw an error.
//    const xrpClient = new FakeXRPClient(
//                fakeBalance,
//                fakePaymentStatus,
//                xrpError,
//                fakeLastLedgerSequenceValue,
//                fakeRawTransactionStatus,
//                fakeAccountExistsResult,
//                fakePaymentHistoryValue,
//                )
//
//    const resolvedXRPAddress = 'r123'
//    const payIDClient = new FakePayIDClient(resolvedXRPAddress)
//
//    const xpringClient = new XpringClient(payIDClient, xrpClient)
//
//        // WHEN XRP is sent to the Pay ID.
//        xpringClient.send(amount, payID, wallet).catch((error) => {
//        // THEN an the error thrown is from XRP.
//        assert.equal(error, xrpError)
//        done()
//    })
//    })
//
//    it('send - failure in both', function(done): void {
//        // GIVEN a XpringClient composed of an XRPClient and a PayID client which both throw errors.
//    const xrpClient = new FakeXRPClient(
//                fakeBalance,
//                fakePaymentStatus,
//                xrpError,
//                fakeLastLedgerSequenceValue,
//                fakeRawTransactionStatus,
//                fakeAccountExistsResult,
//                fakePaymentHistoryValue,
//                )
//
//    const payIDClient = new FakePayIDClient(payIDError)
//
//    const xpringClient = new XpringClient(payIDClient, xrpClient)
//
//        // WHEN XRP is sent to the Pay ID.
//        xpringClient.send(amount, payID, wallet).catch((error) => {
//        // THEN an the error thrown is from PayID.
//        assert.equal(error, payIDError)
//        done()
//    })
//    })
}