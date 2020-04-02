package io.xpring.ilp;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;

public class IlpException extends Exception {

  public static final IlpException NOT_FOUND = new IlpException(IlpExceptionType.ACCOUNT_NOT_FOUND, "Account not found.");
  public static final IlpException UNAUTHENTICATED = new IlpException(IlpExceptionType.UNAUTHENTICATED, "Authentication failed.");
  public static final IlpException INVALID_ARGUMENT = new IlpException(IlpExceptionType.INVALID_ARGUMENT, "Invalid argument in request body.");
  public static final IlpException INTERNAL = new IlpException(IlpExceptionType.INTERNAL, "Internal error occurred on ILP network.");
  public static final IlpException UNKNOWN = new IlpException(IlpExceptionType.UNKNOWN, "Unknown error occurred.");
  public static final IlpException INVALID_ACCESS_TOKEN = new IlpException(
    IlpExceptionType.INVALID_ACCESS_TOKEN,
    "Access token was invalid. Access token should not start with \"Bearer\""
  );

  private IlpExceptionType type;

  public IlpException(IlpExceptionType type, String message) {
    super(message);
    this.type = type;
  }

  public IlpExceptionType getType() {
    return type;
  }

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

