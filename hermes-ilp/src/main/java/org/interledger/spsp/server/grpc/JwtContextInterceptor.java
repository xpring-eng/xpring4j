package org.interledger.spsp.server.grpc;

import com.google.common.net.HttpHeaders;
import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import org.lognet.springboot.grpc.GRpcGlobalInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;

@GRpcGlobalInterceptor
public class JwtContextInterceptor implements ServerInterceptor {

  private static final Logger LOGGER = LoggerFactory.getLogger(JwtContextInterceptor.class);

  public JwtContextInterceptor() {
    LOGGER.info("JwtContextInterceptor started for GRPC");
  }

  @Override
  public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call,
                                                               Metadata headers,
                                                               ServerCallHandler<ReqT, RespT> next) {
    String bearer = headers.get(Metadata.Key.of(HttpHeaders.AUTHORIZATION, Metadata.ASCII_STRING_MARSHALLER));
    AtomicReference<Context> context = new AtomicReference(Context.current());
//    if (!bearer.startsWith("Bearer ")) {
//      LOGGER.warn("Received an authorization header to GRPC that was not a bearer token");
//    }
//    else {
//      String jwt = bearer.substring("Bearer ".length());
      context.set(Context.current().withValue(GrpcConstants.JWT_KEY, bearer));
//    }
    return Contexts.interceptCall(context.get(), call, headers, next);
  }
}
