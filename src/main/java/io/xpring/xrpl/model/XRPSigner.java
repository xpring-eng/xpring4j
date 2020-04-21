package io.xpring.xrpl.model;

import org.immutables.value.Value;
import org.xrpl.rpc.v1.Signer;

/**
 * Represents a signer of a transaction on the XRP Ledger.
 *
 * @see "https://xrpl.org/transaction-common-fields.html#signers-field"
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
@Value.Immutable
public interface XRPSigner {
  static ImmutableXRPSigner.Builder builder() {
    return ImmutableXRPSigner.builder();
  }

  /**
   * The address associated with this signature, as it appears in the SignerList.
   *
   * @return A {@link String} containing the address associated with this signature, as it appears in the SignerList.
   */
  String account();

  /**
   * The public key used to create this signature.
   *
   * @return A byte array containing the public key used to create this signature.
   */
  byte[] signingPublicKey();

  /**
   * A signature for this transaction, verifiable using the {@code signingPublicKey()}.
   *
   * @return A byte array containing a signature for this transaction, verifiable using the {@code signingPublicKey()}.
   */
  byte[] transactionSignature();

  /**
   * Constructs an {@link XRPSigner} from a {@link Signer}.
   *
   * @param signer a {@link Signer} (protobuf object) whose field values will be used
   *               to construct an {@link XRPSigner}
   * @return an {@link XRPSigner} with its fields set via the analogous protobuf fields.
   * @see <a href="https://github.com/ripple/rippled/blob/develop/src/ripple/proto/org/xrpl/rpc/v1/transaction.proto#L90">
   * Signer protocol buffer</a>
   */
  static XRPSigner from(Signer signer) {
    String account = signer.getAccount().getValue().getAddress();

    byte[] signingPublicKey = signer.getSigningPublicKey().getValue().toByteArray();

    byte[] transactionSignature = signer.getTransactionSignature().getValue().toByteArray();

    return XRPSigner.builder()
        .account(account)
        .signingPublicKey(signingPublicKey)
        .transactionSignature(transactionSignature)
        .build();
  }
}
