package io.xpring.xrpl.model;

import com.google.protobuf.ByteString;
import org.xrpl.rpc.v1.Currency;
import org.immutables.value.Value;

/**
 * An issued currency on the XRP LEdger
 *
 * @see "https://xrpl.org/currency-formats.html#currency-codes"
 */
@Value.Immutable
public interface XRPCurrency {
    static ImmutableXRPCurrency.Builder builder() {
        return ImmutableXRPCurrency.builder();
    }

    /**
     * @return 3 character currency ASCII code
     */
    String name();

    /**
     * @return 160 bit currency code. 20 bytes
     */
    ByteString code();

    static XRPCurrency from(Currency currency) {
        if (currency.getName().isEmpty()) {
            return null;
        }
        if (currency.getCode().isEmpty()) {
            return null;

        }
        return builder()
                .name(currency.getName())
                .code(currency.getCode())
                .build();
    }
}