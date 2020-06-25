package io.xpring.payid;

import io.xpring.common.XRPLNetwork;

/**
 * An interface for a PayID client.
 *
 * @deprecated Use the idiomatically named `XrpPayIdClientInterface` interface instead.
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
@Deprecated
public interface XRPPayIDClientInterface {
  /**
   * The {@link XRPLNetwork} that addresses will be resolved on.
   *
   * @return The {@link XRPLNetwork} that addresses will be resolved on.
   */
  XRPLNetwork getXRPLNetwork();

  /**
   * Resolve the given PayID to an XRP Address.
   *
   * @param payID The payID to resolve for an address.
   * @return An XRP address representing the given PayID.
   * @throws PayIDException if the inputs were invalid.
   */
  String xrpAddressForPayID(String payID) throws PayIDException;
}
