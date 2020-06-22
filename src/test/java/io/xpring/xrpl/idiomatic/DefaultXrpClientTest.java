package io.xpring.xrpl.idiomatic;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.Mockito.mock;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
import io.grpc.testing.GrpcCleanupRule;
import io.xpring.common.Result;
import io.xpring.common.idiomatic.XrplNetwork;
import io.xpring.xrpl.ClassicAddress;
import io.xpring.xrpl.FakeXRPProtobufs;
import io.xpring.xrpl.Utils;
import io.xpring.xrpl.Wallet;
import io.xpring.xrpl.XRPException;
import io.xpring.xrpl.helpers.idiomatic.XrpTestUtils;
import io.xpring.xrpl.model.idiomatic.XrpTransaction;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.xrpl.rpc.v1.AccountRoot;
import org.xrpl.rpc.v1.Common.Balance;
import org.xrpl.rpc.v1.CurrencyAmount;
import org.xrpl.rpc.v1.Fee;
import org.xrpl.rpc.v1.GetAccountInfoRequest;
import org.xrpl.rpc.v1.GetAccountInfoResponse;
import org.xrpl.rpc.v1.GetAccountTransactionHistoryRequest;
import org.xrpl.rpc.v1.GetAccountTransactionHistoryResponse;
import org.xrpl.rpc.v1.GetFeeRequest;
import org.xrpl.rpc.v1.GetFeeResponse;
import org.xrpl.rpc.v1.GetTransactionRequest;
import org.xrpl.rpc.v1.GetTransactionResponse;
import org.xrpl.rpc.v1.Meta;
import org.xrpl.rpc.v1.SubmitTransactionRequest;
import org.xrpl.rpc.v1.SubmitTransactionResponse;
import org.xrpl.rpc.v1.TransactionResult;
import org.xrpl.rpc.v1.XRPDropsAmount;
import org.xrpl.rpc.v1.XRPLedgerAPIServiceGrpc;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

/**
 * Unit tests for {@link DefaultXrpClient}.
 */
public class DefaultXrpClientTest {
  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Rule
  public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

  /**
   * The DefaultXrpClient under test.
   */
  private DefaultXrpClient client;

  /**
   * An address on the XRP Ledger.
   */
  private static final String XRPL_ADDRESS = "XVwDxLQ4SN9pEBQagTNHwqpFkPgGppXqrMoTmUcSKdCtcK5";

  /**
   * Mocked values in responses from the gRPC server.
   */
  private static final long DROPS_OF_XRP_IN_ACCOUNT = 10;
  private static final String TRANSACTION_BLOB = "DEADBEEF";
  private static final Throwable GENERIC_ERROR = new Throwable("Mocked network error");
  private static final String TRANSACTION_STATUS_SUCCESS = "tesSUCCESS";
  private static final String[] TRANSACTION_FAILURE_STATUS_CODES = {
      "tefFAILURE",
      "tecCLAIM",
      "telBAD_PUBLIC_KEY",
      "temBAD_FEE",
      "terRETRY"
  };
  private static final String TRANSACTION_HASH = "DEADBEEF";
  private static final long MINIMUM_FEE = 12;
  private static final int LAST_LEDGER_SEQUENCE = 20;

  /**
   * The seed for a wallet with funds on the XRP Ledger test net.
   */
  private static final String WALLET_SEED = "snYP7oArxKepd3GPDcrjMsJYiJeJB";

  /**
   * Drops of XRP to send.
   */
  private static final BigInteger AMOUNT = new BigInteger("1");

  @Test
  public void getBalanceTest() throws IOException, XrpException {
    // GIVEN a DefaultXrpClient with mocked networking which will succeed.
    DefaultXrpClient client = getClient();

    // WHEN the balance is retrieved.
    BigInteger balance = client.getBalance(XRPL_ADDRESS);

    // THEN the balance returned is the the same as the mocked response.
    assertThat(balance).isEqualTo(BigInteger.valueOf(DROPS_OF_XRP_IN_ACCOUNT));
  }

  @Test
  public void getBalanceWithClassicAddressTest() throws IOException, XrpException {
    // GIVEN a classic address.
    ClassicAddress classicAddress = Utils.decodeXAddress(XRPL_ADDRESS);
    DefaultXrpClient client = getClient();

    // WHEN the balance for the classic address is retrieved THEN an error is thrown.
    expectedException.expect(XrpException.class);
    client.getBalance(classicAddress.address());
  }

