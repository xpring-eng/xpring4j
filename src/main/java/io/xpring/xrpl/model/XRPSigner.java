package io.xpring.xrpl.model;

import com.google.protobuf.ByteString;
import org.xrpl.rpc.v1.Signer;
import org.immutables.value.Value;

import javax.annotation.Nullable;

/**
 * Represents a memo on the XRPLedger.
 * @see "https://xrpl.org/transaction-common-fields.html#memos-field"
 */
@Value.Immutable
public interface XRPSigner {
    static ImmutableXRPSigner.Builder builder() {
        return ImmutableXRPSigner.builder();
    }

    static XRPSigner from(Signer memo) {
        return XRPSigner.builder()
                .data(data)
                .format(format)
                .type(type)
                .build();
    }
}