package io.xpring;

import java.util.Optional;

/**
 * Represents a result monad of type `Type` or an error of type `ErrorType`.
 */
// TODO(keefertaylor): This class is more generic than just gRPC. Refactor.
public class GRPCResult<Type, ErrorType extends Throwable> {
  private Optional<Type> value;
  private Optional<ErrorType> error;

  private GRPCResult(Type value, ErrorType error) {
    this.value = Optional.ofNullable(value);
    this.error = Optional.ofNullable(error);
  }

  public static <Type, ErrorType extends Throwable> GRPCResult<Type, ErrorType> ok(Type value) {
    return new GRPCResult<Type, ErrorType>(value, null);
  }

  public static <Type, ErrorType extends Throwable> GRPCResult<Type, ErrorType> error(ErrorType error) {
    return new GRPCResult<>(null, error);
  }

  public boolean isError() {
    return error.isPresent();
  }

  public Type getValue() {
    return value.get();
  }

  public ErrorType getError() {
    return error.get();
  }
}
