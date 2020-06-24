package io.xpring.xrpl.javascript;

import io.xpring.xrpl.Utils;
import io.xpring.xrpl.XRPException;
import io.xpring.xrpl.XRPExceptionType;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Value;

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

  private Value wallet;

  private JavaScriptWalletFactory() throws JavaScriptLoaderException {
    Context context = JavaScriptLoader.getContext();
    this.wallet = JavaScriptLoader.loadResource("Wallet", context);
  }

  public static JavaScriptWalletFactory get() {
    return sharedJavaScriptWalletFactory;
  }

  public String getDefaultDerivationPath() throws JavaScriptLoaderException {
    return this.wallet.getMember("defaultDerivationPath").asString();
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

    Value walletGenerationResult = this.wallet.invokeMember("generateRandomWallet", hexRandomBytes, isTest);
    return new JavaScriptWalletGenerationResult(
        walletGenerationResult.getMember("mnemonic").asString(),
        walletGenerationResult.getMember("derivationPath").asString(),
        new JavaScriptWallet(walletGenerationResult.getMember("wallet"))
    );
  }

  /**
   * Instantiate a new wallet from a set of keys.
   *
   * @param publicKey  A hex encoded string representing the public key.
   * @param privateKey A hex encoded string representing the private key.
   * @param isTest     Whether the address is for use on a test network.
   * @return A new {@link JavaScriptWallet}.
   * @throws XRPException If either input key is malformed.
   */
  public JavaScriptWallet walletFromKeys(String publicKey, String privateKey, boolean isTest) throws XRPException {
    Value wallet = this.wallet.newInstance(publicKey, privateKey, isTest);
    if (wallet.isNull()) {
      throw new XRPException(XRPExceptionType.INVALID_INPUTS, "Invalid inputs");
    }
    return new JavaScriptWallet(wallet);
  }

  /**
   * Initialize a new wallet from a seed.
   *
   * @param seed   A base58check encoded seed for the wallet.
   * @param isTest Whether the address is for use on a test network.
   * @return A new {@link JavaScriptWallet}.
   * @throws XRPException If the seed is malformed.
   */
  public JavaScriptWallet walletFromSeed(String seed, boolean isTest) throws XRPException {
    Value wallet = this.wallet.invokeMember("generateWalletFromSeed", seed, isTest);
    if (wallet.isNull()) {
      throw new XRPException(XRPExceptionType.INVALID_INPUTS, "Invalid Seed");
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
   * @throws XRPException If the mnemonic or derivation path are malformed.
   */
  public JavaScriptWallet walletFromMnemonicAndDerivationPath(
      String mnemonic,
      String derivationPath,
      boolean isTest
  ) throws XRPException {
    try {
      String normalizedDerivationPath = derivationPath != null ? derivationPath : this.getDefaultDerivationPath();
      Value wallet = this.wallet.invokeMember("generateWalletFromMnemonic", mnemonic, normalizedDerivationPath, isTest);

      if (wallet.isNull()) {
        throw new XRPException(XRPExceptionType.INVALID_INPUTS, invalidMnemonicOrDerivationPathMessage);
      }

      return new JavaScriptWallet(wallet);
    } catch (PolyglotException exception) {
      throw new XRPException(XRPExceptionType.INVALID_INPUTS, invalidMnemonicOrDerivationPathMessage);
    } catch (JavaScriptLoaderException exception) {
      throw new XRPException(XRPExceptionType.INVALID_INPUTS, invalidMnemonicOrDerivationPathMessage);
    }
  }

  private byte[] randomBytes(int numBytes) {
    SecureRandom random = new SecureRandom();
    byte[] bytes = new byte[numBytes];
    random.nextBytes(bytes);

    return bytes;
  }
}
