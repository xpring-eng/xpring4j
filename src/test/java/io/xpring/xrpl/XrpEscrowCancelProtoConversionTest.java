package io.xpring.xrpl;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import io.xpring.common.XrplNetwork;
import io.xpring.xrpl.model.XrpCurrencyAmount;
import io.xpring.xrpl.model.XrpEscrowCancel;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.Test;
import org.xrpl.rpc.v1.EscrowCancel;

public class XrpEscrowCancelProtoConversionTest {
  @Test
  public void escrowCancelConversionTest() {
    // GIVEN an EscrowCancel protocol buffer with all fields set.
    final EscrowCancel escrowCancelProto = FakeXrpTransactionProtobufs.escrowCancelProto;

    // WHEN the protocol buffer is converted to a native Java object.
    final XrpEscrowCancel escrowCancel = XrpEscrowCancel.from(escrowCancelProto, XrplNetwork.TEST);

    // THEN the EscrowCancel converted as expected.
    assertThat(escrowCancel.offerSequence()).isEqualTo(escrowCancelProto.getOfferSequence().getValue());

    ClassicAddress ownerClassicAddress = ImmutableClassicAddress.builder()
        .address(escrowCancelProto.getOwner().getValue().getAddress())
        .isTest(true)
        .build();
    final String ownerXAddress = Utils.encodeXAddress(ownerClassicAddress);
    assertThat(escrowCancel.ownerXAddress()).isEqualTo(ownerXAddress);
  }

  @Test
  public void invalidEscrowCancelMissingOfferSequenceTest() {
    // GIVEN an EscrowCancel protocol buffer without an offer sequence set.
    final EscrowCancel escrowCancelProto = FakeXrpTransactionProtobufs.invalidEscrowCancelProtoMissingOfferSequence;

    // WHEN the protocol buffer is converted to a native Java object.
    final XrpEscrowCancel escrowCancel = XrpEscrowCancel.from(escrowCancelProto, XrplNetwork.TEST);

    // THEN the result is null.
    assertThat(escrowCancel).isNull();
  }

  @Test
  public void invalidEscrowCancelMissingOwnerTest() {
    // GIVEN an EscrowCancel protocol buffer without an owner set.
    final EscrowCancel escrowCancelProto = FakeXrpTransactionProtobufs.invalidEscrowCancelProtoMissingOwner;

    // WHEN the protocol buffer is converted to a native Java object.
    final XrpEscrowCancel escrowCancel = XrpEscrowCancel.from(escrowCancelProto, XrplNetwork.TEST);

    // THEN the result is null.
    assertThat(escrowCancel).isNull();
  }
}
