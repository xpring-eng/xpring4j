package io.xpring;

import org.junit.Test;

import static org.junit.Assert.*;

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
        String address = "rU6K7V3Po4snVhBBaU29sesqs2qTQJWDw1";
        long tag = 12345;

        // WHEN they are encoded to an X-Address.
        String xAddress = Utils.encodeXAddress(address, tag);

        // THEN the result is as expected.
        assertEquals(xAddress, "XVfC9CTCJh6GN2x8bnrw3LtdbqiVCUvtU3HnooQDgBnUpQT");
    }

    @Test
    public void testEncodeXAddressWithAddressOnly() {
        // GIVEN a valid classic address.
        String address = "rU6K7V3Po4snVhBBaU29sesqs2qTQJWDw1";

        // WHEN it is encoded to an X-Address.
        String xAddress = Utils.encodeXAddress(address, null);

        // THEN the result is as expected.
        assertEquals(xAddress, "XVfC9CTCJh6GN2x8bnrw3LtdbqiVCUFyQVMzRrMGUZpokKH");
    }

    @Test
    public void testEncodeXAddressWithInvalidAddress() {
        // GIVEN an invalid address.
        String address = "xrp";

        // WHEN it is encoded to an X-Address.
        String xAddress = Utils.encodeXAddress(address, null);

        // THEN the result is null.
        assertNull(xAddress);
    }

    @Test
    public void testDecodeXAddressWithValidAddressContainingTag() {
        // GIVEN an X-Address that encodes an address and a tag.
        String address = "XVfC9CTCJh6GN2x8bnrw3LtdbqiVCUvtU3HnooQDgBnUpQT";

        // WHEN it is decoded to an classic address.
        ClassicAddress classicAddress = Utils.decodeXAddress(address);

        // Then the decoded address and tag as are expected.
        assertEquals(classicAddress.getAddress(), "rU6K7V3Po4snVhBBaU29sesqs2qTQJWDw1");
        assertEquals(classicAddress.getTag(), new Long(12345));
    }

    @Test
    public void testDecodeXAddressWithValidAddressWithoutTag() {
        // GIVEN an X-Address that encodes an address and no tag.
        String address = "XVfC9CTCJh6GN2x8bnrw3LtdbqiVCUFyQVMzRrMGUZpokKH";

        // WHEN it is decoded to an classic address.
        ClassicAddress classicAddress = Utils.decodeXAddress(address);

        // Then the decoded address and tag as are expected.
        assertEquals(classicAddress.getAddress(), "rU6K7V3Po4snVhBBaU29sesqs2qTQJWDw1");
        assertNull(classicAddress.getTag());
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

    @Test
    public void testToTransactionHashValidTransaction() {
        String transactionBlob = "120000240000000561400000000000000168400000000000000C73210261BBB9D242440BA38375DAD79B146E559A9DFB99055F7077DA63AE0D643CA0E174473045022100C8BB1CE19DFB1E57CDD60947C5D7F1ACD10851B0F066C28DBAA3592475BC3808022056EEB85CC8CD41F1F1CF635C244943AD43E3CF0CE1E3B7359354AC8A62CF3F488114F8942487EDB0E4FD86190BF8DCB3AF36F608839D83141D10E382F805CD7033CC4582D2458922F0D0ACA6";
        String expectedHash = "7B9F6E019C2A79857427B4EF968D77D683AC84F5A880830955D7BDF47F120667";
        String hash = Utils.toTransactionHash(transactionBlob);
        assertEquals(hash, expectedHash);
    }

    @Test
    public void testToTransactionHashInvalidTransaction() {
        String hash = Utils.toTransactionHash("xrp");
        assertNull(hash, null);
    }
}
