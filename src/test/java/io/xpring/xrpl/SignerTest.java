package io.xpring.xrpl;

import org.junit.Test;

import java.math.BigInteger;
import java.util.Arrays;

import rpc.v1.Amount;
import rpc.v1.Amount.AccountAddress;
import rpc.v1.Amount.CurrencyAmount;
import rpc.v1.Amount.XRPDropsAmount;
import rpc.v1.TransactionOuterClass.Payment;
import rpc.v1.TransactionOuterClass.Transaction;

import static org.junit.Assert.assertArrayEquals;

public class SignerTest {
    @Test
    public void testSign() throws Exception {
        // GIVEN a wallet, a transaction and an expected serialized and signed output.
        Wallet wallet = new Wallet("snYP7oArxKepd3GPDcrjMsJYiJeJB");

        int sequence = 1;
        XRPDropsAmount feeAmount = XRPDropsAmount.newBuilder().setDrops(10).build();
        XRPDropsAmount sendAmount = XRPDropsAmount.newBuilder().setDrops(1000).build();
        AccountAddress senderAddress = AccountAddress.newBuilder().setAddress("X7vjQVCddnQ7GCESYnYR3EdpzbcoAMbPw7s2xv8YQs94tv4").build();
        AccountAddress destinationAddress = AccountAddress.newBuilder().setAddress("XVPcpSm47b1CZkf5AkKM9a84dQHe3m4sBhsrA4XtnBECTAc").build();
        CurrencyAmount paymentAmount = CurrencyAmount.newBuilder().setXrpAmount(sendAmount).build();
        Payment payment = Payment.newBuilder().setDestination(destinationAddress).setAmount(paymentAmount).build();
        Transaction transaction = Transaction.newBuilder()
                .setAccount(senderAddress)
                .setFee(feeAmount)
                .setSequence(sequence)
                .setPayment(payment)
                .build();

        byte [] expected = {
                18, 0, 0, 36, 0, 0, 0, 1, 32, 27, 0, 0, 0, 0, 97, 64, 0, 0, 0, 0, 0, 3, -24, 104, 64, 0, 0, 0, 0, 0, 0,
                10, 115, 0, 116, 71, 48, 69, 2, 33, 0, -11, 52, -67, 33, -66, -56, 90, 121, -8, -29, 64, -57, -16, -51,
                -35, 62, 60, 86, -64, -100, 67, -108, 18, 99, 33, -87, -18, 71, -125, -70, -64, -116, 2, 32, 99, -37,
                -20, 23, -102, -95, -16, 28, -36, -49, 75, 63, -50, -92, 46, 49, -3, -34, 30, -74, -103, -117, 90, 36,
                -19, 56, 50, 27, -19, 102, 29, 106, -127, 20, 91, -127, 44, -99, 87, 115, 30, 39, -94, -38, -117, 24,
                48, 25, 95, -120, -17, 50, -93, -74, -125, 20, -75, -9, 98, 121, -118, 83, -43, 67, -96, 20, -54, -8,
                -78, -105, -49, -8, -14, -7, 55, -24
        };

        byte [] signedTransaction = Signer.signTransaction(transaction, wallet);

        assertArrayEquals(signedTransaction, expected);
    }
}
