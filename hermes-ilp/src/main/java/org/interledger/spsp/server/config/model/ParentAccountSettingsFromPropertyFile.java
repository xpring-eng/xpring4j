package org.interledger.spsp.server.config.model;

import org.interledger.connector.accounts.AccountId;
import org.interledger.connector.accounts.SettlementEngineDetails;
import org.interledger.link.LinkType;
import org.interledger.spsp.server.model.ParentAccountSettings;

import com.google.common.collect.Maps;
import com.google.common.primitives.UnsignedLong;

import java.util.Map;
import java.util.Optional;

public class ParentAccountSettingsFromPropertyFile implements ParentAccountSettings {

  private AccountId id;
  private String assetCode;
  private int assetScale;
  private LinkType linkType;
  private String maximumPacketAmount;

  private AccountBalanceSettingsFromPropertyFile balanceSettings = new AccountBalanceSettingsFromPropertyFile();
  private SettlementEngineDetailsFromPropertyFile settlementEngineDetails = new SettlementEngineDetailsFromPropertyFile();

  private Map<String, Object> customSettings = Maps.newConcurrentMap();

  @Override
  public AccountId accountId() {
    return id;
  }

  public AccountId getId() {
    return id;
  }

  public void setId(AccountId id) {
    this.id = id;
  }

  @Override
  public String assetCode() {
    return assetCode;
  }

  public void setAssetCode(String assetCode) {
    this.assetCode = assetCode;
  }

  @Override
  public int assetScale() {
    return assetScale;
  }

  public void setAssetScale(int assetScale) {
    this.assetScale = assetScale;
  }

  @Override
  public AccountBalanceSettingsFromPropertyFile balanceSettings() {
    return balanceSettings;
  }

  public void setBalanceSettings(AccountBalanceSettingsFromPropertyFile balanceSettings) {
    this.balanceSettings = balanceSettings;
  }

  @Override
  public Optional<SettlementEngineDetails> settlementEngineDetails() {
    return Optional.ofNullable(settlementEngineDetails);
  }

  public void setSettlementEngineDetails(SettlementEngineDetailsFromPropertyFile settlementEngineDetails) {
    this.settlementEngineDetails = settlementEngineDetails;
  }

  @Override
  public LinkType linkType() {
    return linkType;
  }

  public void setLinkType(LinkType linkType) {
    this.linkType = linkType;
  }

  @Override
  public Optional<UnsignedLong> maximumPacketAmount() {
    return Optional.ofNullable(UnsignedLong.valueOf(maximumPacketAmount));
  }

  public void setMaximumPacketAmount(String maximumPacketAmount) {
    this.maximumPacketAmount = maximumPacketAmount;
  }

  @Override
  public Map<String, Object> customSettings() {
    return customSettings;
  }

  public void setCustomSettings(Map<String, Object> customSettings) {
    this.customSettings = customSettings;
  }

}
