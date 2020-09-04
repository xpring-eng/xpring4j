package io.xpring.xrpl.wallet;

import io.xpring.xrpl.Wallet;
import org.immutables.value.Value;

import java.util.Optional;

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

  String getPrivateKey();

  String getPublicKey();

  Optional<String> getDerivationPath();

  Optional<String> getSeed();

  Optional<String> getMnemonic();

}
