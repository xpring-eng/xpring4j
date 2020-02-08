package org.interledger.spsp.server.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.zalando.problem.StatusType;
import org.zalando.problem.ThrowableProblem;
import org.zalando.problem.spring.web.advice.ProblemHandling;
import org.zalando.problem.spring.web.advice.security.SecurityAdviceTrait;

import java.net.URI;

@ControllerAdvice
class ProblemExceptionHandling implements ProblemHandling, SecurityAdviceTrait {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  /**
   * Override for logging purposes.
   *
   * @see "https://github.com/zalando/problem-spring-web/issues/41"
   */
  @Override
  public ThrowableProblem toProblem(final Throwable throwable, final StatusType status, final URI type) {

    final ThrowableProblem problem = prepare(throwable, status, type).build();
    final StackTraceElement[] stackTrace = createStackTrace(throwable);
    problem.setStackTrace(stackTrace);

    logger.error(throwable.getMessage(), throwable);

    return problem;
  }

}
