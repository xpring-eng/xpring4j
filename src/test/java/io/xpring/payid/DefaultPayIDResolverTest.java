package io.xpring.payid;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import okhttp3.HttpUrl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class DefaultPayIDResolverTest {

  private PayIDResolver defaultPayIDResolver;

  @Mock
  private InteractiveModePayIDResolver interactiveModePayIDResolver;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    defaultPayIDResolver = new DefaultPayIDResolver(interactiveModePayIDResolver, new ManualModePayIDResolver());
  }

  @Test
  public void successfulInteractiveDiscovery() {
    PayID payID = PayID.of("payid:alice$xpring.money");
    HttpUrl interactiveResolvedUrl = HttpUrl.parse("https://xpring.money/?user=alice");
    when(interactiveModePayIDResolver.resolvePayIDUrl(eq(payID))).thenReturn(interactiveResolvedUrl);

    HttpUrl resolvedPayIDUrl = defaultPayIDResolver.resolvePayIDUrl(payID);
    assertThat(resolvedPayIDUrl).isEqualTo(interactiveResolvedUrl);
  }

  @Test
  public void failedInteractiveDiscoveryTriggersFallback() {
    PayID payID = PayID.of("payid:alice$xpring.money");
    when(interactiveModePayIDResolver.resolvePayIDUrl(eq(payID))).thenThrow(PayIDDiscoveryException.class);

    HttpUrl resolvedPayIDUrl = defaultPayIDResolver.resolvePayIDUrl(payID);
    assertThat(resolvedPayIDUrl).isEqualTo(HttpUrl.parse("https://xpring.money/alice"));
  }
}
