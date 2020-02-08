package org.interledger.spsp.server.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableCreateAccountRestRequest.class)
public interface CreateAccountRestRequest {

  static ImmutableCreateAccountRestRequest.Builder builder() {
    return ImmutableCreateAccountRestRequest.builder();
  }

  String accountId();

  String assetCode();

  int assetScale();

  String description();

}
