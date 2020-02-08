package org.interledger.spsp.server.controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import javax.servlet.http.HttpServletRequest;

public abstract class AbstractController {

  @Autowired
  private HttpServletRequest request;

  public String getAuthorization() {
    return request.getHeader("Authorization");
  }

  public String getBearerToken() {
    return getAuthorization().substring(7);
  }

  public DecodedJWT getJwt() {
    DecodedJWT jwt = JWT.decode(getBearerToken());
    if (jwt.getExpiresAt().before(new Date())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "JWT is expired");
    }
    return jwt;
  }
}