  @Test
  public void getBalanceTestWithFailedAccountInfo() throws IOException, XrpException {
    // GIVEN an XrpClient with mocked networking which will fail to retrieve account info.
    Result<GetAccountInfoResponse, Throwable> accountInfoResult = Result.error(GENERIC_ERROR);
    DefaultXrpClient client = getClient(
        accountInfoResult,
        Result.ok(makeTransactionStatus(true, TRANSACTION_STATUS_SUCCESS)),
        Result.ok(makeGetFeeResponse(MINIMUM_FEE, LAST_LEDGER_SEQUENCE)),
        Result.ok(makeSubmitTransactionResponse(TRANSACTION_HASH)),
        Result.ok(makeGetAccountTransactionHistoryResponse())
    );

    // WHEN the balance is retrieved THEN an error is thrown.
    expectedException.expect(Exception.class);
    client.getBalance(XRPL_ADDRESS);
  }


  @Test
  public void paymentStatusWithUnvalidatedTransactionAndFailureCode() throws IOException, XrpException {
    // Iterate over different types of transaction status codes which represent failures.
    for (String transactionFailureCode : TRANSACTION_FAILURE_STATUS_CODES) {
      // GIVEN an XRPClient which will return an unvalidated transaction with a failed code.
      DefaultXrpClient client = getClient(
          Result.ok(makeGetAccountInfoResponse(DROPS_OF_XRP_IN_ACCOUNT)),
          Result.ok(makeTransactionStatus(false, transactionFailureCode)),
          Result.ok(makeGetFeeResponse(MINIMUM_FEE, LAST_LEDGER_SEQUENCE)),
          Result.ok(makeSubmitTransactionResponse(TRANSACTION_HASH)),
          Result.ok(makeGetAccountTransactionHistoryResponse())
      );

      // WHEN the payment status is retrieved.
      io.xpring.xrpl.TransactionStatus paymentStatus = client.getPaymentStatus(TRANSACTION_HASH);

      // THEN the status is PENDING.
      assertThat(paymentStatus).isEqualTo(io.xpring.xrpl.TransactionStatus.PENDING);
    }
  }

  @Test
  public void paymentStatusWithUnvalidatedTransactionAndSuccessCode() throws IOException, XrpException {
    // GIVEN an XRPClient which will return an unvalidated transaction with a success code.
    DefaultXrpClient client = getClient(
        Result.ok(makeGetAccountInfoResponse(DROPS_OF_XRP_IN_ACCOUNT)),
        Result.ok(makeTransactionStatus(false, TRANSACTION_STATUS_SUCCESS)),
        Result.ok(makeGetFeeResponse(MINIMUM_FEE, LAST_LEDGER_SEQUENCE)),
        Result.ok(makeSubmitTransactionResponse(TRANSACTION_HASH)),
        Result.ok(makeGetAccountTransactionHistoryResponse())
    );

    // WHEN the payment status is retrieved.
    io.xpring.xrpl.TransactionStatus paymentStatus = client.getPaymentStatus(TRANSACTION_HASH);

    // THEN the status is PENDING.
    assertThat(paymentStatus).isEqualTo(io.xpring.xrpl.TransactionStatus.PENDING);
  }

  @Test
  public void paymentStatusWithValidatedTransactionAndFailureCode() throws IOException, XrpException {
    // Iterate over different types of transaction status codes which represent failures.
    for (String transactionFailureCode : TRANSACTION_FAILURE_STATUS_CODES) {
      // GIVEN an XRPClient which will return an validated transaction with a failed code.
      DefaultXrpClient client = getClient(
          Result.ok(makeGetAccountInfoResponse(DROPS_OF_XRP_IN_ACCOUNT)),
          Result.ok(makeTransactionStatus(true, transactionFailureCode)),
          Result.ok(makeGetFeeResponse(MINIMUM_FEE, LAST_LEDGER_SEQUENCE)),
          Result.ok(makeSubmitTransactionResponse(TRANSACTION_HASH)),
          Result.ok(makeGetAccountTransactionHistoryResponse())
      );

      // WHEN the payment status is retrieved.
      io.xpring.xrpl.TransactionStatus paymentStatus = client.getPaymentStatus(TRANSACTION_HASH);

      // THEN the status is FAILED.
      assertThat(paymentStatus).isEqualTo(io.xpring.xrpl.TransactionStatus.FAILED);
    }
  }

