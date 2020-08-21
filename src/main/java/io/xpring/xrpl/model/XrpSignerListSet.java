package io.xpring.xrpl.model;

import org.immutables.value.Value;
import org.xrpl.rpc.v1.SignerListSet;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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
  * (Omitted when deleting) Array of XRPSignerEntry objects, indicating the addresses and weights of signers in
   * this list.
   * <p>
   * A SignerList must have at least 1 member and no more than 8 members. No address may appear more than once in the
   * list, nor may the Account submitting the transaction appear in the list.
   * </p>
   *
   * @return A {@link List} containing {@link XrpSignerEntry} objects, indicating the addresses and weights of signers
   *         in this list.
   */
  Optional<List<XrpSignerEntry>> signerEntries();

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

    final Optional<List<XrpSignerEntry>> signerEntriesList = signerListSet.getSignerEntriesList() != null
        ? Optional.of(signerListSet.getSignerEntriesList().stream()
          .map(XrpSignerEntry::from)
          .filter(Objects::nonNull)
          .collect(Collectors.toList()))
        : Optional.empty();

    return XrpSignerListSet.builder()
        .signerQuorum(signerQuorum)
        .build();
  }
}
