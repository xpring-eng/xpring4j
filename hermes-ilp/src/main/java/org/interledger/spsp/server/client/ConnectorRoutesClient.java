package org.interledger.spsp.server.client;

import org.interledger.connector.routing.StaticRoute;
import org.interledger.spsp.server.config.jackson.ObjectMapperFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Feign;
import feign.Headers;
import feign.Param;
import feign.RequestInterceptor;
import feign.RequestLine;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.optionals.OptionalDecoder;
import okhttp3.HttpUrl;
import org.zalando.problem.ThrowableProblem;

import java.util.Objects;

/**
 * A feign HTTP client for managing connector's routes. This should be merged into the ConnectorAdminClient but that
 * lives in java-connector project and I was too lazy to put it there initially.
 */
public interface ConnectorRoutesClient {

  String ACCEPT = "Accept:";
  String CONTENT_TYPE = "Content-Type:";

  String PREFIX = "prefix";
  String APPLICATION_JSON = "application/json";

  /**
   * Static constructor to build a new instance of this Connector Admin client.
   *
   * @param httpUrl                     The {@link HttpUrl} of the Connector.
   * @param basicAuthRequestInterceptor A {@link RequestInterceptor} that injects the HTTP Basic auth credentials into
   *                                    each request.
   *
   * @return A {@link ConnectorRoutesClient}.
   */
  static ConnectorRoutesClient construct(
    final HttpUrl httpUrl, final RequestInterceptor basicAuthRequestInterceptor
  ) {
    Objects.requireNonNull(httpUrl);
    Objects.requireNonNull(basicAuthRequestInterceptor);

    final ObjectMapper objectMapper = ObjectMapperFactory.createObjectMapperForProblemsJson();
    return Feign.builder()
      .encoder(new JacksonEncoder(objectMapper))
      .decode404()
      .decoder(new OptionalDecoder(new JacksonDecoder(objectMapper)))
      .requestInterceptor(basicAuthRequestInterceptor)
      .target(ConnectorRoutesClient.class, httpUrl.toString());
  }

  @RequestLine("PUT /routes/static/{prefix}")
  @Headers( {
    ACCEPT + APPLICATION_JSON,
    CONTENT_TYPE + APPLICATION_JSON
  })
  StaticRoute createStaticRoute(@Param(PREFIX) String prefix, StaticRoute route) throws ThrowableProblem;
}
