package io.xpring.xrpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.Mockito.mock;

import org.interledger.spsp.server.grpc.GetBalanceRequest;
import org.interledger.spsp.server.grpc.GetBalanceResponse;
import org.interledger.stream.proto.BalanceServiceGrpc;

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

/**
 * Unit tests for {@link io.xpring.xrpl.ilp.DefaultIlpClient}
 */
public class DefaultIlpClientTest {

  @Rule
  public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

  private GetBalanceResponse getBalanceResponse;

  @Before
  public void setUp() {
    getBalanceResponse = GetBalanceResponse.newBuilder()
      .setAccountId("bob")
      .setAssetCode("XRP")
      .setAssetScale(9)
      .setNetBalance(1000)
      .setClearingBalance(10)
      .setPrepaidAmount(100)
      .build();
  }

  @Test
  public void getIlpBalanceTest() throws IOException, XpringKitException {
    DefaultIlpClient client = getClient();

    GetBalanceResponse balanceResponse = client.getBalance("bob", "jwtjwtjwtjwt");

    assertThat(balanceResponse).isEqualTo(this.getBalanceResponse);
  }

  /**
   * Convenience method to get a IlpClient which has successful network calls.
   */
  private DefaultIlpClient getClient() throws IOException {
    return getClient(
      GRPCResult.ok(getBalanceResponse)
    );
  }

  /**
   * Return a IlpClient which returns the given results for network calls.
   */

  private DefaultIlpClient getClient(GRPCResult<GetBalanceResponse> getBalanceResult) throws IOException {

    BalanceServiceGrpc.BalanceServiceImplBase serviceImpl = getService(
      getBalanceResult
    );

    // Generate a unique in-process server name.
    String serverName = InProcessServerBuilder.generateName();

    // Create a server, add service, start, and register for automatic graceful shutdown.
    grpcCleanup.register(InProcessServerBuilder
      .forName(serverName).directExecutor().addService(serviceImpl).build().start());

    // Create a client channel and register for automatic graceful shutdown.
    ManagedChannel channel = grpcCleanup.register(
      InProcessChannelBuilder.forName(serverName).directExecutor().build());

    // Create a new XpringClient using the in-process channel;
    return new DefaultIlpClient(channel);
  }

  /**
   * Return a BalanceServiceGrpc implementation which returns the given results for network calls.
   */
  private BalanceServiceGrpc.BalanceServiceImplBase getService(
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
}
