package io.xpring.payid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public class PayIdUtilsTest {
  @Test
  public void testParseValidPayId() {
    // GIVEN a Pay ID with a host and a path.
    String host = "xpring.money";
    String path = "georgewashington";
    String rawPayId = path + "$" + host;

    // WHEN it is parsed to components.
    PayIdComponents payIDComponents = PayIdUtils.parsePayID(rawPayId);

    // THEN the host and path are set correctly.
    assertEquals(payIDComponents.host(), host);
    assertEquals(payIDComponents.path(), "/" + path);
  }

  @Test
  public void testParsePayIdMultipleDollarSigns() {
    // GIVEN a Pay ID with more than one '$'.
    String host = "xpring.money";
    String path = "george$$$washington$$$"; // Extra '$'s
    String rawPayId = path + "$" + host;

    // WHEN it is parsed to components.
    PayIdComponents payIDComponents = PayIdUtils.parsePayID(rawPayId);

    // THEN the host and path are set correctly.
    assertEquals(payIDComponents.host(), host);
    assertEquals(payIDComponents.path(), "/" + path);
  }

  @Test
  public void testParsePayIdNoDollarSigns() {
    // GIVEN a Pay ID with no '$'.
    String host = "xpring.money";
    String path = "georgewashington";
    String rawPayId = path + host;  // Assembled without $

    // WHEN it is parsed to components.
    PayIdComponents payIDComponents = PayIdUtils.parsePayID(rawPayId);

    // THEN the Pay ID failed to parse.
    assertNull(payIDComponents);
  }

  @Test
  public void testParsePayIdHostEndsWithDollarSign() {
    // GIVEN a Pay ID in which the host ends with a $.
    String host = "xpring.money$";
    String path = "georgewashington";
    String rawPayId = path + "$" + host;

    // WHEN it is parsed to components.
    PayIdComponents payIDComponents = PayIdUtils.parsePayID(rawPayId);

    // THEN the Pay ID failed to parse.
    assertNull(payIDComponents);
  }

  @Test
  public void testParsePayIdEmptyHost() {
    // GIVEN a Pay ID with an empty host.
    String host = "";
    String path = "georgewashington";
    String rawPayId = path + "$" + host;

    // WHEN it is parsed to components.
    PayIdComponents payIDComponents = PayIdUtils.parsePayID(rawPayId);

    // THEN the Pay ID failed to parse.
    assertNull(payIDComponents);
  }

  @Test
  public void testParsePayIdEmptyPath() {
    // GIVEN a Pay ID with an empty host.
    String host = "xpring.money"; // Extra '$'
    String path = "";
    String rawPayId = path + "$" + host;

    // WHEN it is parsed to components.
    PayIdComponents payIDComponents = PayIdUtils.parsePayID(rawPayId);

    // THEN the Pay ID failed to parse.
    assertNull(payIDComponents);
  }

  @Test
  public void testParsePayIdNonASCII() {
    // GIVEN a Pay ID with non-ascii characters.
    String rawPayId = "ZA̡͊͠͝LGΌIS̯͈͕̹̘̱ͮ$TO͇̹̺ͅƝ̴ȳ̳TH̘Ë͖́̉ ͠P̯͍̭O̚N̐Y̡";

    // WHEN it is parsed to components.
    PayIdComponents payIDComponents = PayIdUtils.parsePayID(rawPayId);

    // THEN the Pay ID failed to parse.
    assertNull(payIDComponents);
  }
}
