package io.xpring.codec.basex;

import com.google.common.io.BaseEncoding;

public interface IBaseX {
  String encode(byte[] input);

  byte[] decode(String input);

  byte[] findPrefix(int payLoadLength, String desiredPrefix);

  String encodeVersioned(byte[] input, Version version);

  Decoded decodeVersioned(String input, Version... possibleVersions);

  class Decoded {
    public final Version version;
    public final byte[] payload;

    Decoded(Version version, byte[] payload) {
      this.version = version;
      this.payload = payload;
    }
  }

  class Version {
    public final byte[] bytes;
    public final String name;
    public final int expectedLength;

    public Version(byte[] bytes, String name, int length) {
      this.bytes = bytes;
      this.name = name;
      this.expectedLength = length;
    }

    public Version(int value, String name, int length) {
      this(new byte[] {(byte) value}, name, length);
    }

    public Version(String hex, String name, int length) {
      this(BaseEncoding.base16().decode(hex), name, length);
    }
  }
}
