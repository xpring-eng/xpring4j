package io.xpring.xrpl;

import io.xpring.proto.TransactionStatus;
import org.junit.Test;
import org.xrpl.rpc.v1.*;
import org.xrpl.rpc.v1.Transaction;
import org.xrpl.rpc.v1.Common.*;

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
        Flags flags = Flags.newBuilder().setValue(RippledFlags.TF_PARTIAL_PAYMENT.value).build();
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
