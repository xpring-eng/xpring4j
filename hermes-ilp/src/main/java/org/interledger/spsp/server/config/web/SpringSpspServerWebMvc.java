package org.interledger.spsp.server.config.web;

import static org.interledger.spsp.server.config.crypto.CryptoConfigConstants.INTERLEDGER_SPSP_SERVER_PARENT_ACCOUNT;
import static org.interledger.spsp.server.config.crypto.CryptoConfigConstants.LINK_TYPE;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM;

import org.interledger.link.http.IlpOverHttpLink;
import org.interledger.spsp.server.config.jackson.ObjectMapperFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.zalando.problem.ProblemModule;
import org.zalando.problem.spring.common.MediaTypes;

import java.util.List;

/**
 * Web config for the Spring Connector.
 */
@Configuration
@ConditionalOnProperty(prefix = INTERLEDGER_SPSP_SERVER_PARENT_ACCOUNT, name = LINK_TYPE, havingValue = IlpOverHttpLink.LINK_TYPE_STRING)
@EnableWebMvc
@ComponentScan(basePackages = "org.interledger.spsp.server.controllers")
@Import({SecurityConfiguration.class})
public class SpringSpspServerWebMvc implements WebMvcConfigurer {

  // TODO: Configure TLS
  // TODO: Configure HTTP/2

  @Autowired
  private ObjectMapper objectMapper;

  ////////////////////////
  // HttpMessageConverters
  ////////////////////////

  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    // For any byte[] payloads (e.g., `/settlements`)
    ByteArrayHttpMessageConverter octetStreamConverter = new ByteArrayHttpMessageConverter();
    octetStreamConverter.setSupportedMediaTypes(Lists.newArrayList(APPLICATION_OCTET_STREAM));
    converters.add(octetStreamConverter);

    converters.add(constructProblemsJsonConverter()); // For ProblemsJson only.
    converters.add(new MappingJackson2HttpMessageConverter(objectMapper)); // For any JSON payloads.
  }

  @Override
  public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
    converters.replaceAll(messageConverter -> {
      if (messageConverter instanceof MappingJackson2HttpMessageConverter) {
        // in `configureMessageConverters`, there is at least one extra MessageConverter that is used specifically to
        // serialize Problems to JSON with non-String numbers (e.g., the status code). In that case, we don't want to
        // replace the message converter because we want it to use the custom ObjectMapper that it was configured
        // with.
        if (((MappingJackson2HttpMessageConverter) messageConverter).getObjectMapper().getRegisteredModuleIds()
          .contains(ProblemModule.class.getName())) {
          return messageConverter;
        }
        // Necessary to make sure the correct ObjectMapper is used in all Jackson Message Converters.
        return new MappingJackson2HttpMessageConverter(objectMapper);
      } else {
        return messageConverter;
      }
    });
  }

  @Override
  public void addFormatters(FormatterRegistry registry) {
//    registry.addConverter(rateLimitSettingsEntityConverter);
//    registry.addConverter(accountBalanceSettingsEntityConverter);
//    registry.addConverter(settlementEngineDetailsEntityConverter);
//    registry.addConverter(accountSettingsConverter);
//    registry.addConverter(fxRateOverrideEntityConverter);
//    registry.addConverter(staticRouteEntityConverter);
  }

  @VisibleForTesting
  protected MappingJackson2HttpMessageConverter constructProblemsJsonConverter() {
    final ObjectMapper objectMapper = ObjectMapperFactory.createObjectMapperForProblemsJson();
    final MappingJackson2HttpMessageConverter problemsJsonConverter
      = new MappingJackson2HttpMessageConverter(objectMapper);
    problemsJsonConverter.setSupportedMediaTypes(Lists.newArrayList(MediaTypes.PROBLEM, MediaTypes.X_PROBLEM));

    return problemsJsonConverter;
  }
}
