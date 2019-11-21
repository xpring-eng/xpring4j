package io.xpring.xrpl;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;

/**
 * Integration tests for Xpring4J.
 */
public class IntegrationTests {
    /** The XpringClient under test. */
    private XpringClient xpringClient;

    /** An address on the XRP Ledger. */
    private static final String XRPL_ADDRESS = "XVwDxLQ4SN9pEBQagTNHwqpFkPgGppXqrMoTmUcSKdCtcK5";

    /** The seed for a wallet with funds on the XRP Ledger test net. */
    private static final String WALLET_SEED = "snYP7oArxKepd3GPDcrjMsJYiJeJB";

    /** Drops of XRP to send. */
    private static final BigInteger AMOUNT = new BigInteger("1");

    /** Hash of a successful transaction. */
    private static final String TRANSACTION_HASH = "2CBBD2523478848DA256F8EBFCBD490DD6048A4A5094BF8E3034F57EA6AA0522";

    @Before
    public void setUp() throws Exception {
        this.xpringClient = new XpringClient();
    }

    @Test
    public void getBalanceTest() throws XpringKitException {
        BigInteger balance = xpringClient.getBalance(XRPL_ADDRESS);
        assertThat(balance).isGreaterThan(BigInteger.ONE).withFailMessage("Balance should have been positive");
    }

    @Test
    public void getTransactionStatusTest() {
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
