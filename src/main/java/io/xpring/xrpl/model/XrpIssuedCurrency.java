package io.xpring.xrpl.model;

import org.immutables.value.Value;
import org.xrpl.rpc.v1.IssuedCurrencyAmount;

import java.math.BigInteger;

/**
 * An issued currency on the XRP Ledger.
 *
 * @see "https://xrpl.org/basic-data-types.html#specifying-currency-amounts"
 */
@Value.Immutable
public interface XrpIssuedCurrency {
  static ImmutableXrpIssuedCurrency.Builder builder() {
    return ImmutableXrpIssuedCurrency.builder();
  }

  /**
   * The {@link XrpCurrency} used to value the amount.
   *
   * @return The {@link XrpCurrency} used to value the amount.
   */
  XrpCurrency currency();

  /**
   * The value of the amount.
   *
   * @return A {@link BigInteger} representing the value of the amount.
   */
  BigInteger value();

  /**
   * The unique account address of the entity issuing the currency.
   *
   * @return The unique account address of the entity issuing the currency.
   */
  String issuer();

  /**
   * Constructs an {@link XrpIssuedCurrency} from a {@link IssuedCurrencyAmount}.
   *
   * @param issuedCurrency a {@link IssuedCurrencyAmount} (protobuf object) whose field values will be used
   *                       to construct an {@link XrpIssuedCurrency}
   * @return an {@link XrpIssuedCurrency} with its fields set via the analogous protobuf fields.
   * @see <a href="https://github.com/ripple/rippled/blob/develop/src/ripple/proto/org/xrpl/rpc/v1/amount.proto#L28">
   * IssuedCurrencyAmount protocol buffer</a>
   */
  static XrpIssuedCurrency from(IssuedCurrencyAmount issuedCurrency) {
    BigInteger value;
    try {
      value = new BigInteger((issuedCurrency.getValue()));
    } catch (NumberFormatException error) {
      throw error;
    }

    return builder()
        .currency(XrpCurrency.from(issuedCurrency.getCurrency()))
        .value(value)
        .issuer(issuedCurrency.getIssuer().getAddress())
        .build();
  }
}
