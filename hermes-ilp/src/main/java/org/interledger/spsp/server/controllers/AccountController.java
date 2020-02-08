package org.interledger.spsp.server.controllers;

import org.interledger.connector.accounts.AccountId;
import org.interledger.connector.accounts.AccountNotFoundProblem;
import org.interledger.connector.accounts.AccountSettings;
import org.interledger.connector.client.ConnectorAdminClient;
import org.interledger.spsp.server.model.CreateAccountRestRequest;
import org.interledger.spsp.server.services.NewAccountService;

import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.zalando.problem.spring.common.MediaTypes;


@RestController
public class AccountController extends AbstractController {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private final NewAccountService newAccountService;

  private final ConnectorAdminClient adminClient;

  public AccountController(NewAccountService newAccountService, ConnectorAdminClient adminClient) {
    this.newAccountService = newAccountService;
    this.adminClient = adminClient;
  }

  @RequestMapping(
    value = "/accounts", method = {RequestMethod.POST},
    produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.PROBLEM_VALUE}
  )
  public AccountSettings createAccount(@RequestHeader("Authorization") String jwt,
                                       @RequestBody CreateAccountRestRequest createAccountRequest) {
    try {
      String jwtNoBearer = jwt.substring(7);
      return newAccountService.createAccount(jwtNoBearer, createAccountRequest);
    }
    catch (FeignException e) {
      throw new ResponseStatusException(HttpStatus.valueOf(e.status()), e.contentUTF8());
    }
  }

  @RequestMapping(
    value = "/accounts/{accountId}",
    method = {RequestMethod.GET},
    produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.PROBLEM_VALUE}
  )
  public AccountSettings getAccount(@PathVariable("accountId") String accountId) {
    try {
      return adminClient.findAccount(accountId)
        .orElseThrow(() -> new AccountNotFoundProblem(AccountId.of(accountId)));
    } catch (AccountNotFoundProblem e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    } catch (FeignException e) {
      throw new ResponseStatusException(HttpStatus.valueOf(e.status()), e.contentUTF8());
    }
  }

  @RequestMapping(
    value = "/accounts/rainmaker", method = {RequestMethod.POST},
    produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.PROBLEM_VALUE}
  )
  public AccountSettings createRainmaker() {
    try {
      return newAccountService.createRainmaker();
    }
    catch (FeignException e) {
      throw new ResponseStatusException(HttpStatus.valueOf(e.status()), e.contentUTF8());
    }
  }

}
