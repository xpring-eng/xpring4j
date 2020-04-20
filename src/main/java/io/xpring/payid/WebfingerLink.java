package io.xpring.payid;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableWebfingerLink.class)
@JsonSerialize(as = ImmutableWebfingerLink.class)
public interface WebfingerLink {

  static ImmutableWebfingerLink.Builder builder() {
    return ImmutableWebfingerLink.builder();
  }

  String rel();

  String type();

  String href();

}
