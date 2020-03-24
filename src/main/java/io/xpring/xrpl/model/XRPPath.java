package io.xpring.xrpl.model;

import com.google.protobuf.ByteString;
import org.xrpl.rpc.v1.Payment.Path;
import org.immutables.value.Value;

/**
 */
@Value.Immutable
public interface XRPPath {
    static ImmutableXRPPath.Builder builder() {
        return ImmutableXRPPath.builder();
    }


    static XRPPath from(Path path) {
        return builder()
                .build();
    }
}