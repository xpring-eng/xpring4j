package io.xpring.xrpl.wallet;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.io.BaseEncoding;
import com.ripple.core.coretypes.AccountID;
import com.ripple.crypto.Seed;
import com.ripple.crypto.keys.IKeyPair;
import io.xpring.codec.addresses.XAddressCodec;
import io.xpring.xrpl.ClassicAddress;
import io.xpring.xrpl.XrpException;
import io.xpring.xrpl.XrpExceptionType;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.crypto.HDUtils;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.crypto.MnemonicException;

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

  public WalletGenerationResult generateRandomWallet(boolean isTest) throws XrpException {
    byte[] randomBytes = randomBytes(16);
    try {
      List<String> mnemonic = MnemonicCode.INSTANCE.toMnemonic(randomBytes);
      return generateWalletFromMnemonic(Joiner.on(" ").join(mnemonic), defaultDerivationPath, isTest);
    } catch (MnemonicException.MnemonicLengthException e) {
      throw new XrpException(XrpExceptionType.UNKNOWN, e.getMessage());
    }
  }

  public WalletGenerationResult generateWalletFromSeed(String seed, boolean isTest) throws XrpException {
    IKeyPair keyPair = Seed.getKeyPair(seed);
    return generateWalletFromKeys(getPrivateKey(keyPair), keyPair.canonicalPubHex(), isTest);
  }

  public WalletGenerationResult generateWalletFromSeedBytes(byte[] seed, boolean isTest) throws XrpException {
    IKeyPair keyPair = generateHDWalletFromSeed(seed, defaultDerivationPath);
    AccountID accountID = AccountID.fromKeyPair(keyPair);
    return WalletGenerationResult.builder()
        .address(XAddressCodec.encode(toClassicAddress(accountID, isTest)))
        .derivationPath(defaultDerivationPath)
        .privateKey(getPrivateKey(keyPair))
        .publicKey(keyPair.canonicalPubHex())
        .seed(BaseEncoding.base16().encode(seed))
        .build();
  }

  public WalletGenerationResult generateWalletFromKeys(String privateKey, String publicKey, boolean isTest) throws XrpException {
    IKeyPair keyPair = new ProvidedKeyPair(privateKey, publicKey);
    AccountID accountID = AccountID.fromKeyPair(keyPair);
    return WalletGenerationResult.builder()
        .address(XAddressCodec.encode(toClassicAddress(accountID, isTest)))
        .derivationPath(defaultDerivationPath)
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

  public WalletGenerationResult generateWalletFromMnemonic(
      String mnemonic,
      String maybeDerivationPath,
      boolean isTest) throws XrpException {
    String derivationPath = Optional.ofNullable(maybeDerivationPath).orElse(defaultDerivationPath);
    try {
      byte[] seed = toSeedBytes(mnemonic);
      IKeyPair keyPair = generateHDWalletFromSeed(seed, derivationPath);
      AccountID accountID = AccountID.fromKeyPair(keyPair);
      return WalletGenerationResult.builder()
          .address(XAddressCodec.encode(toClassicAddress(accountID, isTest)))
          .derivationPath(derivationPath)
          .privateKey(getPrivateKey(keyPair))
          .publicKey(keyPair.canonicalPubHex())
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

  private IKeyPair generateHDWalletFromSeed(
      byte[] seedBytes,
      String derivationPath) {
    DeterministicKey masterKey = HDKeyDerivation.createMasterPrivateKey(seedBytes);
    List<ChildNumber> childNumbers = HDUtils.parsePath(derivationPath.replaceAll("'", "H").toUpperCase());
    DeterministicKey prevKey = masterKey;
    for(ChildNumber childNumber : childNumbers) {
      prevKey = HDKeyDerivation.deriveChildKey(prevKey, childNumber);
    }
    return new DeterministicKeyPair(prevKey);
  }

  private IKeyPair generateHDWalletFromPrivateKey(String privateKey, String derivationPath) {
    List<ChildNumber> childNumbers = HDUtils.parsePath(derivationPath.replaceAll("'", "H").toUpperCase());
    DeterministicKey masterKey = HDKeyDerivation.createMasterPrivateKey(BaseEncoding.base16().decode(privateKey));
    DeterministicKey prevKey = masterKey;
    for(ChildNumber childNumber : childNumbers) {
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
      return BaseEncoding.base16().decode(
          key.signMessage(BaseEncoding.base16().encode(bytes)));
    }

    @Override
    public byte[] canonicalPubBytes() {
      return key.getPubKey();
    }

    @Override
    public boolean verify(byte[] bytes, byte[] bytes1) {
      throw new UnsupportedOperationException("not implemented");
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
      throw new UnsupportedOperationException("FIXME");
    }

    @Override
    public byte[] canonicalPubBytes() {
      return publicKey;
    }

    @Override
    public boolean verify(byte[] bytes, byte[] bytes1) {
      throw new UnsupportedOperationException("not implemented");
    }
  }
}
