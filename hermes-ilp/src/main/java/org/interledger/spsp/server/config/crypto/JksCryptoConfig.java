package org.interledger.spsp.server.config.crypto;

import static org.interledger.connector.core.ConfigConstants.ENABLED;
import static org.interledger.connector.core.ConfigConstants.TRUE;
import static org.interledger.spsp.server.config.crypto.CryptoConfigConstants.INTERLEDGER_SPSP_SERVER_KEYSTORE_JKS;
import static org.interledger.spsp.server.config.crypto.CryptoConfigConstants.INTERLEDGER_SPSP_SERVER_KEYSTORE_JKS_FILENAME;
import static org.interledger.spsp.server.config.crypto.CryptoConfigConstants.INTERLEDGER_SPSP_SERVER_KEYSTORE_JKS_PASSWORD;
import static org.interledger.spsp.server.config.crypto.CryptoConfigConstants.INTERLEDGER_SPSP_SERVER_KEYSTORE_JKS_SECRET0_ALIAS;
import static org.interledger.spsp.server.config.crypto.CryptoConfigConstants.INTERLEDGER_SPSP_SERVER_KEYSTORE_JKS_SECRET0_PASSWORD;

import org.interledger.crypto.EncryptionException;
import org.interledger.crypto.EncryptionService;
import org.interledger.crypto.JavaKeystoreLoader;
import org.interledger.crypto.impl.JksEncryptionService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;

import javax.crypto.SecretKey;

/**
 * Keystore Configuration that defines keys and secrets as being stored in a Java Keystore located on the classpath.
 */
@Configuration
@ConditionalOnProperty(prefix = INTERLEDGER_SPSP_SERVER_KEYSTORE_JKS, name = ENABLED, havingValue = TRUE)
public class JksCryptoConfig {

  @Value("${" + INTERLEDGER_SPSP_SERVER_KEYSTORE_JKS_FILENAME + "}")
  private String jksFilename;

  @Value("${" + INTERLEDGER_SPSP_SERVER_KEYSTORE_JKS_PASSWORD + "}")
  private String jksPassword;

  @Value("${" + INTERLEDGER_SPSP_SERVER_KEYSTORE_JKS_SECRET0_ALIAS + "}")
  private String secret0Alias;

  @Value("${" + INTERLEDGER_SPSP_SERVER_KEYSTORE_JKS_SECRET0_PASSWORD + "}")
  private String secret0Password;

  /**
   * Load the symmetric encryption SecretKey from a JKS on the classpath.
   */
  @Bean
  SecretKey secret0Key() throws Exception {
    final KeyStore ks = JavaKeystoreLoader.loadFromClasspath(jksFilename, jksPassword.toCharArray());

    // Password-protected keys are not yet supported
    if (ks.isKeyEntry(secret0Alias)) {
      final SecretKey secret0 = (SecretKey) ks.getKey(secret0Alias, secret0Password.toCharArray());
      return secret0;
    } else {
      throw new EncryptionException("No KeyEntry found for secret0Alias: " + secret0Alias);
    }
  }

  @Bean
  EncryptionService encryptionService(SecretKey secret0Key) throws NoSuchAlgorithmException {
    return new JksEncryptionService(secret0Key);
  }

}
