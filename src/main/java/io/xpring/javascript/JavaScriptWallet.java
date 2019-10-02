package io.xpring.javascript;

import io.xpring.XpringKitException;
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
     */
    public JavaScriptWallet(Value javaScriptWallet) {
        this.javaScriptWallet = javaScriptWallet;
    }

    /**
     * Returns the address of this `JavaScriptWallet`.
     */
    public String getAddress() {
        return javaScriptWallet.invokeMember("getAddress").asString();
    }

    /**
     * Returns the public key of this `JavaScriptWallet`.
     */
    public String getPublicKey() {
        return javaScriptWallet.invokeMember("getPublicKey").asString();
    }

    /**
     * Returns the private key of this `JavaScriptWallet`.
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
     */
    public String sign(String input) throws XpringKitException {
        Value javaScriptSignature = javaScriptWallet.invokeMember("sign", input);
        if (javaScriptSignature.isNull()) {
            throw new XpringKitException("Could not sign input");
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
