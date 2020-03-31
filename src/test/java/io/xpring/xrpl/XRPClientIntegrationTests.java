package io.xpring.xrpl;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;

/**
 * Integration tests for Xpring4J.
 */
public class XRPClientIntegrationTests {
    /** The legacy XRPClient under test. */
    private XRPClient legacyXRPClient;

    /** The rippled XRPClient under test. */
    private XRPClient xrpClient;

    /** The gRPC URL */
    private String LEGACY_GRPC_URL = "grpc.xpring.tech";
    private String GRPC_URL = "test.xrp.xpring.io:50051";

    /** An address on the XRP Ledger. */
    private static final String XRPL_ADDRESS = "XVwDxLQ4SN9pEBQagTNHwqpFkPgGppXqrMoTmUcSKdCtcK5";

    /** The seed for a wallet with funds on the XRP Ledger test net. */
    private static final String WALLET_SEED = "snYP7oArxKepd3GPDcrjMsJYiJeJB";

    /** Drops of XRP to send. */
    private static final BigInteger AMOUNT = new BigInteger("1");

    /** Hash of a successful transaction. */
    private static final String TRANSACTION_HASH = "DAA9F31628C952A48DAE71829E91847BF4EF23C0FABDD7218E41836D1E68EEBD";

    @Before
    public void setUp() throws Exception {
        this.legacyXRPClient = new XRPClient(LEGACY_GRPC_URL, false);
        this.xrpClient = new XRPClient(GRPC_URL);
    }

    @Test
    public void getBalanceTest_legacy() throws XpringException {
        BigInteger balance = legacyXRPClient.getBalance(XRPL_ADDRESS);
        assertThat(balance).isGreaterThan(BigInteger.ONE).withFailMessage("Balance should have been positive");
    }

    @Test
    public void getPaymentStatusTest_legacy() throws XpringException  {
        TransactionStatus transactionStatus = legacyXRPClient.getPaymentStatus(TRANSACTION_HASH);
        assertThat(transactionStatus).isEqualTo(TransactionStatus.SUCCEEDED);
    }

    @Test
    public void sendXRPTest_legacy() throws XpringException {
        Wallet wallet = new Wallet(WALLET_SEED);

        String transactionHash = legacyXRPClient.send(AMOUNT, XRPL_ADDRESS, wallet);
        assertThat(transactionHash).isNotNull();
    }

    @Test
    public void getBalanceTest() throws XpringException {
        BigInteger balance = xrpClient.getBalance(XRPL_ADDRESS);
        assertThat(balance).isGreaterThan(BigInteger.ONE).withFailMessage("Balance should have been positive");
    }

    @Test
    public void getPaymentStatusTest() throws XpringException {
        TransactionStatus transactionStatus = xrpClient.getPaymentStatus(TRANSACTION_HASH);
        assertThat(transactionStatus).isEqualTo(TransactionStatus.SUCCEEDED);
    }

    @Test
    public void sendXRPTest() throws XpringException {
        Wallet wallet = new Wallet(WALLET_SEED);

        String transactionHash = xrpClient.send(AMOUNT, XRPL_ADDRESS, wallet);
        assertThat(transactionHash).isNotNull();
    }

    @Test
    public void accountExistsTest_legacy() throws XpringException {
        boolean exists = legacyXRPClient.accountExists(XRPL_ADDRESS);
        assertThat(exists).isEqualTo(true);
    }

    @Test
    public void accountExistsTest() throws XpringException {
        boolean exists = xrpClient.accountExists(XRPL_ADDRESS);
        assertThat(exists).isEqualTo(true);
    }
}
