package org.interledger.spsp.server.config.ilp;

import static okhttp3.CookieJar.NO_COOKIES;

import org.interledger.connector.accounts.AccountId;
import org.interledger.connector.client.ConnectorAdminClient;
import org.interledger.core.InterledgerAddressPrefix;
import org.interledger.link.http.IlpOverHttpLinkSettings;
import org.interledger.link.http.OutgoingLinkSettings;
import org.interledger.link.http.SimpleAuthSettings;
import org.interledger.spsp.PaymentPointerResolver;
import org.interledger.spsp.client.SimpleSpspClient;
import org.interledger.spsp.client.SpspClient;
import org.interledger.spsp.server.client.ConnectorBalanceClient;
import org.interledger.spsp.server.client.ConnectorRoutesClient;
import org.interledger.spsp.server.services.GimmeMoneyService;
import org.interledger.spsp.server.services.NewAccountService;
import org.interledger.spsp.server.services.SendMoneyService;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.ConnectionPool;
import okhttp3.ConnectionSpec;
import okhttp3.Dispatcher;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * <p>Configures ILP-over-HTTP, which provides a single Link-layer mechanism for this Connector's peers.</p>
 *
 * <p>This type of Connector supports a single HTTP/2 Server endpoint in addition to multiple, statically configured,
 * ILP-over-HTTP client links.</p>
 */
@Configuration
public class IlpOverHttpConfig {

  public static final String ILP_OVER_HTTP = "ILP-over-HTTP";

  public static final String SPSP = "SPSP";

  @Bean
  @Qualifier(ILP_OVER_HTTP)
  protected ConnectionPool ilpOverHttpConnectionPool(
    @Value("${interledger.connector.ilpOverHttp.connectionDefaults.maxIdleConnections:10}") final int defaultMaxIdleConnections,
    @Value("${interledger.connector.ilpOverHttp.connectionDefaults.keepAliveSeconds:30}") final long defaultKeepAliveSeconds
  ) {
    return new ConnectionPool(
      defaultMaxIdleConnections,
      defaultKeepAliveSeconds, TimeUnit.SECONDS
    );
  }

  /**
   * A Bean for {@link OkHttp3ClientHttpRequestFactory}.
   *
   * @param ilpOverHttpConnectionPool   A {@link ConnectionPool} as configured above.
   * @param defaultConnectTimeoutMillis Applied when connecting a TCP socket to the target host. A value of 0 means no
   *                                    timeout, otherwise values must be between 1 and {@link Integer#MAX_VALUE} when
   *                                    converted to milliseconds. If unspecified, defaults to 10000.
   * @param defaultReadTimeoutMillis    Applied to both the TCP socket and for individual read IO operations. A value of
   *                                    0 means no timeout, otherwise values must be between 1 and {@link
   *                                    Integer#MAX_VALUE} when converted to milliseconds. If unspecified, defaults to
   *                                    60000.
   * @param defaultWriteTimeoutMillis   Applied to individual write IO operations. A value of 0 means no timeout,
   *                                    otherwise values must be between 1 and {@link Integer#MAX_VALUE} when converted
   *                                    to milliseconds. If unspecified, defaults to 60000.
   * @param maxRequests                 Maximum numbers of concurrent http requests (across all hosts).
   * @param maxRequestsPerHost          Maximum numbers of concurrent http requests per host.
   *
   * @return A {@link OkHttp3ClientHttpRequestFactory}.
   */
  @Bean
  @Qualifier(ILP_OVER_HTTP)
  protected OkHttpClient ilpOverHttpClient(
    @Qualifier(ILP_OVER_HTTP) final ConnectionPool ilpOverHttpConnectionPool,
    @Value("${interledger.connector.ilpOverHttp.connectionDefaults.connectTimeoutMillis:1000}") final long defaultConnectTimeoutMillis,
    @Value("${interledger.connector.ilpOverHttp.connectionDefaults.readTimeoutMillis:60000}") final long defaultReadTimeoutMillis,
    @Value("${interledger.connector.ilpOverHttp.connectionDefaults.writeTimeoutMillis:60000}") final long defaultWriteTimeoutMillis,
    @Value("${interledger.connector.ilpOverHttp.connectionDefaults.maxRequests:100}") final int maxRequests,
    @Value("${interledger.connector.ilpOverHttp.connectionDefaults.maxRequestsPerHost:50}") final int maxRequestsPerHost
  ) {
    OkHttpClient.Builder builder = new OkHttpClient.Builder();
    ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS).build();
    Dispatcher dispatcher = new Dispatcher();
    dispatcher.setMaxRequests(maxRequests);
    dispatcher.setMaxRequestsPerHost(maxRequestsPerHost);
    builder.dispatcher(dispatcher);
    builder.connectionSpecs(Arrays.asList(spec, ConnectionSpec.CLEARTEXT));
    builder.cookieJar(NO_COOKIES);

