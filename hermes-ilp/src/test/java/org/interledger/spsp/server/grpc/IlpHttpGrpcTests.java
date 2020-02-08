package org.interledger.spsp.server.grpc;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.google.common.net.HttpHeaders.AUTHORIZATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.interledger.spsp.server.config.ilp.IlpOverHttpConfig.SPSP;
import static org.junit.Assert.fail;

import org.interledger.connector.accounts.AccountId;
import org.interledger.connector.accounts.AccountRelationship;
import org.interledger.connector.accounts.AccountSettings;
import org.interledger.connector.client.ConnectorAdminClient;
import org.interledger.connector.jackson.ObjectMapperFactory;
import org.interledger.link.http.IlpOverHttpLink;
import org.interledger.link.http.IlpOverHttpLinkSettings;
import org.interledger.link.http.IncomingLinkSettings;
import org.interledger.link.http.JwtAuthSettings;
import org.interledger.link.http.OutgoingLinkSettings;
import org.interledger.spsp.client.SpspClient;
import org.interledger.spsp.server.HermesServerApplication;
import org.interledger.spsp.server.client.AccountBalanceResponse;
import org.interledger.spsp.server.client.ConnectorBalanceClient;
import org.interledger.spsp.server.client.ConnectorRoutesClient;
import org.interledger.spsp.server.grpc.jwt.IlpJwtCallCredentials;
import org.interledger.spsp.server.grpc.utils.InterceptedService;
import org.interledger.spsp.server.services.NewAccountService;
import org.interledger.spsp.server.services.SendMoneyService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import feign.FeignException;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.Testcontainers;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;

