package io.xpring.xrpl.model;

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
    byte[] code();

    /**
     * Constructs an {@link XRPCurrency} from a {@link Currency}
     * @see <a href="https://github.com/ripple/rippled/blob/develop/src/ripple/proto/org/xrpl/rpc/v1/amount.proto#L41">
     *     Currency protocol buffer</a>
     *
     * @param currency a {@link Currency} (protobuf object) whose field values will be used
     *                 to construct an {@link XRPCurrency}
     * @return an {@link XRPCurrency} with its fields set via the analogous protobuf fields.
     */
    static XRPCurrency from(Currency currency) {
        return builder()
                .name(currency.getName())
                .code(currency.getCode().toByteArray())
                .build();
    }
}