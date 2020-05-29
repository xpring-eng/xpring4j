package io.xpring.xrpl.model;

import io.xpring.xrpl.model.idiomatic.XrpCurrency;
import org.immutables.value.Value;
import org.xrpl.rpc.v1.Currency;

/**
 * An issued currency on the XRP Ledger.
 *
 * @deprecated Please use the idiomatically named {@link XrpCurrency} instead.
 *
 * @see "https://xrpl.org/currency-formats.html#currency-codes"
 */
@Deprecated
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
@Value.Immutable
public interface XRPCurrency {
  static ImmutableXRPCurrency.Builder builder() {
    return ImmutableXRPCurrency.builder();
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
   * Constructs an {@link XRPCurrency} from a {@link Currency}.
   *
   * @param currency a {@link Currency} (protobuf object) whose field values will be used
   *                 to construct an {@link XRPCurrency}
   * @return an {@link XRPCurrency} with its fields set via the analogous protobuf fields.
   * @see <a href="https://github.com/ripple/rippled/blob/develop/src/ripple/proto/org/xrpl/rpc/v1/amount.proto#L41">
   * Currency protocol buffer</a>
   */
  static XRPCurrency from(Currency currency) {
    return builder()
        .name(currency.getName())
        .code(currency.getCode().toByteArray())
        .build();
  }
}
