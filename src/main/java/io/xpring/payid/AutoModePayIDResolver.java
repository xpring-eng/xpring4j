package io.xpring.payid;

import static okhttp3.CookieJar.NO_COOKIES;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.HttpHeaders;
import okhttp3.ConnectionSpec;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Optional;

/**
 * Implementation of {@link PayIDResolver} which resolves {@link PayID} to URLs using
 * automated mode.
 *
 * @see "https://github.com/xpring-eng/rfcs/blob/master/payid/src/spec/payid-discovery.md#automated-mode"
 */
public class AutoModePayIDResolver implements PayIDResolver {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  protected static final String WEBFINGER_URL = ".well-known/webfinger";
  private OkHttpClient okHttpClient;
  private ObjectMapper objectMapper;

  /**
   * No-args constructor.
   */
  public AutoModePayIDResolver() {
    this(newOkHttpClient(), new ObjectMapper());
  }

  /**
   * Required args constructor.  Initializes with a default {@link ObjectMapper}.
   *
   * @param okHttpClient An {@link OkHttpClient} to be used by this resolver.
   */
  public AutoModePayIDResolver(OkHttpClient okHttpClient) {
    this(okHttpClient, new ObjectMapper());
  }

  /**
   * Required args constructor.
   *
   * @param okHttpClient An {@link OkHttpClient} to be used by this resolver.
   * @param objectMapper An {@link ObjectMapper} to be used by this resolver.
   */
  public AutoModePayIDResolver(OkHttpClient okHttpClient, ObjectMapper objectMapper) {
    this.okHttpClient = okHttpClient;
    this.objectMapper = objectMapper;
  }

  /**
   * Resolves a {@link PayID} to an {@link HttpUrl} using automated mode.
   *
   * <p>
   *   This implementation recursively sends WebFinger GET requests to the PayID host
   *   until a non-WebFinger URL is returns as the href of the WebFinger Link.  This means that
   *   WebFinger servers can delegate PayID resolution to different hosts.
   * </p>
   *
   * @param payID A {@link PayID} to resolve to an {@link HttpUrl}.
   * @return The {@link Optional} of {@link HttpUrl} of the PayID server for a given PayID, or {@link Optional#empty()}
   *          if there is no WebFinger server running at the PayID's host URL.
   */
  @Override
  public Optional<HttpUrl> resolveHttpUrl(PayID payID) {
    try {
      Optional<WebFingerLink> webFingerLink = this.getWebFingerPayIDLink(payID);

      // Recurse through webfinger href responses until either the webfinger redirect doesn't exist or until
      // we get a non webfinger href URL, in which case we can infer that the href is a PayID server URL.
      while (webFingerLink.isPresent() && webFingerLink.get().href().endsWith(WEBFINGER_URL)) {
        webFingerLink = this.getWebFingerPayIDLink(HttpUrl.parse(webFingerLink.get().href()));
      }

      // On the last webfinger call, the webfinger href was invalid or doesnt exist, in which case we should fall back
      // to manual mode resolution.
      Optional<WebFingerLink> finalWebFingerLink = webFingerLink;
      return finalWebFingerLink
        .map(link -> {
          // webfinger href for payid servers may or may not be templatized.  This should expand the template if it is a
          // template, otherwise will just return the href.
          UriTemplate uriTemplate = new UriTemplate(link.href());
          URI expandedTemplate = uriTemplate.expand(payID.account());
          return HttpUrl.parse(expandedTemplate.toString());
        });
    } catch (JsonProcessingException e) {
      logger.warn("Unable to deserialize WebFinger JRD! message: {}", e.getMessage());
      return Optional.empty();
    }
  }

  /**
   * Get a WebFinger Link for a given PayID.
   *
   * @param payID The {@link PayID} whose host should be queried for a WebFinger Link
   * @return A
   * @throws JsonProcessingException
   */
  protected Optional<WebFingerLink> getWebFingerPayIDLink(PayID payID) throws JsonProcessingException {
    HttpUrl webfingerUrl = new HttpUrl.Builder()
      .scheme("https")
      .host(payID.host())
      .addEncodedPathSegments(WEBFINGER_URL)
      .addQueryParameter("resource", payID.toString())
      .build();

    return this.getWebFingerPayIDLink(webfingerUrl);
  }

  protected Optional<WebFingerLink> getWebFingerPayIDLink(HttpUrl webfingerUrl) throws JsonProcessingException {
    Optional<String> jrdString = this.executeForJrdString(webfingerUrl);
    if (jrdString.isPresent()) {
      WebFingerJrd typedJrd = objectMapper.readValue(jrdString.get(), WebFingerJrd.class);
      return typedJrd.links().stream()
        .filter(link -> link.rel().equals("http://payid.org/rel/discovery/1.0"))
        .findFirst();
    }

    return Optional.empty();
  }

  protected Optional<String> executeForJrdString(HttpUrl webfingerUrl) {
    Request webfingerRequest = new Request.Builder()
      .header(HttpHeaders.CONTENT_TYPE, "application/json")
      .header(HttpHeaders.ACCEPT, "application/json")
      .url(webfingerUrl)
      .get()
      .build();

    try (Response response = this.okHttpClient.newCall(webfingerRequest).execute()) {
      // Auto mode not enabled
      if (response.code() == 404) {
        return Optional.empty();
      }

      ResponseBody body = response.body();
      if (body == null) {
        return Optional.empty();
      }

      return Optional.of(body.string());
    } catch (IOException e) {
      logger.warn("Failed to execute WebFingerRequest. message: {}", e.getMessage());
      return Optional.empty();
    }
  }

  private static OkHttpClient newOkHttpClient() {
    OkHttpClient.Builder builder = new OkHttpClient.Builder();
    ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS).build();
    builder.connectionSpecs(Arrays.asList(spec, ConnectionSpec.CLEARTEXT));
    builder.cookieJar(NO_COOKIES);

    return builder.build();
  }
}
