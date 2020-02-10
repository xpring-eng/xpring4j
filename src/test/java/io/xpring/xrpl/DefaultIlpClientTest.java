package io.xpring.xrpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.Mockito.mock;

import org.interledger.spsp.server.grpc.AccountServiceGrpc;
import org.interledger.spsp.server.grpc.BalanceServiceGrpc;
import org.interledger.spsp.server.grpc.CreateAccountRequest;
import org.interledger.spsp.server.grpc.CreateAccountResponse;
import org.interledger.spsp.server.grpc.GetBalanceRequest;
import org.interledger.spsp.server.grpc.GetBalanceResponse;

import io.grpc.ManagedChannel;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
import io.grpc.testing.GrpcCleanupRule;
import io.xpring.xrpl.ilp.DefaultIlpClient;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Unit tests for {@link io.xpring.xrpl.ilp.DefaultIlpClient}
 */
public class DefaultIlpClientTest {

  @Rule
  public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

  private GetBalanceResponse getBalanceResponse;
  private CreateAccountResponse createAccountResponse;

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

    CreateAccountRequest request = CreateAccountRequest.newBuilder()
      .setAccountId("foo")
      .setAssetCode("XRP")
      .setAssetScale(9)
      .build();

    CreateAccountResponse createAccountResponse = client.createAccount(request, "gobbledygook");
    assertThat(createAccountResponse).isEqualTo(this.createAccountResponse);
  }

  @Test
  public void getIlpBalanceTest() throws XpringKitException, IOException {
    DefaultIlpClient client = getClient();
    GetBalanceResponse balanceResponse = client.getBalance("bob", "jwtjwtjwtjwt");

    assertThat(balanceResponse).isEqualTo(this.getBalanceResponse);
  }

  /**
   * Convenience method to get a IlpClient which has successful network calls.
   */
  private DefaultIlpClient getClient() throws IOException {
    return getClient(
      GRPCResult.ok(getBalanceResponse),
      GRPCResult.ok(createAccountResponse)
    );
  }

  /**
   * Return a IlpClient which returns the given results for network calls.
   */

  private DefaultIlpClient getClient(GRPCResult<GetBalanceResponse> getBalanceResult,
                                     GRPCResult<CreateAccountResponse> createAccountResponse) throws IOException {

    BalanceServiceGrpc.BalanceServiceImplBase balanceServiceImpl = getBalanceService(
      getBalanceResult
    );

    AccountServiceGrpc.AccountServiceImplBase accountServiceImpl = getAccountService(
      createAccountResponse
    );

    // Generate a unique in-process server name.
    String serverName = InProcessServerBuilder.generateName();

    // Create a server, add service, start, and register for automatic graceful shutdown.
    grpcCleanup.register(InProcessServerBuilder
      .forName(serverName)
      .directExecutor()
      .addService(balanceServiceImpl)
      .addService(accountServiceImpl)
      .build()
      .start());

    // Create a client channel and register for automatic graceful shutdown.
    ManagedChannel channel = grpcCleanup.register(
      InProcessChannelBuilder.forName(serverName).directExecutor().build());

    // Create a new XpringClient using the in-process channel;
    return new DefaultIlpClient(channel);
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
    GRPCResult<CreateAccountResponse> createAccountResponse
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
      }));
  }
}
