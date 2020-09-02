package io.xpring.xrpl;

import static io.xpring.xrpl.Utils.dropsToXrp;
import static io.xpring.xrpl.Utils.isTestNetwork;
import static io.xpring.xrpl.Utils.xrpToDrops;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import io.xpring.common.XrplNetwork;
import org.junit.Test;

import java.util.Optional;

/**
 * Unit tests for {@link Utils}.
 */
public class UtilsTest {
  @Test
  public void testIsValidAddressValidClassicAddress() {
    assertTrue(Utils.isValidAddress("rU6K7V3Po4snVhBBaU29sesqs2qTQJWDw1"));
  }

  @Test
  public void testIsValidAddressValidXAddress() {
    assertTrue(Utils.isValidAddress("XVLhHMPHU98es4dbozjVtdWzVrDjtV18pX8yuPT7y4xaEHi"));
  }

  @Test
  public void testIsValidAddressInvalidAlphabet() {
    assertFalse(Utils.isValidAddress("1EAG1MwmzkG6gRZcYqcRMfC17eMt8TDTit"));
  }

  @Test
  public void testIsValidAddressInvalidClassicAddressChecksum() {
    assertFalse(Utils.isValidAddress("rU6K7V3Po4sBBBBBaU29sesqs2qTQJWDw1"));
  }

  @Test
  public void testIsValidAddressInvalidCharacters() {
    assertFalse(Utils.isValidAddress("rU6K7V3Po4sBBBBBaU@#$%qs2qTQJWDw1"));
  }

  @Test
  public void testIsValidAddressTooLong() {
    assertFalse(Utils.isValidAddress("rU6K7V3Po4snVhBBaU29sesqs2qTQJWDw1rU6K7V3Po4snVhBBaU29sesqs2qTQJWDw1"));
  }

  @Test
  public void testIsValidAddressTooShort() {
    assertFalse(Utils.isValidAddress("rU6K7V3Po4s2qTQJWDw1"));
  }

  @SuppressWarnings("checkstyle:LocalVariableName")
  @Test
  public void testEncodeMainNetXAddressWithAddressAndTag() {
    // GIVEN a valid classic address and a tag on MainNet.
    String address = "rU6K7V3Po4snVhBBaU29sesqs2qTQJWDw1";
    int tag = 12345;
    ClassicAddress classicAddress = ImmutableClassicAddress.builder().address(address).tag(tag).isTest(false).build();

    // WHEN they are encoded to an X-Address.
    String xAddress = Utils.encodeXAddress(classicAddress);

    // THEN the result is as expected.
    assertEquals(xAddress, "XVfC9CTCJh6GN2x8bnrw3LtdbqiVCUvtU3HnooQDgBnUpQT");
  }

  @SuppressWarnings("checkstyle:LocalVariableName")
  @Test
  public void testEncodeTestNetXAddressWithAddressAndTag() {
    // GIVEN a valid classic address and a tag on TestNet.
    String address = "rU6K7V3Po4snVhBBaU29sesqs2qTQJWDw1";
    int tag = 12345;
    ClassicAddress classicAddress = ImmutableClassicAddress.builder().address(address).tag(tag).isTest(true).build();

    // WHEN they are encoded to an X-Address.
    String xAddress = Utils.encodeXAddress(classicAddress);

    // THEN the result is as expected.
    assertEquals(xAddress, "TVsBZmcewpEHgajPi1jApLeYnHPJw82v9JNYf7dkGmWphmh");
  }

  @SuppressWarnings("checkstyle:LocalVariableName")
  @Test
  public void testEncodeXAddressWithAddressOnly() {
    // GIVEN a valid classic address without a tag.
    ClassicAddress classicAddress =
        ImmutableClassicAddress.builder().address("rU6K7V3Po4snVhBBaU29sesqs2qTQJWDw1").isTest(false).build();

    // WHEN it is encoded to an X-Address.
    String xAddress = Utils.encodeXAddress(classicAddress);

    // THEN the result is as expected.
    assertEquals(xAddress, "XVfC9CTCJh6GN2x8bnrw3LtdbqiVCUFyQVMzRrMGUZpokKH");
  }

