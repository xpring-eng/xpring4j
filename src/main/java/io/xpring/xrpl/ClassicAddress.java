package io.xpring.xrpl;

import io.xpring.javascript.JavaScriptWallet;
import io.xpring.javascript.JavaScriptWalletFactory;
import io.xpring.javascript.JavaScriptWalletGenerationResult;
import org.immutables.value.Value;

import java.util.Optional;

/**
 * Represents classic address components on the XRP Ledger.
 */
@Value.Immutable
public interface ClassicAddress {
    /**
     * @return The address component of the classic address.
     */
    public String address();

    /**
     * @return The tag component of the classic address.
     */
    public Optional<Long> tag();
}
