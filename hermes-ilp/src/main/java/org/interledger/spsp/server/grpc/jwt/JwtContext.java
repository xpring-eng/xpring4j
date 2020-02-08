package org.interledger.spsp.server.grpc.jwt;

import org.interledger.spsp.server.grpc.GrpcConstants;

import io.grpc.Context;

public final class JwtContext {

  public static String getToken() {
    return (String) GrpcConstants.JWT_KEY.get(Context.current());
  }
}
