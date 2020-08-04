package io.xpring.xrpl.model;

import io.xpring.xrpl.Wallet;
import org.immutables.value.Value;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

/**
 * Describes the fine grained details for sending money over the XRP ledger. The
 * destination field may be a PayID, XAddress, or other type of address. Handling
 * of the given destination type is the responsibility of the client.
 */
@Value.Immutable
public interface SendXrpDetails {
  static ImmutableSendXrpDetails.Builder builder() {
    return ImmutableSendXrpDetails.builder();
  }

  BigInteger amount();
  String destination();
  Wallet sender();
  Optional<List<XrpMemo>> memos();
}