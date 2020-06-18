package io.xpring.xrpl;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import io.xpring.common.XRPLNetwork;
import io.xpring.xrpl.model.XRPTransaction;
import io.xpring.xrpl.model.idiomatic.XrpPayment;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;
import java.util.List;

/**
 * Integration tests for Xpring4J.
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public class XRPClientIntegrationTests {
  /**
   * The rippled XRPClient under test.
   */
  private XRPClient xrpClient;

  /**
   * The gRPC URL.
   */
  private static final String GRPC_URL = "test.xrp.xpring.io:50051";

  /**
   * An address on the XRP Ledger.
   */
  private static final String XRPL_ADDRESS = "XVwDxLQ4SN9pEBQagTNHwqpFkPgGppXqrMoTmUcSKdCtcK5";

  /**
   * The seed for a wallet with funds on the XRP Ledger test net.
   */
  private static final String WALLET_SEED = "snYP7oArxKepd3GPDcrjMsJYiJeJB";

  /**
   * Drops of XRP to send.
   */
  private static final BigInteger AMOUNT = new BigInteger("1");

  @Before
  public void setUp() throws Exception {
    this.xrpClient = new XRPClient(GRPC_URL, XRPLNetwork.TEST);
  }

  @Test
  public void getBalanceTest() throws XRPException {
    BigInteger balance = xrpClient.getBalance(XRPL_ADDRESS);
    assertThat(balance).isGreaterThan(BigInteger.ONE).withFailMessage("Balance should have been positive");
  }

  @Test
  public void getPaymentStatusTest() throws XRPException {
    // GIVEN a hash of a payment transaction.
    Wallet wallet = new Wallet(WALLET_SEED);
    String transactionHash = xrpClient.send(AMOUNT, XRPL_ADDRESS, wallet);

    // WHEN the transaction status is retrieved.
    TransactionStatus transactionStatus = xrpClient.getPaymentStatus(transactionHash);

    // THEN the status is 'succeeded'.
    assertThat(transactionStatus).isEqualTo(TransactionStatus.SUCCEEDED);
  }

  @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
  @Test
  public void sendXRPTest() throws XRPException {
    Wallet wallet = new Wallet(WALLET_SEED);

    String transactionHash = xrpClient.send(AMOUNT, XRPL_ADDRESS, wallet);
    assertThat(transactionHash).isNotNull();
  }

  @Test
  public void sendXRPWithADestinationTag() throws XRPException {
    // GIVEN a transaction hash representing a payment with a destination tag.
    Wallet wallet = new Wallet(WALLET_SEED);
    int tag = 123;
    String address = "rPEPPER7kfTD9w2To4CQk6UCfuHM9c6GDY";
    ClassicAddress classicAddressWithTag = ImmutableClassicAddress.builder()
        .address(address)
        .tag(tag)
        .isTest(true)
        .build();
    String taggedAddress = Utils.encodeXAddress(classicAddressWithTag);
    String transactionHash = xrpClient.send(AMOUNT, taggedAddress, wallet);

    // WHEN the payment is retrieved
    XRPTransaction transaction = xrpClient.getPayment(transactionHash);

    // THEN the payment has the correct destination.
    String destinationXAddress = transaction.paymentFields().destinationXAddress();
    ClassicAddress destinationAddressComponents = Utils.decodeXAddress(destinationXAddress);
    assertThat(destinationAddressComponents.address()).isEqualTo(address);
    assertThat(destinationAddressComponents.tag().get()).isEqualTo(tag);
  }

  @Test
  public void accountExistsTest() throws XRPException {
    boolean exists = xrpClient.accountExists(XRPL_ADDRESS);
    assertThat(exists).isEqualTo(true);
  }

  @Test
  public void paymentHistoryTest() throws XRPException {
    List<XRPTransaction> paymentHistory = xrpClient.paymentHistory(XRPL_ADDRESS);
    assertThat(paymentHistory.size()).isGreaterThan(0);
  }

  @Test
  public void getPaymentTest() throws XRPException {
    // GIVEN a hash of a payment transaction.
    Wallet wallet = new Wallet(WALLET_SEED);
    String transactionHash = xrpClient.send(AMOUNT, XRPL_ADDRESS, wallet);

    // WHEN the transaction is requested.
    XRPTransaction transaction = xrpClient.getPayment(transactionHash);

    // THEN it is found and returned.
    assertThat(transaction).isNotNull();
  }
}
