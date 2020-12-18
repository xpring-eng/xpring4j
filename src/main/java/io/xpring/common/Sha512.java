package io.xpring.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sha512 {
  private MessageDigest messageDigest;

  /**
   * Default constructor.
   */
  public Sha512() {
    try {
      messageDigest = MessageDigest.getInstance("SHA-512");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  public Sha512(byte[] start) {
    this();
    add(start);
  }

  public Sha512 add(byte[] bytes) {
    messageDigest.update(bytes);
    return this;
  }

  /**
   * Calculates sha512 hash for the given value.
   *
   * @param value value to hash.
   * @return hash result.
   */
  public Sha512 addU32(int value) {
    messageDigest.update((byte) ((value >>> 24) & 0xFF));
    messageDigest.update((byte) ((value >>> 16) & 0xFF));
    messageDigest.update((byte) ((value >>> 8) & 0xFF));
    messageDigest.update((byte) ((value) & 0xFF));
    return this;
  }

  private byte[] finishTaking(int size) {
    byte[] hash = new byte[size];
    System.arraycopy(messageDigest.digest(), 0, hash, 0, size);
    return hash;
  }

  public byte[] finish128() {
    return finishTaking(16);
  }

  public byte[] finish256() {
    return finishTaking(32);
  }

  public byte[] finish() {
    return messageDigest.digest();
  }
}
