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
}
