package io.xpring.xrpl.model.idiomatic;

import com.google.protobuf.ByteString;
import org.immutables.value.Value;
import org.xrpl.rpc.v1.Memo;

/**
 * Represents a memo on the XRPLedger.
 *
 * @see "https://xrpl.org/transaction-common-fields.html#memos-field"
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
@Value.Immutable
public interface XrpMemo {
  static ImmutableXrpMemo.Builder builder() {
    return ImmutableXrpMemo.builder();
  }

  /**
   * (Optional) An arbitrary hex value, conventionally containing the content of the memo.
   *
   * @return A byte array of arbitrary hex value, conventionally containing the content of the memo.
   */
  @Value.Default
  default byte[] data() {
    return new byte[0];
  }

  /**
   * (Optional) Hex value representing characters allowed in URLs.
   * Conventionally containing information on how the memo is encoded, for example as a MIME type.
   *
   * @return A byte array of arbitrary hex value containing the characters allowed in URLs.
   */
  @Value.Default
  default byte[] format() {
    return new byte[0];
  }

  /**
   * (Optional) Hex value representing characters allowed in URLs.
   * Conventionally, a unique relation (according to RFC 5988) that defines the format of this memo.
   *
   * @return A byte array of arbitrary hex value containing the characters allowed in URLs.
   */
  @Value.Default
  default byte[] type() {
    return new byte[0];
  }

  /**
   * Constructs an {@link XrpMemo} from a {@link Memo}.
   *
   * @param memo a {@link Memo} (protobuf object) whose field values will be used
   *             to construct an {@link XrpMemo}
   * @return an {@link XrpMemo} with its fields set via the analogous protobuf fields.
   * @see <a href="https://github.com/ripple/rippled/blob/develop/src/ripple/proto/org/xrpl/rpc/v1/transaction.proto#L80">
   * Memo protocol buffer</a>
   */
  static XrpMemo from(Memo memo) {
    ByteString memoData = memo.getMemoData().getValue();
    byte[] data = memoData.toByteArray();

    ByteString memoFormat = memo.getMemoFormat().getValue();
    byte[] format = memoFormat.toByteArray();

    ByteString memoType = memo.getMemoType().getValue();
    byte[] type = memoType.toByteArray();

    return XrpMemo.builder()
        .data(data)
        .format(format)
        .type(type)
        .build();
  }
}
