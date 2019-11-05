package io.xpring;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

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

    @Test
    public void testEncodeXAddressWithAddressAndTag() {
        // GIVEN a valid classic address and a tag.
        String address =  "rU6K7V3Po4snVhBBaU29sesqs2qTQJWDw1";
        long tag = 12345;

        // WHEN they are encoded to an x-address.
        String xAddress = Utils.encodeXAddress(address, tag);

        // THEN the result is as expected.
        assertEquals(xAddress, "XVfC9CTCJh6GN2x8bnrw3LtdbqiVCUvtU3HnooQDgBnUpQT");
    }

    @Test
    public void testEncodeXAddressWithAddressOnly() {
        // GIVEN a valid classic address.
        String address = "rU6K7V3Po4snVhBBaU29sesqs2qTQJWDw1";

        // WHEN it is encoded to an x-address.
        String xAddress = Utils.encodeXAddress(address, null);

        // THEN the result is as expected.
        assertEquals(xAddress, "XVfC9CTCJh6GN2x8bnrw3LtdbqiVCUFyQVMzRrMGUZpokKH");
    }

    @Test
    public void testEncodeXAddressWithInvalidAddress() {
        // GIVEN an invalid address.
        String address = "xrp";

        // WHEN it is encoded to an x-address.
        String xAddress = Utils.encodeXAddress(address, null);

        // THEN the result is undefined.
        assertNull(xAddress);
    }

    @Test
    public void testDecodeXAddressWithValidAddressContainingTag() {
        // GIVEN an x-address that encodes an address and a tag.
        String address = "XVfC9CTCJh6GN2x8bnrw3LtdbqiVCUvtU3HnooQDgBnUpQT";

        // WHEN it is decoded to an classic address
        ClassicAddress classicAddress = Utils.decodeXAddress(address);

        // Then the decoded address and tag as are expected.
        assertEquals(classicAddress.getAddress(), "rU6K7V3Po4snVhBBaU29sesqs2qTQJWDw1");
        assertEquals(classicAddress.getTag(), new Long(12345));
    }

    @Test
    public void testDecodeXAddressWithValidAddressWithoutTag() {
        // GIVEN an x-address that encodes an address and no tag.
        String address = "XVfC9CTCJh6GN2x8bnrw3LtdbqiVCUFyQVMzRrMGUZpokKH";

        // WHEN it is decoded to an classic address
        ClassicAddress classicAddress = Utils.decodeXAddress(address);

        // Then the decoded address and tag as are expected.
        assertEquals(classicAddress.getAddress(), "rU6K7V3Po4snVhBBaU29sesqs2qTQJWDw1");
        assertNull(classicAddress.getTag());
    }

    @Test
    public void testDecodeXAddressWithInvalidXAddress() {
        // GIVEN an invalid address
        String address = "xrp";

        // WHEN it is decoded to an classic address
        ClassicAddress classicAddress = Utils.decodeXAddress(address);

        // Then the decoded address is undefined.
        assertNull(classicAddress);
    }
}
