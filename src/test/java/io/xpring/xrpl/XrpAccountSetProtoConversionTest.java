package io.xpring.xrpl;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import io.xpring.xrpl.model.XrpAccountSet;
import org.junit.Test;
import org.xrpl.rpc.v1.AccountSet;

public class XrpAccountSetProtoConversionTest {
  @Test
  public void allFieldsSetTest() {
    // GIVEN an AccountSet protocol buffer with all fields set.
    final AccountSet accountSetProto = FakeXrpTransactionProtobufs.allFieldsAccountSet;
    // WHEN the protocol buffer is converted to a native Java object.
    final XrpAccountSet accountSet = XrpAccountSet.from(accountSetProto);

    // THEN the AccountSet converted as expected.
    assertThat(accountSet.clearFlag().get()).isEqualTo(accountSetProto.getClearFlag().getValue());
    assertThat(accountSet.domain().get()).isEqualTo(accountSetProto.getDomain().getValue());
    assertThat(accountSet.emailHash()).isEqualTo(accountSetProto.getEmailHash().getValue().toByteArray());
    assertThat(accountSet.messageKey()).isEqualTo(accountSetProto.getMessageKey().getValue().toByteArray());
    assertThat(accountSet.setFlag().get()).isEqualTo(accountSetProto.getSetFlag().getValue());
    assertThat(accountSet.tickSize().get()).isEqualTo(accountSetProto.getTickSize().getValue());
    assertThat(accountSet.transferRate().get()).isEqualTo(accountSetProto.getTransferRate().getValue());
  }

  @Test
  public void oneFieldSetTest() {
    // GIVEN an AccountSet protocol buffer with only one field set.
    final AccountSet accountSetProto = FakeXrpTransactionProtobufs.oneFieldAccountSet;

    // WHEN the protocol buffer is converted to a native Java object.
    final XrpAccountSet accountSet = XrpAccountSet.from(accountSetProto);

    // THEN the AccountSet converted as expected.
    assertThat(accountSet.clearFlag().get()).isEqualTo(accountSetProto.getClearFlag().getValue());
    assertThat(accountSet.domain()).isEmpty();
    assertThat(accountSet.emailHash()).isEmpty();
    assertThat(accountSet.messageKey()).isEmpty();
    assertThat(accountSet.setFlag()).isEmpty();
    assertThat(accountSet.tickSize()).isEmpty();
    assertThat(accountSet.transferRate()).isEmpty();
  }
}
