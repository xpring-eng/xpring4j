package io.xpring.xrpl.model;

import org.xrpl.rpc.v1.Memo;
import org.immutables.value.Value;

/**
 * Represents a memo on the XRPLedger.
 * @see "https://xrpl.org/transaction-common-fields.html#memos-field"
 */
@Value.Immutable
public interface XRPMemo {
    static ImmutableXRPMemo.Builder builder() {
        return ImmutableXRPMemo.builder();
    }

    /**
     *
     * @return Arbitrary hex value, conventionally containing the content of the memo.
     */
    byte[] data();

    /**
     *
     * @return Hex value representing characters allowed in URLs.
     * Conventionally containing information on how the memo is encoded, for example as a MIME type.
     */
    byte[] format();

    /**
     *
     * @return Hex value representing characters allowed in URLs.
     * Conventionally, a unique relation (according to RFC 5988) that defines the format of this memo.
     */
    byte[] type();

    static XRPMemo from(Memo memo) {
        return XRPMemo.builder()
                    .data(memo.getMemoData().getValue().toByteArray())
                    .format(memo.getMemoFormat().getValue().toByteArray())
                    .type(memo.getMemoType().toByteArray())
                    .build();
    }
}