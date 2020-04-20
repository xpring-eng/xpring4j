package io.xpring.payid;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.xpring.common.ObjectMapperFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

public class WebFingerJrdTest {

  private ObjectMapper objectMapper;

  @Before
  public void setUp() throws Exception {
    objectMapper = ObjectMapperFactory.create();
  }

  @Test
  public void testWebFingerLinkJson() throws JsonProcessingException {
    // GIVEN a WebFingerLink
    String rel = "http://payid.org/rel/payid/1.0";
    String href = "https://doug.purdy.im/pay";
    String template = "https://doug.purdy.im/{acctpart}";

    WebFingerLink link = WebFingerLink.builder()
        .href(Optional.of(href))
        .template(Optional.of(template))
        .rel(rel)
        .build();

    // WHEN the link is serialized and deserialized
    String linkJson = objectMapper.writeValueAsString(link);
    WebFingerLink deserializedLink = objectMapper.readValue(linkJson, WebFingerLink.class);

    // THEN all link fields are unchanged.
    assertThat(deserializedLink.href().get()).isEqualTo(href);
    assertThat(deserializedLink.template().get()).isEqualTo(template);
    assertThat(deserializedLink.rel()).isEqualTo(rel);
  }

  @Test
  public void testWebFingerJrdJson() throws JsonProcessingException {
    // GIVEN a WebFingerJrd
    String subject = "payid:doug$payid.ml";
    String rel = "http://payid.org/rel/payid/1.0";
    String href = "https://doug.purdy.im/pay";
    String template = "https://doug.purdy.im/{acctpart}";

    WebFingerJrd jrd = WebFingerJrd.builder()
        .subject(subject)
        .addLinks(WebFingerLink.builder()
          .href(Optional.of(href))
          .template(Optional.of(template))
          .rel(rel)
          .build())
        .build();

    // WHEN the JRD is serialized and deserialized
    String jrdJson = objectMapper.writeValueAsString(jrd);
    WebFingerJrd deserializedJrd = objectMapper.readValue(jrdJson, WebFingerJrd.class);

    // THEN the JRD fields are unchanged.
    assertThat(deserializedJrd.subject()).isEqualTo(subject);
    assertThat(deserializedJrd.links().size()).isEqualTo(1);
  }
}
