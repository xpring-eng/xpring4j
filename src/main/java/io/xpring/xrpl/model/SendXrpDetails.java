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

  /**
   * The amount of XRP, in drops, to send.
   *
   * @return A {@link BigInteger} representing the drops of XRP to send.
   */
  BigInteger amount();

  /**
   * The receiving address.
   *
   * @return A {@link String} representing the address receiving the payment.
   */
  String destination();

  /**
   * The sending wallet.
   *
   * @return A {@link Wallet} that will sign and submit the transaction.
   */
  Wallet sender();

  /**
   * A list of memos to attach to the payment transaction constructed from these details.
   *
   * @return An Optional {@link List<XrpMemo>} representing the memos to attached to the payment
   *         transaction constructed from these details.
   */
  Optional<List<XrpMemo>> memosList();
}