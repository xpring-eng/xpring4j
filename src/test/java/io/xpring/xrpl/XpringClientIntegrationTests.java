package io.xpring.xrpl;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;

/**
 * Integration tests for Xpring4J.
 */
public class XpringClientIntegrationTests {
    /** The legacy XpringClient under test. */
    private XpringClient legacyXpringClient;

    /** The rippled XpringClient under test. */
    private XpringClient xpringClient;

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
    private static final String TRANSACTION_HASH = "A040256A283FA2DC1E732AF70D24DC289E6BE8B9782917F0A7FDCB23D0B48F70";

    @Before
    public void setUp() throws Exception {
        this.legacyXpringClient = new XpringClient(LEGACY_GRPC_URL);
        this.xpringClient = new XpringClient(GRPC_URL, true);
    }

    @Test
    public void getBalanceTest_legacy() throws XpringException {
        BigInteger balance = legacyXpringClient.getBalance(XRPL_ADDRESS);
        assertThat(balance).isGreaterThan(BigInteger.ONE).withFailMessage("Balance should have been positive");
    }

    @Test
    public void getTransactionStatusTest_legacy() throws XpringException  {
        TransactionStatus transactionStatus = legacyXpringClient.getTransactionStatus(TRANSACTION_HASH);
        assertThat(transactionStatus).isEqualTo(TransactionStatus.SUCCEEDED);
    }

    @Test
    public void sendXRPTest_legacy() throws XpringException {
        Wallet wallet = new Wallet(WALLET_SEED);

        String transactionHash = legacyXpringClient.send(AMOUNT, XRPL_ADDRESS, wallet);
        assertThat(transactionHash).isNotNull();
    }

    @Test
    public void getBalanceTest() throws XpringException {
        BigInteger balance = xpringClient.getBalance(XRPL_ADDRESS);
        assertThat(balance).isGreaterThan(BigInteger.ONE).withFailMessage("Balance should have been positive");
    }

    @Test
    public void getTransactionStatusTest() throws XpringException {
        TransactionStatus transactionStatus = xpringClient.getTransactionStatus(TRANSACTION_HASH);
        assertThat(transactionStatus).isEqualTo(TransactionStatus.SUCCEEDED);
    }

    @Test
    public void sendXRPTest() throws XpringException {
        Wallet wallet = new Wallet(WALLET_SEED);

        String transactionHash = xpringClient.send(AMOUNT, XRPL_ADDRESS, wallet);
        assertThat(transactionHash).isNotNull();
    }
}
