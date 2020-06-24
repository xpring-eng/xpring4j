package io.xpring.payid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public class PayIDUtilsTest {
  @Test
  public void testParseValidPayID() {
    // GIVEN a Pay ID with a host and a path.
    String host = "xpring.money";
    String path = "georgewashington";
    String rawPayID = path + "$" + host;

    // WHEN it is parsed to components.
    PayIDComponents payIDComponents = PayIDUtils.parsePayID(rawPayID);

    // THEN the host and path are set correctly.
    assertEquals(payIDComponents.host(), host);
    assertEquals(payIDComponents.path(), "/" + path);
  }

  @Test
  public void testParsePayIDMultipleDollarSigns() {
    // GIVEN a Pay ID with more than one '$'.
    String host = "xpring.money";
    String path = "george$$$washington$$$"; // Extra '$'s
    String rawPayID = path + "$" + host;

    // WHEN it is parsed to components.
    PayIDComponents payIDComponents = PayIDUtils.parsePayID(rawPayID);

    // THEN the host and path are set correctly.
    assertEquals(payIDComponents.host(), host);
    assertEquals(payIDComponents.path(), "/" + path);
  }

  @Test
  public void testParsePayIDNoDollarSigns() {
    // GIVEN a Pay ID with no '$'.
    String host = "xpring.money";
    String path = "georgewashington";
    String rawPayID = path + host;  // Assembled without $

    // WHEN it is parsed to components.
    PayIDComponents payIDComponents = PayIDUtils.parsePayID(rawPayID);

    // THEN the Pay ID failed to parse.
    assertNull(payIDComponents);
  }

  @Test
  public void testParsePayIDHostEndsWithDollarSign() {
    // GIVEN a Pay ID in which the host ends with a $.
    String host = "xpring.money$";
    String path = "georgewashington";
    String rawPayID = path + "$" + host;

    // WHEN it is parsed to components.
    PayIDComponents payIDComponents = PayIDUtils.parsePayID(rawPayID);

    // THEN the Pay ID failed to parse.
    assertNull(payIDComponents);
  }

  @Test
  public void testParsePayIDEmptyHost() {
    // GIVEN a Pay ID with an empty host.
    String host = "";
    String path = "georgewashington";
    String rawPayID = path + "$" + host;

    // WHEN it is parsed to components.
    PayIDComponents payIDComponents = PayIDUtils.parsePayID(rawPayID);

    // THEN the Pay ID failed to parse.
    assertNull(payIDComponents);
  }

  @Test
  public void testParsePayIDEmptyPath() {
    // GIVEN a Pay ID with an empty host.
    String host = "xpring.money"; // Extra '$'
    String path = "";
    String rawPayID = path + "$" + host;

    // WHEN it is parsed to components.
    PayIDComponents payIDComponents = PayIDUtils.parsePayID(rawPayID);

    // THEN the Pay ID failed to parse.
    assertNull(payIDComponents);
  }

  @Test
  public void testParsePayIDNonASCII() {
    // GIVEN a Pay ID with non-ascii characters.
    String rawPayID = "ZA̡͊͠͝LGΌIS̯͈͕̹̘̱ͮ$TO͇̹̺ͅƝ̴ȳ̳TH̘Ë͖́̉ ͠P̯͍̭O̚N̐Y̡";

    // WHEN it is parsed to components.
    PayIDComponents payIDComponents = PayIDUtils.parsePayID(rawPayID);

    // THEN the Pay ID failed to parse.
    assertNull(payIDComponents);
  }
}
