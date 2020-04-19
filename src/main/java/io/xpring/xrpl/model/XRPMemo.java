package io.xpring.xrpl.model;

import com.google.protobuf.ByteString;
import org.immutables.value.Value;
import org.xrpl.rpc.v1.Memo;

import java.util.Optional;

/**
 * Represents a memo on the XRPLedger.
 *
 * @see "https://xrpl.org/transaction-common-fields.html#memos-field"
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
@Value.Immutable
public interface XRPMemo {
  static ImmutableXRPMemo.Builder builder() {
    return ImmutableXRPMemo.builder();
  }

  /**
   * (Optional) An arbitrary hex value, conventionally containing the content of the memo.
   *
   * @return A byte array of arbitrary hex value, conventionally containing the content of the memo.
   */
  Optional<byte[]> data();

  /**
   * (Optional) Hex value representing characters allowed in URLs.
   * Conventionally containing information on how the memo is encoded, for example as a MIME type.
   *
   * @return A byte array of arbitrary hex value containing the characters allowed in URLs.
   */
  Optional<byte[]> format();

  /**
   * (Optional) Hex value representing characters allowed in URLs.
   * Conventionally, a unique relation (according to RFC 5988) that defines the format of this memo.
   *
   * @return A byte array of arbitrary hex value containing the characters allowed in URLs.
   */
  Optional<byte[]> type();

  /**
   * Constructs an {@link XRPMemo} from a {@link Memo}.
   *
   * @param memo a {@link Memo} (protobuf object) whose field values will be used
   *             to construct an {@link XRPMemo}
   * @return an {@link XRPMemo} with its fields set via the analogous protobuf fields.
   * @see <a href="https://github.com/ripple/rippled/blob/develop/src/ripple/proto/org/xrpl/rpc/v1/transaction.proto#L80">
   * Memo protocol buffer</a>
   */
  static XRPMemo from(Memo memo) {
    ByteString memoData = memo.getMemoData().getValue();
    Optional<byte[]> data = memoData.equals(ByteString.EMPTY) ? Optional.empty() : Optional.of(memoData.toByteArray());

    ByteString memoFormat = memo.getMemoFormat().getValue();
    Optional<byte[]> format = memoFormat.equals(ByteString.EMPTY)
            ? Optional.empty() : Optional.of(memoFormat.toByteArray());

    ByteString memoType = memo.getMemoType().getValue();
    Optional<byte[]> type = memoType.equals(ByteString.EMPTY) ? Optional.empty() : Optional.of(memoType.toByteArray());

    return XRPMemo.builder()
        .data(data)
        .format(format)
        .type(type)
        .build();
  }
}