  @SuppressWarnings("checkstyle:LocalVariableName")
  @Test
  public void testEncodeXAddressWithAddressOnlyOnTestnet() {
    // GIVEN a valid classic address without a tag on testnet.
    ClassicAddress classicAddress = ImmutableClassicAddress.builder()
        .address("rU6K7V3Po4snVhBBaU29sesqs2qTQJWDw1")
        .tag(Optional.empty())
        .isTest(true)
        .build();

    // WHEN it is encoded to an X-Address.
    String xAddress = Utils.encodeXAddress(classicAddress);

    // THEN the result is as expected.
    assertEquals(xAddress, "TVsBZmcewpEHgajPi1jApLeYnHPJw8VrMCKS5g28oDXYiVA");
  }

  @SuppressWarnings("checkstyle:LocalVariableName")
  @Test
  public void testEncodeXAddressWithInvalidAddress() {
    // GIVEN an invalid address.
    ClassicAddress classicAddress = ImmutableClassicAddress.builder().address("xrp").isTest(false).build();

    // WHEN it is encoded to an X-Address.
    String xAddress = Utils.encodeXAddress(classicAddress);

    // THEN the result is null.
    assertNull(xAddress);
  }

  @Test
  public void testDecodeXAddressWithValidMainNetAddressContainingTag() {
    // GIVEN an X-Address that encodes an address on mainnet and a tag.
    String address = "XVfC9CTCJh6GN2x8bnrw3LtdbqiVCUvtU3HnooQDgBnUpQT";

    // WHEN it is decoded to an classic address.
    ClassicAddress classicAddress = Utils.decodeXAddress(address);

    // Then the decoded address and tag as are expected.
    assertEquals(classicAddress.address(), "rU6K7V3Po4snVhBBaU29sesqs2qTQJWDw1");
    assertEquals(classicAddress.tag().get(), Integer.valueOf(12345));
    assertFalse(classicAddress.isTest());
  }

  @Test
  public void testDecodeXAddressWithValidTestNetAddressContainingTag() {
    // GIVEN an x-address that encodes an address on a testnet and a tag.
    String address = "TVsBZmcewpEHgajPi1jApLeYnHPJw82v9JNYf7dkGmWphmh";

    // WHEN it is decoded to an classic address.
    ClassicAddress classicAddress = Utils.decodeXAddress(address);

    // Then the decoded address and tag as are expected.
    assertEquals(classicAddress.address(), "rU6K7V3Po4snVhBBaU29sesqs2qTQJWDw1");
    assertEquals(classicAddress.tag().get(), new Integer(12345));
    assertTrue(classicAddress.isTest());
  }

  @Test
  public void testDecodeXAddressWithValidAddressWithoutTag() {
    // GIVEN an X-Address that encodes an address and no tag.
    String address = "XVfC9CTCJh6GN2x8bnrw3LtdbqiVCUFyQVMzRrMGUZpokKH";

    // WHEN it is decoded to an classic address.
    ClassicAddress classicAddress = Utils.decodeXAddress(address);

    // Then the decoded address and tag as are expected.
    assertEquals(classicAddress.address(), "rU6K7V3Po4snVhBBaU29sesqs2qTQJWDw1");
    assertFalse(classicAddress.tag().isPresent());
    assertFalse(classicAddress.isTest());
  }

  @Test
  public void testDecodeXAddressWithInvalidXAddress() {
    // GIVEN an invalid address.
    String address = "xrp";

    // WHEN it is decoded to an classic address.
    ClassicAddress classicAddress = Utils.decodeXAddress(address);

    // Then the decoded address is null.
    assertNull(classicAddress);
  }

