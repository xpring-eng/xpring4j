package io.xpring.payid;

import okhttp3.HttpUrl;

public class ManualModePayIDResolver implements PayIDResolver {
  @Override
  public HttpUrl resolvePayIDUrl(PayID payID) throws PayIDDiscoveryException {
    return new HttpUrl.Builder()
      .scheme("https")
      .host(payID.host())
      .addPathSegment(payID.account())
      .build();
  }
}
