package org.interledger.spsp.server.config.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@Import(SecurityProblemSupport.class)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Autowired
  SecurityProblemSupport problemSupport;

  /**
   * Required for auto-injection of {@link org.springframework.security.core.Authentication} into controllers.
   *
   * @see "https://github.com/spring-projects/spring-security/issues/4011"
   */
  @Bean
  public SecurityContextHolderAwareRequestFilter securityContextHolderAwareRequestFilter() {
    return new SecurityContextHolderAwareRequestFilter();
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList("*"));
    configuration.setAllowedMethods(Arrays.asList("*"));
    configuration.setAllowCredentials(true);
    configuration.setAllowedHeaders(Arrays.asList("*"));
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @Override
  public void configure(final HttpSecurity http) throws Exception {

    // WARNING: Don't add `denyAll` here...it's taken care of after the JWT security below. To verify, turn on debugging
    // for Spring Security (e.g.,  org.springframework.security: DEBUG) and look at the security filter chain).

    http
      .cors().and().csrf().disable()
      .authorizeRequests()
      .anyRequest()
      .permitAll()
      //.and().cors().disable()
      //.antMatchers(HttpMethod.GET, "/**").permitAll()
      .and()
      .formLogin().disable()
      .logout().disable()
      //.anonymous().disable()
      .jee().disable()
      //.authorizeRequests()
      //.antMatchers(HttpMethod.GET, HealthController.SLASH_AH_SLASH_HEALTH).permitAll()
      //.anyRequest().denyAll()
      //.and()
      .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER).enableSessionUrlRewriting(false)
      .and()
      .exceptionHandling().authenticationEntryPoint(problemSupport).accessDeniedHandler(problemSupport);

    // @formatter:on
  }

}
