package io.xpring.xrpl.model;

import org.immutables.value.Value;
import org.xrpl.rpc.v1.Common;

/**
 * Represents a SignerEntry object on the XRP Ledger.
 *
 * @see "https://xrpl.org/signerlist.html#signerentry-object"
 */
@Value.Immutable
public interface XrpSignerEntry {
  static ImmutableXrpSignerEntry.Builder builder() {
    return ImmutableXrpSignerEntry.builder();
  }

  /**
   * An XRP Ledger address whose signature contributes to the multi-signature.
   * <p>
   * It does not need to be a funded address in the ledger.
   * </p>
   *
   * @return A {@link String} containing an XRP Ledger address whose signature contributes to the multi-signature.
   */
  String account();

  /**
   * The weight of a signature from this signer.
   * <p>
   * A multi-signature is only valid if the sum weight of the signatures provided meets or exceeds the SignerList's
   * SignerQuorum value.
   * </p>
   * @return
   */
  Integer signerWeight();

  /**
   * Constructs an {@link XrpSignerEntry} from a {@link Common.SignerEntry} protocol buffer.
   *
   * @param signerEntry A {@link Common.SignerEntry} (protobuf object) whose field values will be used to construct
   *                    an {@link XrpSignerEntry}.
   * @return An {@link XrpSignerEntry} with its fields set via the analogous protobuf fields.
   * @see "https://github.com/ripple/rippled/blob/f43aeda49c5362dc83c66507cae2ec71cfa7bfdf/src/ripple/proto/org/xrpl/rpc/v1/common.proto#L471"
   */
  static XrpSignerEntry from(Common.SignerEntry signerEntry) {
    if (!signerEntry.hasAccount() || !signerEntry.hasSignerWeight()) {
      return null;
    }

    final String account = signerEntry.getAccount().toString();

    final Integer signerWeight = signerEntry.getSignerWeight().getValue();

    return XrpSignerEntry.builder()
        .account(account)
        .signerWeight(signerWeight)
        .build();
  }
}
