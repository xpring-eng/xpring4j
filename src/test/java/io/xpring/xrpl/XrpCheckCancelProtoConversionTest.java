package io.xpring.xrpl;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import io.xpring.xrpl.model.XrpCheckCancel;
import org.junit.Test;
import org.xrpl.rpc.v1.CheckCancel;

public class XrpCheckCancelProtoConversionTest {
  @Test
  public void checkCancelTest() {
    // GIVEN a CheckCancel protocol buffer.
    final CheckCancel checkCancelProto = FakeXrpTransactionProtobufs.checkCancelProto;

    // WHEN the protocol buffer is converted to a native Java object.
    final XrpCheckCancel checkCancel = XrpCheckCancel.from(checkCancelProto);

    // THEN the CheckCancel converted as expected.
    assertThat(checkCancel.checkId()).isEqualTo(checkCancelProto.getCheckId().getValue().toString());
  }

  @Test
  public void invalidCheckCancelTest() {

  }
}
