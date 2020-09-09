package io.xpring.xrpl.wallet;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.io.BaseEncoding;
import com.ripple.core.coretypes.AccountID;
import com.ripple.crypto.Seed;
import com.ripple.crypto.ecdsa.ECDSASignature;
import com.ripple.crypto.ecdsa.SECP256K1;
import com.ripple.crypto.keys.IKeyPair;
import com.ripple.crypto.keys.IVerifyingKey;
import com.ripple.encodings.basex.EncodingFormatException;
import com.ripple.utils.Sha512;
import io.xpring.codec.addresses.XAddressCodec;
import io.xpring.xrpl.ClassicAddress;
import io.xpring.xrpl.WalletGenerationResult;
import io.xpring.xrpl.XrpException;
import io.xpring.xrpl.XrpExceptionType;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.crypto.HDUtils;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.crypto.MnemonicException;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.bouncycastle.crypto.signers.HMacDSAKCalculator;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

public class WalletFactory {

  public static String defaultDerivationPath = "m/44'/144'/0'/0/0";

  private static final WalletFactory INSTANCE = new WalletFactory();

  public static WalletFactory getInstance() {
    return INSTANCE;
  }

  private WalletFactory() {
  }

  /**
   * Generates a new wallet from a random mnemonic.
   *
   * @param isTest if the wallet is a test wallet.
   * @return new wallet.
   */
  public WalletGenerationResult generateRandomWallet(boolean isTest) {
    byte[] randomBytes = randomBytes(16);
    try {
      List<String> mnemonic = MnemonicCode.INSTANCE.toMnemonic(randomBytes);
      return generateWalletFromMnemonic(Joiner.on(" ").join(mnemonic), defaultDerivationPath, isTest);
    } catch (Exception e) {
      throw new RuntimeException("unexpected exception", e);
    }
  }

  /**
   * Generates a new wallet from a secret/seed.
   *
   * @param seed base58 encoded secret to generate the wallet.
   * @param isTest if wallet is a testnet wallet.
   * @return new wallet.
   * @throws XrpException thrown is the seed is not a proper base58 encoded seed.
   */
  public WalletGenerationResult generateWalletFromSeed(String seed, boolean isTest) throws XrpException {
    try {
      IKeyPair keyPair = Seed.getKeyPair(seed);
      return generateWalletFromKeys(getPrivateKey(keyPair), keyPair.canonicalPubHex(), isTest);
    } catch (EncodingFormatException e) {
      throw new XrpException(XrpExceptionType.INVALID_INPUTS, e.getMessage());
    }
  }

  /**
   * Generates a wallet from a seed.
   *
   * @param seed seed bytes.
   * @param isTest if the wallet is a testnet wallet.
   * @return new wallet.
   */
  public WalletGenerationResult generateWalletFromSeedBytes(byte[] seed, boolean isTest) {
    IKeyPair keyPair = generateHdWalletFromSeed(seed, defaultDerivationPath);
    AccountID accountID = AccountID.fromKeyPair(keyPair);
    return WalletGenerationResult.builder()
        .address(XAddressCodec.encode(toClassicAddress(accountID, isTest)))
        .derivationPath(defaultDerivationPath)
        .keyPair(keyPair)
        .privateKey(getPrivateKey(keyPair))
        .publicKey(keyPair.canonicalPubHex())
        .seed(BaseEncoding.base16().encode(seed))
        .build();
  }

  /**
   * Generates a wallet from public/private key pair.
   *
   * @param privateKey private key.
   * @param publicKey public key.
   * @param isTest if the wallet is a testnet wallet.
   * @return new wallet.
   */
  public WalletGenerationResult generateWalletFromKeys(String privateKey, String publicKey, boolean isTest) {
    IKeyPair keyPair = new ProvidedKeyPair(privateKey, publicKey);
    AccountID accountID = AccountID.fromKeyPair(keyPair);
    return WalletGenerationResult.builder()
        .address(XAddressCodec.encode(toClassicAddress(accountID, isTest)))
        .derivationPath(defaultDerivationPath)
        .keyPair(keyPair)
        .privateKey(getPrivateKey(keyPair))
        .publicKey(publicKey) // FIXME is this right?
        .seed(privateKey) // FIXME
        .build();
  }

  private String getPrivateKey(IKeyPair keyPair) {
    String privateKey = BaseEncoding.base16().encode(keyPair.privateKey());
    if (privateKey.length() == 66) {
      return privateKey;
    }
    return "00" + privateKey;
  }

