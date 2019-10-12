package io.xpring.javascript;

import static io.xpring.TransactionOuterClass.Transaction;
import static io.xpring.SignedTransactionOuterClass.SignedTransaction;

import com.google.protobuf.InvalidProtocolBufferException;
import com.oracle.truffle.js.runtime.array.TypedArray;
import io.xpring.Utils;
import io.xpring.Wallet;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import javax.annotation.Signed;

/** Provides JavaScript based Signing functionality. */
public class JavaScriptSigner {
    private Value signerClass;
    private Value walletClass;
    private Value transactionClass;
    private Value utilsClass;

    public JavaScriptSigner() throws JavaScriptLoaderException {
        Context context = JavaScriptLoader.getContext();
        this.signerClass = JavaScriptLoader.loadResource("Signer", context);
        this.walletClass = JavaScriptLoader.loadResource("Wallet", context);
        this.transactionClass = JavaScriptLoader.loadResource("Transaction", context);
        this.utilsClass = JavaScriptLoader.loadResource("Utils", context);
    }

    /**
     * Sign the given transaction with the given wallet.
     *
     * @throws JavaScriptLoaderException An exception if the javascript could not be loaded.
     *
     * @param transaction The transaction to sign.
     * @param wallet The wallet that will sign the transaction.
     *
     * @return A `SignedTransaction`.
     */
    public SignedTransaction signTransaction(Transaction transaction, Wallet wallet) throws JavaScriptLoaderException {
        // Convert Java objects into JavaScript objects.
        Value javaScriptTransaction = transactionToJavaScriptValue(transaction);
        Value javaScriptWallet = walletToJavaScriptValue(wallet);

        // Create a JavaScript SignedTransaction.
        Value javaScriptSignedTransaction = signerClass.invokeMember("signTransaction", javaScriptTransaction, javaScriptWallet);

        // Convert JavaScript SignedTransaction into a Java SignedTransaction.
        try {
            return valueToSignedTransaction(javaScriptSignedTransaction);
        } catch (InvalidProtocolBufferException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Convert a JavaScript SignedTransaction to a native SignedTransaction.
     *
     * @throws InvalidProtocolBufferException An exception if the javascript protocol buffer was invalid or could not be converted.
     *
     * @param javaScriptSignedTransaction A reference to a SignedTransaction in JavaScript .
     *
     * @return A `SignedTransaction` from the JavaScript based input.
     */
    private SignedTransaction valueToSignedTransaction(Value javaScriptSignedTransaction) throws InvalidProtocolBufferException {
        Value javaScriptSignedTransactionBytes = javaScriptSignedTransaction.invokeMember("serializeBinary");
        String signTransactionHex = utilsClass.invokeMember("toHex", javaScriptSignedTransactionBytes).asString();
        byte [] signedTransactionBytes = Utils.hexStringToByteArray(signTransactionHex);
        return SignedTransaction.parseFrom(signedTransactionBytes);
    }

    /**
     * Convert a Wallet to a JavaScript Value reference.
     *
     * @param wallet The wallet to convert.
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
     * @param transaction The Transaction to convert.
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