    builder.connectTimeout(defaultConnectTimeoutMillis, TimeUnit.MILLISECONDS);
    builder.readTimeout(defaultReadTimeoutMillis, TimeUnit.MILLISECONDS);
    builder.writeTimeout(defaultWriteTimeoutMillis, TimeUnit.MILLISECONDS);

    return builder.connectionPool(ilpOverHttpConnectionPool).build();
  }

  /**
   * A Bean for {@link OkHttp3ClientHttpRequestFactory}.
   *
   * @param okHttpClient A {@link OkHttpClient} to use in the Request factory.
   *
   * @return A {@link OkHttp3ClientHttpRequestFactory}.
   */
  @Bean
  @Qualifier(ILP_OVER_HTTP)
  protected OkHttp3ClientHttpRequestFactory ilpOverHttpClientHttpRequestFactory(
    @Qualifier(ILP_OVER_HTTP) final OkHttpClient okHttpClient
  ) {
    return new OkHttp3ClientHttpRequestFactory(okHttpClient);
  }

  @Bean
  @Qualifier(SPSP)
  protected HttpUrl spspReceiverUrl(@Value("${interledger.spsp.spsp-url}") String spspUrl) {
    return HttpUrl.parse(spspUrl);
  }

  @Bean
  @Qualifier(SPSP)
  OutgoingLinkSettings spspSettings(@Qualifier(SPSP) HttpUrl receiverUrl,
                                    @Value("${interledger.spsp.auth-token}") String spspAuthToken) {
    return OutgoingLinkSettings.builder()
      .authType(IlpOverHttpLinkSettings.AuthType.SIMPLE)
      .url(receiverUrl.newBuilder().addPathSegment("ilp").build())
      .simpleAuthSettings(SimpleAuthSettings.forAuthToken(spspAuthToken))
      .build();
  }

  @Bean
  @Qualifier(SPSP)
  InterledgerAddressPrefix spspAddressPrefix(@Value("${interledger.spsp.address-prefix}") String spspAddressPrefix) {
    return InterledgerAddressPrefix.of(spspAddressPrefix);
  }

  @Bean
  SpspClient spspClient(OkHttpClient okHttpClient, PaymentPointerResolver paymentPointerResolver, ObjectMapper objectMapper) {
    return new SimpleSpspClient(okHttpClient, paymentPointerResolver, objectMapper);
  }

  @Bean
  PaymentPointerResolver paymentPointerResolver() {
    return (paymentPointer) -> {
      if (paymentPointer.host().contains("cluster.local")) {
        return HttpUrl.parse("http://" + paymentPointer.host() + paymentPointer.path());
      }
      else {
        return HttpUrl.parse("https://" + paymentPointer.host() + paymentPointer.path());
      }
    };
  }

  @Bean
  public NewAccountService newAccountService(
    ConnectorAdminClient adminClient,
    ConnectorRoutesClient connectorRoutesClient,
    @Qualifier(SPSP) OutgoingLinkSettings spspLinkSettings,
    @Qualifier(SPSP) InterledgerAddressPrefix spspAddressPrefix
  ) {
    return new NewAccountService(adminClient, connectorRoutesClient, spspLinkSettings, spspAddressPrefix);
  }

  @Bean
  public SendMoneyService sendMoneyService(@Value("${interledger.connector.connector-url}") String connectorUrl,
                                           ObjectMapper objectMapper,
                                           ConnectorAdminClient adminClient,
                                           OkHttpClient okHttpClient,
                                           SpspClient spspClient) {
    return new SendMoneyService(HttpUrl.parse(connectorUrl), objectMapper, adminClient, okHttpClient, spspClient);
  }

  @Bean
  public GimmeMoneyService gimmeMoneyService(SendMoneyService sendMoneyService,
                                             @Qualifier(SPSP) HttpUrl spspUrl) {
    return new GimmeMoneyService(sendMoneyService, AccountId.of("rainmaker"), "password", spspUrl);
  }

  @Bean
  public ConnectorBalanceClient balanceClient(@Value("${interledger.connector.connector-url}") String connectorHttpUrl) {
    return ConnectorBalanceClient.construct(HttpUrl.parse(connectorHttpUrl));
  }

  @Bean
  public ConnectorAdminClient adminClient(@Value("${interledger.connector.connector-url}") String connectorHttpUrl/*,
                                          @Value("${interledger.connector.admin-key}") String adminKey*/) {
    return ConnectorAdminClient.construct(HttpUrl.parse(connectorHttpUrl), template -> {
      template.header("Authorization", "Basic YWRtaW46cGFzc3dvcmQ=");
    });
  }

  @Bean
  public ConnectorRoutesClient routesClient(@Value("${interledger.connector.connector-url}") String connectorHttpUrl) {
    return ConnectorRoutesClient.construct(HttpUrl.parse(connectorHttpUrl), template -> {
      template.header("Authorization", "Basic YWRtaW46cGFzc3dvcmQ=");
    });
  }

}
