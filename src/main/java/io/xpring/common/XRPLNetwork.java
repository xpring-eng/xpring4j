package io.xpring.common;

import io.xpring.common.idiomatic.XrplNetwork;

/**
 * Possible networks to resolve.
 *
 * @deprecated Pleaseuse the idiomatically named {@link XrplNetwork} enum instead.
 */
@Deprecated
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public enum XRPLNetwork {
  DEV("devnet"),
  TEST("testnet"),
  MAIN("mainnet");

  private String networkName;

  XRPLNetwork(String networkName) {
    this.networkName = networkName;
  }

  public String getNetworkName() {
    return networkName;
  }
}
