package io.xpring;

import io.xpring.javascript.JavaScriptLoaderException;
import io.xpring.javascript.JavaScriptWallet;
import io.xpring.javascript.JavaScriptWalletFactory;
import io.xpring.javascript.JavaScriptWalletGenerationResult;

/**
 * Represents an account on the XRP Ledger and provides signing / verifying cryptographic functions.
 */
public class Wallet {
    /**
     * The underlying JavaScript wallet.
     */
    private JavaScriptWallet javaScriptWallet;

    /** Initialize a new wallet from a base58check encoded seed. */
    public Wallet(String seed) throws XpringKitException {
        try {
            JavaScriptWalletFactory walletFactory = new JavaScriptWalletFactory();
            this.javaScriptWallet = walletFactory.walletFromSeed(seed);
        } catch (JavaScriptLoaderException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Create a new HD Wallet.
     *
     * @param mnemonic A space separated mnemonic.
     * @param derivationPath A derivation. If null, the default derivation path will be used.
     */
    public Wallet(String mnemonic, String derivationPath) throws XpringKitException  {
        try {
            JavaScriptWalletFactory walletFactory = new JavaScriptWalletFactory();
            this.javaScriptWallet = walletFactory.walletFromMnemonicAndDerivationPath(mnemonic, derivationPath);
        } catch (JavaScriptLoaderException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Generate a random Wallet.
     *
     * @return A {WalletGenerationResult} containing the artifacts of the generation process.
     */
    public static WalletGenerationResult generateRandomWallet() throws XpringKitException {
        try {
            JavaScriptWalletFactory walletFactory = new JavaScriptWalletFactory();
            JavaScriptWalletGenerationResult javaScriptWalletGenerationResult =  walletFactory.generateRandomWallet();

            // TODO(keefertaylor): This should be a direct conversion, rather than recreatign a new wallet.
            Wallet newWallet = new Wallet(javaScriptWalletGenerationResult.getMnemonic(), javaScriptWalletGenerationResult.getDerivationPath());

            return new WalletGenerationResult(javaScriptWalletGenerationResult.getMnemonic(), javaScriptWalletGenerationResult.getDerivationPath(), newWallet);
        } catch (JavaScriptLoaderException exception) {
            throw new RuntimeException(exception);
        }
    }

    /** Returns the address of this `Wallet`. */
    public String getAddress() {
        return javaScriptWallet.getAddress();
    }

    /** Returns the public key of this `Wallet`. */
    public String getPublicKey() {
        return javaScriptWallet.getPublicKey();
    }

    /** Returns the private key of this `Wallet`. */
    public String getPrivateKey() {
        return javaScriptWallet.getPrivateKey();
    }

    /**
     * Sign the given input.
     *
     * @param input The input to sign.
     *
     * @return A hexadecimal encoded signature.
     */
    public String sign(String input) throws XpringKitException {
        return javaScriptWallet.sign(input);
    }

    /**
     *  Verify that a given signature is valid for the given message.
     *
     * @param message A message in hexadecimal encoding.
     * @param signature A signature in hexademical encoding.
     * @return A boolean indicating the validity of the signature.
     */
    public boolean verify(String message, String signature) {
        return javaScriptWallet.verify(message, signature);
    }
}
