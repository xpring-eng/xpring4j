package org.interledger.spsp.server.config.model;


import org.interledger.crypto.CryptoKey;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * An implementation of {@link org.interledger.crypto.CryptoKey} that can be used by Spring Boot to load these properties from a YAML file.
 */
public class CryptoKeyFromPropertyFile implements CryptoKey {

  private String alias;

  private String version;

  @Override
  public String alias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  @Override
  public String version() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || !(o instanceof CryptoKey)) {
      return false;
    }
    CryptoKey that = (CryptoKey) o;
    return alias.equals(that.alias()) &&
      version.equals(that.version());
  }

  @Override
  public int hashCode() {
    return Objects.hash(alias, version);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", CryptoKeyFromPropertyFile.class.getSimpleName() + "[", "]")
      .add("alias='" + alias + "'")
      .add("version='" + version + "'")
      .toString();
  }
}
