package io.xpring.ilp;


import static org.assertj.core.api.Assertions.assertThat;

import org.interledger.spsp.server.grpc.AccountServiceGrpc;
import org.interledger.spsp.server.grpc.CreateAccountRequest;
import org.interledger.spsp.server.grpc.CreateAccountResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.primitives.UnsignedLong;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.xpring.ilp.model.AccountBalance;
import io.xpring.ilp.model.PaymentRequest;
import io.xpring.ilp.model.PaymentResult;
import io.xpring.ilp.util.IlpAuthConstants;
import io.xpring.xrpl.XpringException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;

import java.io.IOException;

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

  private static final int HERMES_GRPC_PORT = 6565;

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
    .withExposedPorts(HERMES_GRPC_PORT)
    .withNetwork(network)
    .withEnv("interledger.connector.connector-url", "http://connector-host:" + CONNECTOR_PORT);

  private IlpClient client;

  private AccountServiceGrpc.AccountServiceBlockingStub accountBlockingStub;

  @Before
  public void setUp() {
    String grpcUrl = hermesNode.getContainerIpAddress() + ":" + hermesNode.getMappedPort(HERMES_GRPC_PORT);
    client = new IlpClient(grpcUrl);

    ManagedChannel channel = ManagedChannelBuilder
      .forTarget(grpcUrl)
      .usePlaintext()
      .build();
    this.accountBlockingStub = AccountServiceGrpc.newBlockingStub(channel);
  }

  @AfterClass
  public static void tearDown() {
    connectorNode.stop();
    hermesNode.stop();
  }

  @Test
  public void getBalance() throws XpringException {
    // GIVEN an instance of Hermes which is connected to an instance of a Java Connector
    // AND a created account
    CreateAccountResponse createAccountResponse = createAccount();

    // WHEN a balance is retrieved
    AccountBalance response = client.getBalance(createAccountResponse.getAccountId(),
      createAccountResponse.getCustomSettingsMap().get(IlpAuthConstants.HTTP_INCOMING_SIMPLE_AUTH_TOKEN));

    // THEN the accountId associated with the balance is equal to the created accountId
    assertThat(response.accountId()).isEqualTo(createAccountResponse.getAccountId());
    // AND the assetCode the balance is denominated in is equal to the created assetCode
    assertThat(response.assetCode()).isEqualTo(createAccountResponse.getAssetCode());
    // AND the assetScale the balance is denominated in is equal to the created assetScale
    assertThat(response.assetScale()).isEqualTo(createAccountResponse.getAssetScale());
    // AND the net balance is 0
    assertThat(response.netBalance()).isEqualTo(0);
    // AND the clearing balance is 0
    assertThat(response.clearingBalance()).isEqualTo(0);
    // AND the prepaid amount is 0
    assertThat(response.prepaidAmount()).isEqualTo(0);
  }

  @Test
  public void sendPayment() throws XpringException {
    // GIVEN an instance of Hermes which is connected to an instance of a Java Connector
    // AND a sender account
    CreateAccountResponse sender = createAccount();
    // AND a receiver account
    CreateAccountResponse receiver = createAccount();

    // WHEN a payment is sent from the sender to the receiver
    PaymentRequest paymentRequest = PaymentRequest.builder()
      .amount(UnsignedLong.valueOf(10))
      .destinationPaymentPointer(receiver.getPaymentPointer())
      .senderAccountId(sender.getAccountId())
      .build();

    PaymentResult response = client.sendPayment(
      paymentRequest,
      sender.getCustomSettingsMap().get(IlpAuthConstants.HTTP_INCOMING_SIMPLE_AUTH_TOKEN)
    );

    PaymentResult expected = PaymentResult.builder()
      .originalAmount(UnsignedLong.valueOf(10))
      .amountSent(UnsignedLong.valueOf(10))
      .amountDelivered(UnsignedLong.valueOf(10))
      .successfulPayment(true)
      .build();

    // THEN the response should equal the mocked response above
    assertThat(response).isEqualToComparingFieldByField(expected);
  }

  private CreateAccountResponse createAccount() {
    return this.accountBlockingStub.createAccount(CreateAccountRequest.newBuilder().build());
  }
}
