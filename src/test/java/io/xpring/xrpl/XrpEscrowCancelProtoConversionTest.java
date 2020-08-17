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

    // WHEN the protocol buffer is converted to a native Typescript type.
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
  public void invalidEscrowCancelMissingOwnerTest() {}
}
