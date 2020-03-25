package io.xpring.xrpl.model;
import org.xrpl.rpc.v1.IssuedCurrencyAmount;
import org.immutables.value.Value;
import org.xrpl.rpc.v1.Payment;

import java.math.BigInteger;


/**
 * An issued currency on the XRP Ledger
 * @see "https://xrpl.org/basic-data-types.html#specifying-currency-amounts"
 */
@Value.Immutable
public interface XRPIssuedCurrency {
    static ImmutableXRPIssuedCurrency.Builder builder() {
        return ImmutableXRPIssuedCurrency.builder();
    }

    /**
     *
     * @return The currency used to value the amount.
     */
    XRPCurrency currency();

    /**
     *
     * @return The value of the amount, 8 bytes.
     */
    BigInteger value();

    /**
     *
     * @return Unique account address of the entity issuing the currency.
     */
    String issuer();

    static XRPIssuedCurrency from(IssuedCurrencyAmount issuedCurrency) {
        return builder()
                .currency(XRPCurrency.from(issuedCurrency.getCurrency()))
                .value(new BigInteger(issuedCurrency.getValue()))
                .issuer(issuedCurrency.getIssuer().getAddress())
                .build();
    }
}