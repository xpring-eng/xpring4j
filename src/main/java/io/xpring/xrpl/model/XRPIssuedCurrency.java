package io.xpring.xrpl.model;

import org.immutables.value.Value;
import org.xrpl.rpc.v1.IssuedCurrencyAmount;

import java.math.BigInteger;

/**
 * An issued currency on the XRP Ledger.
 *
 * @see "https://xrpl.org/basic-data-types.html#specifying-currency-amounts"
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
@Value.Immutable
public interface XRPIssuedCurrency {
  static ImmutableXRPIssuedCurrency.Builder builder() {
    return ImmutableXRPIssuedCurrency.builder();
  }

  /**
   * The currency used to value the amount.
   */
  XRPCurrency currency();

  /**
   * The value of the amount.
   */
  BigInteger value();

  /**
   * Unique account address of the entity issuing the currency.
   */
  String issuer();

  /**
   * Constructs an {@link XRPIssuedCurrency} from a {@link IssuedCurrencyAmount}.
   *
   * @param issuedCurrency a {@link IssuedCurrencyAmount} (protobuf object) whose field values will be used
   *                       to construct an {@link XRPIssuedCurrency}
   * @return an {@link XRPIssuedCurrency} with its fields set via the analogous protobuf fields.
   * @see <a href="https://github.com/ripple/rippled/blob/develop/src/ripple/proto/org/xrpl/rpc/v1/amount.proto#L28">
   * IssuedCurrencyAmount protocol buffer</a>
   */
  static XRPIssuedCurrency from(IssuedCurrencyAmount issuedCurrency) {
    BigInteger value;
    try {
      value = new BigInteger((issuedCurrency.getValue()));
    } catch (NumberFormatException error) {
      throw error;
    }

    return builder()
        .currency(XRPCurrency.from(issuedCurrency.getCurrency()))
        .value(value)
        .issuer(issuedCurrency.getIssuer().getAddress())
        .build();
  }
}
