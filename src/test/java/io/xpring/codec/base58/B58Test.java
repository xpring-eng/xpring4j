package io.xpring.codec.base58;

import static org.junit.Assert.assertEquals;

import com.google.common.io.BaseEncoding;
import io.xpring.codec.addresses.ClassicAddressCodec;
import org.bouncycastle.util.Arrays;
import org.junit.Test;

public class B58Test {

  @Test
  public void testFindsEdPrefix() throws Exception {
    String prefix = "sEd";
    byte[] versionBytes = ClassicAddressCodec.codec.findPrefix(16, prefix);
    testStability(16, prefix, versionBytes);
    assertEncodesTo("01E14B", versionBytes);
  }

  @Test
  public void testFindsecp256k1Prefix() throws Exception {
    String prefix = "secp256k1";
    byte[] versionBytes = ClassicAddressCodec.codec.findPrefix(16, prefix);
    testStability(16, prefix, versionBytes);
    assertEncodesTo("13984B20F2BD93", versionBytes);
  }

  private void testStability(@SuppressWarnings("SameParameterValue")
                                 int length, String prefix, byte[] versionBytes) {
    B58.Version version = new B58.Version(versionBytes, "test", length);
    testStabilityWithAllByteValuesAtIx(0, length, prefix, version);
    testStabilityWithAllByteValuesAtIx(length - 1, length, prefix, version);
  }

  private void testStabilityWithAllByteValuesAtIx(int ix,
                                                  int length,
                                                  String prefix,
                                                  B58.Version version) {
    byte[] sample = new byte[length];
    Arrays.fill(sample, (byte) 0xff);

    for (int i = 0; i < 256; i++) {
      sample[ix] = (byte) i;
      String encoded = ClassicAddressCodec.encode(sample, version);
      assertEquals(prefix, encoded.substring(0, prefix.length()));
    }
  }

  public void assertEncodesTo(String expected, byte[] actual) {
    assertEquals(expected, BaseEncoding.base16().encode(actual));
  }
}