  @Test
  public void paymentStatusWithValidatedTransactionAndSuccessCode() throws IOException, XrpException {
    // GIVEN an XRPClient which will return an validated transaction with a success code.
    DefaultXrpClient client = getClient(
        Result.ok(makeGetAccountInfoResponse(DROPS_OF_XRP_IN_ACCOUNT)),
        Result.ok(makeTransactionStatus(true, TRANSACTION_STATUS_SUCCESS)),
        Result.ok(makeGetFeeResponse(MINIMUM_FEE, LAST_LEDGER_SEQUENCE)),
        Result.ok(makeSubmitTransactionResponse(TRANSACTION_HASH)),
        Result.ok(makeGetAccountTransactionHistoryResponse())
    );

    // WHEN the payment status is retrieved.
    io.xpring.xrpl.TransactionStatus paymentStatus = client.getPaymentStatus(TRANSACTION_HASH);

    // THEN the status is SUCCEEDED.
    assertThat(paymentStatus).isEqualTo(io.xpring.xrpl.TransactionStatus.SUCCEEDED);
  }

  @Test
  public void paymentStatusWithNodeError() throws IOException, XrpException {
    // GIVEN an XRPClient which will error when a transaction status is requested..
    DefaultXrpClient client = getClient(
        Result.ok(makeGetAccountInfoResponse(DROPS_OF_XRP_IN_ACCOUNT)),
        Result.error(GENERIC_ERROR),
        Result.ok(makeGetFeeResponse(MINIMUM_FEE, LAST_LEDGER_SEQUENCE)),
        Result.ok(makeSubmitTransactionResponse(TRANSACTION_HASH)),
        Result.ok(makeGetAccountTransactionHistoryResponse())
    );

    // WHEN the payment status is retrieved THEN an error is thrown..
    expectedException.expect(Exception.class);
    client.getPaymentStatus(TRANSACTION_HASH);
  }

  @Test
  public void submitTransactionTest() throws IOException, XrpException, XRPException {
    // GIVEN an XRPClient with mocked networking which will succeed.
    DefaultXrpClient client = getClient();
    Wallet wallet = new Wallet(WALLET_SEED);

    // WHEN a transaction is sent.
    String transactionHash = client.send(AMOUNT, XRPL_ADDRESS, wallet);

    // THEN the transaction hash is the same as the hash of the mocked transaction blob in the response.
    assertThat(transactionHash).isEqualTo(TRANSACTION_HASH.toLowerCase());
  }

  @Test
  public void submitTransactionWithClassicAddress() throws IOException, XrpException, XRPException {
    // GIVEN a classic address.
    DefaultXrpClient client = getClient();
    ClassicAddress classicAddress = Utils.decodeXAddress(XRPL_ADDRESS);
    Wallet wallet = new Wallet(WALLET_SEED);

    // WHEN XRP is sent to the classic address THEN an error is thrown.
    expectedException.expect(XrpException.class);
    client.send(AMOUNT, classicAddress.address(), wallet);
  }

  @Test
  public void submitTransactionWithFailedAccountInfo() throws IOException, XrpException, XRPException {
    // GIVEN a XRPClient which will fail to return account info.
    Result<GetAccountInfoResponse, Throwable> accountInfoResult = Result.error(GENERIC_ERROR);
    DefaultXrpClient client = getClient(
        accountInfoResult,
        Result.ok(makeTransactionStatus(true, TRANSACTION_STATUS_SUCCESS)),
        Result.ok(makeGetFeeResponse(MINIMUM_FEE, LAST_LEDGER_SEQUENCE)),
        Result.ok(makeSubmitTransactionResponse(TRANSACTION_HASH)),
        Result.ok(makeGetAccountTransactionHistoryResponse())
    );
    Wallet wallet = new Wallet(WALLET_SEED);

    // WHEN XRP is sent then THEN an error is thrown.
    expectedException.expect(Exception.class);
    client.send(AMOUNT, XRPL_ADDRESS, wallet);
  }

