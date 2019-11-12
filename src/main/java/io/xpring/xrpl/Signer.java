package io.xpring.xrpl;

import io.xpring.xrpl.Wallet;
import io.xpring.xrpl.javascript.JavaScriptSigner;

import io.xpring.proto.Transaction;
import io.xpring.proto.SignedTransaction;

public class Signer {
    /** Please do not instantiate this static utility class. */
    private Signer() {}

    /**
     * Sign the given transaction with the given wallet.
     * @param transaction The transaction to sign.
     * @param wallet The wallet that will sign the transaction.
     * @return A `SignedTransaction`.
     */
    public static SignedTransaction signTransaction(Transaction transaction, Wallet wallet) {
        try {
            JavaScriptSigner javascriptSigner = new JavaScriptSigner();
            return javascriptSigner.signTransaction(transaction, wallet);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}