package io.xpring.xrpl.ilp.grpc;

import io.grpc.Context;

/**
 * TODO: Depend on this from hermes?
 */
public final class JwtContext {

  public static String getToken() {
    return (String) GrpcConstants.JWT_KEY.get(Context.current());
  }
}
