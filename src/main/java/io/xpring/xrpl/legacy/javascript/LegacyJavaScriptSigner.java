package io.xpring.xrpl.legacy.javascript;

import io.xpring.proto.Transaction;
import io.xpring.proto.SignedTransaction;

import com.google.protobuf.InvalidProtocolBufferException;
import io.xpring.xrpl.Utils;
import io.xpring.xrpl.Wallet;
import io.xpring.xrpl.javascript.JavaScriptLoaderException;
import io.xpring.xrpl.javascript.JavaScriptLoader;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

/** Provides JavaScript based Signing functionality. */
public class LegacyJavaScriptSigner {
    private Value signerClass;
    private Value walletClass;
    private Value transactionClass;
    private Value utilsClass;

    public LegacyJavaScriptSigner() throws JavaScriptLoaderException {
        Context context = JavaScriptLoader.getContext();
        this.signerClass = JavaScriptLoader.loadResource("Signer", context);
        this.walletClass = JavaScriptLoader.loadResource("Wallet", context);
        this.transactionClass = JavaScriptLoader.loadResource("LegacyTransaction", context);
        this.utilsClass = JavaScriptLoader.loadResource("Utils", context);
    }

    /**
     * Sign the given transaction with the given wallet.
     *
     * @param transaction The {@link Transaction} to sign.
     * @param wallet The {@link Wallet} that will sign the transaction.
     *
     * @return A {@link SignedTransaction}.
     *
     * @throws JavaScriptLoaderException An exception if the javascript could not be loaded.
     */
    public SignedTransaction signTransaction(Transaction transaction, Wallet wallet) throws JavaScriptLoaderException {
        // Convert Java objects into JavaScript objects.
        Value javaScriptTransaction = transactionToJavaScriptValue(transaction);
        Value javaScriptWallet = walletToJavaScriptValue(wallet);

        // Create a JavaScript SignedTransaction.
        Value javaScriptSignedTransaction = signerClass.invokeMember("signLegacyTransaction", javaScriptTransaction, javaScriptWallet);

        // Convert JavaScript SignedTransaction into a Java SignedTransaction.
        try {
            return valueToSignedTransaction(javaScriptSignedTransaction);
        } catch (InvalidProtocolBufferException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Convert a JavaScript SignedTransaction to a native {@link SignedTransaction}.
     *
     * @param javaScriptSignedTransaction A reference to a SignedTransaction in JavaScript.
     *
     * @return A {@link SignedTransaction} from the JavaScript based input.
     *
     * @throws {@link InvalidProtocolBufferException} An exception if the javascript protocol buffer was invalid or could not be converted.
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
