package org.interledger.spsp.server.grpc;

import org.interledger.connector.accounts.AccountId;
import org.interledger.spsp.server.client.ConnectorBalanceClient;
import org.interledger.spsp.server.grpc.jwt.JwtContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.lognet.springboot.grpc.GRpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

@GRpcService
public class BalanceGrpcHandler extends BalanceServiceGrpc.BalanceServiceImplBase {
  Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  protected ConnectorBalanceClient balanceClient;

  @Autowired
  protected ObjectMapper objectMapper;

  @Override
  public void getBalance(GetBalanceRequest request, StreamObserver<GetBalanceResponse> responseObserver) {
    try {
      String jwt = JwtContext.getToken();
      balanceClient.getBalance(jwt, AccountId.of(request.getAccountId()))
        .ifPresent(balanceResponse -> {
          final GetBalanceResponse reply = GetBalanceResponse.newBuilder()
            .setAssetScale(balanceResponse.assetScale())
            .setAssetCode(balanceResponse.assetCode())
            .setNetBalance(balanceResponse.accountBalance().netBalance().longValue())
            .setPrepaidAmount(balanceResponse.accountBalance().prepaidAmount())
            .setClearingBalance(balanceResponse.accountBalance().clearingBalance())
            .setAccountId(balanceResponse.accountBalance().accountId().value())
            .build();

          logger.info("Balance retrieved successfully.");
          logger.info(reply.toString());

          responseObserver.onNext(reply);
          responseObserver.onCompleted();
        });
    } catch (FeignException e) {
      Status exceptionStatus;
      switch (e.status()) {
        case 401:
          try {
            Map<String, String > exceptionBody = objectMapper.readValue(new String(e.content()), HashMap.class);
            String notFoundMessage = "Account not found for principal:";
            exceptionStatus = exceptionBody.getOrDefault("detail", "").contains(notFoundMessage) ?
              Status.NOT_FOUND : Status.PERMISSION_DENIED;
          } catch (JsonProcessingException ex) {
            exceptionStatus = Status.INTERNAL;
          }
          break;
        case 404:
          exceptionStatus = Status.NOT_FOUND;
          break;
        default:
          exceptionStatus = Status.INTERNAL;
          break;
      }
      responseObserver.onError(new StatusRuntimeException(exceptionStatus));
    }
  }

}
