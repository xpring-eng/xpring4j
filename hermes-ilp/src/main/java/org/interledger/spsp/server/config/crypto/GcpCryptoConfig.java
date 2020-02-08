package org.interledger.spsp.server.config.crypto;

import static org.interledger.connector.core.ConfigConstants.ENABLED;
import static org.interledger.connector.core.ConfigConstants.TRUE;
import static org.interledger.crypto.CryptoConfigConstants.GOOGLE_CLOUD_PROJECT;
import static org.interledger.spsp.server.config.crypto.CryptoConfigConstants.INTERLEDGER_SPSP_SERVER_KEYSTORE_GCP;
import static org.interledger.spsp.server.config.crypto.CryptoConfigConstants.INTERLEDGER_SPSP_SERVER_KEYSTORE_LOCATION_ID;

import org.interledger.crypto.EncryptionService;
import org.interledger.crypto.impl.GcpEncryptionService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = INTERLEDGER_SPSP_SERVER_KEYSTORE_GCP, name = ENABLED, havingValue = TRUE)
public class GcpCryptoConfig {

  @Value("${" + GOOGLE_CLOUD_PROJECT + "}")
  private String gcpProjectId;

  @Value("${" + INTERLEDGER_SPSP_SERVER_KEYSTORE_LOCATION_ID + "}")
  private String gcpLocationId;

  @Bean
  EncryptionService encryptionService() {
    return new GcpEncryptionService(gcpProjectId, gcpLocationId);
  }

}
