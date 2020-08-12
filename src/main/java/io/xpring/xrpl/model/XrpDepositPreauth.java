package io.xpring.xrpl.model;

import org.immutables.value.Value;

import java.util.Optional;

/**
 * Represents a DepositPreauth transaction on the XRP Ledger.
 * A DepositPreauth transaction gives another account pre-approval to deliver payments to the sender of this transaction.
 * This is only useful if the sender of this transaction is using (or plans to use) Deposit Authorization.
 *
 * @see "https://xrpl.org/depositpreauth.html"
 */
@Value.Immutable
public interface XrpDepositPreauth {
  static ImmutableXrpDepositPreauth.Builder builder() {
    return ImmutableXrpDepositPreauth.builder();
  }

  /**
   * (Optional) The XRP Ledger address of the sender to preauthorize, encoded as an X-address.
   *
   * @return A {@link String} representing the XRP Ledger address of the sender to preauthorize, encoded as an
   *         X-address.
   */
  Optional<String> authorizeXAddress();

  /**
   * (Optional) The XRP Ledger address of a sender whose preauthorization should be revoked, encoded as an X-address.
   *
   * @return A {@link String} representing the XRP Ledger address of a sender whose preauthorization should be revoked,
   * encoded as an X-address.
   */
  Optional<String> unauthorizeXAddress();
}
