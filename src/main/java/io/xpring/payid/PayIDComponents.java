package io.xpring.payid;

import org.immutables.value.Value;

/**
 * Represents components of a PayID.
 */
@Value.Immutable
public interface PayIDComponents {
  /**
   * The address component of the classic address.
   */
  String host();

  /**
   * The tag component of the classic address.
   */
  String path();
}
