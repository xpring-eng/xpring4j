package io.xpring.xrpl;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import io.xpring.AccountInfoOuterClass.AccountInfo;
import io.xpring.SubmitSignedTransactionResponseOuterClass.SubmitSignedTransactionResponse;
import io.xpring.Wallet;
import io.xpring.XpringKitException;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;

/**
 * Unit tests for {@link XpringClient}.
 */
public class XpringClientTest {

    private static final String XRPL_ADDRESS = "rD7zai6QQQVvWc39ZVAhagDgtH5xwEoeXD";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
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
    public void getFeeTest() {
        BigInteger balance = xpringClient.getCurrentFeeInDrops();
        assertThat(balance).isGreaterThan(BigInteger.ONE).withFailMessage("Fee should have been positive");
    }

    @Test
    public void getAccountInfo() {
        AccountInfo accountInfo = xpringClient.getAccountInfo(XRPL_ADDRESS);
        assertThat(new BigInteger(accountInfo.getBalance().getDrops()))
            .isGreaterThan(BigInteger.ONE)
            .withFailMessage("Balance should have been positive");
    }

    @Test
    public void sendXRPTest() throws XpringKitException {
        BigInteger amount = new BigInteger("1");
        Wallet wallet = new Wallet("snYP7oArxKepd3GPDcrjMsJYiJeJB");

        SubmitSignedTransactionResponse response = xpringClient.send(amount, "rsegqrgSP8XmhCYwL9enkZ9BNDNawfPZnn", wallet);
        assertThat(response.getEngineResultMessage()).isEqualTo("The transaction was applied. Only final in a validated ledger.");
    }
}
