package io.xpring.payid;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

/**
 * Represents a WebFinger Link, as defined in RFC-7033.
 *
 * For the purposes of PayID dereferencing, only rel, href, and type are necessary.  Thus, the titles and properties
 * fields of a WebFinger Link are omitted.
 *
 * @see "https://tools.ietf.org/html/rfc7033#section-4.4.4"
 */
@Value.Immutable
@JsonDeserialize(as = ImmutableWebFingerLink.class)
@JsonSerialize(as = ImmutableWebFingerLink.class)
public interface WebFingerLink {

  static ImmutableWebFingerLink.Builder builder() {
    return ImmutableWebFingerLink.builder();
  }

  /**
   * The {@link WebFingerLink}s relation type.
   *
   * @return A {@link String} representing the relation type.
   */
  String rel();

  /**
   * The target URI of a {@link WebFingerLink}.
   *
   * @return A {@link String} representing the target URI.
   */
  String href();

  /**
   * The media type of what the result of dereferencing the link should be.
   *
   * @return A {@link String} representing the media type.
   */
  String type();

}
