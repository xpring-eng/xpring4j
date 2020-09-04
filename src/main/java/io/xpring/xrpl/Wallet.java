package io.xpring.xrpl;

import com.google.common.annotations.VisibleForTesting;
import io.xpring.xrpl.javascript.JavaScriptWallet;
import io.xpring.xrpl.wallet.WalletFactory;

/**
 * Represents an account on the XRP Ledger and provides signing / verifying cryptographic functions.
 */
public class Wallet {
  public static final WalletFactory WALLET_FACTORY = WalletFactory.getInstance();
;
  /**
   * The underlying JavaScript wallet.
   */
  private io.xpring.xrpl.wallet.WalletGenerationResult walletGenerationResult;

  /**
   * Initialize a new wallet from a seed.
   *
   * @param seed A base58check encoded seed for the wallet.
   * @throws XrpException If the seed is malformed.
   */
  public Wallet(String seed) throws XrpException {
    this(seed, false);
  }

  /**
   * Initialize a new wallet from a seed.
   *
   * @param seed   A base58check encoded seed for the wallet.
   * @param isTest Whether the address is for use on a test network.
   * @throws XrpException If the seed is malformed.
   */
  public Wallet(String seed, boolean isTest) throws XrpException {
    this(WALLET_FACTORY.generateWalletFromSeed(seed, isTest));
  }

  /**
   * Create a new HD Wallet.
   *
   * @param mnemonic       A space separated mnemonic.
   * @param derivationPath A derivation. If null, the default derivation path will be used.
   * @throws XrpException If the mnemonic or derivation path are malformed.
   */
  public Wallet(String mnemonic, String derivationPath) throws XrpException {
    this(mnemonic, derivationPath, false);
  }

  /**
   * Create a new HD Wallet.
   *
   * @param mnemonic       A space separated mnemonic.
   * @param derivationPath A derivation. If null, the default derivation path will be used.
   * @param isTest         Whether the address is for use on a test network.
   * @throws XrpException If the mnemonic or derivation path are malformed.
   */
  public Wallet(String mnemonic, String derivationPath, boolean isTest) throws XrpException {
    this(WALLET_FACTORY.generateWalletFromMnemonic(mnemonic, derivationPath, isTest));
  }

  /**
   * Create a new wallet from a set of keys.
   *
   * @param publicKey  A hex encoded string representing the public key.
   * @param privateKey A hex encoded string representing the private key.
   * @param isTest     Whether the address is for use on a test network.
   * @return A new {@link JavaScriptWallet}.
   * @throws XrpException If either input key is malformed.
   */
  public static Wallet walletFromKeys(String publicKey, String privateKey, boolean isTest) throws XrpException {
    io.xpring.xrpl.wallet.WalletGenerationResult walletGenerationResult =
        WALLET_FACTORY.generateWalletFromKeys(privateKey, publicKey, isTest);
    return new Wallet(walletGenerationResult);
  }

  /**
   * Create a new wallet from an {@link WalletGenerationResult}.
   *
   * @param walletGenerationResult The wallet to wrap.
   */
  @VisibleForTesting
  public Wallet(io.xpring.xrpl.wallet.WalletGenerationResult walletGenerationResult) {
    this.walletGenerationResult = walletGenerationResult;
  }

  /**
   * Generate a random Wallet.
   *
   * @return A {WalletGenerationResult} containing the artifacts of the generation process.
   * @throws XrpException If wallet generation fails.
   */
  public static io.xpring.xrpl.wallet.WalletGenerationResult generateRandomWallet() throws XrpException {
    return generateRandomWallet(false);
  }

  /**
   * Generate a random Wallet.
   *
   * @param isTest Whether the address is for use on a test network.
   * @return A {WalletGenerationResult} containing the artifacts of the generation process.
   * @throws XrpException If wallet generation fails.
   */
  public static io.xpring.xrpl.wallet.WalletGenerationResult generateRandomWallet(boolean isTest) throws XrpException {
    return WALLET_FACTORY.generateRandomWallet(isTest);
  }

  /**
   * The address of this {@link Wallet}.
   *
   * @return The address of this {@link Wallet}.
   */
  public String getAddress() {
    return walletGenerationResult.getAddress();
  }

  /**
   * The public key of this {@link Wallet}.
   *
   * @return The public key of this {@link Wallet}.
   */
  public String getPublicKey() {
    return walletGenerationResult.getPublicKey();
  }

  /**
   * The private key of this {@link Wallet}.
   *
   * @return The private key of this {@link Wallet}.
   */
  public String getPrivateKey() {
    return walletGenerationResult.getPrivateKey();
  }

  /**
   * Sign the given input.
   *
   * @param input The input to sign as a hexadecimal string.
   * @return A hexadecimal encoded signature.
   * @throws XrpException If the input is malformed.
   */
  public String sign(String input) throws XrpException {
    throw new UnsupportedOperationException("not yet implemented");
    //return javaScriptWallet.sign(input);
  }

  /**
   * Verify that a given signature is valid for the given message.
   *
   * @param message   A message in hexadecimal encoding.
   * @param signature A signature in hexademical encoding.
   * @return A boolean indicating the validity of the signature.
   */
  public boolean verify(String message, String signature) {
    throw new UnsupportedOperationException("not yet implemented");
    //return javaScriptWallet.verify(message, signature);
  }
}
