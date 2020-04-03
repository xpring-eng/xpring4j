package io.xpring.ilp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.Mockito.mock;

import org.interledger.spsp.server.grpc.BalanceServiceGrpc;
import org.interledger.spsp.server.grpc.GetBalanceRequest;
import org.interledger.spsp.server.grpc.GetBalanceResponse;
import org.interledger.spsp.server.grpc.IlpOverHttpServiceGrpc;
import org.interledger.spsp.server.grpc.SendPaymentRequest;
import org.interledger.spsp.server.grpc.SendPaymentResponse;

import com.google.common.primitives.UnsignedLong;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
import io.grpc.testing.GrpcCleanupRule;
import io.xpring.GRPCResult;
import io.xpring.ilp.model.AccountBalance;
import io.xpring.ilp.model.PaymentRequest;
import io.xpring.ilp.model.PaymentResult;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

/**
 * Unit tests for {@link io.xpring.ilp.DefaultIlpClient}
 */
public class DefaultIlpClientTest {

  @Rule
  public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

  private GetBalanceResponse getBalanceResponse;
  private SendPaymentResponse sendPaymentResponse;
  public static final PaymentRequest mockPaymentRequest = PaymentRequest.builder()
    .amount(UnsignedLong.valueOf(1000))
    .destinationPaymentPointer("$foo.dev/bar")
    .senderAccountId("baz")
    .build();

  @Before
  public void setUp() {
    int clearingBalance = 10;
    int prepaidAmount = 100;
    getBalanceResponse = GetBalanceResponse.newBuilder()
      .setAccountId("bob")
      .setAssetCode("XRP")
      .setAssetScale(9)
      .setNetBalance(clearingBalance + prepaidAmount)
      .setClearingBalance(clearingBalance)
      .setPrepaidAmount(prepaidAmount)
      .build();

    sendPaymentResponse = SendPaymentResponse.newBuilder()
      .setOriginalAmount(1000)
      .setAmountDelivered(1000)
      .setAmountSent(1000)
      .setSuccessfulPayment(true)
      .build();
  }

  @Test
  public void successfulGetBalanceTest() throws IlpException, IOException {
    // GIVEN a DefaultIlpClient with mocked networking which will succeed.
    DefaultIlpClient client = getSuccessfulClient();

    // WHEN the balance is retrieved for "bob"
    AccountBalance balanceResponse = client.getBalance("bob", "jwtjwtjwtjwt");

    // THEN the balance response is equal to the mocked response
    assertThat(balanceResponse.accountId()).isEqualTo(this.getBalanceResponse.getAccountId());
    assertThat(balanceResponse.assetCode()).isEqualTo(this.getBalanceResponse.getAssetCode());
    assertThat(balanceResponse.assetScale()).isEqualTo(this.getBalanceResponse.getAssetScale());
    assertThat(balanceResponse.clearingBalance()).isEqualTo(this.getBalanceResponse.getClearingBalance());
    assertThat(balanceResponse.prepaidAmount()).isEqualTo(this.getBalanceResponse.getPrepaidAmount());
    assertThat(balanceResponse.netBalance()).isEqualTo(this.getBalanceResponse.getNetBalance());
  }

  @Test
  public void getBalanceThrowsInvalidAccessToken() throws IOException {
    // GIVEN a DefaultIlpClient with mocked networking which will succeed
    DefaultIlpClient client = getSuccessfulClient();

    // WHEN the balance is retrieved for "bob" with an access token prefixed with "Bearer "
    // THEN an IlpException in thrown
    assertThrows(
      IlpException.INVALID_ACCESS_TOKEN.getMessage(),
      IlpException.class,
      () -> client.getBalance("bob", "Bearer password")
    );
  }

  @Test
  public void getBalanceThrowsNotFound() throws IOException {
    // GIVEN a DefaultIlpClient with mocked networking which will throw a StatusRuntimeException
    // with grpc.Status = NOT_FOUND
    DefaultIlpClient client = getFailingClient(Status.NOT_FOUND);

    // WHEN the balance is retrieved THEN IlpException.NOT_FOUND is thrown
    assertGetBalanceThrows(client, IlpException.NOT_FOUND);
  }

  @Test
  public void getBalanceThrowsInvalidArgument() throws IOException {
    // GIVEN a DefaultIlpClient with mocked networking which will throw a StatusRuntimeException
    // with grpc.Status = INVALID_ARGUMENT
    DefaultIlpClient client = getFailingClient(Status.INVALID_ARGUMENT);

    // WHEN the balance is retrieved THEN IlpException.INVALID_ARGUMENT is thrown
    assertGetBalanceThrows(client, IlpException.INVALID_ARGUMENT);
  }