  @Test
  public void testIsValidXAddressWithValidXAddress() {
    assertTrue(Utils.isValidXAddress("XVfC9CTCJh6GN2x8bnrw3LtdbqiVCUvtU3HnooQDgBnUpQT"));
  }

  @Test
  public void testIsValidXAddressWithValidClassicAddress() {
    assertFalse(Utils.isValidXAddress("rU6K7V3Po4snVhBBaU29sesqs2qTQJWDw1"));
  }

  @Test
  public void testIsValidXAddressWithInvalidAddress() {
    assertFalse(Utils.isValidXAddress("xrp"));
  }

  @Test
  public void testIsValidClassicAddressWithValidXAddress() {
    assertFalse(Utils.isValidClassicAddress("XVfC9CTCJh6GN2x8bnrw3LtdbqiVCUvtU3HnooQDgBnUpQT"));
  }

  @Test
  public void testIsValidClassicAddressWithValidClassicAddress() {
    assertTrue(Utils.isValidClassicAddress("rU6K7V3Po4snVhBBaU29sesqs2qTQJWDw1"));
  }

  @Test
  public void testIsValidClassicAddressWithInvalidAddress() {
    assertFalse(Utils.isValidClassicAddress("xrp"));
  }

  @SuppressWarnings("checkstyle:LineLength")
  @Test
  public void testToTransactionHashValidTransaction() {
    String transactionBlob =
        "120000240000000561400000000000000168400000000000000C73210261BBB9D242440BA38375DAD79B146E559A9DFB99055F7077DA63AE0D643CA0E174473045022100C8BB1CE19DFB1E57CDD60947C5D7F1ACD10851B0F066C28DBAA3592475BC3808022056EEB85CC8CD41F1F1CF635C244943AD43E3CF0CE1E3B7359354AC8A62CF3F488114F8942487EDB0E4FD86190BF8DCB3AF36F608839D83141D10E382F805CD7033CC4582D2458922F0D0ACA6";
    String expectedHash = "7B9F6E019C2A79857427B4EF968D77D683AC84F5A880830955D7BDF47F120667";
    String hash = Utils.toTransactionHash(transactionBlob);
    assertEquals(hash, expectedHash);
  }

  @Test
  public void testToTransactionHashInvalidTransaction() {
    String hash = Utils.toTransactionHash("xrp");
    assertNull(hash, null);
  }

  // xrpToDrops and dropsToXrp tests =====================================================
  @Test
  public void dropsToXrpWorksWithTypicalAmount() throws XrpException {
    // GIVEN a typical, valid drops value, WHEN converted to xrp
    String xrp = dropsToXrp("2000000");

    // THEN the conversion is as expected
    assertEquals("2 million drops equals 2 XRP", "2", xrp);
  }

  @Test
  public void dropsToXrpWorksWithFractions() throws XrpException {
    // GIVEN drops amounts that convert to fractional xrp amounts
    // WHEN converted to xrp THEN the conversion is as expected
    String xrp = dropsToXrp("3456789");
    assertEquals("3,456,789 drops equals 3.456789 XRP","3.456789", xrp);

    xrp = dropsToXrp("3400000");
    assertEquals("3,400,000 drops equals 3.4 XRP", "3.4", xrp);

    xrp = dropsToXrp("1");
    assertEquals("1 drop equals 0.000001 XRP", "0.000001", xrp);

    xrp = dropsToXrp("1.0");
    assertEquals("1.0 drops equals 0.000001 XRP", "0.000001", xrp);

    xrp = dropsToXrp("1.00");
    assertEquals("1.00 drops equals 0.000001 XRP", "0.000001", xrp);
  }

