package org.interledger.spsp.server.grpc;


import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

import org.interledger.connector.accounts.AccountId;
import org.interledger.connector.accounts.AccountRelationship;
import org.interledger.connector.accounts.AccountSettings;
import org.interledger.connector.client.ConnectorAdminClient;
import org.interledger.connector.jackson.ObjectMapperFactory;
import org.interledger.link.http.IlpOverHttpLink;
import org.interledger.link.http.IlpOverHttpLinkSettings;
import org.interledger.link.http.ImmutableJwtAuthSettings;
import org.interledger.link.http.IncomingLinkSettings;
import org.interledger.link.http.JwtAuthSettings;
import org.interledger.spsp.server.HermesServerApplication;
import org.interledger.spsp.server.client.ConnectorBalanceClient;
import org.interledger.spsp.server.grpc.jwt.IlpJwtCallCredentials;
import org.interledger.spsp.server.grpc.utils.InterceptedService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import feign.FeignException;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import okhttp3.HttpUrl;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.Testcontainers;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;


@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest(
  classes = {HermesServerApplication.class, BalanceGrpcHandlerTests.TestConfig.class},
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
  properties = {"spring.main.allow-bean-definition-overriding=true"})
public class BalanceGrpcHandlerTests {

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
   * Connector fields
   */
  private static final Network network = Network.newNetwork();
  private static final int CONNECTOR_PORT = 8080;

  /**
   * Admin token for creating the account for accountIdHermes
   */
  private AccountId accountIdHermes;
  public static final String ADMIN_AUTH_TOKEN = "YWRtaW46cGFzc3dvcmQ=";

  /**
   * gRpc stubs to test Hermes
   */
  BalanceServiceGrpc.BalanceServiceBlockingStub blockingStub;

  /**
   * This rule manages automatic graceful shutdown for the registered servers and channels at the
   * end of test.
   */
  @Rule
  public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

  @Autowired
  BalanceGrpcHandler balanceGrpcHandler;

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
  public static GenericContainer interledgerNode = new GenericContainer<>("interledger4j/java-ilpv4-connector:nightly")
    .withExposedPorts(CONNECTOR_PORT)
    .withNetwork(network);


  private ConnectorAdminClient adminClient;
  private JwksServer jwtServer;
  private ObjectMapper objectMapper = ObjectMapperFactory.create();

