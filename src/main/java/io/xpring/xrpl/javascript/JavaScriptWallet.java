package io.xpring.xrpl.javascript;

import io.xpring.xrpl.XRPException;
import org.graalvm.polyglot.Value;

/**
 * Provides Wallet functionality backed by JavaScript.
 */
public class JavaScriptWallet {

    /**
     * An underlying reference to a JavaScript wallet.
     */
    private Value javaScriptWallet;

    /**
     * Initialize a new JavaScriptWallet.
     *
     * @param javaScriptWallet A reference to a JavaScript based wallet.
     */
    public JavaScriptWallet(Value javaScriptWallet) {
        this.javaScriptWallet = javaScriptWallet;
    }

    /**
     * Returns the address of this JavaScriptWallet.
     *
     * @return The address of the wallet.
     */
    public String getAddress() {
        return javaScriptWallet.invokeMember("getAddress").asString();
    }

    /**
     * Returns the public key of this JavaScriptWallet.
     *
     * @return A hexadecimal encoded representation of the wallet's public key.
     */
    public String getPublicKey() {
        return javaScriptWallet.invokeMember("getPublicKey").asString();
    }

    /**
     * Returns the private key of this JavaScriptWallet.
     *
     * @return A hexadecimal encoded representation of the wallet's private key.
     */
    public String getPrivateKey() {
        return javaScriptWallet.invokeMember("getPrivateKey").asString();
    }

    /**
     * Sign the given input.
     *
     * @param input The input to sign.
     *
     * @return A hexadecimal encoded signature.
     *
     * @throws XRPException An exception if the input could not be signed.
     */
    public String sign(String input) throws XRPException {
        Value javaScriptSignature = javaScriptWallet.invokeMember("sign", input);
        if (javaScriptSignature.isNull()) {
            throw new XRPException("Could not sign input");
        }
        return javaScriptSignature.asString();
    }

    /**
     * Verify that a given signature is valid for the given message.
     *
     * @param message   A message in hexadecimal encoding.
     * @param signature A signature in hexademical encoding.
     *
     * @return A boolean indicating the validity of the signature.
     */
    public boolean verify(String message, String signature) {
        return javaScriptWallet.invokeMember("verify", message, signature).asBoolean();
    }
}
