package io.xpring.xrpl;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import io.xpring.proto.AccountInfo;
import io.xpring.Wallet;
import io.xpring.XpringKitException;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.rules.ExpectedException;

import java.math.BigInteger;

/**
 * Unit tests for {@link XpringClient}.
 */
public class XpringClientTest {

    private static final String XRPL_ADDRESS = "XVwDxLQ4SN9pEBQagTNHwqpFkPgGppXqrMoTmUcSKdCtcK5";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /** Drops of XRP to send. */
    private static final BigInteger AMOUNT = new BigInteger("1");

    /** The seed for a wallet with funds on the XRP Ledger test net. */
    private static final String WALLET_SEED = "snYP7oArxKepd3GPDcrjMsJYiJeJB"

    private XpringClient xpringClient;

    @Before
    public void setUp() {
        this.xpringClient = new XpringClient();
    }

    @Test
    public void getBalanceTest() throws XpringKitException {
        BigInteger balance = xpringClient.getBalance(XRPL_ADDRESS);
        assertThat(balance).isGreaterThan(BigInteger.ONE).withFailMessage("Balance should have been positive");
    }

    @Test
    public void getBalanceWithClassicAddressTest() throws XpringKitException {
        // GIVEN a classic address.
        ClassicAddress classicAddress = Utils.decodeXAddress(XRPL_ADDRESS);

        // WHEN the balance for the classic address is retrieved THEN an error is thrown.
        expectedException.expect(XpringKitException.class);
        xpringClient.getBalance(classicAddress.address());
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
        Wallet wallet = new Wallet(WALLET_SEED);

        String transactionHash = xpringClient.send(AMOUNT, XRPL_ADDRESS, wallet);
        assertThat(transactionHash).isNotNull();
    }

    @Test
    public void sendXRPTestWithClassicAddress() throws XpringKitException {
        // GIVEN a classic address.
        ClassicAddress classicAddress = Utils.decodeXAddress(XRPL_ADDRESS);
        Wallet wallet = new Wallet(WALLET_SEED);

        // WHEN XRP is sent to the classic address THEN an error is thrown.
        expectedException.expect(XpringKitException.class);
        xpringClient.send(AMOUNT, XRPL_ADDRESS, wallet);
    }
}
