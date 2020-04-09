package io.xpring.ilp;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;

/**
 * Represents errors thrown by ILP components of the Xpring SDK.
 */
public class IlpException extends Exception {

  /**
   * Default Errors.
   */
  public static final IlpException INTERNAL = new IlpException(
      IlpExceptionType.INTERNAL,
      "Internal error occurred on ILP network."
  );
  public static final IlpException INVALID_ACCESS_TOKEN = new IlpException(
      IlpExceptionType.INVALID_ACCESS_TOKEN,
      "Access token was invalid. Access token should not start with \"Bearer\""
  );
  public static final IlpException INVALID_ARGUMENT =
      new IlpException(IlpExceptionType.INVALID_ARGUMENT, "Invalid argument in request body.");
  public static final IlpException NOT_FOUND =
      new IlpException(IlpExceptionType.ACCOUNT_NOT_FOUND, "Account not found.");
  public static final IlpException UNAUTHENTICATED =
      new IlpException(IlpExceptionType.UNAUTHENTICATED, "Authentication failed.");
  public static final IlpException UNKNOWN =
      new IlpException(IlpExceptionType.UNKNOWN, "Unknown error occurred.");

  /**
   * The type of this error.
   */
  private IlpExceptionType type;

  /**
   * Construct a new instance of {@link IlpException}.
   *
   * @param type The {@link IlpExceptionType} of this {@link IlpException}.
   * @param message A detail message of what went wrong.
   */
  public IlpException(IlpExceptionType type, String message) {
    super(message);
    this.type = type;
  }

  /**
   * Get the type of this exception.
   *
   * @return The {@link IlpExceptionType} of this exception.
   */
  public IlpExceptionType getType() {
    return type;
  }

  /**
   * Construct an {@link IlpException} from a {@link StatusRuntimeException}.
   * <p>
   * gRPC network calls will throw a {@link StatusRuntimeException} if something goes wrong.
   * This method allows for easy translation of {@link StatusRuntimeException}s to {@link IlpException}s.
   * </p>
   * @param grpcException The {@link StatusRuntimeException} that was thrown by a gRPC network call.
   * @return A {@link IlpException} that is analogous to the grpcException thrown.
   */
  public static IlpException from(StatusRuntimeException grpcException) {
    if (Status.NOT_FOUND.equals(grpcException.getStatus())) {
      return NOT_FOUND;
    } else if (Status.UNAUTHENTICATED.equals(grpcException.getStatus())) {
      return UNAUTHENTICATED;
    } else if (Status.INVALID_ARGUMENT.equals(grpcException.getStatus())) {
      return INVALID_ARGUMENT;
    } else if (Status.INTERNAL.equals(grpcException.getStatus())) {
      return INTERNAL;
    }
    return UNKNOWN;
  }
}
