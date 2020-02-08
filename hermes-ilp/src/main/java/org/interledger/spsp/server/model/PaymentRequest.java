package org.interledger.spsp.server.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.primitives.UnsignedLong;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutablePaymentRequest.class)
@JsonSerialize(as = ImmutablePaymentRequest.class)
public interface PaymentRequest {

  static ImmutablePaymentRequest.Builder builder() {
    return ImmutablePaymentRequest.builder();
  }

  String destinationPaymentPointer();
  UnsignedLong amount();

}
