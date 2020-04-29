package io.xpring.payid;

import static org.junit.Assert.assertEquals;

import io.xpring.common.XRPLNetwork;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public class PayIDIntegrationTest {
  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
  public void testXRPAddressForPayIDKnownAddressMainnet() throws PayIDException {
    // GIVEN a Pay ID that will resolve on Mainnet.
    PayIDClient payIDClient = new XRPPayIDClient(XRPLNetwork.MAIN);
    String payID = "alice$dev.payid.xpring.money";

    // WHEN it is resolved to an XRP address
    String xrpAddress = payIDClient.xrpAddressForPayID(payID);

    // THEN the address is the expected value.
    assertEquals(xrpAddress, "X7zmKiqEhMznSXgj9cirEnD5sWo3iZSbeFRexSFN1xZ8Ktn");
  }

  @Test
  @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
  public void testXRPAddressForPayIDKnownAddressTestnet() throws PayIDException {
    // GIVEN a Pay ID that will resolve on Testnet.
    PayIDClient payIDClient = new XRPPayIDClient(XRPLNetwork.TEST);
    String payID = "alice$dev.payid.xpring.money";

    // WHEN it is resolved to an XRP address
    String xrpAddress = payIDClient.xrpAddressForPayID(payID);

    // THEN the address is the expected value.
    assertEquals(xrpAddress, "TVacixsWrqyWCr98eTYP7FSzE9NwupESR4TrnijN7fccNiS");
  }

  @Test
  @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
  public void testXRPAddressForPayIDKnownAddressDevnet() throws PayIDException {
    // GIVEN a Pay ID that will not resolve on Devnet.
    PayIDClient payIDClient = new XRPPayIDClient(XRPLNetwork.DEV);
    String payID = "doesNotExist.payid.xpring.money";

    // WHEN it is resolved to an XRP address THEN a PayID is thrown.
    // TODO(keefertaylor): Tighten this condition to verify the exception is as expected.
    expectedException.expect(PayIDException.class);
    payIDClient.xrpAddressForPayID(payID);
  }
}
