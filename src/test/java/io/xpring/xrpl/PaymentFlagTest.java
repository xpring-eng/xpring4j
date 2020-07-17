package io.xpring.xrpl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import io.xpring.xrpl.model.PaymentFlag;
import org.junit.Test;

public class PaymentFlagTest {
  @Test
  public void testCheckContainsValue() {
    // GIVEN a set of flags that contains the tfPartialPayment flag.
    int flags = PaymentFlag.TF_PARTIAL_PAYMENT.value | 1 | 4; // 1 and 4 are arbitrarily chosen numbers.

    // WHEN the presence of tfPartialPayment is checked THEN the flag is reported as present.
    assertTrue(PaymentFlag.check(PaymentFlag.TF_PARTIAL_PAYMENT, flags));
  }

  @Test
  public void testCheckDoesNotContainValue() {
    // GIVEN a set of flags that does not contain the tfPartialPayment flag.
    int flags = 1 | 4; // 1 and 4 are arbitrarily chosen numbers.

    // WHEN the presence of tfPartialPayment is checked THEN the flag is reported as not present.
    assertFalse(PaymentFlag.check(PaymentFlag.TF_PARTIAL_PAYMENT, flags));
  }
}
