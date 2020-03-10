package io.xpring.ilp;


import static org.assertj.core.api.Assertions.assertThat;

import org.interledger.spsp.server.grpc.CreateAccountResponse;
import org.interledger.spsp.server.grpc.GetAccountResponse;
import org.interledger.spsp.server.grpc.GetBalanceResponse;

import com.google.common.primitives.UnsignedLong;
import io.xpring.ilp.model.CreateAccountRequest;
import io.xpring.ilp.model.PaymentRequest;
import io.xpring.ilp.model.PaymentResponse;
import io.xpring.ilp.util.IlpAuthConstants;
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
   * Hermes and Connector TestContainers fields.
   *
   * These tests will run instances of Hermes and the Java Connector inside of Docker containers.
   * TestContainers maps a random machine port to the port exposed within each Docker container,
   * and in order for two applications running within containers to speak to each other, they both
   * need to be aware of which port was mapped by TestContainers, and they both need to expose those ports to
   * the other container.
   *
   * By placing both TestContainers within the same {@link Network}, the containers do not need to speak
   * to each other on the mapped ports, but instead can use the internal Docker ports.
   */
  private static final Network network = Network.newNetwork();

  private static final int CONNECTOR_PORT = 8080;

  private static final int HERMES_PORT = 6565;

  /**
   *  Start up a connector from the java-ilpv4-connector:0.2.0 docker image.
   *  Make sure to update this image version when upgrading connector dependencies in the SDK
   */
  @ClassRule
  public static GenericContainer connectorNode = new GenericContainer<>("interledger4j/java-ilpv4-connector:0.2.0")
    .withExposedPorts(CONNECTOR_PORT)
    .withNetwork(network)
    .withNetworkAliases("connector-host"); // This will give Hermes a consistent host on the connector container to talk to

  /**
   * Start up Hermes from the hermes-server:xpring4j-hermes docker image
   * Make sure to update this image version when upgrading the Hermes submodule in the SDK
   */
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
    // GIVEN an instance of Hermes which is connected to an instance of a Java Connector

    // WHEN an account is created with no parameters
    CreateAccountResponse response = client.createAccount();

    // THEN the account id has been generated with format user_{random string}
    assertThat(response.getAccountId()).startsWith("user_");
    // AND asset code is XRP
    assertThat(response.getAssetCode()).isEqualTo("XRP");
    // AND asset scale is 9
    assertThat(response.getAssetScale()).isEqualTo(9);
    // AND paymentPointer is equal to the mocked SPSP server + /{accountId}
    assertThat(response.getPaymentPointer()).isEqualTo(paymentPointerBase + "/" + response.getAccountId());
    // AND SIMPLE auth has been chosen
    assertThat(response.getCustomSettingsMap().get(IlpAuthConstants.HTTP_INCOMING_AUTH_TYPE)).isEqualTo(IlpAuthConstants.AuthType.SIMPLE.toString());
    // AND the SIMPLE auth token has not been encrypted in the response
    assertThat(response.getCustomSettingsMap().get(IlpAuthConstants.HTTP_INCOMING_SIMPLE_AUTH_TOKEN)).asString().doesNotStartWith("enc:jks");
  }

  @Test
  public void populatedCreateAccount() throws XpringException {
    // GIVEN an instance of Hermes which is connected to an instance of a Java Connector

    // WHEN an account is created with ALL parameters set
    CreateAccountRequest createAccountRequest = CreateAccountRequest.builder("USD", 6)
      .accountId("baz")
      .description("test account")
      .build();
    CreateAccountResponse response = client.createAccount(createAccountRequest, Optional.of("password"));

    // THEN the accountId in the response is unchanged from the request
    assertThat(response.getAccountId()).isEqualTo("baz");
    // AND the assetCode in the response is unchanged from the request
    assertThat(response.getAssetCode()).isEqualTo("USD");
    // AND the assetScale in the response is unchanged from the request
    assertThat(response.getAssetScale()).isEqualTo(6);
    // AND the paymentPointer is equal to the SPSP server + /{accountId from request}
    assertThat(response.getPaymentPointer()).isEqualTo(paymentPointerBase + "/baz");
    // AND SIMPLE auth has been chosen
    assertThat(response.getCustomSettingsMap().get(IlpAuthConstants.HTTP_INCOMING_AUTH_TYPE)).isEqualTo(IlpAuthConstants.AuthType.SIMPLE.toString());
    // AND the SIMPLE auth token has not been encrypted in the response, and matches the auth token from the request
    assertThat(response.getCustomSettingsMap().get(IlpAuthConstants.HTTP_INCOMING_SIMPLE_AUTH_TOKEN)).isEqualTo("password");
  }

  @Test
  public void createAccountNoAuthYesRequest() throws XpringException {
    // GIVEN an instance of Hermes which is connected to an instance of a Java Connector

    // WHEN an account is created with a populated request but without an auth token
    CreateAccountRequest createAccountRequest = CreateAccountRequest.builder("USD", 6)
      .accountId("bar")
      .description("test account")
      .build();
    CreateAccountResponse response = client.createAccount(createAccountRequest, Optional.empty());

    // THEN the accountId in the response is unchanged from the request
    assertThat(response.getAccountId()).isEqualTo("bar");
    // AND the assetCode in the response is unchanged from the request
    assertThat(response.getAssetCode()).isEqualTo("USD");
    // AND the assetScale in the response is unchanged from the request
    assertThat(response.getAssetScale()).isEqualTo(6);
    // AND the paymentPointer is equal to the SPSP server + /{accountId from request}
    assertThat(response.getPaymentPointer()).isEqualTo(paymentPointerBase + "/bar");
    // AND SIMPLE auth has been chosen
    assertThat(response.getCustomSettingsMap().get(IlpAuthConstants.HTTP_INCOMING_AUTH_TYPE)).isEqualTo(IlpAuthConstants.AuthType.SIMPLE.toString());
    // AND the SIMPLE auth token has not been encrypted in the response
    assertThat(response.getCustomSettingsMap().get(IlpAuthConstants.HTTP_INCOMING_SIMPLE_AUTH_TOKEN)).asString().doesNotStartWith("enc:jks");
  }

  @Test
  public void createAccountNoAuthNoAccountId() throws XpringException {
    // GIVEN an instance of Hermes which is connected to an instance of a Java Connector

    // WHEN an account is created with no auth token and no accountId
    CreateAccountRequest createAccountRequest = CreateAccountRequest.builder("USD", 6)
      .build();
    CreateAccountResponse response = client.createAccount(createAccountRequest, Optional.empty());

    // THEN the account id has been generated with format user_{random string}
    assertThat(response.getAccountId()).startsWith("user_");
    // AND the assetCode in the response is unchanged from the request
    assertThat(response.getAssetCode()).isEqualTo("USD");
    // AND the assetScale in the response is unchanged from the request
    assertThat(response.getAssetScale()).isEqualTo(6);
    // AND the paymentPointer is equal to the SPSP server + /{accountId from request}
    assertThat(response.getPaymentPointer()).isEqualTo(paymentPointerBase + "/" + response.getAccountId());
    // AND SIMPLE auth has been chosen
    assertThat(response.getCustomSettingsMap().get(IlpAuthConstants.HTTP_INCOMING_AUTH_TYPE)).isEqualTo(IlpAuthConstants.AuthType.SIMPLE.toString());
    // AND the SIMPLE auth token has not been encrypted in the response
    assertThat(response.getCustomSettingsMap().get(IlpAuthConstants.HTTP_INCOMING_SIMPLE_AUTH_TOKEN)).asString().doesNotStartWith("enc:jks");
  }

  @Test
  public void getAccount() throws XpringException {
    // GIVEN an instance of Hermes which is connected to an instance of a Java Connector
    // AND a created account
    CreateAccountResponse createAccountResponse = client.createAccount();

    // WHEN that account is retrieved
    GetAccountResponse response = client.getAccount(createAccountResponse.getAccountId(),
      createAccountResponse.getCustomSettingsMap().get(IlpAuthConstants.HTTP_INCOMING_SIMPLE_AUTH_TOKEN));

    // THEN the accountId is equal to the created accountId
    assertThat(response.getAccountId()).isEqualTo(createAccountResponse.getAccountId());
    // AND the assetCode is equal to the created assetCode
    assertThat(response.getAssetCode()).isEqualTo(createAccountResponse.getAssetCode());
    // AND the assetScale is equal to the created assetScale
    assertThat(response.getAssetScale()).isEqualTo(createAccountResponse.getAssetScale());
    // AND the paymentPointer is equal to the created paymentPointer
    assertThat(response.getPaymentPointer()).isEqualTo(createAccountResponse.getPaymentPointer());
    // AND auth type is SIMPLE
    assertThat(response.getCustomSettingsMap().get(IlpAuthConstants.HTTP_INCOMING_AUTH_TYPE)).isEqualTo(IlpAuthConstants.AuthType.SIMPLE.toString());
    // AND the SIMPLE auth token has been encrypted
    assertThat(response.getCustomSettingsMap().get(IlpAuthConstants.HTTP_INCOMING_SIMPLE_AUTH_TOKEN)).startsWith("enc:jks");
  }

  @Test
  public void getBalance() throws XpringException {
    // GIVEN an instance of Hermes which is connected to an instance of a Java Connector
    // AND a created account
    CreateAccountResponse createAccountResponse = client.createAccount();

    // WHEN a balance is retrieved
    GetBalanceResponse response = client.getBalance(createAccountResponse.getAccountId(),
      createAccountResponse.getCustomSettingsMap().get(IlpAuthConstants.HTTP_INCOMING_SIMPLE_AUTH_TOKEN));

    // THEN the accountId associated with the balance is equal to the created accountId
    assertThat(response.getAccountId()).isEqualTo(createAccountResponse.getAccountId());
    // AND the assetCode the balance is denominated in is equal to the created assetCode
    assertThat(response.getAssetCode()).isEqualTo(createAccountResponse.getAssetCode());
    // AND the assetScale the balance is denominated in is equal to the created assetScale
    assertThat(response.getAssetScale()).isEqualTo(createAccountResponse.getAssetScale());
    // AND the net balance is 0
    assertThat(response.getNetBalance()).isEqualTo(0);
    // AND the clearing balance is 0
    assertThat(response.getClearingBalance()).isEqualTo(0);
    // AND the prepaid amount is 0
    assertThat(response.getPrepaidAmount()).isEqualTo(0);
  }

  @Test
  public void sendPayment() throws XpringException {
    // GIVEN an instance of Hermes which is connected to an instance of a Java Connector
    // AND a sender account
    CreateAccountResponse sender = client.createAccount();
    // AND a receiver account
    CreateAccountResponse receiver = client.createAccount();

    // WHEN a payment is sent from the sender to the receiver
    PaymentRequest paymentRequest = PaymentRequest.builder()
      .amount(UnsignedLong.valueOf(10))
      .destinationPaymentPointer(receiver.getPaymentPointer())
      .senderAccountId(sender.getAccountId())
      .build();

    PaymentResponse response = client.sendPayment(
      paymentRequest,
      sender.getCustomSettingsMap().get(IlpAuthConstants.HTTP_INCOMING_SIMPLE_AUTH_TOKEN)
    );

    PaymentResponse expected = PaymentResponse.builder()
      .originalAmount(UnsignedLong.valueOf(10))
      .amountSent(UnsignedLong.valueOf(10))
      .amountDelivered(UnsignedLong.valueOf(10))
      .successfulPayment(true)
      .build();

    // THEN the response should equal the mocked response above
    assertThat(response).isEqualToComparingFieldByField(expected);
  }
}
