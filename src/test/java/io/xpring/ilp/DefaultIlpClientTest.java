package io.xpring.ilp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.Mockito.mock;

import org.interledger.spsp.server.grpc.AccountServiceGrpc;
import org.interledger.spsp.server.grpc.BalanceServiceGrpc;
import org.interledger.spsp.server.grpc.CreateAccountRequest;
import org.interledger.spsp.server.grpc.CreateAccountResponse;
import org.interledger.spsp.server.grpc.GetAccountRequest;
import org.interledger.spsp.server.grpc.GetAccountResponse;
import org.interledger.spsp.server.grpc.GetBalanceRequest;
import org.interledger.spsp.server.grpc.GetBalanceResponse;
import org.interledger.spsp.server.grpc.IlpOverHttpServiceGrpc;
import org.interledger.spsp.server.grpc.SendPaymentRequest;
import org.interledger.spsp.server.grpc.SendPaymentResponse;

import io.grpc.ManagedChannel;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
import io.grpc.testing.GrpcCleanupRule;
import io.xpring.ilp.DefaultIlpClient;
import io.xpring.xrpl.XpringKitException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Represents the result of a gRPC network call for an object of type T or an error.
 */
class GRPCResult<T> {
  private Optional<T> value;
  private Optional<String> error;

  private GRPCResult(T value, String error) {
    this.value = Optional.ofNullable(value);
    this.error = Optional.ofNullable(error);
  }

  public static <U> GRPCResult<U> ok(U value) {
    return new GRPCResult<>(value, null);
  }

  public static <U> GRPCResult<U> error(String error) {
    return new GRPCResult<>(null, error);
  }

  public boolean isError() {
    return error.isPresent();
  }

  public T getValue() {
    return value.get();
  }

  public String getError() {
    return error.get();
  }
}

/**
 * Unit tests for {@link io.xpring.ilp.DefaultIlpClient}
 */
public class DefaultIlpClientTest {

  @Rule
  public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

  private GetBalanceResponse getBalanceResponse;
  private CreateAccountResponse createAccountResponse;
  private SendPaymentResponse sendPaymentResponse;
  private GetAccountResponse getAccountResponse;

  @Before
  public void setUp() throws IOException {
    getBalanceResponse = GetBalanceResponse.newBuilder()
      .setAccountId("bob")
      .setAssetCode("XRP")
      .setAssetScale(9)
      .setNetBalance(1000)
      .setClearingBalance(10)
      .setPrepaidAmount(100)
      .build();

    Map<String, String> customSettings = new HashMap<>();
    customSettings.put("ilpOverHttp.incoming.auth_type", "SIMPLE");
    customSettings.put("ilpOverHttp.incoming.auth_token", "gobbledygook");

    createAccountResponse = CreateAccountResponse.newBuilder()
      .setAccountRelationship("CHILD")
      .setAssetCode("XRP")
      .setAssetScale(9)
      .putAllCustomSettings(customSettings)
      .setAccountId("foo")
      .setDescription("")
      .setLinkType("ILP_OVER_HTTP")
      .setIsConnectionInitiator(true)
      .setIlpAddressSegment("foo")
      .setBalanceSettings(CreateAccountResponse.BalanceSettings.newBuilder().build())
      .setIsChildAccount(true)
      .setIsInternal(false)
      .setIsSendRoutes(true)
      .setIsReceiveRoutes(false)
      .setMaxPacketsPerSecond(0)
      .setIsParentAccount(false)
      .setIsPeerAccount(false)
      .setIsPeerOrParentAccount(false)
      .setPaymentPointer("$xpring.money.dev/foo")
      .build();

    getAccountResponse = GetAccountResponse.newBuilder()
      .setAccountRelationship("CHILD")
      .setAssetCode("XRP")
      .setAssetScale(9)
      .putAllCustomSettings(customSettings)
      .setAccountId("foo")
      .setDescription("")
      .setLinkType("ILP_OVER_HTTP")
      .setIsConnectionInitiator(true)
      .setIlpAddressSegment("foo")
      .setBalanceSettings(GetAccountResponse.BalanceSettings.newBuilder().build())
      .setIsChildAccount(true)
      .setIsInternal(false)
      .setIsSendRoutes(true)
      .setIsReceiveRoutes(false)
      .setMaxPacketsPerSecond(0)
      .setIsParentAccount(false)
      .setIsPeerAccount(false)
      .setIsPeerOrParentAccount(false)
      .setPaymentPointer("$xpring.money.dev/foo")
      .build();

    sendPaymentResponse = SendPaymentResponse.newBuilder()
      .setOriginalAmount(1000)
      .setAmountDelivered(1000)
      .setAmountSent(1000)
      .setSuccessfulPayment(true)
      .build();
  }

  @Test
  public void minimalCreateIlpAccountTest() throws XpringKitException, IOException {
    DefaultIlpClient client = getClient();
    CreateAccountResponse createAccountResponse = client.createAccount();
    assertThat(createAccountResponse).isEqualTo(this.createAccountResponse);
  }

  @Test
  public void populatedCreateIlpAccountTest() throws IOException, XpringKitException {
    DefaultIlpClient client = getClient();

    io.xpring.ilp.CreateAccountRequest createAccountRequest = io.xpring.ilp.CreateAccountRequest.builder("USD", 6)
      .accountId("foo")
      .description("test account")
      .build();
    CreateAccountResponse createAccountResponse = client.createAccount(createAccountRequest, Optional.of("password"));
    assertThat(createAccountResponse).isEqualTo(this.createAccountResponse);
  }

