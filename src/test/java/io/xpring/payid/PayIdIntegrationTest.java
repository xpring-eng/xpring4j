package io.xpring.payid;

import static org.junit.Assert.assertEquals;

import io.xpring.common.XrplNetwork;
import io.xpring.payid.generated.model.Address;
import io.xpring.payid.generated.model.CryptoAddressDetails;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;

public class PayIdIntegrationTest {
  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
  public void testXRPAddressForPayIDKnownAddressMainnet() throws PayIdException {
    // GIVEN a Pay ID that will resolve on Mainnet.
    XrpPayIdClient payIDClient = new XrpPayIdClient(XrplNetwork.MAIN);
    String payID = "alice$dev.payid.xpring.money";

    // WHEN it is resolved to an XRP address
    String xrpAddress = payIDClient.xrpAddressForPayId(payID);

    // THEN the address is the expected value.
    assertEquals(xrpAddress, "X7zmKiqEhMznSXgj9cirEnD5sWo3iZSbeFRexSFN1xZ8Ktn");
  }

  @Test
  @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
  public void testXRPAddressForPayIDKnownAddressTestnet() throws PayIdException {
    // GIVEN a Pay ID that will resolve on Testnet.
    XrpPayIdClient payIDClient = new XrpPayIdClient(XrplNetwork.TEST);
    String payID = "alice$dev.payid.xpring.money";

    // WHEN it is resolved to an XRP address
    String xrpAddress = payIDClient.xrpAddressForPayId(payID);

    // THEN the address is the expected value.
    assertEquals(xrpAddress, "TVacixsWrqyWCr98eTYP7FSzE9NwupESR4TrnijN7fccNiS");
  }

  @Test
  @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
  public void testXRPAddressForPayIDKnownAddressDevnet() throws PayIdException {
    // GIVEN a Pay ID that will not resolve on Devnet.
    XrpPayIdClient payIDClient = new XrpPayIdClient(XrplNetwork.DEV);
    String payID = "doesNotExist.payid.xpring.money";

    // WHEN it is resolved to an XRP address THEN a PayID is thrown.
    // TODO(keefertaylor): Tighten this condition to verify the exception is as expected.
    expectedException.expect(PayIdException.class);
    payIDClient.xrpAddressForPayId(payID);
  }

  @Test
  @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
  public void testBTCAddressForKnownAddressTestnet() throws PayIdException {
    // GIVEN a PayID that will resolve on BTC testnet.
    PayIdClient payIdClient = new PayIdClient();
    String payID = "alice$dev.payid.xpring.money";
    String network = "btc-testnet";

    // WHEN it is resolved to a BTC address
    CryptoAddressDetails btcAddressDetails = payIdClient.cryptoAddressForPayId(payID, network);

    // THEN the address is the expected value.
    assertEquals(btcAddressDetails.getAddress(), "2NF9H32iwQcVcoAiiBmAtjpGmQfsmU5L6SR");
  }

  @Test
  public void testAllAddressesForPayId() throws PayIdException, PayIdException {
    // GIVEN a PayID with multiple addresses.
    String payId = "alice$dev.payid.xpring.money";
    PayIdClient payIdClient = new PayIdClient();

    // WHEN the PayID is resolved to a set of addresses.
    List<Address> addresses = payIdClient.allAddressesForPayId(payId);

    // THEN multiple addresses are returned.
    assert addresses.size() > 1;
  }
}
