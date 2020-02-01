package io.xpring.xrpl;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;

/**
 * Integration tests for Xpring4J.
 */
public class IntegrationTests {
    /** The legacy XpringClient under test. */
    private XpringClient legacyXpringClient;

    /** The rippled XpringClient under test. */
    private XpringClient xpringClient;

    /** An address on the XRP Ledger. */
    private static final String XRPL_ADDRESS = "XVwDxLQ4SN9pEBQagTNHwqpFkPgGppXqrMoTmUcSKdCtcK5";

    /** The seed for a wallet with funds on the XRP Ledger test net. */
    private static final String WALLET_SEED = "snYP7oArxKepd3GPDcrjMsJYiJeJB";

    /** Drops of XRP to send. */
    private static final BigInteger AMOUNT = new BigInteger("1");

    /** Hash of a successful transaction. */
    private static final String TRANSACTION_HASH = "9A88C8548E03958FD97AF44AE5A8668896D195A70CF3FF3CB8E57096AA717135";

    @Before
    public void setUp() throws Exception {
        this.xpringClient = new XpringClient();
        this.xpringClient = new XpringClient(true);
    }

    @Test
    public void getBalanceTest() throws XpringKitException {
        BigInteger balance = xpringClient.getBalance(XRPL_ADDRESS);
        assertThat(balance).isGreaterThan(BigInteger.ONE).withFailMessage("Balance should have been positive");
    }

    @Test
    public void getTransactionStatusTest() throws XpringKitException  {
        TransactionStatus transactionStatus = xpringClient.getTransactionStatus(TRANSACTION_HASH);
        assertThat(transactionStatus).isEqualTo(TransactionStatus.SUCCEEDED);
    }

    @Test
    public void sendXRPTest() throws XpringKitException {
        Wallet wallet = new Wallet(WALLET_SEED);

        String transactionHash = xpringClient.send(AMOUNT, XRPL_ADDRESS, wallet);
        assertThat(transactionHash).isNotNull();
    }
}
