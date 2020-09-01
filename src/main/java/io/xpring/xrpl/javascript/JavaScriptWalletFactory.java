package io.xpring.xrpl.javascript;

import com.eclipsesource.v8.V8Object;
import io.xpring.xrpl.Utils;
import io.xpring.xrpl.XrpException;
import io.xpring.xrpl.XrpExceptionType;

import java.security.SecureRandom;

public class JavaScriptWalletFactory {

  private static final JavaScriptWalletFactory sharedJavaScriptWalletFactory;

  public static String invalidMnemonicOrDerivationPathMessage = "Invalid mnemonic or derivation path.";

  static {
    try {
      sharedJavaScriptWalletFactory = new JavaScriptWalletFactory();
    } catch (JavaScriptLoaderException e) {
      throw new RuntimeException(e);
    }
  }

  private V8Object wallet;

  private JavaScriptWalletFactory() throws JavaScriptLoaderException {
    V8Object context = JavaScriptLoader.getContext();
    this.wallet = JavaScriptLoader.loadResource("Wallet", context);
  }

  public static JavaScriptWalletFactory get() {
    return sharedJavaScriptWalletFactory;
  }

  public String getDefaultDerivationPath() {
    return this.wallet.getString("defaultDerivationPath");
  }

  /**
   * Generate a random Wallet.
   *
   * @param isTest Whether the address is for use on a test network.
   * @return A {WalletGenerationResult} containing the artifacts of the generation process.
   */
  public JavaScriptWalletGenerationResult generateRandomWallet(boolean isTest) {
    byte[] randomBytes = randomBytes(16);
    String hexRandomBytes = Utils.byteArrayToHex(randomBytes);

    V8Object walletGenerationResult =
        (V8Object) this.wallet.executeJSFunction("generateRandomWallet", hexRandomBytes, isTest);
    return new JavaScriptWalletGenerationResult(
        walletGenerationResult.getString("mnemonic"),
        walletGenerationResult.getString("derivationPath"),
        new JavaScriptWallet(walletGenerationResult.getObject("wallet"))
    );
  }

  /**
   * Instantiate a new wallet from a set of keys.
   *
   * @param publicKey  A hex encoded string representing the public key.
   * @param privateKey A hex encoded string representing the private key.
   * @param isTest     Whether the address is for use on a test network.
   * @return A new {@link JavaScriptWallet}.
   * @throws XrpException If either input key is malformed.
   */
  public JavaScriptWallet walletFromKeys(String publicKey, String privateKey, boolean isTest) throws XrpException {
    V8Object wallet = JavaScriptLoader.newWallet(publicKey, privateKey, isTest);
    if (wallet.isUndefined()) {
      throw new XrpException(XrpExceptionType.INVALID_INPUTS, "Invalid inputs");
    }
    return new JavaScriptWallet(wallet);
  }

  /**
   * Initialize a new wallet from a seed.
   *
   * @param seed   A base58check encoded seed for the wallet.
   * @param isTest Whether the address is for use on a test network.
   * @return A new {@link JavaScriptWallet}.
   * @throws XrpException If the seed is malformed.
   */
  public JavaScriptWallet walletFromSeed(String seed, boolean isTest) throws XrpException {
    V8Object wallet = this.wallet.executeObjectFunction("generateWalletFromSeed", JavaScriptLoader.newV8Array(seed, isTest));
    if (wallet.isUndefined()) {
      throw new XrpException(XrpExceptionType.INVALID_INPUTS, "Invalid Seed");
    }
    return new JavaScriptWallet(wallet);
  }

  /**
   * Create a new HD Wallet.
   *
   * @param mnemonic       A space separated mnemonic.
   * @param derivationPath A derivation. If null, the default derivation path will be used.
   * @param isTest         Whether the address is for use on a test network.
   * @return A new {@link JavaScriptWallet}.
   * @throws XrpException If the mnemonic or derivation path are malformed.
   */
  public JavaScriptWallet walletFromMnemonicAndDerivationPath(
      String mnemonic,
      String derivationPath,
      boolean isTest
  ) throws XrpException {
    try {
      String normalizedDerivationPath = derivationPath != null ? derivationPath : this.getDefaultDerivationPath();

      Object result = this.wallet
          .executeJSFunction("generateWalletFromMnemonicX",
              JavaScriptLoader.newV8Array(mnemonic, normalizedDerivationPath, isTest));

      V8Object wallet = (V8Object) result;
      if (wallet.isUndefined()) {
        throw new XrpException(XrpExceptionType.INVALID_INPUTS, invalidMnemonicOrDerivationPathMessage);
      }

      return new JavaScriptWallet(wallet);
    } catch (Exception exception) {
      throw new XrpException(XrpExceptionType.INVALID_INPUTS, invalidMnemonicOrDerivationPathMessage);
    }
  }

  private byte[] randomBytes(int numBytes) {
    SecureRandom random = new SecureRandom();
    byte[] bytes = new byte[numBytes];
    random.nextBytes(bytes);

    return bytes;
  }
}
