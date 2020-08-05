package io.xpring.common;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Unit tests for {@link CommonUtils}.
 */
public class CommonUtilsTest {

  @Test
  public void testStringToHexConvertsStringCorrectly() {
    // GIVEN the best line delivered on film
    // WHEN that line is converted to hex
    // THEN the hex value is correct
    assertEquals(CommonUtils.stringToHex("oh, hi mark"), "6f682c206869206d61726b");
  }

  @Test
  public void testStringToHexEmptyStringHandledCorrectly() {
    // GIVEN an empty string
    // WHEN that string is converted to hex
    // THEN the hex value is also empty
    assertEquals(CommonUtils.stringToHex(""), "");
  }

  @Test
  public void testStringToByteArrayHandlesBlankValuesCorrectly() {
    // GIVEN an empty string
    // WHEN that line is converted to a byte[]
    // THEN the value is undefined
    assertEquals(CommonUtils.stringToByteArray("", false), new byte[0]);
  }

  @Test
  public void testStringToByteArrayHandlesNonHexValuesCorrectly() {
    // GIVEN the best line delivered on film
    // WHEN that line is converted to a byte[]
    // THEN the value is correct
    byte[] expectedBytes = {111, 104, 44, 32, 104, 105, 32, 109, 97, 114, 107};
    assertEquals(
            CommonUtils.stringToByteArray("oh, hi mark", false),
            expectedBytes
    );
  }

  @Test
  public void testStringToByteArrayHandlesHexValuesCorrectly() {
    // GIVEN the hex version of the best line delivered on film
    // WHEN that line is converted to a byte[]
    // THEN the value is correct
    byte[] expectedBytes = {111, 104, 44, 32, 104, 105, 32, 109, 97, 114, 107};
    assertEquals(
            CommonUtils.stringToByteArray("6f682c206869206d61726b", true),
            expectedBytes
    );
  }
}
