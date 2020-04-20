package io.xpring.payid;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;

/**
 * Represents a JSON Resource Descriptor (JRD) returned from a WebFinger request as defined in RFC-7033.
 *
 * <p>
 * For the purposes of PayID Discovery, only subject and links are necessary.  Thus, the aliases and properties
 * fields are omitted.
 * </p>
 *
 * @see "https://tools.ietf.org/html/rfc7033#section-4.4"
 */
@Value.Immutable
@JsonDeserialize(as = ImmutableWebFingerJrd.class)
@JsonSerialize(as = ImmutableWebFingerJrd.class)
public interface WebFingerJrd {

  static ImmutableWebFingerJrd.Builder builder() {
    return ImmutableWebFingerJrd.builder();
  }

  /**
   * The value of the "subject" member is a URI that identifies the entity
   * that the JRD describes.
   *
   * <p>
   * The "subject" value returned by a WebFinger resource MAY differ from
   * the value of the "resource" parameter used in the client's request.
   * This might happen, for example, when the subject's identity changes
   * (e.g., a user moves his or her account to another service) or when
   * the resource prefers to express URIs in canonical form.
   * </p>
   *
   * @return A {@link String} containing the subject of a JRD.
   */
  String subject();

  /**
   * An array of objects that contain link relation information.
   *
   * <p>
   * In the context of PayID, these links will point to either another WebFinger URL or a PayID server endpoint.
   * </p>
   *
   * @return A {@link List} of {@link WebFingerLink}s containing any link relation information.
   */
  List<WebFingerLink> links();
}
