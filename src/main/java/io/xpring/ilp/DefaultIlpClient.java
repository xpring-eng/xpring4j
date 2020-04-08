package io.xpring.ilp;

import org.interledger.spsp.server.grpc.BalanceServiceGrpc;
import org.interledger.spsp.server.grpc.GetBalanceRequest;
import org.interledger.spsp.server.grpc.GetBalanceResponse;
import org.interledger.spsp.server.grpc.IlpOverHttpServiceGrpc;
import org.interledger.spsp.server.grpc.SendPaymentRequest;
import org.interledger.spsp.server.grpc.SendPaymentResponse;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.xpring.ilp.grpc.IlpCredentials;
import io.xpring.ilp.model.AccountBalance;
import io.xpring.ilp.model.PaymentRequest;
import io.xpring.ilp.model.PaymentResult;

import java.util.concurrent.TimeUnit;

/**
 * A client that can get balances and send ILP payments on a connector.
 */
public class DefaultIlpClient implements IlpClientDecorator {

  private final BalanceServiceGrpc.BalanceServiceBlockingStub balanceServiceStub;
  private final IlpOverHttpServiceGrpc.IlpOverHttpServiceBlockingStub ilpOverHttpServiceStub;

  /**
   * Initialize a new client with a configured URL.
   *
   * @param grpcUrl The gRPC URL exposed by Hermes.
   */
  DefaultIlpClient(String grpcUrl) {
    this(ManagedChannelBuilder
        .forAddress(grpcUrl, 443)
        .build()
    );
  }

  /**
   * Required-args Constructor, currently for testing.
   *
   * @param channel A {@link ManagedChannel}.
   */
  DefaultIlpClient(final ManagedChannel channel) {
    this.balanceServiceStub = BalanceServiceGrpc.newBlockingStub(channel);
    this.ilpOverHttpServiceStub = IlpOverHttpServiceGrpc.newBlockingStub(channel);

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      channel.shutdown();
      try {
        channel.awaitTermination(5, TimeUnit.SECONDS);
      } catch (Exception timedOutException) {
        try {
          channel.shutdownNow();
        } catch (Exception e) {
          // nothing more can be done
        }
      }
    }));
  }

  @Override
  public AccountBalance getBalance(final String accountId, final String accessToken) throws IlpException {
    GetBalanceRequest request = GetBalanceRequest.newBuilder()
        .setAccountId(accountId)
        .build();

    try {
      GetBalanceResponse response = this.balanceServiceStub
          .withCallCredentials(IlpCredentials.build(accessToken))
          .getBalance(request);

      // Convert protobuf response to AccountBalanceResponse
      return AccountBalance.from(response);
    } catch (StatusRuntimeException statusRuntimeException) {
      throw IlpException.from(statusRuntimeException);
    }
  }

  @Override
  public PaymentResult sendPayment(final PaymentRequest paymentRequest,
                                   final String accessToken) throws IlpException {
    try {
      // Convert paymentRequest to a protobuf object
      SendPaymentRequest request = paymentRequest.toProto();

      SendPaymentResponse protoResponse = ilpOverHttpServiceStub
          .withCallCredentials(IlpCredentials.build(accessToken))
          .sendMoney(request);

      return PaymentResult.from(protoResponse);

    } catch (StatusRuntimeException statusRuntimeException) {
      throw IlpException.from(statusRuntimeException);
    }
  }
}
