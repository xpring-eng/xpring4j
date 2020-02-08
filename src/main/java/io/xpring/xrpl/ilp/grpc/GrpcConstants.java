package io.xpring.xrpl.ilp.grpc;

import io.grpc.Context;

public final class GrpcConstants {
  public static final Context.Key JWT_KEY = Context.key("ILP_JWT");
}
