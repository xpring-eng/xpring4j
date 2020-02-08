package org.interledger.spsp.server.client;


import org.interledger.spsp.server.config.jackson.ObjectMapperFactory;
import org.interledger.connector.accounts.AccountId;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Feign;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.optionals.OptionalDecoder;
import okhttp3.HttpUrl;

import java.util.Objects;
import java.util.Optional;

public interface ConnectorBalanceClient {
  String ACCEPT = "Accept:";
  String CONTENT_TYPE = "Content-Type:";
  String ID = "id";
  String APPLICATION_JSON = "application/json";

  /**
   * Static constructor to build a new instance of this Connector Balance client.
   *
   * @param connectorHttpUrl                     The {@link HttpUrl} of the Connector.
   *
   *
   * @return A {@link ConnectorBalanceClient}.
   */
  static ConnectorBalanceClient construct(final HttpUrl connectorHttpUrl) {
    Objects.requireNonNull(connectorHttpUrl);
    final ObjectMapper objectMapper = ObjectMapperFactory.createObjectMapperForProblemsJson();
    return Feign.builder()
      .encoder(new JacksonEncoder(objectMapper))
      .decode404()
      .decoder(new OptionalDecoder(new JacksonDecoder(objectMapper)))
      .target(ConnectorBalanceClient.class, connectorHttpUrl.toString());
  }

  @RequestLine("GET /accounts/{id}/balance")
  @Headers( {
    ACCEPT + APPLICATION_JSON,
    "Authorization: {authorizationHeader}"
  })
  Optional<AccountBalanceResponse> getBalance(@Param("authorizationHeader") String authorizationHeader,
                                                      @Param(ID) AccountId accountId);
}
