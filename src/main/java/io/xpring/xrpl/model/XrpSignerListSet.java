package io.xpring.xrpl.model;

import org.immutables.value.Value;
import org.xrpl.rpc.v1.SignerListSet;

/**
 * Represents a {@link SignerListSet} transaction on the XRP Ledger.
 * <p>
 * A {@link SignerListSet} transaction creates, replaces, or removes a list of signers that can be used to multi-sign a
 * transaction. This transaction type was introduced by the MultiSign amendment.
 * </p>
 *
 * @see "https://xrpl.org/signerlistset.html"
 */
@Value.Immutable
public interface XrpSignerListSet {
  static ImmutableXrpSignerListSet.Builder builder() {
    return ImmutableXrpSignerListSet.builder();
  }

  /**
   * A target number for the signer weights.
   * <p>
   * A multi-signature from this list is valid only if the sum weights of the signatures provided is greater than
   * or equal to this value.
   * To delete a SignerList, use the value 0.
   * </p>
   *
   * @return An {@link Integer} containing a target number for the signer weights.
   */
  Integer signerQuorum();

  /**
   * Constructs an {@link XrpSignerListSet} from a {@link SignerListSet} protocol buffer.
   *
   * @param signerListSet A {@link SignerListSet} (protobuf object) whose field values will be used to construct
   *                      an {@link XrpSignerListSet}.
   * @return An {@link XrpSignerListSet} with its fields set via the analogous protobuf fields.
   * @see "https://github.com/ripple/rippled/blob/3d86b49dae8173344b39deb75e53170a9b6c5284/src/ripple/proto/org/xrpl/rpc/v1/transaction.proto#L304"
   */
  static XrpSignerListSet from(SignerListSet signerListSet) {
    if (!signerListSet.hasSignerQuorum()) {
      return null;
    }
    
    final Integer signerQuorum = signerListSet.getSignerQuorum().getValue();

    return XrpSignerListSet.builder()
        .signerQuorum(signerQuorum)
        .build();
  }
}
