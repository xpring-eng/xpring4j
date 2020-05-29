package io.xpring.xrpl.model;

import com.google.protobuf.ByteString;
import io.xpring.xrpl.model.idiomatic.XrpMemo;
import org.immutables.value.Value;
import org.xrpl.rpc.v1.Memo;

/**
 * Represents a memo on the XRPLedger.
 *
 * @deprecated Please use the idiomatically named {@link XrpMemo} instead.
 *
 * @see "https://xrpl.org/transaction-common-fields.html#memos-field"
 */
@Deprecated
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
    byte[] data = memoData.toByteArray();

    ByteString memoFormat = memo.getMemoFormat().getValue();
    byte[] format = memoFormat.toByteArray();

    ByteString memoType = memo.getMemoType().getValue();
    byte[] type = memoType.toByteArray();

    return XRPMemo.builder()
        .data(data)
        .format(format)
        .type(type)
        .build();
  }
}