  /**
   * Generates a new wallet from a mnemonic word list.
   *
   * @param mnemonic list of words to generate the seed from.
   * @param maybeDerivationPath optional deriviation path.
   * @param isTest if the wallet is a testnet wallet.
   * @return new wallet.
   * @throws XrpException if the mnemonic is invalid.
   */
  public WalletGenerationResult generateWalletFromMnemonic(
      String mnemonic,
      String maybeDerivationPath,
      boolean isTest) throws XrpException {
    String derivationPath = Optional.ofNullable(maybeDerivationPath).orElse(defaultDerivationPath);
    try {
      byte[] seed = toSeedBytes(mnemonic);
      IKeyPair keyPair = generateHdWalletFromSeed(seed, derivationPath);
      AccountID accountID = AccountID.fromKeyPair(keyPair);
      return WalletGenerationResult.builder()
          .address(XAddressCodec.encode(toClassicAddress(accountID, isTest)))
          .derivationPath(derivationPath)
          .privateKey(getPrivateKey(keyPair))
          .publicKey(keyPair.canonicalPubHex())
          .keyPair(keyPair)
          .seed(BaseEncoding.base16().encode(seed))
          .mnemonic(mnemonic)
          .build();
    } catch (Exception e) {
      throw new XrpException(XrpExceptionType.INVALID_INPUTS, e.getMessage());
    }
  }

  private byte[] toSeedBytes(String mnemonic) throws XrpException {
    List<String> words = Lists.newArrayList(Splitter.on(" ").split(mnemonic));
    try {
      MnemonicCode.INSTANCE.check(words);
    } catch (MnemonicException e) {
      throw new XrpException(XrpExceptionType.INVALID_INPUTS, e.getMessage());
    }
    return MnemonicCode.toSeed(words, "");
  }

  private IKeyPair generateHdWalletFromSeed(
      byte[] seedBytes,
      String derivationPath) {
    DeterministicKey masterKey = HDKeyDerivation.createMasterPrivateKey(seedBytes);
    List<ChildNumber> childNumbers = HDUtils.parsePath(derivationPath.replaceAll("'", "H").toUpperCase());
    DeterministicKey prevKey = masterKey;
    for (ChildNumber childNumber : childNumbers) {
      prevKey = HDKeyDerivation.deriveChildKey(prevKey, childNumber);
    }
    return new DeterministicKeyPair(prevKey);
  }

  private byte[] randomBytes(int numBytes) {
    SecureRandom random = new SecureRandom();
    byte[] bytes = new byte[numBytes];
    random.nextBytes(bytes);

    return bytes;
  }

  private ClassicAddress toClassicAddress(AccountID accountID, boolean isTest) {
    return ClassicAddress.builder()
        .address(accountID.address)
        .isTest(isTest)
        .build();
  }

  private static class DeterministicKeyPair implements IKeyPair {
    private final DeterministicKey key;

    public DeterministicKeyPair(DeterministicKey key) {
      this.key = key;
    }

    @Override
    public byte[] privateKey() {
      return key.getPrivKeyBytes();
    }

    @Override
    public byte[] signMessage(byte[] bytes) {
      try {
        return sign(privateKey(), bytes);
      } catch (CryptoException e) {
        throw new IllegalArgumentException(e);
      }
    }

    @Override
    public byte[] canonicalPubBytes() {
      return key.getPubKey();
    }

    @Override
    public boolean verify(byte[] bytes, byte[] bytes1) {
      return IVerifyingKey.from(canonicalPubBytes()).verify(bytes, bytes1);
    }
  }

  private static class ProvidedKeyPair implements IKeyPair {
    private final byte[] privateKey;
    private final byte[] publicKey;

    private ProvidedKeyPair(String privateKey, String publicKey) {
      this.privateKey = BaseEncoding.base16().decode(privateKey);
      this.publicKey = BaseEncoding.base16().decode(publicKey);
    }

    @Override
    public byte[] privateKey() {
      return privateKey;
    }

    @Override
    public byte[] signMessage(byte[] bytes) {
      try {
        return sign(privateKey, bytes);
      } catch (CryptoException e) {
        throw new IllegalArgumentException(e);
      }
    }

    @Override
    public byte[] canonicalPubBytes() {
      return publicKey;
    }

    @Override
    public boolean verify(byte[] bytes, byte[] bytes1) {
      return IVerifyingKey.from(canonicalPubBytes()).verify(bytes, bytes1);
    }
  }

  private static byte[] sign(byte[] privateKey, byte[] message) throws CryptoException {
    byte [] hash = new Sha512().add(message).finish256();
    if (privateKey.length == 33 && privateKey[0] == 0xed) {
      Signer signer = new Ed25519Signer();
      signer.init(true, new Ed25519PrivateKeyParameters(privateKey, 0));
      signer.update(hash, 0, hash.length);
      return signer.generateSignature();
    } else {
      ECDSASigner ecdsaSigner = new ECDSASigner(new HMacDSAKCalculator(new SHA1Digest()));
      ECDomainParameters domain = SECP256K1.params();
      ECPrivateKeyParameters privateKeyParms =
          new ECPrivateKeyParameters(new BigInteger(1, privateKey), domain);
      ecdsaSigner.init(true, privateKeyParms);
      return ECDSASignature.createSignature(hash, ecdsaSigner).encodeToDER();
    }
  }
}
