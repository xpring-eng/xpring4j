package io.xpring.common;

import java.util.Optional;

/**
 * Represents a result monad of type `Type` or an error of type `ErrorType`.
 */
public class Result<Type, ErrorType extends Throwable> {
  private Optional<Type> value;
  private Optional<ErrorType> error;

  private Result(Type value, ErrorType error) {
    this.value = Optional.ofNullable(value);
    this.error = Optional.ofNullable(error);
  }

  public static <Type, ErrorType extends Throwable> Result<Type, ErrorType> ok(Type value) {
    return new Result<Type, ErrorType>(value, null);
  }

  public static <Type, ErrorType extends Throwable> Result<Type, ErrorType> error(ErrorType error) {
    return new Result<>(null, error);
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
