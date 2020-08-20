package io.xpring.xrpl;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import io.xpring.xrpl.model.XrpSetRegularKey;
import org.junit.Test;
import org.xrpl.rpc.v1.SetRegularKey;

public class XrpSetRegularKeyProtoConversionTest {
  @Test
  public void setRegularKeyWithKeyTest() {
    // GIVEN a SetRegularKey protocol buffer with regularKey set.
    final SetRegularKey setRegularKeyProto = FakeXrpTransactionProtobufs.setRegularKeyWithKey;

    // WHEN the protocol buffer is converted to a native Java object.
    final XrpSetRegularKey setRegularKey = XrpSetRegularKey.from(setRegularKeyProto);

    // THEN the SetRegularKey converted as expected.
    assertThat(setRegularKey.regularKey().get())
        .isEqualTo(setRegularKeyProto.getRegularKey().getValue().getAddress());
  }

  @Test
  public void setRegularKeyWithNoKeyTest() {
    // GIVEN a SetRegularKey protocol buffer with no regularKey set.
    final SetRegularKey setRegularKeyProto = FakeXrpTransactionProtobufs.setRegularKeyWithNoKey;

    // WHEN the protocol buffer is converted to a native Java object.
    final XrpSetRegularKey setRegularKey = XrpSetRegularKey.from(setRegularKeyProto);

    // THEN the SetRegularKey converted as expected.
    assertThat(setRegularKey.regularKey()).isEmpty();
  }
}
