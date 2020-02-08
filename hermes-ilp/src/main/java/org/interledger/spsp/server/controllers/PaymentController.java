package org.interledger.spsp.server.controllers;

import org.interledger.connector.accounts.AccountId;
import org.interledger.spsp.PaymentPointer;
import org.interledger.spsp.server.model.ImmutablePaymentRequest;
import org.interledger.spsp.server.model.PaymentResponse;
import org.interledger.spsp.server.services.SendMoneyService;
import org.interledger.stream.SendMoneyResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.zalando.problem.spring.common.MediaTypes;


@RestController
public class PaymentController extends AbstractController {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private final SendMoneyService sendMoneyService;

  public PaymentController(SendMoneyService sendMoneyService) {
    this.sendMoneyService = sendMoneyService;
  }

  /**
   * Sends payment from the given account.
   *
   * @param accountId
   * @param paymentRequest
   * @return payment result
   */
  @RequestMapping(
    value = "/accounts/{accountId}/pay", method = {RequestMethod.POST},
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.PROBLEM_VALUE}
  )
  public PaymentResponse sendPayment(@PathVariable("accountId") String accountId,
                                     @RequestBody ImmutablePaymentRequest paymentRequest) {
    try {
      getJwt(); // hack to make sure JWT isn't expired
      SendMoneyResult result = sendMoneyService.sendMoney(AccountId.of(accountId),
        getBearerToken(),
        paymentRequest.amount(),
        PaymentPointer.of(paymentRequest.destinationPaymentPointer()));

      return PaymentResponse.builder()
        .amountDelivered(result.amountDelivered())
        .amountSent(result.amountSent())
        .originalAmount(paymentRequest.amount())
        .successfulPayment(result.successfulPayment())
        .build();
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

}
