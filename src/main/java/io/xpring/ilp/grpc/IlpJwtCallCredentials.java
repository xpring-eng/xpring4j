package io.xpring.ilp.grpc;

import io.grpc.CallCredentials;
import io.grpc.Metadata;
import io.grpc.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;

/**
 * TODO: Depend on this from hermes?
 */
public class IlpJwtCallCredentials extends CallCredentials {
  private static Logger LOGGER = LoggerFactory.getLogger(IlpJwtCallCredentials.class);

  private final String jwtToken;

  private IlpJwtCallCredentials(String jwtToken) {
    this.jwtToken = jwtToken;
  }

  public static IlpJwtCallCredentials build(String token) {
    return new IlpJwtCallCredentials(token);
  }

  @Override
  public void applyRequestMetadata(RequestInfo requestInfo, Executor appExecutor, MetadataApplier applier) {
    try {
      applyToken(applier, jwtToken);
    } catch (RuntimeException e) {
      applyFailure(applier, e);
    }
  }

  @Override
  public void thisUsesUnstableApi() {}

  protected void applyToken(MetadataApplier applier, String jwtToken) {
    Metadata metadata = new Metadata();
    metadata.put(Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER), "Bearer " + jwtToken);
    applier.apply(metadata);
  }

  protected void applyFailure(MetadataApplier applier, Throwable e) {
    String msg = "An exception when obtaining JWT token";
    LOGGER.error(msg, e);
    applier.fail(Status.UNAUTHENTICATED.withDescription(msg).withCause(e));
  }
}
