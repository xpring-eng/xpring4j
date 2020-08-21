package io.xpring.xrpl;

import org.junit.Test;

public class XrpTrustSetProtoConversionTest {
  @Test
  public void trustSetRequiredFieldsTest() {
    // GIVEN a TrustSet protocol buffer with required fields set.
    // WHEN the protocol buffer is converted to a native Java object.
    // THEN the TrustSet converted as expected.
  }

  @Test
  public void trustSetAllFieldsTest() {
    // GIVEN a TrustSet protocol buffer with all fields set.
    // WHEN the protocol buffer is converted to a native Java object.
    // THEN the TrustSet converted as expected.
  }

  @Test
  public void invalidTrustSetMissingLimitAmountTest() {
    // GIVEN a TrustSet protocol buffer missing a limit amount.
    // WHEN the protocol buffer is converted to a native Java object.
    // THEN the TrustSet is null.
  }
}
