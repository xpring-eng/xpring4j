package io.xpring.xrpl.model;

import io.xpring.xrpl.model.ImmutableXrpAccountSet;
import org.immutables.value.Value;
import org.xrpl.rpc.v1.AccountSet;

import java.util.Optional;

/**
 * Represents an AccountSet transaction on the XRP Ledger.
 * <p>
 * An AccountSet transaction modifies the properties of an account in the XRP Ledger.
 * </p>
 *
 * @see "https://xrpl.org/accountset.htm"
 */
@Value.Immutable
public interface XrpAccountSet {
  static ImmutableXrpAccountSet.Builder builder() {
    return ImmutableXrpAccountSet.builder();
  }

  /**
   * (Optional) Unique identifier of a flag to disable for this account.
   *
   * @return An {@link Integer} identifier of a flag to disable for this account.
   */
  Optional<Integer> clearFlag();

  /**
   * (Optional) The domain that owns this account, as a string of hex representing
   * the ASCII for the domain in lowercase.
   *
   * @return A {@link String} representing the domain that owns this account.
   */
  Optional<String> domain();

  /**
   * (Optional) Hash of an email address to be used for generating an avatar image.
   *
   * @return A byte array containing the hash value of an email address to be used for generating an avatar image.
   */
  @Value.Default
  default byte[] emailHash() {
    return new byte[0];
  }

  /**
   * (Optional) Public key for sending encrypted messages to this account.
   *
   * @return A byte array containing the public key for sending encrypted messages to this account.
   */
  @Value.Default
  default byte[] messageKey() {
    return new byte[0];
  }

  /**
   * (Optional) Integer flag to enable for this account.
   *
   * @return An {@link Integer} flag to enable for this account.
   */
  Optional<Integer> setFlag();

  /**
   * (Optional) Tick size to use for offers involving a currency issued by this address.
   * <p>
   * The exchange rates of those offers is rounded to this many significant digits.
   * Valid values are 3 to 15 inclusive, or 0 to disable. (Requires the TickSize amendment.)
   * </p>
   *
   * @return An integer representing the tick size to use for offers involving a currency issued by this address.
   */
  Optional<Integer> tickSize();

  /**
   * (Optional) The fee to charge when users transfer this account's issued currencies,
   * represented as billionths of a unit.
   * <p>
   * Cannot be more than 2000000000 or less than 1000000000, except for the special case
   * 0 meaning no fee.
   * </p>
   *
   * @return An {@link Integer} representing the fee
   *         to charge when users transfer this account's issued currencies, represented as billionths of a unit.
   */
  Optional<Integer> transferRate();

  /**
   * Constructs an {@link XrpAccountSet} from a {@link AccountSet}.
   *
   * @param accountSet A {@link AccountSet} (protobuf object) whose field values will be used to
   *                   construct an {@link XrpAccountSet}
   * @return An {@link XrpAccountSet} with its fields set via the analogous protobuf fields.
   * @see <a href="https://github.com/ripple/rippled/blob/develop/src/ripple/proto/org/xrpl/rpc/v1/transaction.proto#L100">
   * AccountSet protocol buffer</a>
   */
  static XrpAccountSet from(AccountSet accountSet) {
    final Optional<Integer> clearFlag = accountSet.hasClearFlag()
        ? Optional.of(accountSet.getClearFlag().getValue())
        : Optional.empty();

    final Optional<String> domain = accountSet.hasDomain()
        ? Optional.of(accountSet.getDomain().getValue())
        : Optional.empty();

    final byte[] emailHash = accountSet.hasEmailHash()
      ? accountSet.getEmailHash().getValue().toByteArray()
      : new byte[0];

    final byte[] messageKey = accountSet.hasMessageKey()
      ? accountSet.getMessageKey().getValue().toByteArray()
      : new byte[0];

    final Optional<Integer> setFlag = accountSet.hasSetFlag()
        ? Optional.of(accountSet.getSetFlag().getValue())
        : Optional.empty();

    final Optional<Integer> tickSize = accountSet.hasTickSize()
        ? Optional.of(accountSet.getTickSize().getValue())
        : Optional.empty();

    final Optional<Integer> transferRate = accountSet.hasTransferRate()
        ? Optional.of(accountSet.getTransferRate().getValue())
        : Optional.empty();

    return XrpAccountSet.builder()
      .clearFlag(clearFlag)
      .domain(domain)
      .emailHash(emailHash)
      .messageKey(messageKey)
      .setFlag(setFlag)
      .tickSize(tickSize)
      .transferRate(transferRate)
      .build();
  }
}
