package io.xpring.xrpl;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import io.xpring.xrpl.model.XrpCheckCash;
import io.xpring.xrpl.model.XrpCurrencyAmount;
import org.junit.Test;
import org.xrpl.rpc.v1.CheckCash;

public class XrpCheckCashProtoConversionTest {
  @Test
  public void checkCashAmountFieldSetTest() {
    // GIVEN a valid CheckCash protocol buffer with amount field set.
    final CheckCash checkCashProto = FakeXrpTransactionProtobufs.checkCashProtoWithAmount;

    // WHEN the protocol buffer is converted to a native Java object.
    final XrpCheckCash checkCash = XrpCheckCash.from(checkCashProto);

    // THEN the CheckCash converted as expected.
    assertThat(checkCash.amount().get())
        .isEqualTo(XrpCurrencyAmount.from(checkCashProto.getAmount().getValue()));
    assertThat(checkCash.checkId()).isEqualTo(checkCashProto.getCheckId().getValue().toString());
    assertThat(checkCash.deliverMin()).isEmpty();
  }

  @Test
  public void checkCashDeliverMinFieldSetTest() {
    // GIVEN a valid CheckCash protocol buffer with deliverMin field set.
    final CheckCash checkCashProto = FakeXrpTransactionProtobufs.checkCashProtoWithDeliverMin;

    // WHEN the protocol buffer is converted to a native Java object.
    final XrpCheckCash checkCash = XrpCheckCash.from(checkCashProto);

    // THEN the CheckCash converted as expected.
    assertThat(checkCash.amount()).isEmpty();
    assertThat(checkCash.checkId()).isEqualTo(checkCashProto.getCheckId().getValue().toString());
    assertThat(checkCash.deliverMin().get())
        .isEqualTo(XrpCurrencyAmount.from(checkCashProto.getDeliverMin().getValue()));
  }

  @Test
  public void invalidCheckCashMissingCheckIdTest() {
    // GIVEN an invalid CheckCash protocol buffer missing the checkId field.
    final CheckCash checkCashProto = FakeXrpTransactionProtobufs.invalidCheckCashProto;

    // WHEN the protocol buffer is converted to a native Java object.
    final XrpCheckCash checkCash = XrpCheckCash.from(checkCashProto);

    assertThat(checkCash).isNull();
  }
}
