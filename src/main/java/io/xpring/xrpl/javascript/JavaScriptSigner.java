package io.xpring.xrpl.javascript;

import com.eclipsesource.v8.V8Object;
import io.xpring.xrpl.Utils;
import io.xpring.xrpl.Wallet;
import org.graalvm.polyglot.Value;
import org.xrpl.rpc.v1.Transaction;

/**
 * Provides JavaScript based Signing functionality.
 */
public class JavaScriptSigner {
  private V8Object signerClass;
  private V8Object walletClass;
  private V8Object transactionClass;
  private V8Object utilsClass;

  /**
   * Create a new JavaScriptSigner.
   *
   * @throws JavaScriptLoaderException if the bundled JavaScript is malformed.
   */
  public JavaScriptSigner() throws JavaScriptLoaderException {
    V8Object context = JavaScriptLoader.getContext();
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
   * @return An array of bytes representing the signed transaction.
   * @throws JavaScriptLoaderException An exception if the javascript could not be loaded.
   */
  public byte[] signTransaction(Transaction transaction, Wallet wallet) throws JavaScriptLoaderException {
    // Convert Java objects into JavaScript objects.
    Object javaScriptTransaction = transactionToJavaScriptValue(transaction);
    Object javaScriptWallet = walletToJavaScriptValue(wallet);

    // Create a JavaScript SignedTransaction.
    Object javascriptSignedTransaction =
        signerClass.executeJSFunction("signTransaction", javaScriptTransaction, javaScriptWallet);

    // Convert JavaScript SignedTransaction into a Java SignedTransaction.
    return valueToByteArray(javascriptSignedTransaction);
  }

  /**
   * Convert a {@link Value} into a byte array.
   *
   * @param javascriptByteArray The serialized bytes to convert.
   * @return An array of bytes.
   */
  private byte[] valueToByteArray(Object javascriptByteArray) {
    String signTransactionHex = (String) utilsClass.executeJSFunction("toHex", javascriptByteArray);
    return Utils.hexStringToByteArray(signTransactionHex);
  }

  /**
   * Convert a Wallet to a JavaScript Value reference.
   *
   * @param wallet The {@link Wallet} to convert.
   * @return A reference to the analagous wallet in JavaScript.
   */
  private Object walletToJavaScriptValue(Wallet wallet) {
    String publicKeyHex = wallet.getPublicKey();
    String privateKeyHex = wallet.getPrivateKey();
    return JavaScriptLoader.newWallet(publicKeyHex, privateKeyHex);
  }

  /**
   * Convert a Transaction to a JavaScript Value reference.
   *
   * @param transaction The {@link Transaction} to convert.
   * @return A reference to the analagous transaction in JavaScript.
   */
  private Object transactionToJavaScriptValue(Transaction transaction) {
    byte[] transactionBytes = transaction.toByteArray();
    String transactionHex = Utils.byteArrayToHex(transactionBytes);
    Object javaScriptBytes = utilsClass.executeJSFunction("toBytes", transactionHex);

    return transactionClass.executeJSFunction("deserializeBinary", javaScriptBytes);
  }
}
