package io.xpring.xrpl;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.math.BigInteger;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class ReliableSubmissionXpringClientTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static final String XRPL_ADDRESS = "XVwDxLQ4SN9pEBQagTNHwqpFkPgGppXqrMoTmUcSKdCtcK5";
    private static final String TRANSACTION_HASH = "DEADBEEF";
    private static final String WALLET_SEED = "snYP7oArxKepd3GPDcrjMsJYiJeJB";
    private static final BigInteger SEND_AMOUNT = new BigInteger("20");

    private static final int LAST_LEDGER_SEQUENCE = 100;
    private static final String TRANSACTION_STATUS_CODE = "tesSuccess";

    private static final BigInteger DEFAULT_BALANCE_VALUE = new BigInteger("10");
    private static final TransactionStatus DEFAULT_TRANSACTION_STATUS_VALUE = TransactionStatus.SUCCEEDED;
    private static final String DEFAULT_SEND_VALUE = "DEADBEEF";
    private static final int DEFAULT_LATEST_LEDGER_VALUE = 10;
    private static final io.xpring.proto.TransactionStatus DEFAULT_RAW_TRANSACTION_STATUS_VALUE = io.xpring.proto.TransactionStatus.
            newBuilder().
            setValidated(true).
            setTransactionStatusCode(TRANSACTION_STATUS_CODE).
            setLastLedgerSequence(LAST_LEDGER_SEQUENCE).
            build();

    FakeXpringClient fakeXpringClient;
    ReliableSubmissionXpringClient reliableSubmissionXpringClient;
    private ScheduledExecutorService scheduledExecutor;

    @Before
    public void setUp() throws Exception {
        this.scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        this.fakeXpringClient = new FakeXpringClient(
                DEFAULT_BALANCE_VALUE,
                DEFAULT_TRANSACTION_STATUS_VALUE,
                DEFAULT_SEND_VALUE,
                DEFAULT_LATEST_LEDGER_VALUE,
                DEFAULT_RAW_TRANSACTION_STATUS_VALUE
        );

        this.reliableSubmissionXpringClient = new ReliableSubmissionXpringClient(fakeXpringClient);
    }

    @Test
    public void testGetBalance() throws XpringKitException {
        // GIVEN a `ReliableSubmissionClient` decorating a FakeXpringClient WHEN a balance is retrieved
        BigInteger balance = reliableSubmissionXpringClient.getBalance(XRPL_ADDRESS);


        // THEN the result is returned unaltered.
        assertThat(balance).isEqualTo(DEFAULT_BALANCE_VALUE);
    }

    @Test
    public void testGetTransactionStatus() throws XpringKitException {
        // GIVEN a `ReliableSubmissionClient` decorating a FakeXpringClient WHEN a transaction status is retrieved
        TransactionStatus transactionStatus = reliableSubmissionXpringClient.getTransactionStatus(TRANSACTION_HASH);

        // THEN the result is returned unaltered.
        assertThat(transactionStatus).isEqualTo(DEFAULT_TRANSACTION_STATUS_VALUE);
    }

    @Test
    public void testGetLatestValidatedLedgerSequence() throws XpringKitException {
        // GIVEN a `ReliableSubmissionClient` decorating a FakeXpringClient WHEN the latest ledger sequence is retrieved
        int latestSequence = reliableSubmissionXpringClient.getLatestValidatedLedgerSequence();

        // THEN the result is returned unaltered.
        assertThat(latestSequence).isEqualTo(DEFAULT_LATEST_LEDGER_VALUE);
    }

    @Test
    public void testGetRawTransactionStatus() throws XpringKitException {
        // GIVEN a `ReliableSubmissionClient` decorating a FakeXpringClient WHEN a raw transaction status is retrieved
        io.xpring.proto.TransactionStatus transactionStatus = reliableSubmissionXpringClient.getRawTransactionStatus(TRANSACTION_HASH);

        // THEN the result is returned unaltered.
        assertThat(transactionStatus).isEqualTo(DEFAULT_RAW_TRANSACTION_STATUS_VALUE);
    }

    @Test(timeout=10000)
    public void testSendWithExpiredLedgerSequenceAndUnvalidatedTransaction() throws XpringKitException {
        // GIVEN A faked latestLedgerSequence number that will increment past the lastLedgerSequence for a transaction
        this.fakeXpringClient.rawTransactionStatusValue = io.xpring.proto.TransactionStatus.newBuilder()
                .setValidated(false)
                .setLastLedgerSequence(LAST_LEDGER_SEQUENCE)
                .setTransactionStatusCode(TRANSACTION_STATUS_CODE)
                .build();

        runAfterOneSecond(() -> {
            this.fakeXpringClient.latestValidatedLedgerValue = LAST_LEDGER_SEQUENCE + 1;
        });

        // WHEN a reliable send is submitted THEN the send reaches a consistent state and returns.
        this.reliableSubmissionXpringClient.send(SEND_AMOUNT, XRPL_ADDRESS, new Wallet(WALLET_SEED));
    }

    @Test(timeout=10000)
    public void testSendWithUnxpiredLedgerSequenceAndValidatedTransaction() throws XpringKitException {
        // GIVEN A transaction that will validate in one second
        final String transactionStatusCode = "tesSuccess";
        this.fakeXpringClient.rawTransactionStatusValue = io.xpring.proto.TransactionStatus.newBuilder()
                .setValidated(false)
                .setLastLedgerSequence(LAST_LEDGER_SEQUENCE)
                .setTransactionStatusCode(transactionStatusCode)
                .build();

        runAfterOneSecond(() -> {
            this.fakeXpringClient.rawTransactionStatusValue = io.xpring.proto.TransactionStatus.newBuilder()
                    .setValidated(true)
                    .setLastLedgerSequence(LAST_LEDGER_SEQUENCE)
                    .setTransactionStatusCode(TRANSACTION_STATUS_CODE)
                    .build();
        });

        // WHEN a reliable send is submitted THEN the send reaches a consistent state and returns.
        this.reliableSubmissionXpringClient.send(SEND_AMOUNT, XRPL_ADDRESS, new Wallet(WALLET_SEED));
    }

    @Test
    public void testSendWithNoLastLedgerSequence() throws XpringKitException {
        // GIVEN a `ReliableSubmissionXpringClient` decorating a `FakeXpringClient` which will return a transaction that did not have a last ledger sequence attached.
        this.fakeXpringClient.rawTransactionStatusValue = io.xpring.proto.TransactionStatus.newBuilder()
                .setValidated(false)
                .setTransactionStatusCode(TRANSACTION_STATUS_CODE)
                .build();

        // WHEN a reliable send is submitted THEN an error is thrown.
        expectedException.expect(Exception.class);
        this.reliableSubmissionXpringClient.send(SEND_AMOUNT, XRPL_ADDRESS, new Wallet(WALLET_SEED));
    }

    /**
     * Run the given work on an separate thread in after one second.
     *
     * @param work The work to run.
     */
    private void runAfterOneSecond(Runnable work) {
        scheduledExecutor.schedule(work, 1, TimeUnit.SECONDS);
    }
}