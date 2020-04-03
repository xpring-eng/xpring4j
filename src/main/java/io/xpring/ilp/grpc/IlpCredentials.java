package io.xpring.ilp.grpc;

import com.google.common.base.Preconditions;
import io.grpc.CallCredentials;
import io.grpc.Metadata;
import io.grpc.Status;
import io.xpring.ilp.IlpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;

/**
 * An extension of {@link CallCredentials} which provides a convenient way to
 * add an Authorization metadata header, and ensures every bearer token
 * going over the wire is prefixed with 'Bearer '
 */
public class IlpCredentials extends CallCredentials {

  public static final String BEARER_PREFIX = "Bearer ";
  private static Logger LOGGER = LoggerFactory.getLogger(IlpCredentials.class);

  private final String accessToken;

  private IlpCredentials(String accessToken) {
    this.accessToken = accessToken;
  }

  /**
   * Static initializer which enforces that the accessToken does not start with 'Bearer '.
   *
   * @param accessToken Caller's access token, which can not start with 'Bearer '
   * @return An instance of {@link IlpCredentials}
   */
  public static IlpCredentials build(String accessToken) throws IlpException {
    if (accessToken.startsWith(BEARER_PREFIX)) {
      throw IlpException.INVALID_ACCESS_TOKEN;
    }

    return new IlpCredentials(accessToken);
  }

  @Override
  public void applyRequestMetadata(RequestInfo requestInfo, Executor appExecutor, MetadataApplier applier) {
    try {
      applyToken(applier, accessToken);
    } catch (RuntimeException runtimeException) {
      applyFailure(applier, runtimeException);
    }
  }

  @Override
  public void thisUsesUnstableApi() {}

  /**
   * Adds an Authorization header to the request Metadata, prepending 'Bearer ' to the access token
   *
   * @param applier A {@link io.grpc.CallCredentials.MetadataApplier}
   * @param accessToken An access token that does not start with 'Bearer '
   */
  protected void applyToken(MetadataApplier applier, String accessToken) {
    Metadata metadata = new Metadata();
    metadata.put(Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER), BEARER_PREFIX + accessToken);
    applier.apply(metadata);
  }

  protected void applyFailure(MetadataApplier applier, Throwable throwable) {
    String msg = "An exception when obtaining access token";
    LOGGER.error(msg, throwable);
    applier.fail(Status.UNAUTHENTICATED.withDescription(msg).withCause(throwable));
  }
}
