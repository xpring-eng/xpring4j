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
        // GIVEN a hash of a payment transaction.
        Wallet wallet = new Wallet(WALLET_SEED);
        String transactionHash = legacyXRPClient.send(AMOUNT, XRPL_ADDRESS, wallet);

        // WHEN the transaction status is retrieved.
        TransactionStatus transactionStatus = legacyXRPClient.getPaymentStatus(transactionHash);

        // THEN the status is 'succeeded'.
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
        // GIVEN a hash of a payment transaction.
        Wallet wallet = new Wallet(WALLET_SEED);
        String transactionHash = xrpClient.send(AMOUNT, XRPL_ADDRESS, wallet);

        // WHEN the transaction status is retrieved.
        TransactionStatus transactionStatus = xrpClient.getPaymentStatus(transactionHash);

        // THEN the status is 'succeeded'.
        assertThat(transactionStatus).isEqualTo(TransactionStatus.SUCCEEDED);
    }

    @Test
    public void sendXRPTest() throws XpringException {
        Wallet wallet = new Wallet(WALLET_SEED);

        String transactionHash = xrpClient.send(AMOUNT, XRPL_ADDRESS, wallet);
        assertThat(transactionHash).isNotNull();
    }
}
