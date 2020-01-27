package io.xpring.xrpl.legacy;

import io.xpring.xrpl.Wallet;
import io.xpring.xrpl.legacy.javascript.LegacyJavaScriptSigner;

import io.xpring.proto.Transaction;
import io.xpring.proto.SignedTransaction;

public class LegacySigner {
    /** Please do not instantiate this static utility class. */
    private LegacySigner() {}

    /**
     * Sign the given transaction with the given wallet.
     * @param transaction The transaction to sign.
     * @param wallet The wallet that will sign the transaction.
     * @return A `SignedTransaction`.
     */
    public static SignedTransaction signTransaction(Transaction transaction, Wallet wallet) {
        try {
            LegacyJavaScriptSigner javascriptSigner = new LegacyJavaScriptSigner();
            return javascriptSigner.signTransaction(transaction, wallet);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}