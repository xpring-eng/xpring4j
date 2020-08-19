package io.xpring.xrpl;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import io.xpring.common.XrplNetwork;
import io.xpring.xrpl.model.XrpEscrowFinish;
import org.junit.Test;
import org.xrpl.rpc.v1.EscrowFinish;

public class XrpEscrowFinishProtoConversionTest {
  @Test
  public void escrowFinishRequiredFieldsConversionTest() {
    // GIVEN an EscrowFinish protocol buffer with required fields set.
    final EscrowFinish escrowFinishProto = FakeXrpTransactionProtobufs.escrowFinishProtoWithRequiredFields;

    // WHEN the protocol buffer is converted to a native Java object.
    final XrpEscrowFinish escrowFinish = XrpEscrowFinish.from(escrowFinishProto, XrplNetwork.TEST);

    // THEN the EscrowFinish converted as expected.
    assertThat(escrowFinish.offerSequence()).isEqualTo(escrowFinishProto.getOfferSequence().getValue());
    ClassicAddress ownerClassicAddress = ImmutableClassicAddress.builder()
        .address(escrowFinishProto.getOwner().getValue().getAddress())
        .isTest(true)
        .build();
    final String ownerXAddress = Utils.encodeXAddress(ownerClassicAddress);
    assertThat(escrowFinish.ownerXAddress()).isEqualTo(ownerXAddress);

    assertThat(escrowFinish.condition()).isEmpty();
    assertThat(escrowFinish.fulfillment()).isEmpty();
  }

  @Test
  public void escrowFinishAllFieldsConversionTest() {
    // GIVEN an EscrowFinish protocol buffer with all fields set.
    final EscrowFinish escrowFinishProto = FakeXrpTransactionProtobufs.escrowFinishProtoWithAllFields;

    // WHEN the protocol buffer is converted to a native Java object.
    final XrpEscrowFinish escrowFinish = XrpEscrowFinish.from(escrowFinishProto, XrplNetwork.TEST);

    // THEN the EscrowFinish converted as expected.
    assertThat(escrowFinish.condition().get()).isEqualTo(escrowFinishProto.getCondition().getValue().toString());
    assertThat(escrowFinish.fulfillment().get()).isEqualTo(escrowFinishProto.getFulfillment().getValue().toString());
    assertThat(escrowFinish.offerSequence()).isEqualTo(escrowFinishProto.getOfferSequence().getValue());
    ClassicAddress ownerClassicAddress = ImmutableClassicAddress.builder()
        .address(escrowFinishProto.getOwner().getValue().getAddress())
        .isTest(true)
        .build();
    final String ownerXAddress = Utils.encodeXAddress(ownerClassicAddress);
    assertThat(escrowFinish.ownerXAddress()).isEqualTo(ownerXAddress);
  }

  @Test
  public void escrowFinishMissingOfferSequenceTest() {
    // GIVEN an EscrowFinish protocol buffer that's missing an offer sequence.
    final EscrowFinish escrowFinishProto = FakeXrpTransactionProtobufs.invalidEscrowFinishMissingOfferSequence;

    // WHEN the protocol buffer is converted to a native Java object.
    final XrpEscrowFinish escrowFinish = XrpEscrowFinish.from(escrowFinishProto, XrplNetwork.TEST);

    // THEN the conversion returns null;
    assertThat(escrowFinish).isNull();
  }

  @Test
  public void escrowFinishMissingOwnerSequenceTest() {
    // GIVEN an EscrowFinish protocol buffer that's missing an owner.
    final EscrowFinish escrowFinishProto = FakeXrpTransactionProtobufs.invalidEscrowFinishMissingOwner;

    // WHEN the protocol buffer is converted to a native Java object.
    final XrpEscrowFinish escrowFinish = XrpEscrowFinish.from(escrowFinishProto, XrplNetwork.TEST);

    // THEN the conversion returns null;
    assertThat(escrowFinish).isNull();
  }
}
