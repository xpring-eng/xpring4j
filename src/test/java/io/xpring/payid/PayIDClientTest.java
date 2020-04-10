package io.xpring.payid;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.Assert.assertEquals;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import io.xpring.common.XRPLNetwork;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public class PayIDClientTest {
  @Rule
  public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort().dynamicHttpsPort());

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void testXRPAddressForPayIDInvalidPaymentPointer() throws PayIDException {
    // GIVEN a PayIDClient and an invalid PayID.
    String invalidPayID = "georgewashington$xpring$money"; // Too many '$'
    PayIDClient payIDClient = new PayIDClient(XRPLNetwork.MAIN);

    // WHEN an XRPAddress is requested for an invalid pay ID THEN an invalid payment pointer error is thrown.
    // TODO(keefertaylor): Tighten this condition to verify the exception is as expected.
    expectedException.expect(PayIDException.class);
    payIDClient.xrpAddressForPayID(invalidPayID);
  }

  @Test
  public void testXRPAddressForPayIDSuccess() throws PayIDException {
    // GIVEN a PayID client, valid PayID and mocked networking to return a match for the PayID.
    String payID = "georgewashington$localhost:" + wireMockRule.httpsPort();
    PayIDClient client = new PayIDClient(XRPLNetwork.MAIN);
    client.setEnableSSLVerification(false);
    String xrpAddress = "X7cBcY4bdTTzk3LHmrKAK6GyrirkXfLHGFxzke5zTmYMfw4";

    stubFor(get(urlEqualTo("/georgewashington"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/xrpl-mainnet+json")
            .withBody("{ "
                + "addressDetailsType: 'CryptoAddressDetails', "
                + "addressDetails: { "
                + "address: 'X7cBcY4bdTTzk3LHmrKAK6GyrirkXfLHGFxzke5zTmYMfw4' "
                + "}"
                + "}"
            )
        )
    );

    // WHEN an XRP address is requested.
    String address = client.xrpAddressForPayID(payID);

    // THEN the address is the one returned in the response.
    assertEquals(address, "X7cBcY4bdTTzk3LHmrKAK6GyrirkXfLHGFxzke5zTmYMfw4");
  }

  @Test
  public void testXRPAddressForPayIDMatchNotFound() throws PayIDException {
    // GIVEN a PayID client, valid PayID and mocked networking to return a 404 for the payID.
    String payID = "georgewashington$localhost:" + wireMockRule.httpsPort();
    PayIDClient client = new PayIDClient(XRPLNetwork.MAIN);
    client.setEnableSSLVerification(false);
    String xrpAddress = "X7cBcY4bdTTzk3LHmrKAK6GyrirkXfLHGFxzke5zTmYMfw4";

    stubFor(get(urlEqualTo("/georgewashington"))
        .willReturn(aResponse()
            .withStatus(404)));

    // WHEN an XRPAddress is requested THEN a mapping not found error is thrown.
    // TODO(keefertaylor): Tighten this condition to verify the exception is as expected.
    expectedException.expect(PayIDException.class);
    client.xrpAddressForPayID(payID);
  }

  @Test
  public void testXRPAddressForPayIDBadMIMEType() throws PayIDException {
    // GIVEN a PayID client, valid PayID and mocked networking to return a 415 for the payID.
    String payID = "georgewashington$localhost:" + wireMockRule.httpsPort();
    PayIDClient client = new PayIDClient(XRPLNetwork.MAIN);
    client.setEnableSSLVerification(false);
    String xrpAddress = "X7cBcY4bdTTzk3LHmrKAK6GyrirkXfLHGFxzke5zTmYMfw4";

    stubFor(get(urlEqualTo("/georgewashington"))
        .willReturn(aResponse()
            .withStatus(415)));

    // WHEN an XRPAddress is requested THEN a unexpected response error is thrown.
    // TODO(keefertaylor): Tighten this condition to verify the exception is as expected.
    expectedException.expect(PayIDException.class);
    client.xrpAddressForPayID(payID);
  }

  @Test
  public void testXRPAddressForPayIDServerFailure() throws PayIDException {
    // GIVEN a PayID client, valid PayID and mocked networking to return a 503 for the payID.
    String payID = "georgewashington$localhost:" + wireMockRule.httpsPort();
    PayIDClient client = new PayIDClient(XRPLNetwork.MAIN);
    client.setEnableSSLVerification(false);
    String xrpAddress = "X7cBcY4bdTTzk3LHmrKAK6GyrirkXfLHGFxzke5zTmYMfw4";

    stubFor(get(urlEqualTo("/georgewashington"))
        .willReturn(aResponse()
            .withStatus(503)));

    // WHEN an XRPAddress is requested THEN a unexpected response error is thrown.
    // TODO(keefertaylor): Tighten this condition to verify the exception is as expected.
    expectedException.expect(PayIDException.class);
    client.xrpAddressForPayID(payID);
  }
}

