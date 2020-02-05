package io.xpring.xrpl.javascript;

import com.google.protobuf.InvalidProtocolBufferException;
import io.xpring.xrpl.Utils;
import io.xpring.xrpl.Wallet;
import io.xpring.xrpl.javascript.JavaScriptLoaderException;
import io.xpring.xrpl.javascript.JavaScriptLoader;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import rpc.v1.TransactionOuterClass.Transaction;

/** Provides JavaScript based Signing functionality. */
public class JavaScriptSigner {
    private Value signerClass;
    private Value walletClass;
    private Value transactionClass;
    private Value utilsClass;

    public JavaScriptSigner() throws JavaScriptLoaderException {
        Context context = JavaScriptLoader.getContext();
        this.transactionClass = JavaScriptLoader.loadResource("Transaction", context);
        this.signerClass = JavaScriptLoader.loadResource("Signer", context);
        this.walletClass = JavaScriptLoader.loadResource("Wallet", context);
        this.utilsClass = JavaScriptLoader.loadResource("Utils", context);
    }

    /**
     * Sign the given transaction with the given wallet.
     *
     * @param transaction The {@link Transaction} to sign.
     * @param wallet The {@link Wallet} that will sign the transaction.
     *
     * @return An array of bytes representing the signed transaction.
     *
     * @throws JavaScriptLoaderException An exception if the javascript could not be loaded.
     */
    public byte [] signTransaction(Transaction transaction, Wallet wallet) throws JavaScriptLoaderException {
        // Convert Java objects into JavaScript objects.
        Value javaScriptTransaction = transactionToJavaScriptValue(transaction);
        Value javaScriptWallet = walletToJavaScriptValue(wallet);

        // Create a JavaScript SignedTransaction.
        Value javascriptSignedTransaction = signerClass.invokeMember("signTransaction", javaScriptTransaction, javaScriptWallet);

        // Convert JavaScript SignedTransaction into a Java SignedTransaction.
        return valueToByteArray(javascriptSignedTransaction);
    }

    /**
     * Convert a {@link Value} into a byte array.
     *
     * @param javascriptByteArray The serialized bytes to convert.
     * @return An array of bytes.
     */
    private byte [] valueToByteArray(Value javascriptByteArray) {
        String signTransactionHex = utilsClass.invokeMember("toHex", javascriptByteArray).asString();
        return Utils.hexStringToByteArray(signTransactionHex);
    }

    /**
     * Convert a Wallet to a JavaScript Value reference.
     *
     * @param wallet The {@link Wallet} to convert.
     *
     * @return A reference to the analagous wallet in JavaScript.
     */
    private Value walletToJavaScriptValue(Wallet wallet) {
        String publicKeyHex = wallet.getPublicKey();
        String privateKeyHex = wallet.getPrivateKey();
        return walletClass.newInstance(publicKeyHex, privateKeyHex);
    }

    /**
     * Convert a Transaction to a JavaScript Value reference.
     *
     * @param transaction The {@link Transaction} to convert.
     *
     * @return A reference to the analagous transaction in JavaScript.
     */
    private Value transactionToJavaScriptValue(Transaction transaction) {
        byte [] transactionBytes = transaction.toByteArray();
        String transactionHex = Utils.byteArrayToHex(transactionBytes);
        Value javaScriptBytes = utilsClass.invokeMember("toBytes", transactionHex);

        return transactionClass.invokeMember("deserializeBinary", javaScriptBytes);
    }
}
