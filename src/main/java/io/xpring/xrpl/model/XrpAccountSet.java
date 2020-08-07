package io.xpring.xrpl.model;

import io.xpring.xrpl.model.ImmutableXrpAccountSet;
import org.immutables.value.Value;
import org.xrpl.rpc.v1.AccountSet;

import java.util.Optional;

/**
 * Represents an AccountSet transaction on the XRP Ledger.
 * An AccountSet transaction modifies the properties of an account in the XRP Ledger.
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
   * The exchange rates of those offers is rounded to this many significant digits.
   * Valid values are 3 to 15 inclusive, or 0 to disable. (Requires the TickSize amendment.)
   *
   * @return An integer representing the tick size to use for offers involving a currency issued by this address.
   */
  Optional<Integer> tickSize();

  /**
   * (Optional) The fee to charge when users transfer this account's issued currencies,
   * represented as billionths of a unit.
   * Cannot be more than 2000000000 or less than 1000000000, except for the special case
   * 0 meaning no fee.
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
    Optional<Integer> clearFlag = Optional.empty();
    if (accountSet.hasClearFlag()) {
      clearFlag = Optional.of(accountSet.getClearFlag().getValue());
    }

    Optional<String> domain = Optional.empty();
    if (accountSet.hasDomain()) {
      domain = Optional.of(accountSet.getDomain().getValue());
    }

    final byte[] emailHash = accountSet.getEmailHash().getValue().toByteArray();

    final byte[] messageKey = accountSet.getMessageKey().getValue().toByteArray();

    Optional<Integer> setFlag = Optional.empty();
    if (accountSet.hasSetFlag()) {
      setFlag = Optional.of(accountSet.getSetFlag().getValue());
    }

    Optional<Integer> tickSize = Optional.empty();
    if (accountSet.hasTickSize()) {
      tickSize = Optional.of(accountSet.getTickSize().getValue());
    }

    Optional<Integer> transferRate = Optional.empty();
    if (accountSet.hasTransferRate()) {
      transferRate = Optional.of(accountSet.getTransferRate().getValue());
    }

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
