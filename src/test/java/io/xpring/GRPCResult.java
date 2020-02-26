package io.xpring;

import java.util.Optional;

/**
 * Represents the result of a gRPC network call for an object of type T or an error.
 */
public class GRPCResult<T> {
  private Optional<T> value;
  private Optional<String> error;

  private GRPCResult(T value, String error) {
    this.value = Optional.ofNullable(value);
    this.error = Optional.ofNullable(error);
  }

  public static <U> GRPCResult<U> ok(U value) {
    return new GRPCResult<>(value, null);
  }

  public static <U> GRPCResult<U> error(String error) {
    return new GRPCResult<>(null, error);
  }

  public boolean isError() {
    return error.isPresent();
  }

  public T getValue() {
    return value.get();
  }

  public String getError() {
    return error.get();
  }
}
