package io.xpring.ilp;


import static org.assertj.core.api.Assertions.assertThat;

import org.interledger.spsp.server.grpc.CreateAccountResponse;
import org.interledger.spsp.server.grpc.GetAccountResponse;
import org.interledger.spsp.server.grpc.GetBalanceResponse;
import org.interledger.spsp.server.grpc.SendPaymentResponse;

import io.xpring.ilp.util.IlpConstants;
import io.xpring.xrpl.XpringException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;

import java.util.Optional;

public class IlpIntegrationTests {

  /**
   * Connector fields
   */
  // This should allow hermes to talk to the connector without exposing any ports
  private static final Network network = Network.newNetwork();

  private static final int CONNECTOR_PORT = 8080;

  private static final int HERMES_PORT = 6565;

  /**
   *  Start up a connector from the nightly docker image
   */
  @ClassRule
  public static GenericContainer connectorNode = new GenericContainer<>("interledger4j/java-ilpv4-connector:0.2.0") // FIXME use nightly
    .withExposedPorts(CONNECTOR_PORT)
    .withNetwork(network)
    .withNetworkAliases("connector-host"); // Need this so hermes can communicate with the connector container

  @ClassRule
  public static GenericContainer hermesNode = new GenericContainer<>("interledger4j/hermes-server:xpring4j-hermes")
    .withExposedPorts(HERMES_PORT)
    .withNetwork(network)
    .withEnv("interledger.connector.connector-url", "http://connector-host:" + CONNECTOR_PORT);

  private String paymentPointerBase;
  private IlpClient client;

  @Before
  public void setUp() {
    client = new IlpClient(hermesNode.getContainerIpAddress() + ":" + hermesNode.getMappedPort(HERMES_PORT));
    paymentPointerBase = "$money.ilpv4.dev";
  }

  @AfterClass
  public static void tearDown() {
    connectorNode.stop();
    hermesNode.stop();
  }


  @Test
  public void minimalCreateAccount() throws XpringException {

    CreateAccountResponse response = client.createAccount();

    assertThat(response.getAccountId()).startsWith("user_");
    assertThat(response.getAssetCode()).isEqualTo("XRP");
    assertThat(response.getAssetScale()).isEqualTo(9);
    assertThat(response.getPaymentPointer()).isEqualTo(paymentPointerBase + "/" + response.getAccountId());
    assertThat(response.getCustomSettingsMap().get(IlpConstants.HTTP_INCOMING_AUTH_TYPE)).isEqualTo(IlpConstants.AuthType.SIMPLE.toString());
    assertThat(response.getCustomSettingsMap().get(IlpConstants.HTTP_INCOMING_SIMPLE_AUTH_TOKEN)).asString().isNotEmpty();
    assertThat(response.getCustomSettingsMap().get(IlpConstants.HTTP_INCOMING_SIMPLE_AUTH_TOKEN)).asString().doesNotStartWith("enc:jks");
  }

  @Test
  public void populatedCreateAccount() throws XpringException {
    CreateAccountRequest createAccountRequest = CreateAccountRequest.builder("USD", 6)
      .accountId("baz")
      .description("test account")
      .build();
    CreateAccountResponse response = client.createAccount(createAccountRequest, Optional.of("password"));
    assertThat(response.getAccountId()).isEqualTo("baz");
    assertThat(response.getAssetCode()).isEqualTo("USD");
    assertThat(response.getAssetScale()).isEqualTo(6);
    assertThat(response.getPaymentPointer()).isEqualTo(paymentPointerBase + "/baz");
    assertThat(response.getCustomSettingsMap().get(IlpConstants.HTTP_INCOMING_AUTH_TYPE)).isEqualTo(IlpConstants.AuthType.SIMPLE.toString());
    assertThat(response.getCustomSettingsMap().get(IlpConstants.HTTP_INCOMING_SIMPLE_AUTH_TOKEN)).asString().isNotEmpty();
    assertThat(response.getCustomSettingsMap().get(IlpConstants.HTTP_INCOMING_SIMPLE_AUTH_TOKEN)).isEqualTo("password");
  }

