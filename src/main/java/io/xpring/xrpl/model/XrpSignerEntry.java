package io.xpring.xrpl.model;

import org.immutables.value.Value;

/**
 * Represents a SignerEntry object on the XRP Ledger.
 *
 * @see "https://xrpl.org/signerlist.html#signerentry-object"
 */
@Value.Immutable
public interface XrpSignerEntry {
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
}
