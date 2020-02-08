package org.interledger.spsp.server.controllers;

import org.interledger.connector.accounts.AccountId;
import org.interledger.spsp.server.client.AccountBalanceResponse;
import org.interledger.spsp.server.client.ConnectorBalanceClient;

import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.zalando.problem.spring.common.MediaTypes;


@RestController
public class BalanceController extends AbstractController {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private ConnectorBalanceClient balanceClient;

  public BalanceController(ConnectorBalanceClient balanceClient) {
    this.balanceClient = balanceClient;
  }

  /**
   * Gets the {@link AccountBalanceResponse} for the given {@code accountId}
   *
   * @param accountId
   * @return balance for account
   */
  @RequestMapping(
    value = "/accounts/{accountId}/balance", method = {RequestMethod.GET},
    produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.PROBLEM_VALUE}
  )
  public AccountBalanceResponse getBalance(@PathVariable("accountId") String accountId) {
    try {
      return balanceClient.getBalance(getAuthorization(), AccountId.of(accountId))
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
    catch (FeignException e) {
      throw new ResponseStatusException(HttpStatus.valueOf(e.status()), e.contentUTF8());
    }
  }

}
