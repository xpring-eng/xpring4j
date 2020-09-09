package io.xpring.xrpl.converters;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value.Immutable;

import javax.annotation.Nullable;

/**
 * Represents a memo on the XRPLedger.
 *
 * @see "https://xrpl.org/transaction-common-fields.html#memos-field"
 */
@Immutable
@JsonSerialize(as = ImmutableJsonMemo.class)
@JsonDeserialize(as = ImmutableJsonMemo.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public interface JsonMemo {
  static ImmutableJsonMemo.Builder builder() {
    return ImmutableJsonMemo.builder();
  }

  /**
   * (Optional) An arbitrary hex value, conventionally containing the content of the memo.
   *
   * @return A byte array of arbitrary hex value, conventionally containing the content of the memo.
   */
  @Nullable
  @JsonProperty("MemoData")
  String data();

  /**
   * (Optional) Hex value representing characters allowed in URLs.
   * Conventionally containing information on how the memo is encoded, for example as a MIME type.
   *
   * @return A byte array of arbitrary hex value containing the characters allowed in URLs.
   */
  @Nullable
  @JsonProperty("MemoFormat")
  String format();

  /**
   * (Optional) Hex value representing characters allowed in URLs.
   * Conventionally, a unique relation (according to RFC 5988) that defines the format of this memo.
   *
   * @return A byte array of arbitrary hex value containing the characters allowed in URLs.
   */
  @Nullable
  @JsonProperty("MemoType")
  String type();

}
