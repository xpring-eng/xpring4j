package io.xpring.xrpl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import io.xpring.xrpl.model.AccountRootFlag;
import io.xpring.xrpl.model.PaymentFlag;
import org.junit.Test;

public class FlagsTest {
  @Test
  public void testPaymentFlagCheckContainsValue() {
    // GIVEN a set of flags that contains the tfPartialPayment flag.
    int flags = PaymentFlag.TF_PARTIAL_PAYMENT.value | 1 | 4; // 1 and 4 are arbitrarily chosen numbers.

    // WHEN the presence of tfPartialPayment is checked THEN the flag is reported as present.
    assertTrue(PaymentFlag.check(PaymentFlag.TF_PARTIAL_PAYMENT, flags));
  }

  @Test
  public void testPaymentFlagCheckDoesNotContainValue() {
    // GIVEN a set of flags that does not contain the tfPartialPayment flag.
    int flags = 1 | 4; // 1 and 4 are arbitrarily chosen numbers.

    // WHEN the presence of tfPartialPayment is checked THEN the flag is reported as not present.
    assertFalse(PaymentFlag.check(PaymentFlag.TF_PARTIAL_PAYMENT, flags));
  }

  @Test
  public void testAccountRootFlagCheckContainsValue() {
    // GIVEN a set of flags that contains the lsfDepositAuth flag.
    int flags = AccountRootFlag.LSF_DEPOSIT_AUTH.value | 2 | 8; // 2 and 8 are arbitrarily chosen numbers.

    // WHEN the presence of lsfDepositAuth is checked THEN the flag is reported as present.
    assertTrue(AccountRootFlag.check(AccountRootFlag.LSF_DEPOSIT_AUTH, flags));
  }

  @Test
  public void testAccountRootFlagCheckDoesNotContainValue() {
    // GIVEN a set of flags that does not contain the lsfDepositAuth flag.
    int flags = 2 | 8; // 2 and 8 are arbitrarily chosen numbers.

    // WHEN the presence of lsfDepositAuth is checked THEN the flag is reported as not present.
    assertFalse(AccountRootFlag.check(AccountRootFlag.LSF_DEPOSIT_AUTH, flags));
  }
}
