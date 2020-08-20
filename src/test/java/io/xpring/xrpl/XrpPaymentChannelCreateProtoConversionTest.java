package io.xpring.xrpl;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import io.xpring.common.XrplNetwork;
import io.xpring.xrpl.model.XrpCurrencyAmount;
import io.xpring.xrpl.model.XrpPaymentChannelCreate;
import org.junit.Test;
import org.xrpl.rpc.v1.PaymentChannelCreate;

public class XrpPaymentChannelCreateProtoConversionTest {
  @Test
  public void paymentChannelCreateRequiredFields() {
    // GIVEN a PaymentChannelCreate protocol buffer with required fields set.
    final PaymentChannelCreate paymentChannelCreateProto = FakeXrpTransactionProtobufs
        .paymentChannelCreateWithRequiredFields;

    // WHEN the protocol buffer is converted to a native Java object.
    final XrpPaymentChannelCreate paymentChannelCreate = XrpPaymentChannelCreate
        .from(paymentChannelCreateProto, XrplNetwork.TEST);

    // THEN the PaymentChannelCreate converted as expected.
    assertThat(paymentChannelCreate.amount())
      .isEqualTo(XrpCurrencyAmount.from(paymentChannelCreateProto.getAmount().getValue()));

    ClassicAddress destinationClassicAddress = ImmutableClassicAddress.builder()
      .address(paymentChannelCreateProto.getDestination().getValue().getAddress())
      .tag(paymentChannelCreateProto.getDestinationTag().getValue())
      .isTest(true)
      .build();
    final String destinationXAddress = Utils.encodeXAddress(destinationClassicAddress);
    assertThat(paymentChannelCreate.destinationXAddress()).isEqualTo(destinationXAddress);

    assertThat(paymentChannelCreate.publicKey())
      .isEqualTo(paymentChannelCreateProto.getPublicKey().toString());
    assertThat(paymentChannelCreate.settleDelay()).isEqualTo(paymentChannelCreateProto.getSettleDelay().getValue());

    assertThat(paymentChannelCreate.cancelAfter()).isEmpty();
  }

  @Test
  public void paymentChannelCreateAllFields() {
    // GIVEN a PaymentChannelCreate protocol buffer with all fields set.
    final PaymentChannelCreate paymentChannelCreateProto = FakeXrpTransactionProtobufs
      .paymentChannelCreateWithAllFields;

    // WHEN the protocol buffer is converted to a native Java object.
    final XrpPaymentChannelCreate paymentChannelCreate = XrpPaymentChannelCreate
      .from(paymentChannelCreateProto, XrplNetwork.TEST);

    // THEN the PaymentChannelCreate converted as expected.
    assertThat(paymentChannelCreate.amount())
      .isEqualTo(XrpCurrencyAmount.from(paymentChannelCreateProto.getAmount().getValue()));
    assertThat(paymentChannelCreate.cancelAfter().get()).isEqualTo(paymentChannelCreateProto.getCancelAfter().getValue());

    ClassicAddress destinationClassicAddress = ImmutableClassicAddress.builder()
      .address(paymentChannelCreateProto.getDestination().getValue().getAddress())
      .tag(paymentChannelCreateProto.getDestinationTag().getValue())
      .isTest(true)
      .build();
    final String destinationXAddress = Utils.encodeXAddress(destinationClassicAddress);
    assertThat(paymentChannelCreate.destinationXAddress()).isEqualTo(destinationXAddress);

    assertThat(paymentChannelCreate.publicKey())
      .isEqualTo(paymentChannelCreateProto.getPublicKey().toString());
    assertThat(paymentChannelCreate.settleDelay()).isEqualTo(paymentChannelCreateProto.getSettleDelay().getValue());
  }
}
