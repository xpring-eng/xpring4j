package io.xpring.xrpl;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import io.xpring.xrpl.model.XrpSignerEntry;
import org.junit.Test;
import org.xrpl.rpc.v1.Common;

public class XrpSignerEntryProtoConversionTest {
  @Test
  public void signerEntryAllFieldsTest() {
    // GIVEN a SignerEntry protocol buffer with all fields set.
    Common.SignerEntry signerEntryProto = FakeXrpTransactionProtobufs.signerEntryAllFields;

    // WHEN the protocol buffer is converted to a native Java object.
    XrpSignerEntry signerEntry = XrpSignerEntry.from(signerEntryProto);

    // THEN the SignerEntry converted as expected.
    assertThat(signerEntry.account()).isEqualTo(signerEntryProto.getAccount().toString());
    assertThat(signerEntry.signerWeight()).isEqualTo(signerEntryProto.getSignerWeight().getValue());
  }

  @Test
  public void invalidSignerEntryNoFieldsTest() {
    // GIVEN a SignerEntry protocol buffer with no fields set.
    Common.SignerEntry signerEntryProto = FakeXrpTransactionProtobufs.invalidSignerEntryNoFields;

    // WHEN the protocol buffer is converted to a native Java object.
    XrpSignerEntry signerEntry = XrpSignerEntry.from(signerEntryProto);

    // THEN the SignerEntry is null;
    assertThat(signerEntry).isNull();
  }
}
