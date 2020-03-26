package io.xpring.xrpl.model;

import org.xrpl.rpc.v1.CurrencyAmount;
import org.immutables.value.Value;
import org.xrpl.rpc.v1.IssuedCurrencyAmount;

/**
 * An amount of currency on the XRP Ledger
 * @see "https://xrpl.org/basic-data-types.html#specifying-currency-amounts"
 */
@Value.Immutable
public interface XRPCurrencyAmount {
    static ImmutableXRPCurrencyAmount.Builder builder() {
        return ImmutableXRPCurrencyAmount.builder();
    }

    /**
     *
     * @return
     */
    String drops();

    /**
     *
     * @return
     */
    XRPIssuedCurrency issuedCurrency();

    static XRPCurrencyAmount from(CurrencyAmount currencyAmount) {
        switch (currencyAmount.getAmountCase()) {
            // Mutually exclusive: either drops or issuedCurrency is set in an XRPCurrencyAmount
            case ISSUED_CURRENCY_AMOUNT: {
                IssuedCurrencyAmount issuedCurrencyAmount = currencyAmount.getIssuedCurrencyAmount();
                if (issuedCurrencyAmount!= null) {
                    XRPIssuedCurrency xrpIssuedCurrency = XRPIssuedCurrency.from(issuedCurrencyAmount);
                    if (xrpIssuedCurrency != null) {
                        return builder().drops(null).issuedCurrency(xrpIssuedCurrency).build();
                    }
                }
                // if AmountCase is ISSUED_CURRENCY_AMOUNT, we must be able to convert this to an XRPIssuedCurrency
                return null;
            }
            case XRP_AMOUNT: {
                long numeric_drops = currencyAmount.getXrpAmount().getDrops();
                String drops = Long.toString(numeric_drops);
                return builder().drops(drops).issuedCurrency(null).build();
            }
            default:
                return null;
        }
    }
}