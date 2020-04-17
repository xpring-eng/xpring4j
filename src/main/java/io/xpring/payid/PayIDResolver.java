package io.xpring.payid;

import okhttp3.HttpUrl;

public interface PayIDResolver {

  /**
   * Resolves a {@link PayID} to it's endpoint URL.
   *
   * @param payID A {@link PayID} to resolve to an {@link HttpUrl}.
   *
   * @return The {@link HttpUrl} that {@code payID} resolves to.
   *
   * @see "https://github.com/xpring-eng/rfcs/blob/master/payid/src/spec/payid-discovery.md"
   */
  HttpUrl resolveHttpUrl(PayID payID);

}
