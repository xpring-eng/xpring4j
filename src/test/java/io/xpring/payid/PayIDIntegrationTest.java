package io.xpring.payid;

import static org.junit.Assert.assertEquals;

import io.xpring.common.XRPLNetwork;
import io.xpring.payid.generated.model.Address;
import io.xpring.payid.generated.model.CryptoAddressDetails;
import io.xpring.payid.idiomatic.PayIdException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;

@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public class PayIDIntegrationTest {
  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
  public void testXRPAddressForPayIDKnownAddressMainnet() throws PayIDException {
    // GIVEN a Pay ID that will resolve on Mainnet.
    XRPPayIDClient payIDClient = new XRPPayIDClient(XRPLNetwork.MAIN);
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
    XRPPayIDClient payIDClient = new XRPPayIDClient(XRPLNetwork.TEST);
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
    XRPPayIDClient payIDClient = new XRPPayIDClient(XRPLNetwork.DEV);
    String payID = "doesNotExist.payid.xpring.money";

    // WHEN it is resolved to an XRP address THEN a PayID is thrown.
    // TODO(keefertaylor): Tighten this condition to verify the exception is as expected.
    expectedException.expect(PayIDException.class);
    payIDClient.xrpAddressForPayID(payID);
  }

  @Test
  public void testBTCAddressForKnownAddressTestnet() throws PayIDException {
    // GIVEN a PayID that will resolve on BTC testnet.
    PayIDClient payIDClient = new PayIDClient();
    String payID = "alice$dev.payid.xpring.money";
    String network = "btc-testnet";

    // WHEN it is resolved to a BTC address
    CryptoAddressDetails btcAddressDetails = payIDClient.cryptoAddressForPayId(payID, network);

    // THEN the address is the expected value.
    assertEquals(btcAddressDetails.getAddress(), "2NF9H32iwQcVcoAiiBmAtjpGmQfsmU5L6SR");
  }

  @Test
  public void testAllAddressesForPayId() throws PayIdException, PayIDException {
    // GIVEN a PayID with multiple addresses.
    String payId = "alice$dev.payid.xpring.money";
    PayIDClient payIdClient = new PayIDClient();

    // WHEN the PayID is resolved to a set of addresses.
    List<Address> addresses = payIdClient.allAddressesForPayId(payId);

    // THEN multiple addresses are returned.
    assert addresses.size() > 1;
  }
}
