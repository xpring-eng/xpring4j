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

  static XrpSignerEntry from(Common.SignerEntry signerEntry) {
    if (!signerEntry.hasAccount() || !signerEntry.hasSignerWeight()) {
      return null;
    }

    return XrpSignerEntry.builder()
        .build();
  }
}
