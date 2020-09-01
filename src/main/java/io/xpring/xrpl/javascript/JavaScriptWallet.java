package io.xpring.xrpl.javascript;

import com.eclipsesource.v8.V8Object;
import io.xpring.xrpl.XrpException;

/**
 * Provides Wallet functionality backed by JavaScript.
 */
public class JavaScriptWallet {

  /**
   * An underlying reference to a JavaScript wallet.
   */
  private V8Object javaScriptWallet;

  /**
   * Initialize a new JavaScriptWallet.
   *
   * @param javaScriptWallet A reference to a JavaScript based wallet.
   */
  public JavaScriptWallet(V8Object javaScriptWallet) {
    this.javaScriptWallet = javaScriptWallet;
  }

  /**
   * Returns the address of this JavaScriptWallet.
   *
   * @return The address of the wallet.
   */
  public String getAddress() {
    return (String) javaScriptWallet.executeJSFunction("getAddress");
  }

  /**
   * Returns the public key of this JavaScriptWallet.
   *
   * @return A hexadecimal encoded representation of the wallet's public key.
   */
  public String getPublicKey() {
    return javaScriptWallet.getString("publicKey");
  }

  /**
   * Returns the private key of this JavaScriptWallet.
   *
   * @return A hexadecimal encoded representation of the wallet's private key.
   */
  public String getPrivateKey() {
    return javaScriptWallet.getString("privateKey");
  }

  /**
   * Sign the given input.
   *
   * @param input The input to sign.
   * @return A hexadecimal encoded signature.
   * @throws XrpException An exception if the input could not be signed.
   */
  public String sign(String input) throws XrpException {
    return (String) javaScriptWallet.executeJSFunction("sign", input);
  }

  /**
   * Verify that a given signature is valid for the given message.
   *
   * @param message   A message in hexadecimal encoding.
   * @param signature A signature in hexademical encoding.
   * @return A boolean indicating the validity of the signature.
   */
  public boolean verify(String message, String signature) {
    return javaScriptWallet.executeBooleanFunction("verify", JavaScriptLoader.newV8Array(message, signature));
  }
}
