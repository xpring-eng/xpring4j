package io.xpring.payid;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;

import okhttp3.HttpUrl;
import org.junit.Before;
import org.junit.Test;

public class ManualModePayIDResolverTest {

  private PayIDResolver manualPayIdResolver;

  @Before
  public void setUp() {
    manualPayIdResolver = new ManualModePayIDResolver();
  }

  @Test
  public void resolvesSimplePayID() {
    PayID payID = PayID.of("payid:alice$xpring.money");
    HttpUrl resolvedUrl = manualPayIdResolver.resolvePayIDUrl(payID);

    HttpUrl expectedUrl = HttpUrl.parse("https://xpring.money/alice");
    assertThat(resolvedUrl).isEqualTo(expectedUrl);
  }

  @Test
  public void resolvesPayIDWithTwoDollarSigns() {
    PayID payID = PayID.of("payid:alice$example.com$xpring.money");
    HttpUrl resolvedUrl = manualPayIdResolver.resolvePayIDUrl(payID);

    HttpUrl expectedUrl = HttpUrl.parse("https://xpring.money/alice$example.com");
    assertThat(resolvedUrl).isEqualTo(expectedUrl);
  }
}
