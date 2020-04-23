package io.xpring.xrpl.model;


import org.immutables.value.Value;
import org.xrpl.rpc.v1.CurrencyAmount;
import org.xrpl.rpc.v1.IssuedCurrencyAmount;

import javax.annotation.Nullable;

/**
 * An amount of currency on the XRP Ledger.
 *
 * @see "https://xrpl.org/basic-data-types.html#specifying-currency-amounts"
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
@Value.Immutable
public interface XRPCurrencyAmount {
  static ImmutableXRPCurrencyAmount.Builder builder() {
    return ImmutableXRPCurrencyAmount.builder();
  }

  /**
   * An amount of XRP, specified in drops.
   * <p>
   * Note: Mutually exclusive fields - only drops XOR issuedCurrency should be set.
   * </p>
   *
   * @return The {@link String} representation of an amount of XRP, specified in drops.
   */
  @Nullable
  String drops();

  /**
   * An amount of an issued currency.
   * <p>
   * Note: Mutually exclusive fields - only drops XOR issuedCurrency should be set.
   * </p>
   *
   * @return The {@link XRPIssuedCurrency} of this {@link XRPCurrencyAmount}.
   */
  @Nullable
  XRPIssuedCurrency issuedCurrency();

  /**
   * Constructs an {@link XRPCurrencyAmount} from a {@link CurrencyAmount}.
   *
   * @param currencyAmount a {@link CurrencyAmount} (protobuf object) whose field values will be used
   *                       to construct an {@link XRPCurrencyAmount}
   * @return an {@link XRPCurrencyAmount} with its fields set via the analogous protobuf fields.
   * @see <a href="https://github.com/ripple/rippled/blob/develop/src/ripple/proto/org/xrpl/rpc/v1/amount.proto#L10">
   * CurrencyAmount protocol buffer</a>
   */
  static XRPCurrencyAmount from(CurrencyAmount currencyAmount) throws NumberFormatException {
    switch (currencyAmount.getAmountCase()) {
      // Mutually exclusive: either drops or issuedCurrency is set in an XRPCurrencyAmount
      case ISSUED_CURRENCY_AMOUNT: {
        IssuedCurrencyAmount issuedCurrencyAmount = currencyAmount.getIssuedCurrencyAmount();
        if (issuedCurrencyAmount != null) {
          XRPIssuedCurrency xrpIssuedCurrency = XRPIssuedCurrency.from(issuedCurrencyAmount);
          if (xrpIssuedCurrency != null) {
            return builder().issuedCurrency(xrpIssuedCurrency).build();
          }
        }
        // if AmountCase is ISSUED_CURRENCY_AMOUNT, we must be able to convert this to an XRPIssuedCurrency
        return null;
      }
      case XRP_AMOUNT: {
        long numericDrops = currencyAmount.getXrpAmount().getDrops();
        String drops = Long.toString(numericDrops);
        return builder().drops(drops)
            .build();
      }
      // if no AmountCase is set (e.g. empty CurrencyAmount protobuf was passed to constructor)
      default:
        return null;
    }
  }
}
