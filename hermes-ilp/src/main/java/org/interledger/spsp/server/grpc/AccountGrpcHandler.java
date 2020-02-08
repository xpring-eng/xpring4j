package org.interledger.spsp.server.grpc;

import org.interledger.connector.accounts.AccountId;
import org.interledger.connector.accounts.AccountNotFoundProblem;
import org.interledger.connector.accounts.AccountSettings;
import org.interledger.connector.client.ConnectorAdminClient;
import org.interledger.spsp.server.client.ConnectorRoutesClient;
import org.interledger.spsp.server.grpc.services.AccountRequestResponseConverter;
import org.interledger.spsp.server.services.NewAccountService;

import feign.FeignException;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.lognet.springboot.grpc.GRpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@GRpcService
public class AccountGrpcHandler extends AccountServiceGrpc.AccountServiceImplBase {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  protected ConnectorAdminClient adminClient;

  @Autowired
  protected NewAccountService newAccountService;

  @Autowired
  protected ConnectorRoutesClient routesClient;

  @Override
  public void getAccount(GetAccountRequest request, StreamObserver<GetAccountResponse> responseObserver) {
    Status grpcStatus;
    try {
      GetAccountResponse accountResponse = adminClient.findAccount(request.getAccountId())
        .map(AccountRequestResponseConverter::createGetAccountResponseFromAccountSettings)
        .orElseThrow(() -> new AccountNotFoundProblem(AccountId.of(request.getAccountId())));

      responseObserver.onNext(accountResponse);
      responseObserver.onCompleted();
      return;
    } catch (FeignException e) {
      if (e.status() == 401) {
        grpcStatus = Status.PERMISSION_DENIED;
      } else {
        grpcStatus = Status.INTERNAL;
      }
    } catch (AccountNotFoundProblem e) {
      grpcStatus = Status.NOT_FOUND;
    }

    responseObserver.onError(new StatusRuntimeException(grpcStatus));
  }

  @Override
  public void createAccount(CreateAccountRequest request, StreamObserver<CreateAccountResponse> responseObserver) {
    Status grpcStatus;
    try {

      // Create account on the connector
      AccountSettings returnedAccountSettings = newAccountService.createAccount(request);

      // Convert returned AccountSettings into Grpc response object
      final CreateAccountResponse.Builder replyBuilder =
        AccountRequestResponseConverter.generateCreateAccountResponseFromAccountSettings(returnedAccountSettings);

      responseObserver.onNext(replyBuilder.build());
      responseObserver.onCompleted();
      return;
    } catch (FeignException e) {
      switch (e.status()) {
        case 401:
          grpcStatus = Status.PERMISSION_DENIED;
          break;
        case 409:
          grpcStatus = Status.ALREADY_EXISTS;
          break;
        default:
          grpcStatus = Status.INTERNAL;
      }
    }

    responseObserver.onError(new StatusRuntimeException(grpcStatus));
  }



}
