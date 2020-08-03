package io.xpring.xrpl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import io.xpring.xrpl.model.PaymentFlag;
import org.junit.Test;
import org.xrpl.rpc.v1.Common.Flags;
import org.xrpl.rpc.v1.GetTransactionResponse;
import org.xrpl.rpc.v1.Payment;
import org.xrpl.rpc.v1.Transaction;


public class RawTransactionStatusTest {
  @Test
  public void testIsFullPaymentNonPayment() {
    // GIVEN a getTxResponse which is not a payment.
    Transaction transaction = Transaction.newBuilder().clearPayment().build();
    GetTransactionResponse getTxResponse = GetTransactionResponse.newBuilder().setTransaction(transaction).build();

    // WHEN the raw transaction status is wrapped into a RawTransactionStatus object.
    RawTransactionStatus rawTransactionStatus = new RawTransactionStatus(getTxResponse);

    // THEN the raw transaction status reports it is not a full payment.
    assertFalse(rawTransactionStatus.isFullPayment());
  }

  @Test
  public void testIsFullPaymentPartialPayment() {
    // GIVEN a getTxResponse which is a payment with the partial payment flags set.
    Payment payment = Payment.newBuilder().build();
    Flags flags = Flags.newBuilder().setValue(PaymentFlag.TF_PARTIAL_PAYMENT.value).build();
    Transaction transaction = Transaction.newBuilder()
        .setPayment(payment)
        .setFlags(flags)
        .build();
    GetTransactionResponse getTransactionResponse = GetTransactionResponse.newBuilder()
        .setTransaction(transaction)
        .build();

    // WHEN the raw transaction status is wrapped into a RawTransactionStatus object.
    RawTransactionStatus rawTransactionStatus = new RawTransactionStatus(getTransactionResponse);

    // THEN the raw transaction status reports it is not a full payment.
    assertFalse(rawTransactionStatus.isFullPayment());
  }

  @Test
  public void testIsFullPaymentPayment() {
    // GIVEN a getTxResponse which is a payment.
    Payment payment = Payment.newBuilder().build();
    Transaction transaction = Transaction.newBuilder().setPayment(payment).build();
    GetTransactionResponse getTransactionResponse = GetTransactionResponse.newBuilder()
        .setTransaction(transaction)
        .build();

    // WHEN the raw transaction status is wrapped into a RawTransactionStatus object.
    RawTransactionStatus rawTransactionStatus = new RawTransactionStatus(getTransactionResponse);

    // THEN the raw transaction status reports it is a full payment.
    assertTrue(rawTransactionStatus.isFullPayment());
  }
}