  @Test
  public void getBalanceThrowsUnauthenticated() throws IOException {
    // GIVEN a DefaultIlpClient with mocked networking which will throw a StatusRuntimeException
    // with grpc.Status = UNAUTHENTICATED
    DefaultIlpClient client = getFailingClient(Status.UNAUTHENTICATED);

    // WHEN the balance is retrieved THEN IlpException.UNAUTHENTICATED is thrown
    assertGetBalanceThrows(client, IlpException.UNAUTHENTICATED);
  }

  @Test
  public void getBalanceThrowsInternal() throws IOException {
    // GIVEN a DefaultIlpClient with mocked networking which will throw a StatusRuntimeException
    // with grpc.Status = INTERNAL
    DefaultIlpClient client = getFailingClient(Status.INTERNAL);

    // WHEN the balance is retrieved THEN IlpException.INTERNAL is thrown
    assertGetBalanceThrows(client, IlpException.INTERNAL);
  }

  @Test
  public void getBalanceThrowsUnknown() throws IOException {
    // GIVEN a DefaultIlpClient with mocked networking which will throw a StatusRuntimeException
    // with grpc.Status = UNKNOWN
    DefaultIlpClient client = getFailingClient(Status.UNKNOWN);

    // WHEN the balance is retrieved THEN IlpException.UNKNOWN is thrown
    assertGetBalanceThrows(client, IlpException.UNKNOWN);
  }

  @Test
  public void successfulSendPaymentTest() throws IOException, IlpException {
    // GIVEN a DefaultIlpClient with mocked networking which will succeed.
    DefaultIlpClient client = getSuccessfulClient();

    // WHEN a payment is sent
    PaymentResult response = client.sendPayment(mockPaymentRequest, "gobbledygook");

    // THEN the payment result is equal to the mocked response
    assertThat(response.originalAmount().longValue()).isEqualTo(this.sendPaymentResponse.getOriginalAmount());
    assertThat(response.amountDelivered().longValue()).isEqualTo(this.sendPaymentResponse.getAmountDelivered());
    assertThat(response.amountSent().longValue()).isEqualTo(this.sendPaymentResponse.getAmountSent());
    assertThat(response.successfulPayment()).isEqualTo(this.sendPaymentResponse.getSuccessfulPayment());
  }

  @Test
  public void sendPaymentThrowsInvalidAccessToken() throws IOException, IlpException {
    // GIVEN a DefaultIlpClient with mocked networking which will succeed
    DefaultIlpClient client = getSuccessfulClient();

    // WHEN a payment is sent with an access token prefixed with "Bearer "
    // THEN an IlpException in thrown
    assertThrows(
      IlpException.INVALID_ACCESS_TOKEN.getMessage(),
      IlpException.class,
      () -> client.sendPayment(mockPaymentRequest, "Bearer bob")
    );
  }

  @Test
  public void sendPaymentThrowsNotFound() throws IOException {
    // GIVEN a DefaultIlpClient with mocked networking which will throw a StatusRuntimeException
    // with grpc.Status = NOT_FOUND
    DefaultIlpClient client = getFailingClient(Status.NOT_FOUND);

    // WHEN a payment is sent THEN IlpException.NOT_FOUND is thrown
    assertSendPaymentThrows(client, IlpException.NOT_FOUND);
  }

  @Test
  public void sendPaymentThrowsInvalidArgument() throws IOException {
    // GIVEN a DefaultIlpClient with mocked networking which will throw a StatusRuntimeException
    // with grpc.Status = INVALID_ARGUMENT
    DefaultIlpClient client = getFailingClient(Status.INVALID_ARGUMENT);

    // WHEN a payment is sent THEN IlpException.INVALID_ARGUMENT is thrown
    assertSendPaymentThrows(client, IlpException.INVALID_ARGUMENT);
  }

  @Test
  public void sendPaymentThrowsUnauthenticated() throws IOException {
    // GIVEN a DefaultIlpClient with mocked networking which will throw a StatusRuntimeException
    // with grpc.Status = UNAUTHENTICATED
    DefaultIlpClient client = getFailingClient(Status.UNAUTHENTICATED);

    // WHEN a payment is sent THEN IlpException.UNAUTHENTICATED is thrown
    assertSendPaymentThrows(client, IlpException.UNAUTHENTICATED);
  }

  @Test
  public void sendPaymentThrowsInternal() throws IOException {
    // GIVEN a DefaultIlpClient with mocked networking which will throw a StatusRuntimeException
    // with grpc.Status = INTERNAL
    DefaultIlpClient client = getFailingClient(Status.INTERNAL);

    // WHEN a payment is sent THEN IlpException.INTERNAL is thrown
    assertSendPaymentThrows(client, IlpException.INTERNAL);
  }

  @Test
  public void sendPaymentThrowsUnknown() throws IOException {
    // GIVEN a DefaultIlpClient with mocked networking which will throw a StatusRuntimeException
    // with grpc.Status = UNKNOWN
    DefaultIlpClient client = getFailingClient(Status.UNKNOWN);

    // WHEN a payment is sent THEN IlpException.UNKNOWN is thrown
    assertSendPaymentThrows(client, IlpException.UNKNOWN);
  }

