package io.xpring.payid;
import static org.assertj.core.api.Assertions.assertThat;
import okhttp3.HttpUrl;
import okhttp3.HttpUrl.Builder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
/**
 * Unit tests for {@link PayID}.
 */
@RunWith(Parameterized.class)
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public class InteractiveModeHttpUrlValidValuesTest {
  private PayID sourcePayId;
  private String sourceUriTemplate;
  private String expectedPayIdUrl;
  private InteractiveModePayIDResolver resolver;
  /**
   * Required args constructor. Used by @Parameters annotated method to instantiate several test cases with different
   * inputs.
   *
   * @param sourcePayIdUri      A {@link String} representing a {@link PayID}.
   * @param expectedAccountPart A {@link String} representing the account part of a PayID that should be derived from
   *                            {@code sourcePayID}.
   * @param expectedHost        A {@link String} representing the host part of a PayID that should be derived from
   *                            {@code sourcePayID}.
   * @param expectedPayIdUrl    A {@link HttpUrl} containing the result of PayID Discovery on a PayID URI.
   */
  public InteractiveModeHttpUrlValidValuesTest(
    PayID sourcePayId,
    String sourceUriTemplate,
    String expectedPayIdUrl
  ) {
    this.sourcePayId = Objects.requireNonNull(sourcePayId);
    this.sourceUriTemplate = Objects.requireNonNull(sourceUriTemplate);
    this.expectedPayIdUrl = Objects.requireNonNull(expectedPayIdUrl);
    this.resolver = new InteractiveModePayIDResolver();
  }
  // https://example.com/.well-known/webfinger?resource=payid:alice%40example.net%20shoppingsite.example
  // sub-delims    = "!" / "$" / "&" / "'" / "(" / ")"
  //                 / "*" / "+" / "," / ";" / "="
  // payid:!alice@foo.com$example.com
  // https://example.com/alice@foo.com
  // A-Z0-9-_
  // sappenin+costco@gmail.com
  /**
   * The data for this test...
   */
  @Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][]{
      //0
      {
        PayID.of("payid:alice$example.com"), // payid
        "https://example.com/{acctpart}", // template
        "https://example.com/alice", // expectedToString
      },
      // 1 Two dollar-signs
      {
        PayID.of("payid:alice$foo.com$example.com"), // payid
        "https://example.com/{acctpart}", // template
        "https://example.com/alice$foo.com", // expectedToString
      },
      // 1 Three dollar-signs
      {
        PayID.of("payid:alice$foo.com$bar.com$example.com"), // payid
        "https://example.com/{acctpart}", // template
        "https://example.com/alice$foo.com$bar.com", // expectedToString
      },
      // 1 Sub-delims + "@" + ":"
      {
        PayID.of("payid:alice!$&'()*+,;=@:$example.com"), // payid
        "https://example.com/{acctpart}", // template
        "https://example.com/alice!$&'()*+,;=@:", // expectedToString
      },
      {
        PayID.of("payid:alice!$&'()*+,;=@:$example.com"), // payid
        "https://example.com?users={acctpart}", // template
        "https://example.com/?users=alice%21%24%26%27%28%29*%2B%2C%3B%3D%40%3A", // expectedToString
      },
      {
        PayID.of("payid:alice$foo.com$example.com"), // payid
        "https://example.com?users={acctpart}", // template
        "https://example.com/?users=alice%24foo.com", // expectedToString
      },
      {
        PayID.of("payid:alice&bob$foo.com$example.com"), // payid
        "https://example.com?users={acctpart}", // template
        "https://example.com/?users=alice%26bob%24foo.com", // expectedToString
      },
      // 1 Pchar
      {
        PayID.of("payid:alice!$&'()*+,;=$example.com"), // payid
        "https://example.com/{acctpart}", // template
        "https://example.com/alice!$&'()*+,;=", // expectedToString
      },
      //1 (unreserved chars)
      {
        PayID.of("payid:abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-._~$example.com"), // input
        "https://example.com/{acctpart}", // template
        "https://example.com/abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz0123456789-._~", // expectedToString
      },
      {
        PayID.of("payid:abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-._~$example.com"), // input
        "https://example.com/?users={acctpart}", // template
        "https://example.com/?users=abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz0123456789-._%7E", // expectedToString
      },
//      //4 (unreserved first, then sub-delims)
//      {
//        "payid:aliCE$!example.net", // input
//        "alice", // accountPart
//        "!example.net", // host
//        "payid:alice$!example.net", // expectedToString
//      },
//      //5 (percent-encoded $)
//      {
//        "payid:alice%24wallet.example$bank.example.net", // input
//        "alice%24wallet.example", // accountPart
//        "bank.example.net", // host
//        "payid:alice%24wallet.example$bank.example.net", // expectedToString
//      },
//      //6 (encoded IDN)
//      {
//        "payid:alice$nic.xn--rovu88b", // input
//        "alice", // accountPart
//        "nic.xn--rovu88b", // host
//        "payid:alice$nic.xn--rovu88b", // expectedToString
//      },
//      //7 (host with port)
//      {
//        "payid:alice$example.com:8080", // input
//        "alice", // accountPart
//        "example.com:8080", // host
//        "payid:alice$example.com:8080", // expectedToString
//      },
//      //8 (Capitalized PAYID)
//      {
//        "PAYID:alice$example.com:8080", // input
//        "alice", // accountPart
//        "example.com:8080", // host
//        "payid:alice$example.com:8080", // expectedToString
//      },
//      //9 (accept ":" in host)
//      {
//        "payid:alice$:example.com", // input
//        "alice", // accountPart
//        ":example.com", // host
//        "payid:alice$:example.com", // expectedToString
//      }
    });
  }
  @Test
  public void testValidValues() {
    HttpUrl payIdUrl = resolver.expandUrlTemplate(this.sourceUriTemplate, this.sourcePayId);
    String parsedUrl = new Builder()
      .scheme("https")
      .host(sourcePayId.host())
      .addQueryParameter("users", sourcePayId.account())
      .build().toString();
    assertThat(payIdUrl.toString()).isEqualTo(expectedPayIdUrl);
  }
//  @Test
//  public void testEquality() {
//    final String firstPayID = "payid:alice$sub1.example.net";
//    final String secondPayID = "payid:alice$sub2.example.net";
//
//    assertThat(firstPayID).isNotEqualTo(secondPayID);
//    assertThat(secondPayID).isNotEqualTo(firstPayID);
//    assertThat(firstPayID).isEqualTo(PayID.of("payid:alice$sub1.example.net").toString());
//    assertThat(firstPayID).isNotEqualTo(PayID.of("payid:alice$sub2.example.net").toString());
//  }
//
//  @Test
//  public void testupperCasePercentEncoded() {
//    assertThat(upperCasePercentEncoded("foo")).isEqualTo("foo");
//    assertThat(upperCasePercentEncoded("Foo$%af%2eBAR")).isEqualTo("Foo$%AF%2EBAR");
//    assertThat(upperCasePercentEncoded("foo$%af%2ebar")).isEqualTo("foo$%AF%2Ebar");
//  }
}
