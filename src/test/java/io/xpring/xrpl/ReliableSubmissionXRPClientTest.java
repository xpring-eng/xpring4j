package io.xpring.xrpl;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import io.xpring.common.Result;
import io.xpring.common.XRPLNetwork;
import io.xpring.xrpl.helpers.XRPTestUtils;
import io.xpring.xrpl.model.XRPTransaction;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.xrpl.rpc.v1.Common.LastLedgerSequence;
import org.xrpl.rpc.v1.GetTransactionResponse;
import org.xrpl.rpc.v1.Meta;
import org.xrpl.rpc.v1.Transaction;
import org.xrpl.rpc.v1.TransactionResult;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
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
                      LastLedgerSequence.newBuilder()
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
  private static final List<XRPTransaction> DEFAULT_PAYMENT_HISTORY_VALUE = XRPTestUtils
      .transactionHistoryToPaymentsList(FakeXRPProtobufs.paymentOnlyGetAccountTransactionHistoryResponse);
  private static final boolean DEFAULT_ACCOUNT_EXISTS_VALUE = true;

  @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
  FakeXRPClient fakeXRPClient;
  ReliableSubmissionXRPClient reliableSubmissionXRPClient;
  private ScheduledExecutorService scheduledExecutor;

  /**
   * Set up test parameters.
   */
  @Before
  public void setUp() throws Exception {
    this.scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
    this.fakeXRPClient = new FakeXRPClient(
        XRPLNetwork.TEST,
        Result.ok(DEFAULT_BALANCE_VALUE),
        Result.ok(DEFAULT_TRANSACTION_STATUS_VALUE),
        Result.ok(DEFAULT_SEND_VALUE),
        Result.ok(DEFAULT_LATEST_LEDGER_VALUE),
        Result.ok(DEFAULT_RAW_TRANSACTION_STATUS_VALUE),
        Result.ok(DEFAULT_PAYMENT_HISTORY_VALUE),
        Result.ok(DEFAULT_ACCOUNT_EXISTS_VALUE)
    );

    this.reliableSubmissionXRPClient = new ReliableSubmissionXRPClient(fakeXRPClient);
  }

  @Test
  public void testGetBalance() throws XRPException {
    // GIVEN a `ReliableSubmissionClient` decorating a FakeXRPClient WHEN a balance is retrieved
    BigInteger balance = reliableSubmissionXRPClient.getBalance(XRPL_ADDRESS);


    // THEN the result is returned unaltered.
    assertThat(balance).isEqualTo(DEFAULT_BALANCE_VALUE);
  }

  @Test
  public void testGetPaymentStatus() throws XRPException {
    // GIVEN a `ReliableSubmissionClient` decorating a FakeXRPClient WHEN a payment status is retrieved
    TransactionStatus paymentStatus = reliableSubmissionXRPClient.getPaymentStatus(TRANSACTION_HASH);

    // THEN the result is returned unaltered.
    assertThat(paymentStatus).isEqualTo(DEFAULT_TRANSACTION_STATUS_VALUE);
  }

  @Test
  public void testGetLatestValidatedLedgerSequence() throws XRPException {
    // GIVEN a `ReliableSubmissionClient` decorating a FakeXRPClient WHEN the latest ledger sequence is retrieved
    int latestSequence = reliableSubmissionXRPClient.getLatestValidatedLedgerSequence();

    // THEN the result is returned unaltered.
    assertThat(latestSequence).isEqualTo(DEFAULT_LATEST_LEDGER_VALUE);
  }

  @Test
  public void testGetRawTransactionStatus() throws XRPException {
    // GIVEN a `ReliableSubmissionClient` decorating a FakeXRPClient WHEN a raw transaction status is retrieved
    RawTransactionStatus transactionStatus = reliableSubmissionXRPClient.getRawTransactionStatus(TRANSACTION_HASH);

    // THEN the result is returned unaltered.
    assertThat(transactionStatus).isEqualTo(DEFAULT_RAW_TRANSACTION_STATUS_VALUE);
  }

  @Test(timeout = 10000)
  public void testSendWithExpiredLedgerSequenceAndUnvalidatedTransaction() throws XRPException {
    // GIVEN A faked latestLedgerSequence number that will increment past the lastLedgerSequence for a transaction
    this.fakeXRPClient.rawTransactionStatusResult = Result.ok(new RawTransactionStatus(
        GetTransactionResponse.newBuilder()
            .setValidated(false)
            .setTransaction(
                Transaction.newBuilder()
                    .setLastLedgerSequence(
                        LastLedgerSequence.newBuilder()
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
    ));

    runAfterOneSecond(() -> {
      this.fakeXRPClient.latestValidatedLedgerResult = Result.ok(LAST_LEDGER_SEQUENCE + 1);
    });

    // WHEN a reliable send is submitted THEN the send reaches a consistent state and returns.
    this.reliableSubmissionXRPClient.send(SEND_AMOUNT, XRPL_ADDRESS, new Wallet(WALLET_SEED));
  }

  @Test(timeout = 10000)
  public void testSendWithUnxpiredLedgerSequenceAndValidatedTransaction() throws XRPException {
    // GIVEN A transaction that will validate in one second
    final String transactionStatusCode = "tesSuccess";
    this.fakeXRPClient.rawTransactionStatusResult = Result.ok(new RawTransactionStatus(
        GetTransactionResponse.newBuilder()
            .setValidated(false)
            .setTransaction(
                Transaction.newBuilder()
                    .setLastLedgerSequence(
                        LastLedgerSequence.newBuilder()
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
    ));

    runAfterOneSecond(() -> {
      this.fakeXRPClient.rawTransactionStatusResult = Result.ok(new RawTransactionStatus(
          GetTransactionResponse.newBuilder()
              .setValidated(true)
              .setTransaction(
                  Transaction.newBuilder()
                      .setLastLedgerSequence(
                          LastLedgerSequence.newBuilder()
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
      ));
    });

    // WHEN a reliable send is submitted THEN the send reaches a consistent state and returns.
    this.reliableSubmissionXRPClient.send(SEND_AMOUNT, XRPL_ADDRESS, new Wallet(WALLET_SEED));
  }

  @Test
  public void testSendWithNoLastLedgerSequence() throws XRPException {
    // GIVEN a `ReliableSubmissionXRPClient` decorating a `FakeXRPClient` which will return a transaction that did not
    // have a last ledger sequence attached.
    this.fakeXRPClient.rawTransactionStatusResult = Result.ok(new RawTransactionStatus(
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
    ));

    // WHEN a reliable send is submitted THEN an error is thrown.
    expectedException.expect(Exception.class);
    this.reliableSubmissionXRPClient.send(SEND_AMOUNT, XRPL_ADDRESS, new Wallet(WALLET_SEED));
  }

  @Test
  public void testPaymentHistoryWithUnmodifiedResponse() throws XRPException {
    // GIVEN a `ReliableSubmissionXRPClient` decorating a `FakeXRPClient` WHEN transaction history is retrieved.
    List<XRPTransaction> returnedValue = this.fakeXRPClient.paymentHistory(XRPL_ADDRESS);

    // THEN the result is returned unaltered.
    assertThat(returnedValue).isEqualTo(this.fakeXRPClient.paymentHistoryResult.getValue());
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