  @Before
  public void setUp() throws IOException {
    // Set up the JWKS server
    jwtServer = new JwksServer();
    resetJwks();
    issuer = HttpUrl.parse("http://host.testcontainers.internal:" + wireMockRule.port());

    // Create an admin client to create a test account
    accountIdHermes = AccountId.of("hermes");
    this.adminClient = ConnectorAdminClient
      .construct(getInterledgerBaseUri(), template -> {
        template.header("Authorization", "Basic " + ADMIN_AUTH_TOKEN);
      });

    JwtAuthSettings jwtAuthSettings = defaultAuthSettings(issuer);

    // Set up auth settings to use JWT_RS_256
    Map<String, Object> customSettings = new HashMap<>();
    customSettings.put(IncomingLinkSettings.HTTP_INCOMING_AUTH_TYPE, IlpOverHttpLinkSettings.AuthType.JWT_RS_256);
    customSettings.put(IncomingLinkSettings.HTTP_INCOMING_TOKEN_ISSUER, jwtAuthSettings.tokenIssuer().get().toString());
    customSettings.put(IncomingLinkSettings.HTTP_INCOMING_TOKEN_AUDIENCE, jwtAuthSettings.tokenAudience().get());
    customSettings.put(IncomingLinkSettings.HTTP_INCOMING_TOKEN_SUBJECT, jwtAuthSettings.tokenSubject());

    try {
      this.adminClient.createAccount(
        AccountSettings.builder()
          .accountId(accountIdHermes)
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

    registerGrpc();
  }

  private void registerGrpc() throws IOException {
    // Generate a unique in-process server name.
    String serverName = InProcessServerBuilder.generateName();

    // Create a server, add service, start, and register for automatic graceful shutdown.
    grpcCleanup.register(
      InProcessServerBuilder
        .forName(serverName)
        .directExecutor()
        .addService(InterceptedService.of(balanceGrpcHandler))
        .build()
        .start()
    );

    blockingStub = BalanceServiceGrpc.newBlockingStub(
      // Create a client channel and register for automatic graceful shutdown.
      grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build()));
  }

  /**
   * Helper method to return the base URL for the Rust Connector.
   *
   * @return An {@link HttpUrl} to communicate with.
   */
  private static HttpUrl getInterledgerBaseUri() {
    return new HttpUrl.Builder()
      .scheme("http")
      .host(interledgerNode.getContainerIpAddress())
      .port(interledgerNode.getFirstMappedPort())
      .build();
  }

  /**
   * Get the balance for the account we created in the setUp method.
   *
   * Balances should all be 0, as the hermes account has not sent or received any money.
   */
  @Test
  public void getBalanceTest() {

    JwtAuthSettings jwtAuthSettings = defaultAuthSettings(issuer);
    String jwt = jwtServer.createJwt(jwtAuthSettings, Instant.now().plusSeconds(10));

    GetBalanceResponse reply =
      blockingStub
        .withCallCredentials(IlpJwtCallCredentials.build(jwt))
        .getBalance(
          GetBalanceRequest.newBuilder()
          .setAccountId(accountIdHermes.value())
          .setJwt(jwt)
          .build()
        );

    logger.info("Balance: " + reply);
    assertThat(reply.getAccountId()).isEqualTo("hermes");
    assertThat(reply.getAssetCode()).isEqualTo("XRP");
    assertThat(reply.getAssetScale()).isEqualTo(9);
    assertThat(reply.getClearingBalance()).isEqualTo(0L);
    assertThat(reply.getPrepaidAmount()).isEqualTo(0L);
    assertThat(reply.getNetBalance()).isZero();
  }

  /**
   * Get the balance for the account we created in the setUp method without passing in a jwt.
   *
   * Should pass if grpc returns a {@link io.grpc.StatusRuntimeException} with Status.PERMISSION_DENIED
   */
  @Test
  public void getBalanceTestFailsNoJwt() {

    expectedException.expect(StatusRuntimeException.class);
    expectedException.expectMessage(Status.PERMISSION_DENIED.getCode().name());

    blockingStub
      .withCallCredentials(IlpJwtCallCredentials.build("thisIsNotAValidJwt"))
      .getBalance(
        GetBalanceRequest.newBuilder()
          .setAccountId(accountIdHermes.value())
          .setJwt("thisIsNotAValidJwt")
          .build()
      );
  }

  /**
   * Get the balance for the account we created in the setUp method without passing in a jwt.
   *
   * Should pass if grpc returns a {@link io.grpc.StatusRuntimeException} with Status.PERMISSION_DENIED
   */
  @Test
  public void getBalanceTestFailsAccountNotFound() {

    expectedException.expect(StatusRuntimeException.class);
    expectedException.expectMessage(Status.NOT_FOUND.getCode().name());

    JwtAuthSettings jwtAuthSettings = defaultAuthSettings(issuer);
    String jwt = jwtServer.createJwt(jwtAuthSettings, Instant.now().plusSeconds(10));

    blockingStub
      .withCallCredentials(IlpJwtCallCredentials.build(jwt))
      .getBalance(
        GetBalanceRequest.newBuilder()
          .setAccountId("thisAccountDoesntExist")
          .setJwt(jwt)
          .build()
      );
  }

  private ImmutableJwtAuthSettings defaultAuthSettings(HttpUrl issuer) {
    return JwtAuthSettings.builder()
      .tokenIssuer(issuer)
      .tokenSubject("foo")
      .tokenAudience("bar")
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
     * Overrides the balanceClient bean for test purposes to connect to our Connector container
     *
     * @return a ConnectorBalanceClient that can speak to the test container connector
     */
    @Bean
    @Primary
    public ConnectorBalanceClient balanceClient() {
      return ConnectorBalanceClient.construct(getInterledgerBaseUri());
    }
  }
}