import java.io.IOException;
import java.math.BigInteger;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest(
    classes = {HermesServerApplication.class, IlpHttpGrpcTests.TestConfig.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {"spring.main.allow-bean-definition-overriding=true"})
public class IlpHttpGrpcTests {

  private static final HttpUrl SPSP_SERVER_URL = HttpUrl.parse("https://money.ilpv4.dev");
  private Logger logger = LoggerFactory.getLogger(this.getClass());

  @Rule
  public ExpectedException expectedException = ExpectedException.none();
  /**
   * Fields for our JWKS mock server
   */
  public static final String WELL_KNOWN_JWKS_JSON = "/.well-known/jwks.json";
  public static final int WIRE_MOCK_PORT = 32987;
  HttpUrl issuer;

  /**
   * Connector/SPSP server container fields
   */
  private static final Network network = Network.newNetwork();
  private static final int CONNECTOR_PORT = 8080;
  private static final Integer SPSP_SERVER_PORT = 8080;

  /**
   * Admin token for creating accounds
   */
  public static final String ADMIN_AUTH_TOKEN = "YWRtaW46cGFzc3dvcmQ=";

  /**
   * This rule manages automatic graceful shutdown for the registered servers and channels at the
   * end of test.
   */
  @Rule
  public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

  private IlpOverHttpServiceGrpc.IlpOverHttpServiceBlockingStub blockingStub;

  @Autowired
  IlpOverHttpGrpcHandler ilpOverHttpGrpcHandler;

  /**
   * This starts up a mock JWKS server
   */
  @Rule
  public WireMockRule wireMockRule = new WireMockRule(WIRE_MOCK_PORT);

  // Need this to have the JWKS port exposed to the connector running in the container

  static {
    Testcontainers.exposeHostPorts(WIRE_MOCK_PORT);
  }
  /**
   *  Start up a connector from the nightly docker image
   */
  @ClassRule
  public static GenericContainer connector = new GenericContainer<>("interledger4j/java-ilpv4-connector:nightly")
    .withExposedPorts(CONNECTOR_PORT)
    .withNetwork(network);

  @Autowired
  private NewAccountService newAccountService;

  @Autowired
  private ConnectorBalanceClient balanceClient;

  private JwksServer jwtServer;
  private ObjectMapper objectMapper = ObjectMapperFactory.create();

  @Autowired
  private OutgoingLinkSettings outgoingLinkSettings;

  @Before
  public void startUp() throws IOException {
    // Set up the JWKS server
    jwtServer = new JwksServer();
    resetJwks();
    issuer = HttpUrl.parse("http://host.testcontainers.internal:" + wireMockRule.port());

    createTestAccount("alice");
    createTestAccount("bob");

    registerGrpc();
  }

  public void registerGrpc() throws IOException {
    // Generate a unique in-process server name.
    String serverName = InProcessServerBuilder.generateName();

    // Create a server, add service, start, and register for automatic graceful shutdown.
    grpcCleanup.register(
      InProcessServerBuilder
        .forName(serverName)
        .directExecutor()
        .addService(InterceptedService.of(ilpOverHttpGrpcHandler))
        .build()
        .start()
    );

    blockingStub = IlpOverHttpServiceGrpc.newBlockingStub(
        // Create a client channel and register for automatic graceful shutdown.
        grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build()));
  }

  private void createTestAccount(String jwtSubject) {
    JwtAuthSettings jwtAuthSettings = jwtAuthSettings(jwtSubject);
    createTestAccount(jwtAuthSettings);
  }

  private void createTestAccount(JwtAuthSettings jwtAuthSettings) {
    // Set up auth settings to use JWT_RS_256
    Map<String, Object> customSettings = new HashMap<>();
    customSettings.put(IncomingLinkSettings.HTTP_INCOMING_AUTH_TYPE, IlpOverHttpLinkSettings.AuthType.JWT_RS_256);
    customSettings.put(IncomingLinkSettings.HTTP_INCOMING_TOKEN_ISSUER, jwtAuthSettings.tokenIssuer().get().toString());
    customSettings.put(IncomingLinkSettings.HTTP_INCOMING_TOKEN_AUDIENCE, jwtAuthSettings.tokenAudience().get());
    customSettings.put(IncomingLinkSettings.HTTP_INCOMING_TOKEN_SUBJECT, jwtAuthSettings.tokenSubject());
    customSettings.putAll(outgoingLinkSettings.toCustomSettingsMap()
      .entrySet()
      .stream()
      .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().toString()))
    );

    try {
      this.newAccountService.createAccount(
        AccountSettings.builder()
          .accountId(AccountId.of(jwtAuthSettings.tokenSubject()))
          .assetCode("XRP")
          .assetScale(9)
          .linkType(IlpOverHttpLink.LINK_TYPE)
          .accountRelationship(AccountRelationship.CHILD)
          .customSettings(customSettings)
          .build()
      );
    } catch (FeignException e) {
      if (e.status() != 409) {
        throw e;
      } else {
        logger.warn("Hermes account already exists. If you want to update the account, delete it and try again with new settings.");
      }
    }
  }

  private JwtAuthSettings jwtAuthSettings(String subject) {
    return JwtAuthSettings.builder()
      .tokenIssuer(issuer)
      .tokenSubject(subject)
      .tokenAudience("bar")
      .build();
  }

  @Test
  public void sendMoneyTest() throws JsonProcessingException {
    JwtAuthSettings aliceJwtAuthSettings = jwtAuthSettings("alice");
    int sendAmount = 10000;
    String aliceJwt = jwtServer.createJwt(aliceJwtAuthSettings, Instant.now().plusSeconds(sendAmount));

    SendPaymentRequest sendMoneyRequest = SendPaymentRequest.newBuilder()
      .setAccountId("alice")
      .setAmount(sendAmount)
      .setDestinationPaymentPointer(paymentPointerFromBaseUrl() + "/bob")
      .setJwt(aliceJwt)
      .build();

    SendPaymentResponse response = blockingStub
      .withCallCredentials(IlpJwtCallCredentials.build(aliceJwt))
      .sendMoney(sendMoneyRequest);
    if (!response.getSuccessfulPayment()) {
      fail();
    }
    logger.info("Payment sent successfully!  Payment Response: ");
    logger.info(response.toString());

    SendPaymentResponse expected = SendPaymentResponse.newBuilder()
      .setAmountSent(sendAmount)
      .setOriginalAmount(sendAmount)
      .setAmountDelivered(sendAmount)
      .setAmountSent(sendAmount)
      .setSuccessfulPayment(true)
      .build();

    assertThat(response).isEqualToComparingFieldByField(expected);

    JwtAuthSettings bobJwtAuthSettings = jwtAuthSettings("bob");
    String bobJwt = jwtServer.createJwt(bobJwtAuthSettings, Instant.now().plusSeconds(sendAmount));
    Optional<AccountBalanceResponse> aliceBalance = balanceClient.getBalance("Bearer " + aliceJwt, AccountId.of("alice"));
    Optional<AccountBalanceResponse> bobBalance = balanceClient.getBalance("Bearer " + bobJwt, AccountId.of("bob"));

    logger.info("Alice's balance after sending payment: ");
    logger.info(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(aliceBalance.get()));

    logger.info("Bob's balance after receiving payment: ");
    logger.info(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(bobBalance.get()));

    assertThat(aliceBalance.get().accountBalance().netBalance())
      .isEqualTo(bobBalance.get().accountBalance().netBalance().negate());
    assertThat(aliceBalance.get().accountBalance().netBalance()).isEqualTo(BigInteger.valueOf(-sendAmount));
  }

  private String paymentPointerFromBaseUrl() {
    HttpUrl spspServerUrl = SPSP_SERVER_URL;
    return "$" + spspServerUrl.host() + ":" + spspServerUrl.port();
  }


  /**
   * Helper method to return the base URL for the Rust Connector.
   *
   * @return An {@link HttpUrl} to communicate with.
   */
  private static HttpUrl getContainerBaseUri(GenericContainer container) {
    return new HttpUrl.Builder()
      .scheme("http")
      .host(container.getContainerIpAddress())
      .port(container.getFirstMappedPort())
      .build();
  }

  private void resetJwks() throws JsonProcessingException {
    jwtServer.resetKeyPairs();
    WireMock.reset();
    stubFor(get(urlEqualTo(WELL_KNOWN_JWKS_JSON))
      .willReturn(aResponse()
        .withStatus(200)
        .withBody(objectMapper.writeValueAsString(jwtServer.getJwks()))
      ));
  }

  public static class TestConfig {

    /**
     * Overrides the adminClient bean for test purposes to connect to our Connector container
     *
     * @return a ConnectorAdminClient that can speak to the test container connector
     */
    @Bean
    @Primary
    public ConnectorAdminClient adminClient() {
      return ConnectorAdminClient.construct(getContainerBaseUri(connector), template -> {
        template.header(AUTHORIZATION, "Basic " + ADMIN_AUTH_TOKEN);
      });
    }

    @Bean
    @Primary
    public ConnectorBalanceClient balanceClient() {
      return ConnectorBalanceClient.construct(getContainerBaseUri(connector));
    }

    @Bean
    @Primary
    public ConnectorRoutesClient routesClient() {
      return ConnectorRoutesClient.construct(getContainerBaseUri(connector), template -> {
        template.header("Authorization", "Basic YWRtaW46cGFzc3dvcmQ=");
      });
    }

    @Bean
    @Primary
    public SendMoneyService sendMoneyService(ObjectMapper objectMapper,
                                             ConnectorAdminClient adminClient,
                                             OkHttpClient okHttpClient,
                                             SpspClient spspClient) {
      return new SendMoneyService(getContainerBaseUri(connector), objectMapper, adminClient, okHttpClient, spspClient);
    }

    @Bean
    @Qualifier(SPSP)
    @Primary
    protected HttpUrl spspReceiverUrl() {
//      return HttpUrl.parse(getContainerBaseUri(spspServer).toString());
      return SPSP_SERVER_URL;
    }
  }
}


