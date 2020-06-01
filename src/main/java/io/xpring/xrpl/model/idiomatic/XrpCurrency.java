package io.xpring.xrpl.model.idiomatic;

import org.immutables.value.Value;
import org.xrpl.rpc.v1.Currency;

/**
 * An issued currency on the XRP Ledger.
 *
 * @see "https://xrpl.org/currency-formats.html#currency-codes"
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
@Value.Immutable
public interface XrpCurrency {
  static ImmutableXrpCurrency.Builder builder() {
    return ImmutableXrpCurrency.builder();
  }

  /**
   * The 3 character currency ASCII code.
   *
   * @return The 3 character currency ASCII code.
   */
  String name();

  /**
   * The 160 bit currency code. 20 bytes.
   *
   * @return The 160 bit currency code. 20 bytes.
   */
  byte[] code();

  /**
   * Constructs an {@link XrpCurrency} from a {@link Currency}.
   *
   * @param currency a {@link Currency} (protobuf object) whose field values will be used
   *                 to construct an {@link XrpCurrency}
   * @return an {@link XrpCurrency} with its fields set via the analogous protobuf fields.
   * @see <a href="https://github.com/ripple/rippled/blob/develop/src/ripple/proto/org/xrpl/rpc/v1/amount.proto#L41">
   * Currency protocol buffer</a>
   */
  static XrpCurrency from(Currency currency) {
    return builder()
        .name(currency.getName())
        .code(currency.getCode().toByteArray())
        .build();
  }
}
