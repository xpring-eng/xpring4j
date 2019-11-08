package io.xpring;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import io.xpring.xrpl.XpringClient;
import io.xpring.proto.AccountInfo;
import io.xpring.Wallet;
import io.xpring.XpringKitException;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;

/**
 * Integration tests for Xpring4J.
 */
public class IntegrationTests {
    private static final String XRPL_ADDRESS = "rD7zai6QQQVvWc39ZVAhagDgtH5xwEoeXD";

    private XpringClient xpringClient;

    @Before
    public void setUp() {
        this.xpringClient = new XpringClient();
    }

    @Test
    public void getBalanceTest() {
        BigInteger balance = xpringClient.getBalance(XRPL_ADDRESS);
        assertThat(balance).isGreaterThan(BigInteger.ONE).withFailMessage("Balance should have been positive");
    }

    @Test
    public void sendXRPTest() throws XpringKitException {
        BigInteger amount = new BigInteger("1");
        Wallet wallet = new Wallet("snYP7oArxKepd3GPDcrjMsJYiJeJB");

        String transactionHash = xpringClient.send(amount, "rsegqrgSP8XmhCYwL9enkZ9BNDNawfPZnn", wallet);
        assertThat(transactionHash).isNotNull();
    }
}
