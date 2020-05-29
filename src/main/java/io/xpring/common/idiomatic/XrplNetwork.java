package io.xpring.common.idiomatic;

/**
 * Possible networks to resolve.
 */
public enum XrplNetwork {
  DEV("devnet"),
  TEST("testnet"),
  MAIN("mainnet");

  private String networkName;

  XrplNetwork(String networkName) {
    this.networkName = networkName;
  }

  public String getNetworkName() {
    return networkName;
  }
}
