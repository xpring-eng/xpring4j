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
}
