package io.xpring.payid;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.Assert.assertEquals;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import io.xpring.common.XRPLNetwork;
import io.xpring.payid.idiomatic.PayIdException;
import io.xpring.xrpl.ClassicAddress;
import io.xpring.xrpl.ImmutableClassicAddress;
import io.xpring.xrpl.Utils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public class XRPPayIDClientTest {
  @Rule
  public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort().dynamicHttpsPort());

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void testXRPAddressForPayIDInvalidPaymentPointer() throws PayIDException {
    // GIVEN a PayIDClient and an invalid PayID.
    String invalidPayID = "georgewashington$xpring$money"; // Too many '$'
    XRPPayIDClient payIDClient = new XRPPayIDClient(XRPLNetwork.MAIN);

    // WHEN an XRPAddress is requested for an invalid pay ID THEN an invalid payment pointer error is thrown.
    // TODO(keefertaylor): Tighten this condition to verify the exception is as expected.
    expectedException.expect(PayIDException.class);
    payIDClient.xrpAddressForPayID(invalidPayID);
  }

  @Test
  public void testXRPAddressForPayIDSuccessWithXAddress() throws PayIDException {
    // GIVEN a PayID client, a valid PayID and mocked networking to return an X-Address for the PayID.
    String payID = "georgewashington$localhost:" + wireMockRule.httpsPort();
    XRPPayIDClient client = new XRPPayIDClient(XRPLNetwork.MAIN);
    client.setEnableSSLVerification(false);
    String expectedAddress = "X7cBcY4bdTTzk3LHmrKAK6GyrirkXfLHGFxzke5zTmYMfw4";

    stubFor(get(urlEqualTo("/georgewashington"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/xrpl-mainnet+json")
            .withBody("{ "
                + "addresses: [{"
                + "addressDetailsType: 'CryptoAddressDetails', "
                + "addressDetails: { "
                + "address: '" + expectedAddress + "' "
                + "}"
                + "}]"
                + "}"
            )
        )
    );

    // WHEN an XRP address is requested.
    String address = client.xrpAddressForPayID(payID);

    // THEN the address is the one returned in the response.
    assertEquals(address, expectedAddress);
  }

  @Test
  public void testXRPAddressForPayIDSuccessWithClassicAddressNoTag() throws PayIDException {
    // GIVEN a PayID client, a valid PayID and mocked networking to return an classic address without a tag.
    String payID = "georgewashington$localhost:" + wireMockRule.httpsPort();
    XRPPayIDClient client = new XRPPayIDClient(XRPLNetwork.TEST);
    client.setEnableSSLVerification(false);

    ClassicAddress classicAddress = ImmutableClassicAddress.builder()
        .address("rPEPPER7kfTD9w2To4CQk6UCfuHM9c6GDY")
        .isTest(true)
        .build();

    String expectedAddress = Utils.encodeXAddress(classicAddress);

    stubFor(get(urlEqualTo("/georgewashington"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/xrpl-mainnet+json")
            .withBody("{ "
                + "addresses: [{"
                + "addressDetailsType: 'CryptoAddressDetails', "
                + "addressDetails: { "
                + "address: '" + classicAddress.address() + "' "
                + "}"
                + "}]"
                + "}"
            )
        )
    );

    // WHEN an XRP address is requested.
    String address = client.xrpAddressForPayID(payID);

    // THEN the address is the X-Address encoded version of the response.
    assertEquals(address, expectedAddress);
  }

  @Test
  public void testXRPAddressForPayIDSuccessWithClassicAddressWithTag() throws PayIDException {
    // GIVEN a PayID client, a valid PayID and mocked networking to return an classic address with a tag.
    String payID = "georgewashington$localhost:" + wireMockRule.httpsPort();
    XRPPayIDClient client = new XRPPayIDClient(XRPLNetwork.TEST);
    client.setEnableSSLVerification(false);

    ClassicAddress classicAddress = ImmutableClassicAddress.builder()
        .address("rPEPPER7kfTD9w2To4CQk6UCfuHM9c6GDY")
        .tag(12345)
        .isTest(true)
        .build();

    String expectedAddress = Utils.encodeXAddress(classicAddress);

    stubFor(get(urlEqualTo("/georgewashington"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/xrpl-mainnet+json")
            .withBody("{ "
                + "addresses: [{"
                + "addressDetailsType: 'CryptoAddressDetails', "
                + "addressDetails: { "
                + "address: '" + classicAddress.address() + "', "
                + "tag: '" + classicAddress.tag().get() + "' "
                + "}"
                + "}]"
                + "}"
            )
        )
    );

    // WHEN an XRP address is requested.
    String address = client.xrpAddressForPayID(payID);

    // THEN the address is the X-Address encoded version of the response.
    assertEquals(address, expectedAddress);
  }

  @Test
  public void testXRPAddressForPayIDMatchNotFound() throws PayIDException {
    // GIVEN a PayID client, valid PayID and mocked networking to return a 404 for the payID.
    final String payID = "georgewashington$localhost:" + wireMockRule.httpsPort();
    XRPPayIDClient client = new XRPPayIDClient(XRPLNetwork.MAIN);
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
    final String payID = "georgewashington$localhost:" + wireMockRule.httpsPort();
    XRPPayIDClient client = new XRPPayIDClient(XRPLNetwork.MAIN);
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
    final String payID = "georgewashington$localhost:" + wireMockRule.httpsPort();
    XRPPayIDClient client = new XRPPayIDClient(XRPLNetwork.MAIN);
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

  @Test
  public void testXRPAddressForPayIdMultipleAddressesReturned() throws PayIDException {
    // GIVEN a PayID client, a valid PayID and mocked networking to return multiple addresses.
    String payID = "georgewashington$localhost:" + wireMockRule.httpsPort();
    XRPPayIDClient client = new XRPPayIDClient(XRPLNetwork.TEST);
    client.setEnableSSLVerification(false);

    ClassicAddress classicAddress = ImmutableClassicAddress.builder()
            .address("rPEPPER7kfTD9w2To4CQk6UCfuHM9c6GDY")
            .isTest(true)
            .build();

    String expectedAddress = Utils.encodeXAddress(classicAddress);

    stubFor(get(urlEqualTo("/georgewashington"))
            .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/xrpl-mainnet+json")
                    .withBody("{ "
                            + "addresses: [{"
                            + "addressDetailsType: 'CryptoAddressDetails', "
                            + "addressDetails: { "
                            + "address: '" + classicAddress.address() + "' "
                            + "}"
                            + "}, "
                            + "{"
                            + "addressDetailsType: 'CryptoAddressDetails', "
                            + "addressDetails: { "
                            + "address: '" + classicAddress.address() + "' "
                            + "}"
                            + "}"
                            + "]"
                            + "}"
                    )
            )
    );

    // WHEN an XRPAddress is requested THEN a unexpected response error is thrown.
    expectedException.expect(PayIDException.class);
    client.xrpAddressForPayID(payID);
  }
}

