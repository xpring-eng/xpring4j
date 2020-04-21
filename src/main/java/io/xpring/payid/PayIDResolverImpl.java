package io.xpring.payid;

import okhttp3.HttpUrl;

// TODO: Rename this class to something better
public class PayIDResolverImpl implements PayIDResolver {

  private PayIDResolver autoModePayIDResolver;
  private PayIDResolver manualModePayIDResolver;

  public PayIDResolverImpl() {
    this(new AutoModePayIDResolver(), new ManualModePayIDResolver());
  }

  public PayIDResolverImpl(PayIDResolver autoModePayIDResolver, PayIDResolver manualModePayIDResolver) {
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
