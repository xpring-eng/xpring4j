package org.interledger.spsp.server.config.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class JacksonConfig {

  @Bean
  @Primary
  protected ObjectMapper objectMapper() {
    return ObjectMapperFactory.create();
  }

}
