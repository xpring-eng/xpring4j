package io.xpring.xrpl.model;

import com.google.protobuf.ByteString;
import org.xrpl.rpc.v1.Memo;
import org.immutables.value.Value;

import javax.annotation.Nullable;

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
     * @return Arbitrary hex value, conventionally containing the content of the memo.
     */
    @Nullable  byte[] data();

    /**
     * @return Hex value representing characters allowed in URLs.
     * Conventionally containing information on how the memo is encoded, for example as a MIME type.
     */
    @Nullable byte[] format();

    /**
     * @return Hex value representing characters allowed in URLs.
     * Conventionally, a unique relation (according to RFC 5988) that defines the format of this memo.
     */
    @Nullable byte[] type();

    static XRPMemo from(Memo memo) {
        byte[] data;
        byte[] format;
        byte[] type;
        if (memo.getMemoData().getValue() == ByteString.EMPTY) {
            data = null;
        } else {
            data = memo.getMemoType().getValue().toByteArray();
        }
        if (memo.getMemoFormat().getValue() == ByteString.EMPTY) {
            format = memo.getMemoFormat().getValue().toByteArray();
        } else {
            format = null;
        }
        if (memo.getMemoType().getValue() == ByteString.EMPTY) {
            type = memo.getMemoType().toByteArray();
        } else {
            type = null;
        }
        return XRPMemo.builder()
                    .data(data)
                    .format(format)
                    .type(type)
                    .build();
    }
}