package io.xpring;

import org.junit.Test;
import io.xpring.Signer;
import io.xpring.Wallet;
import io.xpring.proto.XRPAmount;
import io.xpring.proto.Transaction;
import io.xpring.proto.Payment;
import io.xpring.proto.SignedTransaction;

import static org.junit.Assert.assertEquals;

public class SignerTest {
    @Test
    public void testSign() throws Exception {
        Wallet wallet = new Wallet("snYP7oArxKepd3GPDcrjMsJYiJeJB");
        XRPAmount amount = XRPAmount.newBuilder().setDrops("1").build();
        XRPAmount fee = XRPAmount.newBuilder().setDrops("12").build();

        Payment payment = Payment.newBuilder().setXrpAmount(amount).setDestination("rsegqrgSP8XmhCYwL9enkZ9BNDNawfPZnn").build();

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