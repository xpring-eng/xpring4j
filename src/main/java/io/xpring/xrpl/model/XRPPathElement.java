package io.xpring.xrpl.model;

import com.google.protobuf.ByteString;
import org.xrpl.rpc.v1.Payment.PathElement;
import org.immutables.value.Value;

/**
 */
@Value.Immutable
public interface XRPPathElement {
    static ImmutableXRPPathElement.Builder builder() {
        return ImmutableXRPPathElement.builder();
    }


    static XRPPathElement from(PathElement pathElement) {
        return builder()
                .build();
    }
}