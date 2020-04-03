package io.xpring.xrpl;

import io.xpring.GRPCResult;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.math.BigInteger;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.xrpl.rpc.v1.*;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class ReliableSubmissionXRPClientTest {
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
    private static final RawTransactionStatus DEFAULT_RAW_TRANSACTION_STATUS_VALUE = new RawTransactionStatus(
        GetTransactionResponse.newBuilder()
            .setValidated(true)
            .setTransaction(
                    Transaction.newBuilder()
                    .setLastLedgerSequence(
                            Common.LastLedgerSequence.newBuilder()
                                    .setValue(LAST_LEDGER_SEQUENCE)
                                    .build()
                    )
                    .build()
            )
            .setMeta(
                    Meta.newBuilder().setTransactionResult(
                            TransactionResult.newBuilder()
                                    .setResult(TRANSACTION_STATUS_CODE)
                                    .build()
                    )
            ).build()
    );
    private static final boolean DEFAULT_ACCOUNT_EXISTS_VALUE = true;

    FakeXRPClient fakeXRPClient;
    ReliableSubmissionXRPClient reliableSubmissionXRPClient;
    private ScheduledExecutorService scheduledExecutor;

    @Before
    public void setUp() throws Exception {
        this.scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        this.fakeXRPClient = new FakeXRPClient(
                GRPCResult.ok(DEFAULT_BALANCE_VALUE),
                GRPCResult.ok(DEFAULT_TRANSACTION_STATUS_VALUE),
                GRPCResult.ok(DEFAULT_SEND_VALUE),
                GRPCResult.ok(DEFAULT_LATEST_LEDGER_VALUE),
                GRPCResult.ok(DEFAULT_RAW_TRANSACTION_STATUS_VALUE),
                GRPCResult.ok(DEFAULT_ACCOUNT_EXISTS_VALUE)
        );

        this.reliableSubmissionXRPClient = new ReliableSubmissionXRPClient(fakeXRPClient);
    }

    @Test
    public void testGetBalance() throws XpringException {
        // GIVEN a `ReliableSubmissionClient` decorating a FakeXRPClient WHEN a balance is retrieved
        BigInteger balance = reliableSubmissionXRPClient.getBalance(XRPL_ADDRESS);


        // THEN the result is returned unaltered.
        assertThat(balance).isEqualTo(DEFAULT_BALANCE_VALUE);
    }

    @Test
    public void testGetPaymentStatus() throws XpringException {
        // GIVEN a `ReliableSubmissionClient` decorating a FakeXRPClient WHEN a payment status is retrieved
        TransactionStatus paymentStatus = reliableSubmissionXRPClient.getPaymentStatus(TRANSACTION_HASH);

        // THEN the result is returned unaltered.
        assertThat(paymentStatus).isEqualTo(DEFAULT_TRANSACTION_STATUS_VALUE);
    }

    @Test
    public void testGetLatestValidatedLedgerSequence() throws XpringException {
        // GIVEN a `ReliableSubmissionClient` decorating a FakeXRPClient WHEN the latest ledger sequence is retrieved
        int latestSequence = reliableSubmissionXRPClient.getLatestValidatedLedgerSequence();

        // THEN the result is returned unaltered.
        assertThat(latestSequence).isEqualTo(DEFAULT_LATEST_LEDGER_VALUE);
    }

    @Test
    public void testGetRawTransactionStatus() throws XpringException {
        // GIVEN a `ReliableSubmissionClient` decorating a FakeXRPClient WHEN a raw transaction status is retrieved
        RawTransactionStatus transactionStatus = reliableSubmissionXRPClient.getRawTransactionStatus(TRANSACTION_HASH);

        // THEN the result is returned unaltered.
        assertThat(transactionStatus).isEqualTo(DEFAULT_RAW_TRANSACTION_STATUS_VALUE);
    }

    @Test(timeout=10000)
    public void testSendWithExpiredLedgerSequenceAndUnvalidatedTransaction() throws XpringException {
        // GIVEN A faked latestLedgerSequence number that will increment past the lastLedgerSequence for a transaction
        this.fakeXRPClient.rawTransactionStatusResult = GRPCResult.ok(
                new RawTransactionStatus(
                    GetTransactionResponse.newBuilder()
                            .setValidated(false)
                            .setTransaction(
                                        Transaction.newBuilder()
                                            .setLastLedgerSequence(
                                                    Common.LastLedgerSequence.newBuilder()
                                                            .setValue(LAST_LEDGER_SEQUENCE)
                                                            .build()
                                            )
                                            .build()
                            )
                            .setMeta(
                                    Meta.newBuilder().setTransactionResult(
                                            TransactionResult.newBuilder()
                                                    .setResult(TRANSACTION_STATUS_CODE)
                                                    .build()
                                    )
                            ).build()
            )
        );

        runAfterOneSecond(() -> {
            this.fakeXRPClient.latestValidatedLedgerResult = GRPCResult.ok(LAST_LEDGER_SEQUENCE + 1);
        });

        // WHEN a reliable send is submitted THEN the send reaches a consistent state and returns.
        this.reliableSubmissionXRPClient.send(SEND_AMOUNT, XRPL_ADDRESS, new Wallet(WALLET_SEED));
    }

    @Test(timeout=10000)
    public void testSendWithUnxpiredLedgerSequenceAndValidatedTransaction() throws XpringException {
        // GIVEN A transaction that will validate in one second
        final String transactionStatusCode = "tesSuccess";
        this.fakeXRPClient.rawTransactionStatusResult = GRPCResult.ok(
                new RawTransactionStatus(
                    GetTransactionResponse.newBuilder()
                            .setValidated(false)
                            .setTransaction(
                                    Transaction.newBuilder()
                                            .setLastLedgerSequence(
                                                    Common.LastLedgerSequence.newBuilder()
                                                            .setValue(LAST_LEDGER_SEQUENCE)
                                                            .build()
                                            )
                                            .build()
                            )
                            .setMeta(
                                    Meta.newBuilder().setTransactionResult(
                                            TransactionResult.newBuilder()
                                                    .setResult(transactionStatusCode)
                                                    .build()
                                    )
                            ).build()
            )
        );

        runAfterOneSecond(() -> {
            this.fakeXRPClient.rawTransactionStatusResult = GRPCResult.ok(
                    new RawTransactionStatus(
                        GetTransactionResponse.newBuilder()
                                .setValidated(true)
                                .setTransaction(
                                        Transaction.newBuilder()
                                                .setLastLedgerSequence(
                                                        Common.LastLedgerSequence.newBuilder()
                                                                .setValue(LAST_LEDGER_SEQUENCE)
                                                                .build()
                                                )
                                                .build()
                                )
                                .setMeta(
                                        Meta.newBuilder().setTransactionResult(
                                                TransactionResult.newBuilder()
                                                        .setResult(TRANSACTION_STATUS_CODE)
                                                        .build()
                                        )
                                ).build()
                )
            );
        });

        // WHEN a reliable send is submitted THEN the send reaches a consistent state and returns.
        this.reliableSubmissionXRPClient.send(SEND_AMOUNT, XRPL_ADDRESS, new Wallet(WALLET_SEED));
    }

    @Test
    public void testSendWithNoLastLedgerSequence() throws XpringException {
        // GIVEN a `ReliableSubmissionXRPClient` decorating a `FakeXRPClient` which will return a transaction that did not have a last ledger sequence attached.
        this.fakeXRPClient.rawTransactionStatusResult = GRPCResult.ok(
                new RawTransactionStatus(
                    GetTransactionResponse.newBuilder()
                            .setValidated(false)
                            .setTransaction(
                                    Transaction.newBuilder()
                                            .build()
                            )
                            .setMeta(
                                    Meta.newBuilder().setTransactionResult(
                                            TransactionResult.newBuilder()
                                                    .setResult(TRANSACTION_STATUS_CODE)
                                                    .build()
                                    )
                            ).build()
            )
        );

        // WHEN a reliable send is submitted THEN an error is thrown.
        expectedException.expect(Exception.class);
        this.reliableSubmissionXRPClient.send(SEND_AMOUNT, XRPL_ADDRESS, new Wallet(WALLET_SEED));
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