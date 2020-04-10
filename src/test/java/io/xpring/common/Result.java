package io.xpring.common;

import java.util.Optional;

/**
 * Represents a result monad of type `Type` or an error of type `ErrorType`.
 */
public class Result<T, E extends Throwable> {
  private Optional<T> value;
  private Optional<E> error;

  private Result(T value, E error) {
    this.value = Optional.ofNullable(value);
    this.error = Optional.ofNullable(error);
  }

  public static <T, E extends Throwable> Result<T, E> ok(T value) {
    return new Result<T, E>(value, null);
  }

  public static <T, E extends Throwable> Result<T, E> error(E error) {
    return new Result<>(null, error);
  }

  public boolean isError() {
    return error.isPresent();
  }

  public T getValue() {
    return value.get();
  }

  public E getError() {
    return error.get();
  }
}
