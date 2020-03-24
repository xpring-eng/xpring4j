package io.xpring.ilp.grpc;

import com.google.common.base.Preconditions;
import io.grpc.CallCredentials;
import io.grpc.Metadata;
import io.grpc.Status;
import io.xpring.xrpl.XpringException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;

public class IlpCredentials extends CallCredentials {

  public static final String BEARER_SPACE = "Bearer ";
  private static Logger LOGGER = LoggerFactory.getLogger(IlpCredentials.class);

  private final String accessToken;

  private IlpCredentials(String accessToken) {
    this.accessToken = accessToken;
  }

  public static IlpCredentials build(String accessToken) {
    Preconditions.checkArgument(
      !accessToken.startsWith(BEARER_SPACE),
      "accessToken cannot start with \"Bearer \""
    );
    return new IlpCredentials(accessToken);
  }

  @Override
  public void applyRequestMetadata(RequestInfo requestInfo, Executor appExecutor, MetadataApplier applier) {
    try {
      applyToken(applier, accessToken);
    } catch (RuntimeException e) {
      applyFailure(applier, e);
    }
  }

  @Override
  public void thisUsesUnstableApi() {}

  protected void applyToken(MetadataApplier applier, String accessToken) {
    Metadata metadata = new Metadata();
    metadata.put(Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER), BEARER_SPACE + accessToken);
    applier.apply(metadata);
  }

  protected void applyFailure(MetadataApplier applier, Throwable e) {
    String msg = "An exception when obtaining access token";
    LOGGER.error(msg, e);
    applier.fail(Status.UNAUTHENTICATED.withDescription(msg).withCause(e));
  }
}
