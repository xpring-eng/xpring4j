package io.xpring;

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

    /**
     * Initialize a new wallet from a seed.
     *
     * @param seed A base58check encoded seed for the wallet.
     * @throws XpringKitException If the seed is malformed.
     */
    public Wallet(String seed) throws XpringKitException {
        this.javaScriptWallet = JavaScriptWalletFactory.get().walletFromSeed(seed);
    }

    /**
     * Create a new HD Wallet.
     *
     * @param mnemonic       A space separated mnemonic.
     * @param derivationPath A derivation. If null, the default derivation path will be used.
     * @throws XpringKitException If the mnemonic or derivation path are malformed.
     */
    public Wallet(String mnemonic, String derivationPath) throws XpringKitException {
        this.javaScriptWallet = JavaScriptWalletFactory.get().walletFromMnemonicAndDerivationPath(mnemonic,
            derivationPath);
    }

    /**
     * Generate a random Wallet.
     *
     * @return A {WalletGenerationResult} containing the artifacts of the generation process.
     * @throws XpringKitException If wallet generation fails.
     */
    public static WalletGenerationResult generateRandomWallet() throws XpringKitException {
        JavaScriptWalletGenerationResult javaScriptWalletGenerationResult = JavaScriptWalletFactory.get()
            .generateRandomWallet();

        // TODO(keefertaylor): This should be a direct conversion, rather than recreating a new wallet.
        Wallet newWallet = new Wallet(javaScriptWalletGenerationResult.getMnemonic(),
            javaScriptWalletGenerationResult.getDerivationPath());

        return new WalletGenerationResult(javaScriptWalletGenerationResult.getMnemonic(),
            javaScriptWalletGenerationResult.getDerivationPath(), newWallet);
    }

    /**
     * @return The address of this `Wallet`.
     */
    public String getAddress() {
        return javaScriptWallet.getAddress();
    }

    /**
     * @return The public key of this `Wallet`.
     */
    public String getPublicKey() {
        return javaScriptWallet.getPublicKey();
    }

    /**
     * @return The private key of this `Wallet`.
     */
    public String getPrivateKey() {
        return javaScriptWallet.getPrivateKey();
    }

    /**
     * Sign the given input.
     *
     * @param input The input to sign as a hexadecimal string.
     * @return A hexadecimal encoded signature.
     * @throws XpringKitException If the input is malformed.
     */
    public String sign(String input) throws XpringKitExceptiaon {
        return javaScriptWallet.sign(input);
    }

    /**
     * Verify that a given signature is valid for the given message.
     *
     * @param message   A message in hexadecimal encoding.
     * @param signature A signature in hexademical encoding.
     * @return A boolean indicating the validity of the signature.
     */
    public boolean verify(String message, String signature) {
        return javaScriptWallet.verify(message, signature);
    }
}
