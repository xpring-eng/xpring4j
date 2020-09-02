package io.xpring.xrpl.model;

import org.immutables.value.Value;
import org.xrpl.rpc.v1.SetRegularKey;

import java.util.Optional;

/**
 * Represents a SetRegularKey transaction on the XRP Ledger.
 * <p>
 * A SetRegularKey transaction assigns, changes, or removes the regular key pair associated with an account.
 * You can protect your account by assigning a regular key pair to it and using it instead of the master key
 * pair to sign transactions whenever possible. If your regular key pair is compromised, but your master key
 * pair is not, you can use a SetRegularKey transaction to regain control of your account.
 * </p>
 *
 * @see "https://xrpl.org/setregularkey.html"
 */
@Value.Immutable
public interface XrpSetRegularKey {
  static ImmutableXrpSetRegularKey.Builder builder() {
    return ImmutableXrpSetRegularKey.builder();
  }

  /**
   * (Optional) A base-58-encoded Address that indicates the regular key pair to be assigned to the account.
   * <p>
   * If omitted, removes any existing regular key pair from the account.
   * Must not match the master key pair for the address.
   * </p>
   *
   * @return A {@link String} containing a base-58-encoded Address that indicates the regular key pair to be
   *         assigned to the account.
   */
  Optional<String> regularKey();

  /**
   * Constructs an {@link XrpSetRegularKey} from a {@link SetRegularKey} protocol buffer.
   *
   * @param setRegularKey A {@link SetRegularKey} (protobuf object) whose field values will be used to construct
   *                      an {@link XrpSetRegularKey}.
   * @return An {@link XrpSetRegularKey} with its fields set via the analogous protobuf fields.
   * @see "https://github.com/ripple/rippled/blob/3d86b49dae8173344b39deb75e53170a9b6c5284/src/ripple/proto/org/xrpl/rpc/v1/transaction.proto#L298"
   */
  static XrpSetRegularKey from(SetRegularKey setRegularKey) {
    final Optional<String> regularKey = setRegularKey.hasRegularKey()
        ? Optional.of(setRegularKey.getRegularKey().getValue().getAddress())
        : Optional.empty();

    return XrpSetRegularKey.builder()
        .regularKey(regularKey)
        .build();
  }
}
