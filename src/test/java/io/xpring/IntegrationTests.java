package io.xpring;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import io.xpring.xrpl.XpringClient;
import io.xpring.proto.AccountInfo;
import io.xpring.Wallet;
import io.xpring.XpringKitException;
import org.junit.Before;
import org.junit.Rule;
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

    /** Mocked values in responses from the gRPC server. */
    private static final String DROPS_OF_XRP_IN_ACCOUNT = "10";
    private static final String DROPS_OF_XRP_FOR_FEE = "20";
    private static final String TRANSACTION_BLOB = "DEADBEEF";

    /** Mocks gRPC networking inside of XpringClient. */
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
    public void sendXRPTest() throws XpringKitException {
        Wallet wallet = new Wallet(WALLET_SEED);

        String transactionHash = xpringClient.send(AMOUNT, XRPL_ADDRESS, wallet);
        assertThat(transactionHash).isNotNull();
    }
}
