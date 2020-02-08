package org.interledger.spsp.server.services;

import org.interledger.connector.accounts.AccountId;
import org.interledger.spsp.PaymentPointer;

import com.google.common.primitives.UnsignedLong;
import okhttp3.HttpUrl;

import java.util.concurrent.ExecutionException;

public class GimmeMoneyService {

  private final SendMoneyService sendMoneyService;
  private final AccountId rainmakerAccountId;
  private final String rainmakerBearerToken;
  private final HttpUrl spspUrl;


  public GimmeMoneyService(SendMoneyService sendMoneyService, AccountId rainmakerAccountId, String rainmakerBearerToken, HttpUrl spspUrl) {
    this.sendMoneyService = sendMoneyService;
    this.rainmakerAccountId = rainmakerAccountId;
    this.rainmakerBearerToken = rainmakerBearerToken;
    this.spspUrl = spspUrl;
  }

  public UnsignedLong gimmeMoney(AccountId destinationAccount, UnsignedLong amount) throws ExecutionException, InterruptedException {
    return sendMoneyService.sendMoney(rainmakerAccountId, rainmakerBearerToken, amount,
        PaymentPointer.of("$" + spspUrl.host() + "/" + destinationAccount.value())).amountSent();
  }
}
