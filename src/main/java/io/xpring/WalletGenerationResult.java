package io.xpring;

/** Contains artifacts of generating a new Wallet. */
public class WalletGenerationResult {
    /** The mnemonic of the newly generated Wallet. */
    private String mnemonic;
    public String getMnemonic() { return mnemonic; }

    /** The derivation path of the newly generated Wallet. */
    private String derivationPath;
    public String getDerivationPath() { return derivationPath; }

    /** The newly generated Wallet. */
    private Wallet wallet;
    public Wallet getWallet()  { return wallet; }


    public WalletGenerationResult(String mnemonic, String derivationPath, Wallet wallet) {
        this.mnemonic = mnemonic;
        this.derivationPath = derivationPath;
        this.wallet = wallet;
    }
}