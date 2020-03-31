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
    @Nullable
    byte[] data();

    /**
     * @return Hex value representing characters allowed in URLs.
     * Conventionally containing information on how the memo is encoded, for example as a MIME type.
     */
    @Nullable
    byte[] format();

    /**
     * @return Hex value representing characters allowed in URLs.
     * Conventionally, a unique relation (according to RFC 5988) that defines the format of this memo.
     */
    @Nullable
    byte[] type();

    /**
     * Constructs an {@link XRPMemo} from a {@link Memo}
     * @see <a href="https://github.com/ripple/rippled/blob/develop/src/ripple/proto/org/xrpl/rpc/v1/transaction.proto#L80">
     *     Memo protocol buffer</a>
     *
     * @param memo a {@link Memo} (protobuf object) whose field values will be used
     *                 to construct an {@link XRPMemo}
     * @return an {@link XRPMemo} with its fields set via the analogous protobuf fields.
     */
    static XRPMemo from(Memo memo) {
        ByteString memoData = memo.getMemoData().getValue();
        byte[] data = memoData.equals(ByteString.EMPTY) ? null : memoData.toByteArray();

        ByteString memoFormat = memo.getMemoFormat().getValue();
        byte[] format = memoFormat.equals(ByteString.EMPTY) ? null : memoFormat.toByteArray();

        ByteString memoType = memo.getMemoType().getValue();
        byte[] type = memoType.equals(ByteString.EMPTY) ? null : memoType.toByteArray();

        return XRPMemo.builder()
                    .data(data)
                    .format(format)
                    .type(type)
                    .build();
    }
}
