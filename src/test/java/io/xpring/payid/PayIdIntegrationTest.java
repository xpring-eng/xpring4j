package io.xpring.payid;

import static org.junit.Assert.assertEquals;

<<<<<<< HEAD:src/test/java/io/xpring/payid/PayIdIntegrationTest.java
import io.xpring.common.XrplNetwork;
=======
import io.xpring.common.XRPLNetwork;
>>>>>>> origin/master:src/test/java/io/xpring/payid/PayIDIntegrationTest.java
import io.xpring.payid.generated.model.Address;
import io.xpring.payid.generated.model.CryptoAddressDetails;
import io.xpring.payid.idiomatic.PayIdException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;

<<<<<<< HEAD:src/test/java/io/xpring/payid/PayIdIntegrationTest.java
public class PayIdIntegrationTest {
=======
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public class PayIDIntegrationTest {
>>>>>>> origin/master:src/test/java/io/xpring/payid/PayIDIntegrationTest.java
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
<<<<<<< HEAD:src/test/java/io/xpring/payid/PayIdIntegrationTest.java
    PayIdClient payIdClient = new PayIdClient();
=======
    PayIDClient payIDClient = new PayIDClient();
>>>>>>> origin/master:src/test/java/io/xpring/payid/PayIDIntegrationTest.java
    String payID = "alice$dev.payid.xpring.money";
    String network = "btc-testnet";

    // WHEN it is resolved to a BTC address
<<<<<<< HEAD:src/test/java/io/xpring/payid/PayIdIntegrationTest.java
    CryptoAddressDetails btcAddressDetails = payIdClient.cryptoAddressForPayId(payID, network);
=======
    CryptoAddressDetails btcAddressDetails = payIDClient.cryptoAddressForPayId(payID, network);
>>>>>>> origin/master:src/test/java/io/xpring/payid/PayIDIntegrationTest.java

    // THEN the address is the expected value.
    assertEquals(btcAddressDetails.getAddress(), "2NF9H32iwQcVcoAiiBmAtjpGmQfsmU5L6SR");
  }

  @Test
<<<<<<< HEAD:src/test/java/io/xpring/payid/PayIdIntegrationTest.java
  public void testAllAddressesForPayId() throws PayIdException, PayIdException {
    // GIVEN a PayID with multiple addresses.
    String payId = "alice$dev.payid.xpring.money";
    PayIdClient payIdClient = new PayIdClient();
=======
  public void testAllAddressesForPayId() throws PayIdException, PayIDException {
    // GIVEN a PayID with multiple addresses.
    String payId = "alice$dev.payid.xpring.money";
    PayIDClient payIdClient = new PayIDClient();
>>>>>>> origin/master:src/test/java/io/xpring/payid/PayIDIntegrationTest.java

    // WHEN the PayID is resolved to a set of addresses.
    List<Address> addresses = payIdClient.allAddressesForPayId(payId);

    // THEN multiple addresses are returned.
    assert addresses.size() > 1;
  }
}