  @Test
  public void submitTransactionWithFailedFee() throws IOException, XrpException, XRPException {
    // GIVEN a XRPClient which will fail to retrieve a fee.
    Result<GetFeeResponse, Throwable> feeResult = Result.error(GENERIC_ERROR);
    DefaultXrpClient client = getClient(
        Result.ok(makeGetAccountInfoResponse(DROPS_OF_XRP_IN_ACCOUNT)),
        Result.ok(makeTransactionStatus(true, TRANSACTION_STATUS_SUCCESS)),
        feeResult,

        Result.ok(makeSubmitTransactionResponse(TRANSACTION_HASH)),
        Result.ok(makeGetAccountTransactionHistoryResponse())
    );
    Wallet wallet = new Wallet(WALLET_SEED);

    // WHEN XRP is sent then THEN an error is thrown.
    expectedException.expect(Exception.class);
    client.send(AMOUNT, XRPL_ADDRESS, wallet);
  }


  @Test
  public void submitTransactionWithFailedSubmit() throws IOException, XrpException, XRPException {
    // GIVEN a XRPClient which will fail to submit a transaction.
    Result<SubmitTransactionResponse, Throwable> submitResult = Result.error(GENERIC_ERROR);
    DefaultXrpClient client = getClient(
        Result.ok(makeGetAccountInfoResponse(DROPS_OF_XRP_IN_ACCOUNT)),
        Result.ok(makeTransactionStatus(true, TRANSACTION_STATUS_SUCCESS)),
        Result.ok(makeGetFeeResponse(MINIMUM_FEE, LAST_LEDGER_SEQUENCE)),
        submitResult,
        Result.ok(makeGetAccountTransactionHistoryResponse())
    );
    Wallet wallet = new Wallet(WALLET_SEED);

    // WHEN XRP is sent then THEN an error is thrown.
    expectedException.expect(Exception.class);
    client.send(AMOUNT, XRPL_ADDRESS, wallet);
  }

  @Test
  public void paymentHistoryWithSuccessfulResponseTest() throws IOException, XrpException {
    // GIVEN a DefaultXRPClient with mocked networking that will succeed.
    DefaultXrpClient xrpClient = getClient();

    // WHEN the payment history for an address is requested.
    List<XrpTransaction> paymentHistory = xrpClient.paymentHistory(XRPL_ADDRESS);

    List<XrpTransaction> expectedPaymentHistory = XrpTestUtils.transactionHistoryToPaymentsList(
        makeGetAccountTransactionHistoryResponse());

    // THEN the payment history is returned as expected.
    assertThat(paymentHistory).isEqualTo(expectedPaymentHistory);
  }

  @Test
  public void paymentHistoryWithClassicAddressTest() throws IOException, XrpException {
    // GIVEN an XRPClient and a classic address
    DefaultXrpClient xrpClient = getClient();
    ClassicAddress classicAddress = Utils.decodeXAddress(XRPL_ADDRESS);

    // WHEN the payment history for an account is requested THEN an error to use X-Addresses is thrown.
    expectedException.expect(XrpException.class);
    xrpClient.paymentHistory(classicAddress.address());
  }

  @Test
  public void paymentHistoryWithNetworkFailureTest() throws IOException, XrpException {
    // GIVEN an XRPClient which will return a network error when calling paymentHistory.
    Result<GetAccountTransactionHistoryResponse, Throwable> getAccountTransactionHistoryResponse =
        Result.error(GENERIC_ERROR);
    DefaultXrpClient xrpClient = getClient(
        Result.ok(makeGetAccountInfoResponse(DROPS_OF_XRP_IN_ACCOUNT)),
        Result.ok(makeTransactionStatus(true, TRANSACTION_STATUS_SUCCESS)),
        Result.ok(makeGetFeeResponse(MINIMUM_FEE, LAST_LEDGER_SEQUENCE)),
        Result.ok(makeSubmitTransactionResponse(TRANSACTION_HASH)),
        getAccountTransactionHistoryResponse);

    // WHEN the payment history is requested THEN an error is propagated.
    expectedException.expect(Exception.class);
    xrpClient.paymentHistory(XRPL_ADDRESS);
  }

