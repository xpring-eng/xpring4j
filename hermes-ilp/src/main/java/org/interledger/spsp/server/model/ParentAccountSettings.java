package org.interledger.spsp.server.model;

import org.interledger.connector.accounts.AccountBalanceSettings;
import org.interledger.connector.accounts.AccountId;
import org.interledger.connector.accounts.AccountSettings;
import org.interledger.connector.accounts.ImmutableAccountBalanceSettings;
import org.interledger.connector.accounts.SettlementEngineDetails;
import org.interledger.core.InterledgerAddress;
import org.interledger.link.LinkType;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.primitives.UnsignedLong;
import org.immutables.value.Value;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

/**
 * Tracks settings for the parent connector <tt>account</tt> that this receiver is connected to.
 */
public interface ParentAccountSettings {

  static ImmutableParentAccountSettings.Builder builder() {
    return ImmutableParentAccountSettings.builder();
  }

  /**
   * <p>An optionally present unique identifier for this account. For example, <tt>alice</tt> or <tt>123456789</tt>.
   * Note that this is not an {@link InterledgerAddress} because an account's address is assigned when a connection is
   * made, generally using information from the client and this identifier.</p>
   */
  AccountId accountId();

  /**
   * The date/time this Account was created.
   *
   * @return An {@link Instant}.
   */
  default Instant createdAt() {
    return Instant.now();
  }

  /**
   * The date/time this Account was last modified.
   *
   * @return An {@link Instant}.
   */
  default Instant modifiedAt() {
    return Instant.now();
  }

  /**
   * The {@link LinkType} that should be used for this account in order to send "data".
   */
  LinkType linkType();

  /**
   * Currency code or other asset identifier that will be used to select the correct rate for this account.
   */
  String assetCode();

  /**
   * Interledger amounts are integers, but most currencies are typically represented as # fractional units, e.g. cents.
   * This property defines how many Interledger units make # up one regular unit. For dollars, this would usually be set
   * to 9, so that Interledger # amounts are expressed in nano-dollars.
   *
   * @return an int representing this account's asset scale.
   */
  int assetScale();

  /**
   * The maximum amount per-packet for incoming prepare packets. The connector will reject any incoming prepare packets
   * from this account with a higher amount.
   *
   * @return The maximum packet amount allowed by this account.
   */
  Optional<UnsignedLong> maximumPacketAmount();

  /**
   * Defines whether the connector should maintain and enforce a balance for this account.
   *
   * @return The parameters for tracking balances for this account.
   */
  AccountBalanceSettings balanceSettings();

  /**
   * <p>Optionally present information about how this account can be settled.</p>
   *
   * @return An optionally present {@link SettlementEngineDetails}. If this value is absent, then this account does not
   *   support settlement.
   */
  Optional<SettlementEngineDetails> settlementEngineDetails();

  /**
   * Additional, custom settings that any plugin can define.
   */
  Map<String, Object> customSettings();


  // Purposefully not interned. Because we desire hashcode/equals to align with AccountSettingsEntity, if this class
  // were to be interned, then constructing a new instance with the same AccountId as an already interned instance
  // would simply return the old, immutable value, which would be incorrect.
  @Value.Immutable
  @Value.Modifiable
  @JsonSerialize(as = ImmutableParentAccountSettings.class)
  @JsonDeserialize(as = ImmutableParentAccountSettings.class)
  @JsonPropertyOrder({"accountId", "createdAt", "modifiedAt", "description", "assetCode",
    "assetScale", "linkType", "balanceSettings", "settlementEngineDetails", "customSettings"})
  abstract class AbstractParentAccountSettings implements ParentAccountSettings {

    @Override
    public abstract AccountId accountId();

    @Override
    @Value.Default
    public Instant createdAt() {
      return Instant.now();
    }

    @Override
    @Value.Default
    public Instant modifiedAt() {
      return Instant.now();
    }

    @Override
    public abstract LinkType linkType();

    @Value.Default
    @Override
    @JsonSerialize(as = ImmutableAccountBalanceSettings.class)
    @JsonDeserialize(as = ImmutableAccountBalanceSettings.class)
    public AccountBalanceSettings balanceSettings() {
      return AccountBalanceSettings.builder().build();
    }


    @Override
    public abstract Optional<SettlementEngineDetails> settlementEngineDetails();

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      AccountSettings accountSettings = (AccountSettings) o;

      return accountId().equals(accountSettings.accountId());
    }

    @Override
    public int hashCode() {
      return accountId().hashCode();
    }
  }
}