  @Test
  public void createAccountNoAuthYesRequest() throws XpringException {
    CreateAccountRequest createAccountRequest = CreateAccountRequest.builder("USD", 6)
      .accountId("bar")
      .description("test account")
      .build();
    CreateAccountResponse response = client.createAccount(createAccountRequest, Optional.empty());
    assertThat(response.getAccountId()).isEqualTo("bar");
    assertThat(response.getAssetCode()).isEqualTo("USD");
    assertThat(response.getAssetScale()).isEqualTo(6);
    assertThat(response.getPaymentPointer()).isEqualTo(paymentPointerBase + "/bar");
    assertThat(response.getCustomSettingsMap().get(IlpConstants.HTTP_INCOMING_AUTH_TYPE)).isEqualTo(IlpConstants.AuthType.SIMPLE.toString());
    assertThat(response.getCustomSettingsMap().get(IlpConstants.HTTP_INCOMING_SIMPLE_AUTH_TOKEN)).asString().isNotEmpty();
    assertThat(response.getCustomSettingsMap().get(IlpConstants.HTTP_INCOMING_SIMPLE_AUTH_TOKEN)).asString().doesNotStartWith("enc:jks");
  }

  @Test
  public void createAccountNoAuthNoAccountId() throws XpringException {
    CreateAccountRequest createAccountRequest = CreateAccountRequest.builder("USD", 6)
      .build();
    CreateAccountResponse response = client.createAccount(createAccountRequest, Optional.empty());
    assertThat(response.getAccountId()).startsWith("user_");
    assertThat(response.getAssetCode()).isEqualTo("USD");
    assertThat(response.getAssetScale()).isEqualTo(6);
    assertThat(response.getPaymentPointer()).isEqualTo(paymentPointerBase + "/" + response.getAccountId());
    assertThat(response.getCustomSettingsMap().get(IlpConstants.HTTP_INCOMING_AUTH_TYPE)).isEqualTo(IlpConstants.AuthType.SIMPLE.toString());
    assertThat(response.getCustomSettingsMap().get(IlpConstants.HTTP_INCOMING_SIMPLE_AUTH_TOKEN)).asString().isNotEmpty();
    assertThat(response.getCustomSettingsMap().get(IlpConstants.HTTP_INCOMING_SIMPLE_AUTH_TOKEN)).asString().doesNotStartWith("enc:jks");
  }

  @Test
  public void getAccount() throws XpringException {
    CreateAccountResponse createAccountResponse = client.createAccount();
    GetAccountResponse response = client.getAccount(createAccountResponse.getAccountId(),
      createAccountResponse.getCustomSettingsMap().get(IlpConstants.HTTP_INCOMING_SIMPLE_AUTH_TOKEN));

    assertThat(response.getAccountId()).isEqualTo(createAccountResponse.getAccountId());
    assertThat(response.getAssetCode()).isEqualTo(createAccountResponse.getAssetCode());
    assertThat(response.getAssetScale()).isEqualTo(createAccountResponse.getAssetScale());
    assertThat(response.getPaymentPointer()).isEqualTo(createAccountResponse.getPaymentPointer());
    assertThat(response.getCustomSettingsMap().get(IlpConstants.HTTP_INCOMING_AUTH_TYPE)).isEqualTo(IlpConstants.AuthType.SIMPLE.toString());
    assertThat(response.getCustomSettingsMap().get(IlpConstants.HTTP_INCOMING_SIMPLE_AUTH_TOKEN)).startsWith("enc:jks");
  }

  @Test
  public void getBalance() throws XpringException {
    CreateAccountResponse createAccountResponse = client.createAccount();
    GetBalanceResponse response = client.getBalance(createAccountResponse.getAccountId(),
      createAccountResponse.getCustomSettingsMap().get(IlpConstants.HTTP_INCOMING_SIMPLE_AUTH_TOKEN));

    assertThat(response.getAccountId()).isEqualTo(createAccountResponse.getAccountId());
    assertThat(response.getAssetCode()).isEqualTo(createAccountResponse.getAssetCode());
    assertThat(response.getAssetScale()).isEqualTo(createAccountResponse.getAssetScale());
    assertThat(response.getNetBalance()).isEqualTo(0);
    assertThat(response.getClearingBalance()).isEqualTo(0);
    assertThat(response.getPrepaidAmount()).isEqualTo(0);
  }

  @Test
  public void sendPayment() throws XpringException {
    CreateAccountResponse sender = client.createAccount();
    CreateAccountResponse receiver = client.createAccount();

    SendPaymentResponse response = client.sendPayment(receiver.getPaymentPointer(),
      10,
      sender.getAccountId(),
      sender.getCustomSettingsMap().get(IlpConstants.HTTP_INCOMING_SIMPLE_AUTH_TOKEN));

    SendPaymentResponse expected = SendPaymentResponse.newBuilder()
      .setOriginalAmount(10)
      .setAmountSent(10)
      .setAmountDelivered(10)
      .setSuccessfulPayment(true)
      .build();

    assertThat(response).isEqualToComparingFieldByField(expected);
  }
}