  @Test
  public void paymentHistoryWithSomeNonPaymentTransactionsTest() throws IOException, XrpException {
    // GIVEN an XRPClient client which will return a transaction history which contains non-payment transactions.
    Result<GetAccountTransactionHistoryResponse, Throwable> getAccountTransactionHistoryResponse =
        Result.ok(FakeXRPProtobufs.mixedGetAccountTransactionHistoryResponse);

    DefaultXrpClient xrpClient = getClient(
        Result.ok(makeGetAccountInfoResponse(DROPS_OF_XRP_IN_ACCOUNT)),
        Result.ok(makeTransactionStatus(true, TRANSACTION_STATUS_SUCCESS)),
        Result.ok(makeGetFeeResponse(MINIMUM_FEE, LAST_LEDGER_SEQUENCE)),
        Result.ok(makeSubmitTransactionResponse(TRANSACTION_HASH)),
        getAccountTransactionHistoryResponse);

    // WHEN the transactionHistory is requested.
    List<XrpTransaction> transactionHistory = xrpClient.paymentHistory(XRPL_ADDRESS);

    // THEN the returned transactions are conversions of the inputs with non-payment transactions filtered.
    List<XrpTransaction> expectedTransactionHistory = XrpTestUtils.transactionHistoryToPaymentsList(
        FakeXRPProtobufs.mixedGetAccountTransactionHistoryResponse);
  }

  @Test
  public void paymentHistoryWithInvalidPaymentTest() throws IOException, XrpException {
    // GIVEN an XRPClient client which will return a transaction history which contains a malformed payment.
    Result<GetAccountTransactionHistoryResponse, Throwable> getAccountTransactionHistoryResponse =
        Result.ok(FakeXRPProtobufs.invalidPaymentGetAccountTransactionHistoryResponse);

    DefaultXrpClient xrpClient = getClient(
        Result.ok(makeGetAccountInfoResponse(DROPS_OF_XRP_IN_ACCOUNT)),
        Result.ok(makeTransactionStatus(true, TRANSACTION_STATUS_SUCCESS)),
        Result.ok(makeGetFeeResponse(MINIMUM_FEE, LAST_LEDGER_SEQUENCE)),
        Result.ok(makeSubmitTransactionResponse(TRANSACTION_HASH)),
        getAccountTransactionHistoryResponse);

    // WHEN the transactionHistory is requested THEN a conversion error is thrown.
    expectedException.expect(XrpException.class);
    xrpClient.paymentHistory(XRPL_ADDRESS);
  }

  @Test
  public void accountExistsTest() throws IOException, XrpException {
    // GIVEN a DefaultXRPClient with mocked networking which will succeed.
    DefaultXrpClient client = getClient();

    // WHEN the account is checked
    boolean exists = client.accountExists(XRPL_ADDRESS);

    // THEN the existence of the account is the the same as the mocked response.
    assertThat(exists).isEqualTo(true);
  }

  @Test
  public void accountExistsWithClassicAddressTest() throws IOException, XrpException {
    // GIVEN a classic address.
    ClassicAddress classicAddress = Utils.decodeXAddress(XRPL_ADDRESS);
    DefaultXrpClient client = getClient();

    // WHEN the existence of the account is checked for the classic address THEN an error is thrown.
    expectedException.expect(XrpException.class);
    client.accountExists(classicAddress.address());
  }

  @Test
  public void accountExistsTestWithNotFoundError() throws IOException, XrpException {
    // GIVEN a DefaultXRPClient with mocked networking which will fail to retrieve account info w/ NOT_FOUND error code.
    StatusRuntimeException notFoundError = new StatusRuntimeException(Status.NOT_FOUND);
    Result<GetAccountInfoResponse, Throwable> accountInfoResult = Result.error(notFoundError);
    DefaultXrpClient client = getClient(
        accountInfoResult,
        Result.ok(makeTransactionStatus(true, TRANSACTION_STATUS_SUCCESS)),
        Result.ok(makeGetFeeResponse(MINIMUM_FEE, LAST_LEDGER_SEQUENCE)),
        Result.ok(makeSubmitTransactionResponse(TRANSACTION_HASH)),
        Result.ok(makeGetAccountTransactionHistoryResponse())
    );

    // WHEN the existence of the account is checked
    boolean exists = client.accountExists(XRPL_ADDRESS);

    // THEN false is returned.
    assertThat(exists).isEqualTo(false);
  }

