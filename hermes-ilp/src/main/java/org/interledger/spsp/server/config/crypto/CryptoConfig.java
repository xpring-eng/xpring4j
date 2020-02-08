package org.interledger.spsp.server.config.crypto;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(
  {
    GcpCryptoConfig.class,
    JksCryptoConfig.class
  }
)
public class CryptoConfig {
}
