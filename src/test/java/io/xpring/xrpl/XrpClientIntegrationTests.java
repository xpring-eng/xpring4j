package io.xpring.xrpl;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.xpring.common.XrplNetwork;
import io.xpring.xrpl.helpers.XrpTestUtils;
import io.xpring.xrpl.model.AccountRootFlag;
import io.xpring.xrpl.model.TransactionResult;
import io.xpring.xrpl.model.XrpTransaction;
import org.junit.Before;
import org.junit.Test;
import org.xrpl.rpc.v1.AccountAddress;
import org.xrpl.rpc.v1.AccountRoot;
import org.xrpl.rpc.v1.GetAccountInfoRequest;
import org.xrpl.rpc.v1.GetAccountInfoResponse;
import org.xrpl.rpc.v1.LedgerSpecifier;
import org.xrpl.rpc.v1.XRPLedgerAPIServiceGrpc;
import org.xrpl.rpc.v1.XRPLedgerAPIServiceGrpc.XRPLedgerAPIServiceBlockingStub;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

/**
 * Integration tests for Xpring4J.
 */
public class XrpClientIntegrationTests {
  /**
   * The rippled XrpClient under test.
   */
  private XrpClient xrpClient;

  /**
   * The gRPC URL.
   */
  private static final String GRPC_URL = "test.xrp.xpring.io:50051";

  /**
   * An address on the XRP Ledger.
   */
  private static final String XRPL_ADDRESS = "XVwDxLQ4SN9pEBQagTNHwqpFkPgGppXqrMoTmUcSKdCtcK5";

  /**
   * Drops of XRP to send.
   */
  private static final BigInteger AMOUNT = new BigInteger("1");

  /**
   * A Wallet with funds on Testnet.
   */
  private static Wallet WALLET = null;
  static {
    try {
      WALLET = XrpTestUtils.randomWalletFromFaucet();
    } catch (Exception e) {}
  }

  @Before
  public void setUp() throws Exception {
    this.xrpClient = new XrpClient(GRPC_URL, XrplNetwork.TEST);
  }

  @Test
  public void getBalanceTest() throws XrpException {
    BigInteger balance = xrpClient.getBalance(XRPL_ADDRESS);
    assertThat(balance).isGreaterThan(BigInteger.ONE).withFailMessage("Balance should have been positive");
  }

  @Test
  public void getPaymentStatusTest() throws XrpException {
    // GIVEN a hash of a payment transaction.
    String transactionHash = xrpClient.send(AMOUNT, XRPL_ADDRESS, WALLET);

    // WHEN the transaction status is retrieved.
    TransactionStatus transactionStatus = xrpClient.getPaymentStatus(transactionHash);

    // THEN the status is 'succeeded'.
    assertThat(transactionStatus).isEqualTo(TransactionStatus.SUCCEEDED);
  }

  @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
  @Test
  public void sendXRPTest() throws XrpException {
    String transactionHash = xrpClient.send(AMOUNT, XRPL_ADDRESS, WALLET);
    assertThat(transactionHash).isNotNull();
  }

  @Test
  @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
  public void sendXRPWithADestinationTag() throws XrpException {
    // GIVEN a transaction hash representing a payment with a destination tag.
    int tag = 123;
    String address = "rPEPPER7kfTD9w2To4CQk6UCfuHM9c6GDY";
    ClassicAddress classicAddressWithTag = ImmutableClassicAddress.builder()
        .address(address)
        .tag(tag)
        .isTest(true)
        .build();
    String taggedAddress = Utils.encodeXAddress(classicAddressWithTag);
    String transactionHash = xrpClient.send(AMOUNT, taggedAddress, WALLET);

    // WHEN the payment is retrieved
    XrpTransaction transaction = xrpClient.getPayment(transactionHash);

    // THEN the payment has the correct destination.
    String destinationXAddress = transaction.paymentFields().destinationXAddress();
    ClassicAddress destinationAddressComponents = Utils.decodeXAddress(destinationXAddress);
    assertThat(destinationAddressComponents.address()).isEqualTo(address);
    assertThat(destinationAddressComponents.tag().get()).isEqualTo(tag);
  }

  @Test
  public void accountExistsTest() throws XrpException {
    boolean exists = xrpClient.accountExists(XRPL_ADDRESS);
    assertThat(exists).isEqualTo(true);
  }

  @Test
  public void paymentHistoryTest() throws XrpException {
    List<XrpTransaction> paymentHistory = xrpClient.paymentHistory(XRPL_ADDRESS);
    assertThat(paymentHistory.size()).isGreaterThan(0);
  }

  @Test
  public void getPaymentTest() throws XrpException {
    // GIVEN a hash of a payment transaction.
    String transactionHash = xrpClient.send(AMOUNT, XRPL_ADDRESS, WALLET);

    // WHEN the transaction is requested.
    XrpTransaction transaction = xrpClient.getPayment(transactionHash);

    // THEN it is found and returned.
    assertThat(transaction).isNotNull();
  }

  @Test
  public void enableDepositAuthTest() throws XrpException {
    // GIVEN an existing testnet account
    // WHEN enableDepositAuth is called
    TransactionResult result = xrpClient.enableDepositAuth(WALLET);

    // THEN the transaction was successfully submitted and the correct flag was set on the account.
    String transactionHash = result.hash();
    TransactionStatus transactionStatus = result.status();

    // get the account data and check the flag bitmap to see if it was correctly set
    ManagedChannel channel = ManagedChannelBuilder.forTarget(GRPC_URL).usePlaintext().build();
    XRPLedgerAPIServiceBlockingStub networkClient = XRPLedgerAPIServiceGrpc.newBlockingStub(channel);

    String address = Utils.decodeXAddress(WALLET.getAddress()).address();
    AccountAddress account = AccountAddress.newBuilder().setAddress(address).build();

    LedgerSpecifier ledger = LedgerSpecifier.newBuilder()
            .setShortcut(LedgerSpecifier.Shortcut.SHORTCUT_VALIDATED)
            .build();

    GetAccountInfoRequest request = GetAccountInfoRequest.newBuilder().setAccount(account).setLedger(ledger).build();

    GetAccountInfoResponse accountInfo = networkClient.getAccountInfo(request);

    AccountRoot accountData = accountInfo.getAccountData();

    Integer flags = accountData.getFlags().getValue();

    assertThat(transactionHash).isNotNull();
    assertThat(transactionStatus).isEqualTo(TransactionStatus.SUCCEEDED);
    assertThat(AccountRootFlag.check(AccountRootFlag.LSF_DEPOSIT_AUTH, flags)).isTrue();
  }
}
