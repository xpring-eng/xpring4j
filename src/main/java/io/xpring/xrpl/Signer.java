package io.xpring.xrpl;

import rpc.v1.TransactionOuterClass.Transaction;
import io.xpring.xrpl.javascript.JavaScriptSigner;

public class Signer {
    /** Please do not instantiate this static utility class. */
    private Signer() {}

    /**
     * Sign the given transaction with the given wallet.
     * @param transaction The transaction to sign.
     * @param wallet The wallet that will sign the transaction.
     * @return A `SignedTransaction`.
     */
    public static byte[] signTransaction(Transaction transaction, Wallet wallet) {
        try {
            JavaScriptSigner javascriptSigner = new JavaScriptSigner();
            return javascriptSigner.signTransaction(transaction, wallet);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}
