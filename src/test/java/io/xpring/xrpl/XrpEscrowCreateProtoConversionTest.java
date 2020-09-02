package io.xpring.xrpl;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import io.xpring.common.XrplNetwork;
import io.xpring.xrpl.model.XrpCurrencyAmount;
import io.xpring.xrpl.model.XrpEscrowCreate;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.Test;
import org.xrpl.rpc.v1.EscrowCreate;

public class XrpEscrowCreateProtoConversionTest {
  @Test
  public void escrowCreateRequiredFieldsConversionTest() {
    // GIVEN an EscrowCreate protocol buffer with all fields set.
    final EscrowCreate escrowCreateProto = FakeXrpTransactionProtobufs.escrowCreateProtoWithRequiredFields;

    // WHEN the protocol buffer is converted to a native Java object.
    final XrpEscrowCreate escrowCreate = XrpEscrowCreate.from(escrowCreateProto, XrplNetwork.TEST);

    // THEN the EscrowCreate converted as expected.
    assertThat(escrowCreate.amount()).isEqualTo(XrpCurrencyAmount.from(escrowCreateProto.getAmount().getValue()));
    ClassicAddress destinationClassicAddress = ImmutableClassicAddress.builder()
        .address(escrowCreateProto.getDestination().getValue().getAddress())
        .tag(escrowCreateProto.getDestinationTag().getValue())
        .isTest(true)
        .build();
    final String destinationXAddress = Utils.encodeXAddress(destinationClassicAddress);
    assertThat(escrowCreate.destinationXAddress()).isEqualTo(destinationXAddress);
    assertThat(escrowCreate.cancelAfter()).isEmpty();
    assertThat(escrowCreate.condition()).isEmpty();
    assertThat(escrowCreate.finishAfter()).isEmpty();
  }

  @Test
  public void escrowCreateAllFieldsConversionTest() {
    // GIVEN an EscrowCreate protocol buffer with all fields set.
    final EscrowCreate escrowCreateProto = FakeXrpTransactionProtobufs.escrowCreateProtoWithAllFields;

    // WHEN the protocol buffer is converted to a native Java object.
    final XrpEscrowCreate escrowCreate = XrpEscrowCreate.from(escrowCreateProto, XrplNetwork.TEST);

    // THEN the EscrowCreate converted as expected.
    assertThat(escrowCreate.amount()).isEqualTo(XrpCurrencyAmount.from(escrowCreateProto.getAmount().getValue()));
    assertThat(escrowCreate.cancelAfter().get()).isEqualTo(escrowCreateProto.getCancelAfter().getValue());
    assertThat(escrowCreate.condition().get()).isEqualTo(escrowCreateProto.getCondition().getValue().toString());
    assertThat(escrowCreate.finishAfter().get()).isEqualTo(escrowCreateProto.getFinishAfter().getValue());

    ClassicAddress destinationClassicAddress = ImmutableClassicAddress.builder()
        .address(escrowCreateProto.getDestination().getValue().getAddress())
        .tag(escrowCreateProto.getDestinationTag().getValue())
        .isTest(true)
        .build();
    final String destinationXAddress = Utils.encodeXAddress(destinationClassicAddress);
    AssertionsForClassTypes.assertThat(escrowCreate.destinationXAddress()).isEqualTo(destinationXAddress);
  }

  @Test
  public void invalidEscrowCreateMissingAmountConversionTest() {
    // GIVEN an EscrowCreate protocol buffer missing an amount.
    final EscrowCreate escrowCreateProto = FakeXrpTransactionProtobufs.invalidEscrowCreateMissingAmount;

    // WHEN the protocol buffer is converted to a native Java object.
    final XrpEscrowCreate escrowCreate = XrpEscrowCreate.from(escrowCreateProto, XrplNetwork.TEST);

    // THEN the EscrowCreate is null.
    assertThat(escrowCreate).isNull();
  }

  @Test
  public void invalidEscrowCreateMissingDestinationConversionTest() {
    // GIVEN an EscrowCreate protocol buffer missing a destination.
    final EscrowCreate escrowCreateProto = FakeXrpTransactionProtobufs.invalidEscrowCreateMissingDestination;

    // WHEN the protocol buffer is converted to a native Java object.
    final XrpEscrowCreate escrowCreate = XrpEscrowCreate.from(escrowCreateProto, XrplNetwork.TEST);

    // THEN the EscrowCreate is null.
    assertThat(escrowCreate).isNull();
  }

  @Test
  public void invalidEscrowCreateInvalidAmountConversionTest() {
    // GIVEN an EscrowCreate protocol buffer with an invalid amount.
    final EscrowCreate escrowCreateProto = FakeXrpTransactionProtobufs.invalidEscrowCreateInvalidAmount;

    // WHEN the protocol buffer is converted to a native Java object.
    final XrpEscrowCreate escrowCreate = XrpEscrowCreate.from(escrowCreateProto, XrplNetwork.TEST);

    // THEN the EscrowCreate is null.
    assertThat(escrowCreate).isNull();
  }
}
