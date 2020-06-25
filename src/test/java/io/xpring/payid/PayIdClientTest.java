package io.xpring.payid;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.Assert.assertEquals;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import io.xpring.payid.generated.model.Address;
import io.xpring.payid.generated.model.CryptoAddressDetails;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;

public class PayIdClientTest {
  @Rule
  public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort().dynamicHttpsPort());

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void testAllAddressesForPayIdInvalidPayId() throws PayIdException {
    // Given a PayIdClient and an invalid PayID.
    String invalidPayId = "xpring.money/georgewashington"; // no "$"
    PayIdClient payIdClient = new PayIdClient();

    // WHEN all addresses are resolved THEN an invalid Pay ID error is thrown.
    expectedException.expect(PayIdException.class);
    payIdClient.allAddressesForPayId(invalidPayId);
  }

  @Test
  public void testAllAddressesForPayIdSuccessfulResponseMatchFound() throws PayIdException {
    // GIVEN a PayIdClient, a valid PayID and mocked networking to return a set of matches for the PayID.
    final String payId = "georgewashington$localhost:" + wireMockRule.httpsPort();
    PayIdClient payIdClient = new PayIdClient();
    payIdClient.setEnableSSLVerification(false);

    CryptoAddressDetails cryptoAddressDetails1 = new CryptoAddressDetails();
    cryptoAddressDetails1.setAddress("X7cBcY4bdTTzk3LHmrKAK6GyrirkXfLHGFxzke5zTmYMfw4");
    CryptoAddressDetails cryptoAddressDetails2 = new CryptoAddressDetails();
    cryptoAddressDetails2.setAddress("XV5sbjUmgPpvXv4ixFWZ5ptAYZ6PD28Sq49uo34VyjnmK5H");

    Address address1 = new Address();
    address1.setAddressDetailsType("CryptoAddressDetails");
    address1.setAddressDetails(cryptoAddressDetails1);
    Address address2 = new Address();
    address2.setAddressDetailsType("CryptoAddressDetails");
    address2.setAddressDetails(cryptoAddressDetails2);

    List<Address> addresses;
    addresses = new ArrayList<>();
    addresses.add(address1);
    addresses.add(address2);

    stubFor(get(urlEqualTo("/georgewashington"))
            .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/payid+json")
                    .withBody("{ "
                            + "addresses: [{"
                            + "addressDetailsType: 'CryptoAddressDetails', "
                            + "addressDetails: { "
                            + "address: '" + "X7cBcY4bdTTzk3LHmrKAK6GyrirkXfLHGFxzke5zTmYMfw4" + "' "
                            + "}"
                            + "}, "
                            + "{"
                            + "addressDetailsType: 'CryptoAddressDetails', "
                            + "addressDetails: { "
                            + "address: '" + "XV5sbjUmgPpvXv4ixFWZ5ptAYZ6PD28Sq49uo34VyjnmK5H" + "' "
                            + "}"
                            + "}"
                            + "]"
                            + "}"
                    )
            )
    );

    // WHEN all addresses are resolved.
    List<Address> resolvedAddresses = payIdClient.allAddressesForPayId(payId);

    // THEN the returned data is as expected.
    assertEquals(addresses, resolvedAddresses);
  }

  @Test
  public void testAllAddressesForPayIdSuccessfulResponseMatchNotFound() throws PayIdException {
    // GIVEN a PayIdClient, a valid PayID and mocked networking to return a 404 for the payID.
    String payId = "georgewashington$localhost:" + wireMockRule.httpsPort();
    PayIdClient payIdClient = new PayIdClient();

    stubFor(get(urlEqualTo("/georgewashington"))
            .willReturn(aResponse()
                    .withStatus(404)));

    // WHEN all addresses are resolved THEN a mapping not found error is thrown.
    // TODO(amiecorso): Tighten this condition to verify the exception is as expected.
    expectedException.expect(PayIdException.class);
    payIdClient.allAddressesForPayId(payId);
  }
}
