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

import static org.junit.Assert.assertEquals;

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
//
//        byte [] expected = {
//                18, 0, 0, 36, 0, 0, 0, 1, 32, 27, 0, 0, 0, 0, 97, 64, 0, 0, 0, 0, 0, 3, 232, 104, 64, 0, 0, 0, 0, 0, 0,
//                10, 115, 0, 116, 71, 48, 69, 2, 33, 0, (byte) 245, 52, 189, 33, 190, 200, 90, 121, 248, 227, 64, 199, 240, 205,
//                (byte) 221, 62, 60, 86, 192, 156, 67, 148, 18, 99, 33, 169, 238, 71, 131, 186, 192, 140, 2, 32, 99, 219, 236,
//                23, 154, 161, 240, 28, 220, 207, 75, 63, 206, 164, 46, 49, 253, 222, 30, 182, 153, 139, 90, 36, 237, 56,
//                50, 27, 237, 102, 29, 106, 129, 20, 91, 129, 44, 157, 87, 115, 30, 39, 162, 218, 139, 24, 48, 25, 95,
//                136, 239, 50, 163, 182, 131, 20, 181, 247, 98, 121, 138, 83, 213, 67, 160, 20, 202, 248, 178, 151, 207,
//                248, 242, 249, 55, 232
//        };

        byte [] signedTransaction = Signer.signTransaction(transaction, wallet);

        System.out.println("Hola " + Arrays.toString(signedTransaction));
//        assertEquals(signedTransaction, expected);
    }
}
