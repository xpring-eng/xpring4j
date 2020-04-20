package io.xpring.payid;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;

@Value.Style(jdkOnly = true)
@Value.Immutable
@JsonDeserialize(as = ImmutableWebfingerResponse.class)
@JsonSerialize(as = ImmutableWebfingerResponse.class)
public interface WebfingerResponse {

  static ImmutableWebfingerResponse.Builder builder() {
    return ImmutableWebfingerResponse.builder();
  }

  String subject();

  List<WebfingerLink> links();
}