  @Test
  public void createAccountNoAuthYesRequest() throws XpringKitException, IOException {
    io.xpring.ilp.CreateAccountRequest createAccountRequest = io.xpring.ilp.CreateAccountRequest.builder("USD", 6)
      .accountId("foo")
      .description("test account")
      .build();
    DefaultIlpClient client = getClient();
    CreateAccountResponse response = client.createAccount(createAccountRequest, Optional.empty());
    assertThat(response).isEqualTo(this.createAccountResponse);
  }

  @Test
  public void createAccountNoAuthNoAccountId() throws XpringKitException, IOException {
    DefaultIlpClient client = getClient();

    io.xpring.ilp.CreateAccountRequest createAccountRequest = io.xpring.ilp.CreateAccountRequest.builder("USD", 6)
      .build();
    CreateAccountResponse response = client.createAccount(createAccountRequest, Optional.empty());
    assertThat(response).isEqualTo(this.createAccountResponse);
  }


  @Test
  public void getIlpBalanceTest() throws XpringKitException, IOException {
    DefaultIlpClient client = getClient();
    GetBalanceResponse balanceResponse = client.getBalance("bob", "jwtjwtjwtjwt");

    assertThat(balanceResponse).isEqualTo(this.getBalanceResponse);
  }

  @Test
  public void getAccountTest() throws IOException, XpringKitException {
    DefaultIlpClient client = getClient();
    GetAccountResponse response = client.getAccount("foo", "gobbledygook");

    assertThat(response).isEqualTo(this.getAccountResponse);
  }

  @Test
  public void sendPaymentTest() throws IOException, XpringKitException {
    DefaultIlpClient client = getClient();
    SendPaymentResponse response = client.sendPayment("$foo.dev/bar", 1000, "baz", "gobbledygook");

    assertThat(response).isEqualTo(this.sendPaymentResponse);
  }

  /**
   * Convenience method to get a IlpClient which has successful network calls.
   */
  private DefaultIlpClient getClient() throws IOException {
    return getClient(
      GRPCResult.ok(getBalanceResponse),
      GRPCResult.ok(createAccountResponse),
      GRPCResult.ok(sendPaymentResponse),
      GRPCResult.ok(getAccountResponse)
    );
  }

  /**
   * Return a IlpClient which returns the given results for network calls.
   */

  private DefaultIlpClient getClient(GRPCResult<GetBalanceResponse> getBalanceResult,
                                     GRPCResult<CreateAccountResponse> createAccountResponse,
                                     GRPCResult<SendPaymentResponse> sendPaymentResponse,
                                     GRPCResult<GetAccountResponse> getAccountResponse) throws IOException {

    BalanceServiceGrpc.BalanceServiceImplBase balanceServiceImpl = getBalanceService(
      getBalanceResult
    );

    AccountServiceGrpc.AccountServiceImplBase accountServiceImpl = getAccountService(
      createAccountResponse,
      getAccountResponse
    );

    IlpOverHttpServiceGrpc.IlpOverHttpServiceImplBase ilpOverHttpServiceImpl = ilpOverHttpService(sendPaymentResponse);

    // Generate a unique in-process server name.
    String serverName = InProcessServerBuilder.generateName();

    // Create a server, add service, start, and register for automatic graceful shutdown.
    grpcCleanup.register(InProcessServerBuilder
      .forName(serverName)
      .directExecutor()
      .addService(balanceServiceImpl)
      .addService(accountServiceImpl)
      .addService(ilpOverHttpServiceImpl)
      .build()
      .start());

    // Create a client channel and register for automatic graceful shutdown.
    ManagedChannel channel = grpcCleanup.register(
      InProcessChannelBuilder.forName(serverName).directExecutor().build());

    // Create a new XpringClient using the in-process channel;
    return new DefaultIlpClient(channel);
  }

  private IlpOverHttpServiceGrpc.IlpOverHttpServiceImplBase ilpOverHttpService(GRPCResult<SendPaymentResponse> sendPaymentResponse) {
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
    GRPCResult<GetBalanceResponse> getBalanceResult
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
   * Return a BalanceServiceGrpc implementation which returns the given results for network calls.
   */
  private AccountServiceGrpc.AccountServiceImplBase getAccountService(
    GRPCResult<CreateAccountResponse> createAccountResponse,
    GRPCResult<GetAccountResponse> getAccountResponse
  ) {
    return mock(AccountServiceGrpc.AccountServiceImplBase.class, delegatesTo(
      new AccountServiceGrpc.AccountServiceImplBase() {
        @Override
        public void createAccount(CreateAccountRequest request, StreamObserver<CreateAccountResponse> responseObserver) {
          if (createAccountResponse.isError()) {
            responseObserver.onError(new Throwable(createAccountResponse.getError()));
          } else {
            responseObserver.onNext(createAccountResponse.getValue());
            responseObserver.onCompleted();
          }
        }

        @Override
        public void getAccount(GetAccountRequest request, StreamObserver<GetAccountResponse> responseObserver) {
          if (getAccountResponse.isError()) {
            responseObserver.onError(new Throwable(getAccountResponse.getError()));
          } else {
            responseObserver.onNext(getAccountResponse.getValue());
            responseObserver.onCompleted();
          }
        }
      }));
  }
}
