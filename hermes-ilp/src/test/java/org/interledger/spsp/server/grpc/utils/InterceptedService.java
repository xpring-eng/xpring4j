package org.interledger.spsp.server.grpc.utils;

import org.interledger.spsp.server.grpc.JwtContextInterceptor;

import io.grpc.BindableService;
import io.grpc.ServerInterceptors;
import io.grpc.ServerServiceDefinition;

public final class InterceptedService {

  public static ServerServiceDefinition of(BindableService service) {
    return ServerInterceptors.intercept(service, new JwtContextInterceptor());
  }
}