  @Test
  public void dropsToXrpWorksWithZero() throws XrpException {
    // GIVEN several equivalent representations of zero
    // WHEN converted to xrp, THEN the result is zero
    String xrp = dropsToXrp("0");
    assertEquals("0 drops equals 0 XRP", "0", xrp);

    // negative zero is equivalent to zero
    xrp = dropsToXrp("-0");
    assertEquals("-0 drops equals 0 XRP", "0", xrp);

    xrp = dropsToXrp("0.00");
    assertEquals("0.00 drops equals 0 XRP", "0", xrp);

    xrp = dropsToXrp("000000000");
    assertEquals("000000000 drops equals 0 XRP", "0", xrp);
  }

  @Test
  public void dropsToXrpWorksWithNegativeValues() throws XrpException {
    // GIVEN a negative drops amount
    // WHEN converted to xrp
    String xrp = dropsToXrp("-2000000");

    // THEN the conversion is also negative
    assertEquals("-2 million drops equals -2 XRP", "-2", xrp);
  }

  @Test
  public void dropsToXrpWorksWithValueEndingWithDecimalPoint() throws XrpException {
    // GIVEN a positive or negative drops amount that ends with a decimal point
    // WHEN converted to xrp THEN the conversion is successful and correct
    String xrp = dropsToXrp("2000000.");
    assertEquals("2000000. drops equals 2 XRP", "2", xrp);

    xrp = dropsToXrp("-2000000.");
    assertEquals("-2000000. drops equals -2 XRP", "-2", xrp);
  }

  @Test
  public void dropsToXrpThrowsWithAnAmountWithTooManyDecimalPlaces() {
    assertThrows("has too many decimal places", XrpException.class, () -> dropsToXrp("1.2"));
    assertThrows("has too many decimal places", XrpException.class, () -> dropsToXrp("0.10"));
  }

  @Test
  public void dropsToXrpThrowsWithAnInvalidValue() {
    // GIVEN invalid drops values, WHEN converted to xrp, THEN an exception is thrown
    assertThrows("invalid value", XrpException.class, () -> dropsToXrp("FOO"));
    assertThrows("invalid value", XrpException.class, () -> dropsToXrp("1e-7"));
    assertThrows("invalid value", XrpException.class, () -> dropsToXrp("2,0"));
    assertThrows("invalid value", XrpException.class, () -> dropsToXrp("."));
  }

  @Test
  public void dropsToXrpThrowsWithAnAmountMoreThanOneDecimalPoint() {
    // GIVEN invalid drops values that contain more than one decimal point
    // WHEN converted to xrp THEN an exception is thrown
    assertThrows("invalid value", XrpException.class, () -> dropsToXrp("1.0.0"));
    assertThrows("invalid value", XrpException.class, () -> dropsToXrp("..."));
  }

  @Test
  public void dropsToXrpThrowsWithNullArgument() {
    // GIVEN a null drops value, WHEN converted to XRP,
    // THEN an exception is thrown
    assertThrows("null argument", NullPointerException.class, () -> dropsToXrp(null));
  }

  @Test
  public void xrpToDropsWorksWithATypicalAmount() throws XrpException {
    // GIVEN an xrp amount that is typical and valid
    // WHEN converted to drops
    String drops = xrpToDrops("2");

    // THEN the conversion is successful and correct
    assertEquals("2 XRP equals 2 million drops", "2000000", drops);
  }

  @Test
  public void xrpToDropsWorksWithFractions() throws XrpException {
    // GIVEN xrp amounts that are fractional
    // WHEN converted to drops THEN the conversions are successful and correct
    String drops = xrpToDrops("3.456789");
    assertEquals("3.456789 XRP equals 3,456,789 drops", "3456789", drops);
    drops = xrpToDrops("3.400000");
    assertEquals("3.400000 XRP equals 3,400,000 drops", "3400000", drops);
    drops = xrpToDrops("0.000001");
    assertEquals("0.000001 XRP equals 1 drop", "1", drops);
    drops = xrpToDrops("0.0000010");
    assertEquals("0.0000010 XRP equals 1 drop", "1", drops);
  }

