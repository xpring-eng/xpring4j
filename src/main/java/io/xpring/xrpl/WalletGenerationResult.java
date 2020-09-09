package io.xpring.xrpl;

import com.ripple.crypto.keys.IKeyPair;
import org.immutables.value.Value;

import java.util.Optional;

/**
 * Contains artifacts of generating a new Wallet.
 */
@Value.Immutable
public interface WalletGenerationResult {

  static ImmutableWalletGenerationResult.Builder builder() {
    return ImmutableWalletGenerationResult.builder();
  }

  @Value.Auxiliary
  default Wallet getWallet() {
    return new Wallet(this);
  }

  String getAddress();

  IKeyPair getKeyPair();

  String getPrivateKey();

  String getPublicKey();

  Optional<String> getDerivationPath();

  Optional<String> getSeed();

  Optional<String> getMnemonic();

}