  @Test
  public void accountExistsTestWithUnknownError() throws IOException, XrpException {
    // GIVEN a DefaultXRPClient with mocked networking which will fail to retrieve account info w/ UNKNOWN error code.
    StatusRuntimeException unknownError = new StatusRuntimeException(Status.UNKNOWN);
    Result<GetAccountInfoResponse, Throwable> accountInfoResult = Result.error(unknownError);
    DefaultXrpClient client = getClient(
        accountInfoResult,
        Result.ok(makeTransactionStatus(true, TRANSACTION_STATUS_SUCCESS)),
        Result.ok(makeGetFeeResponse(MINIMUM_FEE, LAST_LEDGER_SEQUENCE)),
        Result.ok(makeSubmitTransactionResponse(TRANSACTION_HASH)),
        Result.ok(makeGetAccountTransactionHistoryResponse())
    );

    // WHEN the existence of the account is checked THEN the error is re-thrown.
    expectedException.expect(StatusRuntimeException.class);
    client.getBalance(XRPL_ADDRESS);
  }

  @Test
  public void getTransactionWithSuccessfulResponseTest() throws XrpException, IOException {
    // GIVEN a DefaultXRPClient with mocked networking that will succeed for getTransaction.
    Result<GetTransactionResponse, Throwable> getTransactionResult = Result.ok(
        FakeXRPProtobufs.getTransactionResponsePaymentAllFields
    );

    DefaultXrpClient xrpClient = getClient(
        Result.ok(makeGetAccountInfoResponse(DROPS_OF_XRP_IN_ACCOUNT)),
        getTransactionResult,
        Result.ok(makeGetFeeResponse(MINIMUM_FEE, LAST_LEDGER_SEQUENCE)),
        Result.ok(makeSubmitTransactionResponse(TRANSACTION_HASH)),
        Result.ok(makeGetAccountTransactionHistoryResponse())
    );
    // WHEN a transaction is requested.
    XrpTransaction transaction = xrpClient.getPayment(TRANSACTION_HASH);

    // THEN the returned transaction is as expected.
    assertThat(transaction).isEqualTo(XrpTransaction.from(
        FakeXRPProtobufs.getTransactionResponsePaymentAllFields,
        XrplNetwork.TEST
        )
    );
  }

  @Test
  public void getTransactionWithNotFoundErrorTest() throws XrpException, IOException {
    // GIVEN a DefaultXRPClient with mocked networking that will fail to retrieve a transaction w/ NOT_FOUND error code.
    StatusRuntimeException notFoundError = new StatusRuntimeException(Status.NOT_FOUND);
    Result<GetTransactionResponse, Throwable> getTransactionResult = Result.error(notFoundError);

    DefaultXrpClient client = getClient(
        Result.ok(makeGetAccountInfoResponse(DROPS_OF_XRP_IN_ACCOUNT)),
        getTransactionResult,
        Result.ok(makeGetFeeResponse(MINIMUM_FEE, LAST_LEDGER_SEQUENCE)),
        Result.ok(makeSubmitTransactionResponse(TRANSACTION_HASH)),
        Result.ok(makeGetAccountTransactionHistoryResponse())
    );

    // WHEN a transaction is requested, THEN the error is re-thrown.
    expectedException.expect(StatusRuntimeException.class);
    client.getPayment(TRANSACTION_HASH);
  }

  @Test
  public void getTransactionWithMalformedPaymentTest() throws XrpException, IOException {
    // GIVEN a DefaultXrpClient with mocked networking that will return a malformed payment transaction.
    Result<GetTransactionResponse, Throwable> getTransactionResult = Result.ok(
        FakeXRPProtobufs.invalidGetTransactionResponseEmptyPaymentFields
    );
    DefaultXrpClient client = getClient(
        Result.ok(makeGetAccountInfoResponse(DROPS_OF_XRP_IN_ACCOUNT)),
        getTransactionResult,
        Result.ok(makeGetFeeResponse(MINIMUM_FEE, LAST_LEDGER_SEQUENCE)),
        Result.ok(makeSubmitTransactionResponse(TRANSACTION_HASH)),
        Result.ok(makeGetAccountTransactionHistoryResponse())
    );

    // WHEN a transaction is requested.
    XrpTransaction transaction = client.getPayment(TRANSACTION_HASH);

    // THEN the result is null.
    assertThat(transaction).isNull();
  }

