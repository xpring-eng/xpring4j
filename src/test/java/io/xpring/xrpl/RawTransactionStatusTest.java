package io.xpring.xrpl;

import io.xpring.proto.TransactionStatus;
import org.junit.Test;
import protocol.Ripple;
import rpc.v1.TransactionOuterClass.Payment;
import rpc.v1.TransactionOuterClass.Transaction;
import rpc.v1.Tx.GetTxResponse;

import static org.junit.Assert.*;

public class RawTransactionStatusTest {
    @Test
    public void testIsFullPaymentLegacyProto() {
        // GIVEN a legacy transaction status protocol buffer.
        TransactionStatus transactionStatus = TransactionStatus.newBuilder().build();

        // WHEN the transaction status is wrapped into a RawTransactionStatus object.
        RawTransactionStatus rawTransactionStatus = new RawTransactionStatus(transactionStatus);

        // THEN the raw transaction status reports it is a full payment.
        assertTrue(rawTransactionStatus.isFullPayment());
    }

    @Test
    public void testIsFullPaymentNonPayment() {
        // GIVEN a getTxResponse which is not a payment.
        Transaction transaction = Transaction.newBuilder().clearPayment().build();
        GetTxResponse getTxResponse = GetTxResponse.newBuilder().setTransaction(transaction).build();

        // WHEN the raw transaction status is wrapped into a RawTransactionStatus object.
        RawTransactionStatus rawTransactionStatus = new RawTransactionStatus(getTxResponse);

        // THEN the raw transaction status reports it is not a full payment.
        assertFalse(rawTransactionStatus.isFullPayment());
    }

    @Test
    public void testIsFullPaymentPartialPayment() {
        // GIVEN a getTxResponse which is a payment with the partial payment flags set.
        Payment payment = Payment.newBuilder().build();
        Transaction transaction = Transaction.newBuilder().setPayment(payment).setFlags(RippledFlags.TF_PARTIAL_PAYMENT.value).build();
        GetTxResponse getTxResponse = GetTxResponse.newBuilder().setTransaction(transaction).build();

        // WHEN the raw transaction status is wrapped into a RawTransactionStatus object.
        RawTransactionStatus rawTransactionStatus = new RawTransactionStatus(getTxResponse);

        // THEN the raw transaction status reports it is not a full payment.
        assertFalse(rawTransactionStatus.isFullPayment());
    }

    @Test
    public void testIsFullPaymentPayment() {
        // GIVEN a getTxResponse which is a payment.
        Payment payment = Payment.newBuilder().build();
        Transaction transaction = Transaction.newBuilder().setPayment(payment).build();
        GetTxResponse getTxResponse = GetTxResponse.newBuilder().setTransaction(transaction).build();

        // WHEN the raw transaction status is wrapped into a RawTransactionStatus object.
        RawTransactionStatus rawTransactionStatus = new RawTransactionStatus(getTxResponse);

        // THEN the raw transaction status reports it is a full payment.
        assertTrue(rawTransactionStatus.isFullPayment());
    }
}
