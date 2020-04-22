package io.xpring.payid;

import static io.xpring.payid.InteractiveModePayIDResolver.DISCOVERY_URL;
import static io.xpring.payid.InteractiveModePayIDResolver.PAY_ID_URL;
import static io.xpring.payid.InteractiveModePayIDResolver.WEBFINGER_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.xpring.common.ObjectMapperFactory;
import okhttp3.HttpUrl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Optional;

@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public class InteractiveModePayIDResolverTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  private InteractiveModePayIDResolver interactiveModePayIDResolver;
  private ObjectMapper objectMapper;

  /**
   * Set up the tests.
   */
  @Before
  public void setUp() {
    initMocks(this);
    objectMapper = ObjectMapperFactory.create();
    interactiveModePayIDResolver = Mockito.spy(new InteractiveModePayIDResolver());
  }

  /**
   * Test that a call to {@link InteractiveModePayIDResolver#resolvePayIDUrl(PayID)} with a mocked WebFinger server which
   * returns a PayID URL resolves correctly.
   *
   * @throws IOException if the mock response we want is not serializable.
   */
  @Test
  public void resolvePayIDUrlOneRedirect() throws IOException {
    String payIDUrl = "https://doug.purdy.im/pay";
    WebFingerJrd webFingerResponse = WebFingerJrd.builder()
        .subject("payid.ml")
        .addLinks(WebFingerLink.builder()
          .rel(PAY_ID_URL)
          .href(payIDUrl)
          .build())
        .build();


    PayID payID = PayID.of("payid:doug$payid.ml");

    doReturn(objectMapper.writeValueAsString(webFingerResponse))
        .when(interactiveModePayIDResolver)
        .executeForJrdString(any());

    HttpUrl httpUrl = interactiveModePayIDResolver.resolvePayIDUrl(payID);
    verify(interactiveModePayIDResolver, times(1)).executeForJrdString(any());
    assertThat(httpUrl.toString()).isEqualTo(payIDUrl);
  }

  /**
   * Test that a call to {@link InteractiveModePayIDResolver#resolvePayIDUrl(PayID)} with a mocked WebFinger server which
   * returns another WebFinger URL correctly recurses to a WebFinger server which returns a PayID URL, which is resolved
   * correctly.
   *
   * @throws IOException if the mock response we want is not serializable.
   */
  @Test
  public void resolvePayIDUrlTwoRedirects() throws JsonProcessingException {
    String ultimatePayIDUrl = "https://doug.purdy.im/pay";
    String firstPayIDUrl = "https://doug.purdy.im/.well-known/webfinger";
    PayID payID = PayID.of("payid:doug$payid.ml");

    WebFingerJrd firstWebFingerResponse = WebFingerJrd.builder()
        .subject("payid.ml")
        .addLinks(WebFingerLink.builder()
          .rel(DISCOVERY_URL)
          .href(firstPayIDUrl)
          .build())
        .build();

    WebFingerJrd secondWebFingerResponse = WebFingerJrd.builder()
        .subject("payid.ml")
        .addLinks(WebFingerLink.builder()
          .rel(PAY_ID_URL)
          .href(ultimatePayIDUrl)
          .build())
        .build();

    HttpUrl firstWebfingerUrl = new HttpUrl.Builder()
        .scheme("https")
        .host(payID.host())
        .addEncodedPathSegments(WEBFINGER_URL)
        .addQueryParameter("resource", payID.toString())
        .build();

    doReturn(objectMapper.writeValueAsString(firstWebFingerResponse))
        .when(interactiveModePayIDResolver)
        .executeForJrdString(firstWebfingerUrl);
    doReturn(objectMapper.writeValueAsString(secondWebFingerResponse))
        .when(interactiveModePayIDResolver)
        .executeForJrdString(HttpUrl.parse(firstPayIDUrl));

    HttpUrl httpUrl = interactiveModePayIDResolver.resolvePayIDUrl(payID);
    verify(interactiveModePayIDResolver, times(2)).executeForJrdString(any());
    assertThat(httpUrl.toString()).isEqualTo(ultimatePayIDUrl);
  }

  /**
   * Test that a call to {@link InteractiveModePayIDResolver#resolvePayIDUrl(PayID)} with a mocked WebFinger server which
   * returns a PayID URL with a URL template resolves correctly and expands the template with the account part of
   * the PayID.
   *
   * @throws IOException if the mock response we want is not serializable.
   */
  @Test
  public void resolvePayIDUrlExpandsUrlTemplate() throws JsonProcessingException {
    String payIDUrl = "https://doug.purdy.im/pay";
    WebFingerJrd webFingerResponse = WebFingerJrd.builder()
        .subject("payid.ml")
        .addLinks(WebFingerLink.builder()
          .rel(PAY_ID_URL)
          .template(payIDUrl + "/{acctpart}")
          .build())
        .build();

    PayID payID = PayID.of("payid:doug$payid.ml");

    doReturn(objectMapper.writeValueAsString(webFingerResponse))
        .when(interactiveModePayIDResolver)
        .executeForJrdString(any());

    HttpUrl httpUrl = interactiveModePayIDResolver.resolvePayIDUrl(payID);
    verify(interactiveModePayIDResolver, times(1)).executeForJrdString(any());
    assertThat(httpUrl.toString()).isEqualTo(payIDUrl + "/doug");
  }

  /**
   * Test that a call to {@link InteractiveModePayIDResolver#resolvePayIDUrl(PayID)} which makes WebFinger requests to a server
   * that doesn't exist or does not have a mapping for the PayID returns {@link Optional#empty()}.
   *
   * @throws IOException if the mock response we want is not serializable.
   */
  @Test
  public void resolvePayIDUrlNoJrdAvailable() {
    doThrow(PayIDDiscoveryException.class).when(interactiveModePayIDResolver).executeForJrdString(any());

    PayID payID = PayID.of("payid:doug$payid.ml");

    expectedException.expect(PayIDDiscoveryException.class);
    interactiveModePayIDResolver.resolvePayIDUrl(payID);
    verify(interactiveModePayIDResolver, times(1)).executeForJrdString(any());
  }

  /**
   * Test that a call to {@link InteractiveModePayIDResolver#resolvePayIDUrl(PayID)} with a mocked WebFinger server which
   * returns a JRD which has no link with rel="http://payid.org/rel/discovery/1.0" returns {@link Optional#empty()}.
   *
   * @throws IOException if the mock response we want is not serializable.
   */
  @Test
  public void resolvePayIDUrlJrdAvailableButNoMatchingLink() throws JsonProcessingException {
    String payIDUrl = "https://doug.purdy.im/pay";
    WebFingerJrd webFingerResponse = WebFingerJrd.builder()
        .subject("payid.ml")
        .addLinks(WebFingerLink.builder()
          .rel("http://this.is.not.the.rel.you.are.looking.for")
          .href(payIDUrl + "/{acctpart}")
          .build())
        .build();


    PayID payID = PayID.of("payid:doug$payid.ml");

    doReturn(objectMapper.writeValueAsString(webFingerResponse))
        .when(interactiveModePayIDResolver)
        .executeForJrdString(any());

    expectedException.expect(PayIDDiscoveryException.class);
    interactiveModePayIDResolver.resolvePayIDUrl(payID);
    verify(interactiveModePayIDResolver, times(1)).executeForJrdString(any());
  }
}
