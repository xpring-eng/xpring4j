package io.xpring.payid;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

/**
 * Represents a response from the Pay ID Service.
 */
// TODO(keefertaylor): Generalize this class when response format is decided.
@Value.Immutable
@JsonDeserialize(as = ImmutablePayIDResponse.class)
@JsonPropertyOrder( {"address"})
public interface PayIDResponse {
    /** @return The address represented by this response. */
    public String address();
}
 