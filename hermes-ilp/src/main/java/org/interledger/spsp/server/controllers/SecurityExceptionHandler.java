package org.interledger.spsp.server.controllers;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.zalando.problem.spring.web.advice.security.SecurityAdviceTrait;

/**
 * @see "https://github.com/zalando/problem-spring-web/tree/master/problem-spring-web#security"
 */
@ControllerAdvice
public class SecurityExceptionHandler implements SecurityAdviceTrait {
}
