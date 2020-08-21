package io.xpring.xrpl;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import io.xpring.xrpl.model.XrpSignerEntry;
import io.xpring.xrpl.model.XrpSignerListSet;
import org.junit.Test;
import org.xrpl.rpc.v1.SignerListSet;

import java.util.stream.Collectors;

public class XrpSignerListSetProtoConversionTest {
  @Test
  public void signerListSetWithSignerEntriesTest() {
    // GIVEN a SignerListSet protocol buffer with no SignerEntries.
    SignerListSet signerListSetProto = FakeXrpTransactionProtobufs.signerListSetWithSignerEntries;

    // WHEN the protocol buffer is converted to a native Java object.
    XrpSignerListSet signerListSet = XrpSignerListSet.from(signerListSetProto);

    // THEN the SignerListSet converted correctly.
    assertThat(signerListSet.signerQuorum()).isEqualTo(signerListSetProto.getSignerQuorum().getValue());

    assertThat(signerListSet.signerEntries().get()).isEqualTo(signerListSetProto.getSignerEntriesList()
        .stream()
        .map(signerEntry -> XrpSignerEntry.from(signerEntry))
        .collect(Collectors.toList()));
  }

  @Test
  public void signerListSetWithNoSignerEntriesTest() {
    // GIVEN a SignerListSet protocol buffer with no SignerEntries.
    SignerListSet signerListSetProto = FakeXrpTransactionProtobufs.signerListSetWithNoSignerEntries;

    // WHEN the protocol buffer is converted to a native Java object.
    XrpSignerListSet signerListSet = XrpSignerListSet.from(signerListSetProto);

    // THEN the SignerListSet converted correctly.
    assertThat(signerListSet.signerQuorum()).isEqualTo(signerListSetProto.getSignerQuorum().getValue());

    assertThat(signerListSet.signerEntries()).isEmpty();
  }

  @Test
  public void invalidSignerListSetMissingSignerQuorumTest() {
    // GIVEN a SignerListSet protocol buffer with no SignerEntries.
    SignerListSet signerListSetProto = FakeXrpTransactionProtobufs.invalidSignerListSetMissingSignerQuorum;

    // WHEN the protocol buffer is converted to a native Java object.
    XrpSignerListSet signerListSet = XrpSignerListSet.from(signerListSetProto);

    // THEN the SignerListSet is null.
    assertThat(signerListSet).isNull();
  }
}
