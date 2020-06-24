package io.xpring.xrpl.model;


import org.immutables.value.Value;
import org.xrpl.rpc.v1.CurrencyAmount;

import java.util.Optional;

/**
 * An amount of currency on the XRP Ledger.
 *
 * @see "https://xrpl.org/basic-data-types.html#specifying-currency-amounts"
 */
@Value.Immutable
public interface XrpCurrencyAmount {
  static ImmutableXrpCurrencyAmount.Builder builder() {
    return ImmutableXrpCurrencyAmount.builder();
  }

  /**
   * (Optional) An amount of XRP, specified in drops.
   * Note: Mutually exclusive fields - only drops XOR issuedCurrency should be set.
   *
   * @return The Optional {@link String} representation of an amount of XRP, specified in drops.
   */
  Optional<String> drops();

  /**
   * (Optional) An amount of an issued currency.
   * Note: Mutually exclusive fields - only drops XOR issuedCurrency should be set.
   *
   * @return The Optional {@link XrpIssuedCurrency} of this {@link XrpCurrencyAmount}.
   */
  Optional<XrpIssuedCurrency> issuedCurrency();

  /**
   * Constructs an {@link XrpCurrencyAmount} from a {@link CurrencyAmount}.
   *
   * @param currencyAmount a {@link CurrencyAmount} (protobuf object) whose field values will be used
   *                       to construct an {@link XrpCurrencyAmount}
   * @return an {@link XrpCurrencyAmount} with its fields set via the analogous protobuf fields.
   * @see <a href="https://github.com/ripple/rippled/blob/develop/src/ripple/proto/org/xrpl/rpc/v1/amount.proto#L10">
   * CurrencyAmount protocol buffer</a>
   */
  static XrpCurrencyAmount from(CurrencyAmount currencyAmount) throws NumberFormatException {
    switch (currencyAmount.getAmountCase()) {
      // Mutually exclusive: either drops or issuedCurrency is set in an XRPCurrencyAmount
      case ISSUED_CURRENCY_AMOUNT: {
        XrpIssuedCurrency xrpIssuedCurrency = XrpIssuedCurrency.from(currencyAmount.getIssuedCurrencyAmount());
        if (xrpIssuedCurrency != null) {
          return builder().issuedCurrency(xrpIssuedCurrency).build();
        }
        // if AmountCase is ISSUED_CURRENCY_AMOUNT, we must be able to convert this to an XRPIssuedCurrency
        return null;
      }
      case XRP_AMOUNT: {
        long numericDrops = currencyAmount.getXrpAmount().getDrops();
        String drops = Long.toString(numericDrops);
        return builder().drops(drops).build();
      }
      // if no AmountCase is set (e.g. empty CurrencyAmount protobuf was passed to constructor)
      default:
        return null;
    }
  }
}
