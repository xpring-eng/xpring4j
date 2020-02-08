package org.interledger.spsp.server.grpc;

import org.interledger.connector.accounts.AccountId;
import org.interledger.spsp.PaymentPointer;
import org.interledger.spsp.server.grpc.jwt.JwtContext;
import org.interledger.spsp.server.grpc.services.AccountRequestResponseConverter;
import org.interledger.spsp.server.services.SendMoneyService;
import org.interledger.stream.SendMoneyResult;

import com.google.common.primitives.UnsignedLong;
import io.grpc.stub.StreamObserver;
import org.lognet.springboot.grpc.GRpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ExecutionException;

@GRpcService
public class IlpOverHttpGrpcHandler extends IlpOverHttpServiceGrpc.IlpOverHttpServiceImplBase {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  protected SendMoneyService sendMoneyService;

  @Override
  public void sendMoney(SendPaymentRequest request, StreamObserver<SendPaymentResponse> responseObserver) {
    // Send payment using STREAM
    SendMoneyResult result = null;

    try {
      String jwt = JwtContext.getToken();
      result = sendMoneyService.sendMoney(AccountId.of(request.getAccountId()),
        jwt.substring("Bearer ".length()),
        UnsignedLong.valueOf(request.getAmount()),
        PaymentPointer.of(request.getDestinationPaymentPointer()));
    } catch (InterruptedException | ExecutionException e) {
      responseObserver.onError(e);
      return;
    }

    SendPaymentResponse sendPaymentResponse = AccountRequestResponseConverter.sendPaymentResponseFromSendMoneyResult(result);
    System.out.println("Send Payment Response: " + sendPaymentResponse);
    responseObserver.onNext(sendPaymentResponse);

    responseObserver.onCompleted();
  }

}