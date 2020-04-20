package io.xpring.payid;

import static io.xpring.payid.AbstractPayID.upperCasePercentEncoded;
import static org.assertj.core.api.Assertions.assertThat;

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
public class PayIDValidValuesTest {

  private String sourcePayID;
  private String expectedAccountPart;
  private String expectedHost;
  private String expectedPayIDToString;

  /**
   * Required args constructor. Used by @Parameters annotated method to instantiate several test cases with different
   * inputs.
   *
   * @param sourcePayID A {@link String} representing a {@link PayID}.
   * @param expectedAccountPart A {@link String} representing the account part of a PayID that should be derived from
   *                            {@code sourcePayID}.
   * @param expectedHost A {@link String} representing the host part of a PayID that should be derived from
   *                     {@code sourcePayID}.
   * @param expectedPayIDToString A {@link String} containing the expected value of a {@code toString()} call on
   *                              a {@link PayID}.
   */
  public PayIDValidValuesTest(
      String sourcePayID,
      String expectedAccountPart,
      String expectedHost,
      String expectedPayIDToString
  ) {
    this.sourcePayID = Objects.requireNonNull(sourcePayID);
    this.expectedAccountPart = Objects.requireNonNull(expectedAccountPart);
    this.expectedHost = Objects.requireNonNull(expectedHost);
    this.expectedPayIDToString = Objects.requireNonNull(expectedPayIDToString);
  }

  /**
   * The data for this test...
   */
  @Parameters
  public static Collection<Object[]> data() {

    return Arrays.asList(new Object[][]{
      //0
      {
        "payid:alice$example.com", // input
        "alice", // accountPart
        "example.com", // host
        "payid:alice$example.com", // expectedToString
      },
      //1 (unreserved chars)
      {
        "payid:abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-._~$example.net", // input
        "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz0123456789-._~", // accountPart
        "example.net", // host
        "payid:abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz0123456789-._~$example.net", // expectedToString
      },
      //2 (sub-delims)
      {
        "payid:!&'()*+,;=$example.net", // input
        "!&'()*+,;=", // accountPart
        "example.net", // host
        "payid:!&'()*+,;=$example.net", // expectedToString
      },
      //3 (sub-delims first, then unreserved)
      {
        "payid:!alice$example.net", // input
        "!alice", // accountPart
        "example.net", // host
        "payid:!alice$example.net", // expectedToString
      },
      //4 (unreserved first, then sub-delims)
      {
        "payid:aliCE$!example.net", // input
        "alice", // accountPart
        "!example.net", // host
        "payid:alice$!example.net", // expectedToString
      },
      //5 (percent-encoded $)
      {
        "payid:alice%24wallet.example$bank.example.net", // input
        "alice%24wallet.example", // accountPart
        "bank.example.net", // host
        "payid:alice%24wallet.example$bank.example.net", // expectedToString
      },
      //6 (encoded IDN)
      {
        "payid:alice$nic.xn--rovu88b", // input
        "alice", // accountPart
        "nic.xn--rovu88b", // host
        "payid:alice$nic.xn--rovu88b", // expectedToString
      },
      //7 (host with port)
      {
        "payid:alice$example.com:8080", // input
        "alice", // accountPart
        "example.com:8080", // host
        "payid:alice$example.com:8080", // expectedToString
      },
      //8 (Capitalized PAYID)
      {
        "PAYID:alice$example.com:8080", // input
        "alice", // accountPart
        "example.com:8080", // host
        "payid:alice$example.com:8080", // expectedToString
      },
      //9 (accept ":" in host)
      {
        "payid:alice$:example.com", // input
        "alice", // accountPart
        ":example.com", // host
        "payid:alice$:example.com", // expectedToString
      }
    });
  }

  @Test
  public void testValidValues() {
    final PayID payID = PayID.of(sourcePayID);

    assertThat(payID).isNotNull();
    assertThat(payID.account()).isEqualTo(expectedAccountPart);
    assertThat(payID.host()).isEqualTo(expectedHost);
    assertThat(payID.toString()).isEqualTo(expectedPayIDToString);
  }

  @Test
  public void testEquality() {
    final String firstPayID = "payid:alice$sub1.example.net";
    final String secondPayID = "payid:alice$sub2.example.net";

    assertThat(firstPayID).isNotEqualTo(secondPayID);
    assertThat(secondPayID).isNotEqualTo(firstPayID);
    assertThat(firstPayID).isEqualTo(PayID.of("payid:alice$sub1.example.net").toString());
    assertThat(firstPayID).isNotEqualTo(PayID.of("payid:alice$sub2.example.net").toString());
  }

  @Test
  public void testupperCasePercentEncoded() {
    assertThat(upperCasePercentEncoded("foo")).isEqualTo("foo");
    assertThat(upperCasePercentEncoded("Foo$%af%2eBAR")).isEqualTo("Foo$%AF%2EBAR");
    assertThat(upperCasePercentEncoded("foo$%af%2ebar")).isEqualTo("foo$%AF%2Ebar");
  }
}
