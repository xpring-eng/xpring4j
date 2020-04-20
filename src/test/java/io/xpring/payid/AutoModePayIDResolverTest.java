package io.xpring.payid;

import static io.xpring.payid.AutoModePayIDResolver.WEBFINGER_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.HttpUrl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Optional;

public class AutoModePayIDResolverTest {

  private AutoModePayIDResolver autoModePayIDResolver;
  private ObjectMapper objectMapper;

  @Before
  public void setUp() {
    initMocks(this);
    objectMapper = new ObjectMapper();
    autoModePayIDResolver = Mockito.spy(new AutoModePayIDResolver());
  }

  /**
   * Test that a call to {@link AutoModePayIDResolver#resolveHttpUrl(PayID)} with a mocked WebFinger server which
   * returns a PayID URL resolves correctly.
   *
   * @throws IOException if the mock response we want is not serializable.
   */
  @Test
  public void resolveHttpUrlOneRedirect() throws IOException {
    String payIDUrl = "https://doug.purdy.im/pay";
    WebFingerJrd webFingerResponse = WebFingerJrd.builder()
      .subject("payid.ml")
      .addLinks(WebFingerLink.builder()
        .rel("http://payid.org/rel/discovery/1.0")
        .type("application/payid-uri-template")
        .href(payIDUrl)
        .build())
      .build();


    PayID payID = PayID.of("payid:doug$payid.ml");

    doReturn(Optional.of(objectMapper.writeValueAsString(webFingerResponse))).when(autoModePayIDResolver).executeForJrdString(any());

    Optional<HttpUrl> httpUrl = autoModePayIDResolver.resolveHttpUrl(payID);
    verify(autoModePayIDResolver, times(1)).executeForJrdString(any());
    assertThat(httpUrl.get().toString()).isEqualTo(payIDUrl);
  }

  /**
   * Test that a call to {@link AutoModePayIDResolver#resolveHttpUrl(PayID)} with a mocked WebFinger server which
   * returns another WebFinger URL correctly recurses to a WebFinger server which returns a PayID URL, which is resolved
   * correctly.
   *
   * @throws IOException if the mock response we want is not serializable.
   */
  @Test
  public void resolveHttpUrlTwoRedirects() throws JsonProcessingException {
    String ultimatePayIDUrl = "https://doug.purdy.im/pay";
    String firstPayIDUrl = "https://doug.purdy.im/.well-known/webfinger";
    PayID payID = PayID.of("payid:doug$payid.ml");

    WebFingerJrd firstWebFingerResponse = WebFingerJrd.builder()
      .subject("payid.ml")
      .addLinks(WebFingerLink.builder()
        .rel("http://payid.org/rel/discovery/1.0")
        .type("application/payid-uri-template")
        .href(firstPayIDUrl)
        .build())
      .build();

    WebFingerJrd secondWebFingerResponse = WebFingerJrd.builder()
      .subject("payid.ml")
      .addLinks(WebFingerLink.builder()
        .rel("http://payid.org/rel/discovery/1.0")
        .type("application/payid-uri-template")
        .href(ultimatePayIDUrl)
        .build())
      .build();

    HttpUrl firstWebfingerUrl = new HttpUrl.Builder()
      .scheme("https")
      .host(payID.host())
      .addEncodedPathSegments(WEBFINGER_URL)
      .addQueryParameter("resource", payID.toString())
      .build();

    doReturn(Optional.of(objectMapper.writeValueAsString(firstWebFingerResponse))).when(autoModePayIDResolver).executeForJrdString(firstWebfingerUrl);
    doReturn(Optional.of(objectMapper.writeValueAsString(secondWebFingerResponse))).when(autoModePayIDResolver).executeForJrdString(HttpUrl.parse(firstPayIDUrl));
    Optional<HttpUrl> httpUrl = autoModePayIDResolver.resolveHttpUrl(payID);
    verify(autoModePayIDResolver, times(2)).executeForJrdString(any());
    assertThat(httpUrl.get().toString()).isEqualTo(ultimatePayIDUrl);
  }

  /**
   * Test that a call to {@link AutoModePayIDResolver#resolveHttpUrl(PayID)} with a mocked WebFinger server which
   * returns a PayID URL with a URL template resolves correctly and expands the template with the account part of
   * the PayID.
   *
   * @throws IOException if the mock response we want is not serializable.
   */
  @Test
  public void resolveHttpUrlExpandsUrlTemplate() throws JsonProcessingException {
    String payIDUrl = "https://doug.purdy.im/pay";
    WebFingerJrd webFingerResponse = WebFingerJrd.builder()
      .subject("payid.ml")
      .addLinks(WebFingerLink.builder()
        .rel("http://payid.org/rel/discovery/1.0")
        .type("application/payid-uri-template")
        .href(payIDUrl + "/{acctpart}")
        .build())
      .build();


    PayID payID = PayID.of("payid:doug$payid.ml");

    doReturn(Optional.of(objectMapper.writeValueAsString(webFingerResponse))).when(autoModePayIDResolver).executeForJrdString(any());

    Optional<HttpUrl> httpUrl = autoModePayIDResolver.resolveHttpUrl(payID);
    verify(autoModePayIDResolver, times(1)).executeForJrdString(any());
    assertThat(httpUrl.get().toString()).isEqualTo(payIDUrl + "/doug");
  }

  /**
   * Test that a call to {@link AutoModePayIDResolver#resolveHttpUrl(PayID)} which makes WebFinger requests to a server
   * that doesn't exist or does not have a mapping for the PayID returns {@link Optional#empty()}.
   *
   * @throws IOException if the mock response we want is not serializable.
   */
  @Test
  public void resolveHttpUrlNoJrdAvailable() {
    doReturn(Optional.empty()).when(autoModePayIDResolver).executeForJrdString(any());

    PayID payID = PayID.of("payid:doug$payid.ml");
    Optional<HttpUrl> httpUrl = autoModePayIDResolver.resolveHttpUrl(payID);
    verify(autoModePayIDResolver, times(1)).executeForJrdString(any());
    assertThat(httpUrl).isEmpty();
  }

  /**
   * Test that a call to {@link AutoModePayIDResolver#resolveHttpUrl(PayID)} with a mocked WebFinger server which
   * returns a JRD which has no link with rel="http://payid.org/rel/discovery/1.0" returns {@link Optional#empty()}.
   *
   * @throws IOException if the mock response we want is not serializable.
   */
  @Test
  public void resolveHttpUrlJrdAvailableButNoMatchingLink() throws JsonProcessingException {
    String payIDUrl = "https://doug.purdy.im/pay";
    WebFingerJrd webFingerResponse = WebFingerJrd.builder()
      .subject("payid.ml")
      .addLinks(WebFingerLink.builder()
        .rel("http://this.is.not.the.rel.you.are.looking.for")
        .type("application/payid-uri-template")
        .href(payIDUrl + "/{acctpart}")
        .build())
      .build();


    PayID payID = PayID.of("payid:doug$payid.ml");

    doReturn(Optional.of(objectMapper.writeValueAsString(webFingerResponse))).when(autoModePayIDResolver).executeForJrdString(any());

    Optional<HttpUrl> httpUrl = autoModePayIDResolver.resolveHttpUrl(payID);
    verify(autoModePayIDResolver, times(1)).executeForJrdString(any());
    assertThat(httpUrl).isEmpty();
  }
}
