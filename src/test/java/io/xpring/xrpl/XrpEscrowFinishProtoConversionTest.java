package io.xpring.xrpl;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import io.xpring.common.XrplNetwork;
import io.xpring.xrpl.model.XrpEscrowFinish;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.Test;
import org.xrpl.rpc.v1.EscrowFinish;

public class XrpEscrowFinishProtoConversionTest {
  @Test
  public void escrowFinishRequiredFieldsConversionTest() {
    // GIVEN an EscrowCreate protocol buffer with required fields set.
    final EscrowFinish escrowFinishProto = FakeXrpTransactionProtobufs.escrowFinishProtoWithRequiredFields;

    // WHEN the protocol buffer is converted to a native Java object.
    final XrpEscrowFinish escrowFinish = XrpEscrowFinish.from(escrowFinishProto, XrplNetwork.TEST);

    // THEN the EscrowCreate converted as expected.
    assertThat(escrowFinish.condition()).isEmpty();
    assertThat(escrowFinish.fulfillment()).isEmpty();
    assertThat(escrowFinish.offerSequence()).isEqualTo(escrowFinishProto.getOfferSequence().getValue());
  }

  @Test
  public void escrowFinishAllFieldsConversionTest() {
    // GIVEN an EscrowCreate protocol buffer with all fields set.
    final EscrowFinish escrowFinishProto = FakeXrpTransactionProtobufs.escrowFinishProtoWithAllFields;

    // WHEN the protocol buffer is converted to a native Java object.
    final XrpEscrowFinish escrowFinish = XrpEscrowFinish.from(escrowFinishProto, XrplNetwork.TEST);

    // THEN the EscrowCreate converted as expected.
  }

}