  /**
   * Convenience method to get a IlpClient which has successful network calls.
   */
  private DefaultIlpClient getSuccessfulClient() throws IOException {
    return getClient(
      GRPCResult.ok(getBalanceResponse),
      GRPCResult.ok(sendPaymentResponse)
    );
  }

  /**
   * Convenience method to get a DefaultIlpClient whose network calls cause exceptions with exceptionStatus
   *
   * @param exceptionStatus The {@link Status} of the {@link StatusRuntimeException} thrown by the network calls
   * @return a {@link DefaultIlpClient} whose network calls cause exceptions
   * @throws IOException if the test gRPC service fails to start
   */
  private DefaultIlpClient getFailingClient(Status exceptionStatus) throws IOException {
    return getClient(
      GRPCResult.error(new StatusRuntimeException(exceptionStatus)),
      GRPCResult.error(new StatusRuntimeException(exceptionStatus))
    );
  }

  /**
   * Return a IlpClient which returns the given results for network calls.
   */
  private DefaultIlpClient getClient(GRPCResult<GetBalanceResponse, Throwable> getBalanceResult,
                                     GRPCResult<SendPaymentResponse, Throwable> sendPaymentResponse) throws IOException {

    BalanceServiceGrpc.BalanceServiceImplBase balanceServiceImpl = getBalanceService(
      getBalanceResult
    );

    IlpOverHttpServiceGrpc.IlpOverHttpServiceImplBase ilpOverHttpServiceImpl = ilpOverHttpService(sendPaymentResponse);

    // Generate a unique in-process server name.
    String serverName = InProcessServerBuilder.generateName();

    // Create a server, add service, start, and register for automatic graceful shutdown.
    grpcCleanup.register(InProcessServerBuilder
      .forName(serverName)
      .directExecutor()
      .addService(balanceServiceImpl)
      .addService(ilpOverHttpServiceImpl)
      .build()
      .start());

    // Create a client channel and register for automatic graceful shutdown.
    ManagedChannel channel = grpcCleanup.register(
      InProcessChannelBuilder.forName(serverName).directExecutor().build());

    // Create a new XRPClient using the in-process channel;
    return new DefaultIlpClient(channel);
  }

  private IlpOverHttpServiceGrpc.IlpOverHttpServiceImplBase ilpOverHttpService(GRPCResult<SendPaymentResponse, Throwable> sendPaymentResponse) {
    return mock(IlpOverHttpServiceGrpc.IlpOverHttpServiceImplBase.class, delegatesTo(
      new IlpOverHttpServiceGrpc.IlpOverHttpServiceImplBase() {
        @Override
        public void sendMoney(SendPaymentRequest request, StreamObserver<SendPaymentResponse> responseObserver) {
          if (sendPaymentResponse.isError()) {
            responseObserver.onError(new Throwable(sendPaymentResponse.getError()));
          } else {
            responseObserver.onNext(sendPaymentResponse.getValue());
            responseObserver.onCompleted();
          }
        }
      }));
  }

  /**
   * Return a BalanceServiceGrpc implementation which returns the given results for network calls.
   */
  private BalanceServiceGrpc.BalanceServiceImplBase getBalanceService(
    GRPCResult<GetBalanceResponse, Throwable> getBalanceResult
  ) {
    return mock(BalanceServiceGrpc.BalanceServiceImplBase.class, delegatesTo(
      new BalanceServiceGrpc.BalanceServiceImplBase() {
        @Override
        public void getBalance(GetBalanceRequest request, StreamObserver<GetBalanceResponse> responseObserver) {
          if (getBalanceResult.isError()) {
            responseObserver.onError(new Throwable(getBalanceResult.getError()));
          } else {
            responseObserver.onNext(getBalanceResult.getValue());
            responseObserver.onCompleted();
          }
        }
      }));
  }

  /**
   * Assert that a call to {@link DefaultIlpClient#getBalance} with the
   * given {@link DefaultIlpClient} throws the given exception
   *
   * @param client a {@link DefaultIlpClient} to test
   * @param expectedException the {@link IlpException} that client should throw
   */
  private void assertGetBalanceThrows(DefaultIlpClient client, IlpException expectedException) {
    assertThrows(
      expectedException.getMessage(),
      IlpException.class,
      () -> client.getBalance("bob", "password")
    );
  }

  /**
   * Assert that a call to {@link DefaultIlpClient#sendPayment} with the
   * given {@link DefaultIlpClient} throws the given exception
   *
   * @param client a {@link DefaultIlpClient} to test
   * @param expectedException the {@link IlpException} that client should throw
   */
  private void assertSendPaymentThrows(DefaultIlpClient client, IlpException expectedException) {
    assertThrows(
      expectedException.getMessage(),
      IlpException.class,
      () -> client.sendPayment(mockPaymentRequest, "password")
    );
  }
}