  @Test
  public void getTransactionWithUnsupportedTypeTest() throws XrpException, IOException {
    // GIVEN a DefaultXrpClient with mocked networking that will return an unsupported transaction type.
    Result<GetTransactionResponse, Throwable> getTransactionResult = Result.ok(
        FakeXRPProtobufs.invalidGetTransactionResponseUnsupportedTransactionType
    );
    DefaultXrpClient client = getClient(
        Result.ok(makeGetAccountInfoResponse(DROPS_OF_XRP_IN_ACCOUNT)),
        getTransactionResult,
        Result.ok(makeGetFeeResponse(MINIMUM_FEE, LAST_LEDGER_SEQUENCE)),
        Result.ok(makeSubmitTransactionResponse(TRANSACTION_HASH)),
        Result.ok(makeGetAccountTransactionHistoryResponse())
    );

    // WHEN a transaction is requested.
    XrpTransaction transaction = client.getPayment(TRANSACTION_HASH);

    // THEN the result is null.
    assertThat(transaction).isNull();
  }

  /**
   * Convenience method to get an XrpClient which has successful network calls.
   */
  private DefaultXrpClient getClient() throws IOException {
    return getClient(
        Result.ok(makeGetAccountInfoResponse(DROPS_OF_XRP_IN_ACCOUNT)),
        Result.ok(makeTransactionStatus(true, TRANSACTION_STATUS_SUCCESS)),
        Result.ok(makeGetFeeResponse(MINIMUM_FEE, LAST_LEDGER_SEQUENCE)),
        Result.ok(makeSubmitTransactionResponse(TRANSACTION_HASH)),
        Result.ok(makeGetAccountTransactionHistoryResponse())
    );
  }

  /**
   * Return an XrpClient which returns the given results for network calls.
   */
  private DefaultXrpClient getClient(
      Result<GetAccountInfoResponse, Throwable> getAccountInfoResponseResult,
      Result<GetTransactionResponse, Throwable> getTransactionResponseResult,
      Result<GetFeeResponse, Throwable> getFeeResult,
      Result<SubmitTransactionResponse, Throwable> submitTransactionResult,
      Result<GetAccountTransactionHistoryResponse, Throwable> getAccountTransactionHistoryResult
  ) throws IOException {
    XRPLedgerAPIServiceGrpc.XRPLedgerAPIServiceImplBase serviceImpl = getService(
        getAccountInfoResponseResult,
        getTransactionResponseResult,
        getFeeResult,
        submitTransactionResult,
        getAccountTransactionHistoryResult
    );

    // Generate a unique in-process server name.
    String serverName = InProcessServerBuilder.generateName();

    // Create a server, add service, start, and register for automatic graceful shutdown.
    grpcCleanup.register(InProcessServerBuilder
        .forName(serverName).directExecutor().addService(serviceImpl).build().start());

    // Create a client channel and register for automatic graceful shutdown.
    ManagedChannel channel = grpcCleanup.register(
        InProcessChannelBuilder.forName(serverName).directExecutor().build());

    // Create a new XrpClient using the in-process channel;
    return new DefaultXrpClient(channel, XrplNetwork.TEST);
  }


