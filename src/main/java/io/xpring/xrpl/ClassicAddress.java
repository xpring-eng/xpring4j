package io.xpring.xrpl;

import org.immutables.value.Value;

import java.util.Optional;

/**
 * Represents classic address components on the XRP Ledger.
 */
@Value.Immutable
public interface ClassicAddress {

  static ImmutableClassicAddress.Builder builder() {
    return ImmutableClassicAddress.builder();
  }

  /**
   * The address component of the classic address.
   *
   * @return A {@link String} representing the address component of the classic address.
   */
  public String address();

  /**
   * The tag component of the classic address.
   *
   * @return An {@link Optional} of {@link Integer} representing the tag component of the classic address.
   */
  public Optional<Integer> tag();

  /**
   * Whether this address is for use on a test network.
   *
   * @return A boolean indicating whether this address is for use on a test network.
   */
  public boolean isTest();

}
