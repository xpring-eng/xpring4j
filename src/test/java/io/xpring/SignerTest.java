package io.xpring;

import org.junit.Test;
import io.xpring.Signer;
import io.xpring.Wallet;
import io.xpring.XrpAmount.XRPAmount;
import io.xpring.TransactionOuterClass.Transaction;
import io.xpring.PaymentOuterClass.Payment;
import io.xpring.SignedTransactionOuterClass.SignedTransaction;

import static org.junit.Assert.assertEquals;

public class SignerTest {
    @Test
    public void testSign() throws Exception {
        Wallet wallet = new Wallet("snYP7oArxKepd3GPDcrjMsJYiJeJB");
        XrpAmount.XRPAmount amount = XRPAmount.newBuilder().setDrops("1").build();
        XrpAmount.XRPAmount fee = XRPAmount.newBuilder().setDrops("12").build();

        Payment payment = Payment.newBuilder().setXRPAmount(amount).setDestination("rsegqrgSP8XmhCYwL9enkZ9BNDNawfPZnn").build();

        Transaction transaction = Transaction.newBuilder()
                .setSequence(40)
                .setAccount(wallet.getAddress())
                .setSigningPublicKeyHex(wallet.getPublicKey())
                .setFee(fee)
                .setPayment(payment)
                .build();

        SignedTransaction signedTransaction = Signer.signTransaction(transaction, wallet);
        assertEquals(signedTransaction.getTransaction(), transaction);
        assertEquals(
                signedTransaction.getTransactionSignatureHex(),
                "304402201AC810C79CC7053A3ECFFB59873D2DB3B5531E87A58E8FCB4D118AB9634DB66202201345DB2A7757D2A6CEA82BD1C1FB8E17204273BD6148BE2CE68F7F32CF937AF9"
        );
    }
}