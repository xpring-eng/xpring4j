package io.xpring.payid;

import okhttp3.HttpUrl;

public class DefaultPayIDResolver implements PayIDResolver {

  private PayIDResolver autoModePayIDResolver;
  private PayIDResolver manualModePayIDResolver;

  public DefaultPayIDResolver() {
    this(new AutoModePayIDResolver(), new ManualModePayIDResolver());
  }

  public DefaultPayIDResolver(PayIDResolver autoModePayIDResolver, PayIDResolver manualModePayIDResolver) {
    this.autoModePayIDResolver = autoModePayIDResolver;
    this.manualModePayIDResolver = manualModePayIDResolver;
  }

  @Override
  public HttpUrl resolvePayIDUrl(PayID payID) throws PayIDDiscoveryException {
    try {
      return autoModePayIDResolver.resolvePayIDUrl(payID);
    } catch (PayIDDiscoveryException e) {
      return manualModePayIDResolver.resolvePayIDUrl(payID);
    }
  }
}
