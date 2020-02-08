package org.interledger.spsp.server;

import org.interledger.spsp.server.config.ilp.IlpOverHttpConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import javax.annotation.PostConstruct;

@SpringBootApplication
@Import(IlpOverHttpConfig.class)
public class HermesServerApplication {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public static void main(String[] args) {
    SpringApplication.run(HermesServerApplication.class, args);
  }

}