  @Test
  public void xrpToDropsWorksWithZero() throws XrpException {
    // GIVEN xrp amounts that are various equivalent representations of zero
    // WHEN converted to drops THEN the conversions are equal to zero
    String drops = xrpToDrops("0");
    assertEquals("0 XRP equals 0 drops", "0", drops);
    drops = xrpToDrops("-0"); // negative zero is equivalent to zero
    assertEquals("-0 XRP equals 0 drops", "0", drops);
    drops = xrpToDrops("0.000000");
    assertEquals("0.000000 XRP equals 0 drops", "0", drops);
    drops = xrpToDrops("0.0000000");
    assertEquals( "0.0000000 XRP equals 0 drops", "0", drops);
  }

  @Test
  public void xrpToDropsWorksWithNegativeValues() throws XrpException {
    // GIVEN a negative xrp amount
    // WHEN converted to drops THEN the conversion is also negative
    String drops = xrpToDrops("-2");
    assertEquals("-2 XRP equals -2 million drops", "-2000000", drops);
  }

  @Test
  public void xrpToDropsWorksWithAValueEndingWithADecimalPoint() throws XrpException {
    // GIVEN an xrp amount that ends with a decimal point
    // WHEN converted to drops THEN the conversion is correct and successful
    String drops = xrpToDrops("2.");
    assertEquals("2. XRP equals 2000000 drops", "2000000", drops);
    drops = xrpToDrops("-2.");
    assertEquals( "-2. XRP equals -2000000 drops", "-2000000", drops);
  }

  @Test
  public void xrpToDropsThrowsWithAnAmountWithTooManyDecimalPlaces() {
    // GIVEN an xrp amount with too many decimal places
    // WHEN converted to a drops amount THEN an exception is thrown
    assertThrows("has too many decimal places", XrpException.class, () -> xrpToDrops("1.1234567"));
    assertThrows("has too many decimal places", XrpException.class, () -> xrpToDrops("0.0000001"));
  }

  @Test
  public void xrpToDropsThrowsWithAnInvalidValue() {
    // GIVEN xrp amounts represented as various invalid values
    // WHEN converted to drops THEN an exception is thrown
    assertThrows("invalid value", XrpException.class, () -> xrpToDrops("FOO"));
    assertThrows("invalid value", XrpException.class, () -> xrpToDrops("1e-7"));
    assertThrows("invalid value", XrpException.class, () -> xrpToDrops("2,0"));
    assertThrows("invalid value", XrpException.class, () -> xrpToDrops("."));
  }

  @Test
  public void xrpToDropsThrowsWithAnAmountMoreThanOneDecimalPoint() {
    // GIVEN an xrp amount with more than one decimal point, or all decimal points
    // WHEN converted to drops THEN an exception is thrown
    assertThrows("invalid value", XrpException.class, () -> xrpToDrops("1.0.0"));
    assertThrows("invalid value", XrpException.class, () -> xrpToDrops("..."));
  }

  @Test
  public void xrpToDropsThrowsWithNullArgument() {
    // GIVEN a null xrp value, WHEN converted to drops,
    // THEN an exception is thrown
    assertThrows("null argument", NullPointerException.class, () -> xrpToDrops(null));
  }

  @Test
  public void isTestNetworkWithTest() {
    // GIVEN an XrplNetwork of testnet, WHEN checked if it's a test network,
    // THEN it is a test network
    assertTrue("testnet not a test network", isTestNetwork(XrplNetwork.TEST));
  }

  @Test
  public void isTestNetworkWithDev() {
    // GIVEN an XrplNetwork of devnet, WHEN checked if it's a test network,
    // THEN it is a test network
    assertTrue("devnet not a test network", isTestNetwork(XrplNetwork.DEV));
  }

  @Test
  public void isTestNetworkWithMain() {
    // GIVEN an XrplNetwork of mainnet, WHEN checked if it's a test network,
    // THEN it is not a test network
    assertFalse("mainnet is a test network", isTestNetwork(XrplNetwork.MAIN));
  }
}
