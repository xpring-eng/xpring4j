package io.xpring.xrpl.legacy;

import org.junit.Test;
import io.xpring.proto.XRPAmount;
import io.xpring.proto.Transaction;
import io.xpring.proto.Payment;
import io.xpring.proto.SignedTransaction;
import io.xpring.xrpl.Wallet;

import static org.junit.Assert.assertEquals;

public class LegacySignerTest {
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

        SignedTransaction signedTransaction = LegacySigner.signTransaction(transaction, wallet);
        assertEquals(signedTransaction.getTransaction(), transaction);
        assertEquals(
                signedTransaction.getTransactionSignatureHex(),
                "30450221009EBB075B5140895F818DB8B7B934D515B497A0B65D19192BCCEE83C47BD289BA02201699BB09DDC5305F71CDB9459AFBE50237F2A83F20EBF7A161401D2878C18140"
        );
    }
}