  /**
   * Return an XRPLedgerService implementation which returns the given results for network calls.
   */
  private XRPLedgerAPIServiceGrpc.XRPLedgerAPIServiceImplBase getService(
      Result<GetAccountInfoResponse, Throwable> getAccountInfoResult,
      Result<GetTransactionResponse, Throwable> getTransactionResponseResult,
      Result<GetFeeResponse, Throwable> getFeeResult,
      Result<SubmitTransactionResponse, Throwable> submitTransactionResult,
      Result<GetAccountTransactionHistoryResponse, Throwable> getTransactionHistoryResult
  ) {
    return mock(XRPLedgerAPIServiceGrpc.XRPLedgerAPIServiceImplBase.class, delegatesTo(
        new XRPLedgerAPIServiceGrpc.XRPLedgerAPIServiceImplBase() {
          @Override
          public void getAccountInfo(
              GetAccountInfoRequest request,
              StreamObserver<GetAccountInfoResponse> responseObserver
          ) {
            if (getAccountInfoResult.isError()) {
              responseObserver.onError(getAccountInfoResult.getError());
            } else {
              responseObserver.onNext(getAccountInfoResult.getValue());
              responseObserver.onCompleted();
            }
          }

          @Override
          public void getTransaction(
              GetTransactionRequest request,
              StreamObserver<GetTransactionResponse> responseObserver
          ) {
            if (getTransactionResponseResult.isError()) {
              responseObserver.onError(new Throwable(getTransactionResponseResult.getError()));
            } else {
              responseObserver.onNext(getTransactionResponseResult.getValue());
              responseObserver.onCompleted();
            }
          }

          @Override
          public void getFee(GetFeeRequest request,
                             StreamObserver<GetFeeResponse> responseObserver) {
            if (getFeeResult.isError()) {
              responseObserver.onError(getFeeResult.getError());
            } else {
              responseObserver.onNext(getFeeResult.getValue());
              responseObserver.onCompleted();
            }
          }

          @Override
          public void submitTransaction(SubmitTransactionRequest request,
                                        StreamObserver<SubmitTransactionResponse> responseObserver) {
            if (submitTransactionResult.isError()) {
              responseObserver.onError(submitTransactionResult.getError());
            } else {
              responseObserver.onNext(submitTransactionResult.getValue());
              responseObserver.onCompleted();
            }
          }

          @Override
          public void getAccountTransactionHistory(
              GetAccountTransactionHistoryRequest request,
              StreamObserver<GetAccountTransactionHistoryResponse> responseObserver
          ) {
            if (getTransactionHistoryResult.isError()) {
              responseObserver.onError(getTransactionHistoryResult.getError());
            } else {
              responseObserver.onNext(getTransactionHistoryResult.getValue());
              responseObserver.onCompleted();
            }
          }
        }
    ));
  }

  /**
   * Make a SubmitSignedTransaction response protocol buffer with the given inputs.
   */
  private SubmitTransactionResponse makeSubmitTransactionResponse(String hash) {
    ByteString bytes = ByteString.copyFrom(Utils.hexStringToByteArray(hash));
    return SubmitTransactionResponse.newBuilder().setHash(bytes).build();
  }

  /**
   * Make a GetFeeResponse protocol buffer with the given inputs.
   */
  private GetFeeResponse makeGetFeeResponse(long minimumFee, int lastLedgerSequence) {
    XRPDropsAmount minimumDrops = XRPDropsAmount.newBuilder().setDrops(minimumFee).build();
    Fee fee = Fee.newBuilder().setMinimumFee(minimumDrops).build();
    return GetFeeResponse.newBuilder().setLedgerCurrentIndex(lastLedgerSequence).setFee(fee).build();
  }

  /**
   * Make an GetAccountInfoResponse protocol buffer with the given balance.
   */
  private GetAccountInfoResponse makeGetAccountInfoResponse(long balance) {
    XRPDropsAmount xrpAccountBalance = XRPDropsAmount.newBuilder().setDrops(balance).build();
    CurrencyAmount currencyAccountBalance = CurrencyAmount.newBuilder().setXrpAmount(xrpAccountBalance).build();
    Balance accountBalance = Balance.newBuilder().setValue(currencyAccountBalance).build();

    AccountRoot accountData = AccountRoot.newBuilder().setBalance(accountBalance).build();
    return GetAccountInfoResponse.newBuilder().setAccountData(accountData).build();
  }

  /**
   * Make a GetTransactionResponse.
   */
  private GetTransactionResponse makeTransactionStatus(Boolean validated, String result) {
    TransactionResult transactionResult = TransactionResult.newBuilder()
        .setResult(result)
        .build();
    Meta meta = Meta.newBuilder().setTransactionResult(transactionResult).build();

    return GetTransactionResponse.newBuilder().setValidated(validated).setMeta(meta).build();
  }

  /**
   * Make a GetAccountTransactionHistoryResponse.
   * <p>
   * Note: Delegates to FakeXRPProtobufs for re-usability.
   * </p>
   */
  private GetAccountTransactionHistoryResponse makeGetAccountTransactionHistoryResponse() {
    return FakeXRPProtobufs.paymentOnlyGetAccountTransactionHistoryResponse;
  }
}
