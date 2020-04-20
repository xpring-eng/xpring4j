package io.xpring.payid;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.Optional;

/**
 * Represents a WebFinger Link, as defined in RFC-7033.
 *
 * <p>
 * For the purposes of PayID Discovery, only rel and href or template are necessary.  Thus, the titles, type, and
 * properties fields of a WebFinger Link are omitted.
 * </p>
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
   * The target URI of a {@link WebFingerLink}. In the case that this is empty, {@code template()} should be present.
   *
   * @return A present {@link String} representing the target URI or {@link Optional#empty()} if no href was given.
   */
  Optional<String> href();

  /**
   * The target URI template of a {@link WebFingerLink}. In the case that this is empty,
   * {@code href()} should be present.
   *
   * @return A present {@link String} representing the target URI template or {@link Optional#empty()}
   *          if no href was given.
   */
  Optional<String> template();
}
