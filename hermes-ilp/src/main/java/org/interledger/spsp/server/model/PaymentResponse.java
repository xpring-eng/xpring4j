package org.interledger.spsp.server.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.primitives.UnsignedLong;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutablePaymentResponse.class)
@JsonSerialize(as = ImmutablePaymentResponse.class)
public interface PaymentResponse {
  static ImmutablePaymentResponse.Builder builder() {
    return ImmutablePaymentResponse.builder();
  }

  UnsignedLong originalAmount();
  UnsignedLong amountDelivered();
  UnsignedLong amountSent();
  boolean successfulPayment();

}
