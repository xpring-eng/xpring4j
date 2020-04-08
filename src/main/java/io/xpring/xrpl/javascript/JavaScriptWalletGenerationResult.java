package io.xpring.xrpl.javascript;

import io.xpring.xrpl.Wallet;

/**
 * Contains JavaScript artifacts of generating a new Wallet.
 */
public class JavaScriptWalletGenerationResult {
  /**
   * The mnemonic of the newly generated {@link Wallet}.
   */
  private String mnemonic;

  public String getMnemonic() {
    return mnemonic;
  }

  /**
   * The derivation path of the newly generated {@link Wallet}.
   */
  private String derivationPath;

  public String getDerivationPath() {
    return derivationPath;
  }

  /**
   * The newly generated {@link Wallet}.
   */
  private JavaScriptWallet wallet;

  public JavaScriptWallet getWallet() {
    return wallet;
  }

  /**
   * Create a new JavaScriptWalletGenerationResult.
   *
   * @param mnemonic       The mnemonic that generated the wallet.
   * @param derivationPath The derivation path that generated the wallet.
   * @param wallet         The newly generated wallet.
   */
  public JavaScriptWalletGenerationResult(String mnemonic, String derivationPath, JavaScriptWallet wallet) {
    this.mnemonic = mnemonic;
    this.derivationPath = derivationPath;
    this.wallet = wallet;
  }
}