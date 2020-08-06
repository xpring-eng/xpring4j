package io.xpring.xrpl;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import io.xpring.common.Result;
import io.xpring.common.XrplNetwork;
import io.xpring.xrpl.helpers.XrpTestUtils;
import io.xpring.xrpl.model.XrpTransaction;
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
public class ReliableSubmissionXrpClientTest {
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
  private static final List<XrpTransaction> DEFAULT_PAYMENT_HISTORY_VALUE = XrpTestUtils
      .transactionHistoryToPaymentsList(FakeXrpProtobufs.paymentOnlyGetAccountTransactionHistoryResponse);
  private static final boolean DEFAULT_ACCOUNT_EXISTS_VALUE = true;
  private static final XrpTransaction DEFAULT_GET_TRANSACTION_VALUE = XrpTransaction.from(
          FakeXrpProtobufs.getTransactionResponsePaymentAllFields,
          XrplNetwork.TEST
  );
  private static final io.xpring.xrpl.model.TransactionResult DEFAULT_ENABLE_DEPOSIT_AUTH_VALUE =
          io.xpring.xrpl.model.TransactionResult.builder()
                                                .hash(TRANSACTION_HASH)
                                                .status(DEFAULT_TRANSACTION_STATUS_VALUE)
                                                .validated(true)
                                                .build();

  @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
  FakeXrpClient fakeXRPClient;
  ReliableSubmissionXrpClient reliableSubmissionXRPClient;
  private ScheduledExecutorService scheduledExecutor;

  /**
   * Set up test parameters.
   */
  @Before
  public void setUp() throws Exception {
    this.scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
    this.fakeXRPClient = new FakeXrpClient(
        XrplNetwork.TEST,
        Result.ok(DEFAULT_BALANCE_VALUE),
        Result.ok(DEFAULT_TRANSACTION_STATUS_VALUE),
        Result.ok(DEFAULT_SEND_VALUE),
        Result.ok(DEFAULT_LATEST_LEDGER_VALUE),
        Result.ok(DEFAULT_RAW_TRANSACTION_STATUS_VALUE),
        Result.ok(DEFAULT_PAYMENT_HISTORY_VALUE),
        Result.ok(DEFAULT_ACCOUNT_EXISTS_VALUE),
        Result.ok(DEFAULT_GET_TRANSACTION_VALUE),
        Result.ok(DEFAULT_ENABLE_DEPOSIT_AUTH_VALUE)
    );

    this.reliableSubmissionXRPClient = new ReliableSubmissionXrpClient(fakeXRPClient);
  }

  @Test
  public void testGetBalance() throws XrpException {
    // GIVEN a `ReliableSubmissionClient` decorating a FakeXrpClient WHEN a balance is retrieved
    BigInteger balance = reliableSubmissionXRPClient.getBalance(XRPL_ADDRESS);


    // THEN the result is returned unaltered.
    assertThat(balance).isEqualTo(DEFAULT_BALANCE_VALUE);
  }

  @Test
  public void testGetPaymentStatus() throws XrpException {
    // GIVEN a `ReliableSubmissionClient` decorating a FakeXrpClient WHEN a payment status is retrieved
    TransactionStatus paymentStatus = reliableSubmissionXRPClient.getPaymentStatus(TRANSACTION_HASH);

    // THEN the result is returned unaltered.
    assertThat(paymentStatus).isEqualTo(DEFAULT_TRANSACTION_STATUS_VALUE);
  }

  @Test
  public void testGetLatestValidatedLedgerSequence() throws XrpException {
    // GIVEN a `ReliableSubmissionClient` decorating a FakeXrpClient WHEN the latest ledger sequence is retrieved
    int latestSequence = reliableSubmissionXRPClient.getLatestValidatedLedgerSequence(XRPL_ADDRESS);

    // THEN the result is returned unaltered.
    assertThat(latestSequence).isEqualTo(DEFAULT_LATEST_LEDGER_VALUE);
  }

  @Test
  public void testGetRawTransactionStatus() throws XrpException {
    // GIVEN a `ReliableSubmissionClient` decorating a FakeXrpClient WHEN a raw transaction status is retrieved
    RawTransactionStatus transactionStatus = reliableSubmissionXRPClient.getRawTransactionStatus(TRANSACTION_HASH);

    // THEN the result is returned unaltered.
    assertThat(transactionStatus).isEqualTo(DEFAULT_RAW_TRANSACTION_STATUS_VALUE);
  }

  @Test(timeout = 10000)
  public void testSendWithExpiredLedgerSequenceAndUnvalidatedTransaction() throws XrpException {
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
  public void testSendWithUnexpiredLedgerSequenceAndValidatedTransaction() throws XrpException {
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
  public void testSendWithNoLastLedgerSequence() throws XrpException {
    // GIVEN a `ReliableSubmissionXrpClient` decorating a `FakeXrpClient` which will return a transaction that did not
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
  public void testPaymentHistoryWithUnmodifiedResponse() throws XrpException {
    // GIVEN a `ReliableSubmissionXrpClient` decorating a `FakeXrpClient` WHEN transaction history is retrieved.
    List<XrpTransaction> returnedValue = this.fakeXRPClient.paymentHistory(XRPL_ADDRESS);

    // THEN the result is returned unaltered.
    assertThat(returnedValue).isEqualTo(this.fakeXRPClient.paymentHistoryResult.getValue());
  }

  @Test(timeout = 10000)
  public void testEnableDepositAuthWithExpiredLastLedgerSequenceAndUnvalidatedTransaction() throws XrpException {
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

    // WHEN enableDepositAuth is called THEN the reliable submission reaches a consistent state and returns.
    this.reliableSubmissionXRPClient.enableDepositAuth(new Wallet(WALLET_SEED));
  }

  @Test(timeout = 10000)
  public void testEnableDepositAuthWithUnexpiredLedgerSequenceAndValidatedTransaction() throws XrpException {
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

    // WHEN enableDepositAuth is called THEN the reliable submission reaches a consistent state and returns.
    this.reliableSubmissionXRPClient.enableDepositAuth(new Wallet(WALLET_SEED));
  }

  @Test
  public void testEnableDepositAuthWithNoLastLedgerSequence() throws XrpException {
    // GIVEN a `ReliableSubmissionXrpClient` decorating a `FakeXrpClient` which will return a transaction that did not
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

    // WHEN enableDepositAuth is caled THEN an error is thrown.
    expectedException.expect(Exception.class);
    this.reliableSubmissionXRPClient.enableDepositAuth(new Wallet(WALLET_SEED));
  }

  /**
   * Run the given work on a separate thread in after one second.
   *
   * @param work The work to run.
   */
  private void runAfterOneSecond(Runnable work) {
    scheduledExecutor.schedule(work, 1, TimeUnit.SECONDS);
  }
}