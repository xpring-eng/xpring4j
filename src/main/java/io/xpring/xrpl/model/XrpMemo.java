package io.xpring.xrpl.model;

import com.google.protobuf.ByteString;
import io.xpring.common.CommonUtils;
import org.immutables.value.Value;
import org.xrpl.rpc.v1.Memo;

import java.util.Optional;

/**
 * Represents a memo on the XRPLedger.
 *
 * @see "https://xrpl.org/transaction-common-fields.html#memos-field"
 */
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


  /**
   * Converts strings that may or may not be hex (as indicated by the MemoField argument) into the
   * byte[] fields needed for an XrpMemo instance.
   *
   * @param data - an Optional {@link MemoField} with a string which may or may not be converted to a hex string.
   * @param format - an Optional {@link MemoField} with a string which may or may not be converted to a hex string.
   * @param type - an Optional {@link MemoField} with a string which may or may not be converted to a hex string.
   * @return an {@link XrpMemo} with each potentially hex-encoded field set to the byte[] version of said field.
   */
  static XrpMemo fromMemoFields(
          Optional<MemoField> data,
          Optional<MemoField> format,
          Optional<MemoField> type
  ) {
    return XrpMemo.builder()
                .data(data.map(memoField -> CommonUtils.stringToByteArray(memoField.value(), memoField.isHex()))
                        .orElseGet(() -> new byte[0]))
                .format(format.map(field -> CommonUtils.stringToByteArray(field.value(), field.isHex()))
                        .orElseGet(() -> new byte[0]))
                .type(type.map(value -> CommonUtils.stringToByteArray(value.value(), value.isHex()))
                        .orElseGet(() -> new byte[0]))
                .build();
  }
}
