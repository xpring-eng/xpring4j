package io.xpring.xrpl;

import io.xpring.common.XrplNetwork;
import io.xpring.xrpl.model.XrpCurrencyAmount;
import io.xpring.xrpl.model.XrpEscrowCreate;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.Test;
import org.xrpl.rpc.v1.EscrowCreate;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class XrpEscrowCreateProtoConversionTest {
  @Test
  public void escrowCreateConversionTest() {
    // GIVEN an EscrowCreate protocol buffer with all fields set.
    final EscrowCreate escrowCreateProto = FakeXrpTransactionProtobufs.escrowCreateProto;

    // WHEN the protocol buffer is converted to a native Java object.
    final XrpEscrowCreate escrowCreate = XrpEscrowCreate.from(escrowCreateProto, XrplNetwork.TEST);

    // THEN the EscrowCreate converted as expected.
    assertThat(escrowCreate.amount()).isEqualTo(XrpCurrencyAmount.from(escrowCreateProto.getAmount().getValue()));
    assertThat(escrowCreate.cancelAfter().get()).isEqualTo(escrowCreateProto.getCancelAfter().getValue());
    assertThat(escrowCreate.condition().get()).isEqualTo(escrowCreateProto.getCondition().getValue().toString());
    assertThat(escrowCreate.finishAfter().get()).isEqualTo(escrowCreateProto.getFinishAfter().getValue());

    ClassicAddress destinationClassicAddress = ImmutableClassicAddress.builder()
      .address(escrowCreateProto.getDestination().getValue().getAddress())
      .isTest(true)
      .build();
    final String destinationXAddress = Utils.encodeXAddress(destinationClassicAddress);
    AssertionsForClassTypes.assertThat(escrowCreate.destinationXAddress()).isEqualTo(destinationXAddress);
  }
}
