package io.xpring.ilp;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.primitives.UnsignedLong;
import io.xpring.ilp.model.AccountBalance;
import io.xpring.ilp.model.PaymentRequest;
import io.xpring.ilp.model.PaymentResult;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;

public class IlpIntegrationTests {

  private IlpClient client;

  @Before
  public void setUp() {
    String grpcUrl = "hermes-envoy-test.xpring.io";
    client = new IlpClient(grpcUrl);
  }

  @Test
  public void getBalance() throws IlpException {
    // GIVEN an account on the testnet connector with accountId = sdk_account1

    // WHEN a balance is retrieved
    String accountId = "sdk_account1";
    AccountBalance response = client.getBalance(accountId, "password");

    // THEN the accountId associated with the balance is equal to the created accountId
    assertThat(response.accountId()).isEqualTo(accountId);
    // AND the assetCode the balance is denominated in is equal to the created assetCode
    assertThat(response.assetCode()).isEqualTo("XRP");
    // AND the assetScale the balance is denominated in is equal to the created assetScale
    assertThat(response.assetScale()).isEqualTo(9);
    // AND the net balance is <= 0
    assertThat(response.netBalance()).isLessThanOrEqualTo(BigInteger.ZERO);
    // AND the clearing balance is <= 0
    assertThat(response.clearingBalance()).isLessThanOrEqualTo(BigInteger.ZERO);
    // AND the prepaid amount is 0
    assertThat(response.prepaidAmount()).isEqualTo(0);
  }

  @Test
  public void sendPayment() throws IlpException {
    // GIVEN an account on the connector with accountId = sdk_account1
    // AND an account on the connector with accountId = sdk_account2

    // WHEN a payment is sent from the sender to the receiver
    PaymentRequest paymentRequest = PaymentRequest.builder()
        .amount(UnsignedLong.valueOf(10))
        .destinationPaymentPointer("$spsp-test.xpring.io/sdk_account2")
        .senderAccountId("sdk_account1")
        .build();

    PaymentResult response = client.sendPayment(paymentRequest, "password");

    PaymentResult expected = PaymentResult.builder()
        .originalAmount(UnsignedLong.valueOf(10))
        .amountSent(UnsignedLong.valueOf(10))
        .amountDelivered(UnsignedLong.valueOf(10))
        .successfulPayment(true)
        .build();

    // THEN the response should equal the mocked response above
    assertThat(response).isEqualToComparingFieldByField(expected);
  }
}
