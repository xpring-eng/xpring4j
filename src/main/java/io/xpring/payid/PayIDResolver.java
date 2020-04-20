package io.xpring.payid;

import okhttp3.HttpUrl;

import java.util.Optional;

/**
 * Defines how to resolve a PayID to a URL.
 *
 * @see "https://github.com/xpring-eng/rfcs/blob/master/payid/src/spec/payid-discovery.md"
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public interface PayIDResolver {

  /**
   * Resolves a {@link PayID} to its endpoint URL.
   *
   * @param payID A {@link PayID} to resolve to an {@link HttpUrl}.
   * @return The {@link HttpUrl} that {@code payID} resolves to.
   * @throws PayIDDiscoveryException if the inputs were invalid or the PayID was unresolvable.
   * @see "https://github.com/xpring-eng/rfcs/blob/master/payid/src/spec/payid-discovery.md"
   */
  Optional<HttpUrl> resolvePayIDUrl(PayID payID) throws PayIDDiscoveryException;

}
