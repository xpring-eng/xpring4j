package io.xpring.xrpl;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import io.xpring.xrpl.model.XrpCurrencyAmount;
import io.xpring.xrpl.model.XrpPaymentChannelClaim;
import org.junit.Test;
import org.xrpl.rpc.v1.PaymentChannelClaim;

public class XrpPaymentChannelClaimProtoConversionTest {
  @Test
  public void paymentChannelClaimRequiredFields() {
    // GIVEN a PaymentChannelClaim protocol buffer with required fields.
    final PaymentChannelClaim paymentChannelClaimProto = FakeXrpTransactionProtobufs
        .paymentChannelClaimWithRequiredFields;

    // WHEN the protocol buffer is converted to a native Java object.
    final XrpPaymentChannelClaim paymentChannelClaim = XrpPaymentChannelClaim.from(paymentChannelClaimProto);

    // THEN the PaymentChannelClaim converted as expected.
    assertThat(paymentChannelClaim.channel()).isEqualTo(paymentChannelClaimProto.getChannel().toString());

    assertThat(paymentChannelClaim.amount()).isEmpty();
    assertThat(paymentChannelClaim.balance()).isEmpty();
    assertThat(paymentChannelClaim.publicKey()).isEmpty();
    assertThat(paymentChannelClaim.signature()).isEmpty();
  }

  @Test
  public void paymentChannelClaimAllFields() {
    // GIVEN a PaymentChannelClaim protocol buffer with all fields.
    final PaymentChannelClaim paymentChannelClaimProto = FakeXrpTransactionProtobufs.paymentChannelClaimWithAllFields;

    // WHEN the protocol buffer is converted to a native Java object.
    final XrpPaymentChannelClaim paymentChannelClaim = XrpPaymentChannelClaim.from(paymentChannelClaimProto);

    // THEN the PaymentChannelClaim converted as expected.
    assertThat(paymentChannelClaim.amount().get())
        .isEqualTo(XrpCurrencyAmount.from(paymentChannelClaimProto.getAmount().getValue()));
    assertThat(paymentChannelClaim.balance().get())
        .isEqualTo(XrpCurrencyAmount.from(paymentChannelClaimProto.getBalance().getValue()));
    assertThat(paymentChannelClaim.channel()).isEqualTo(paymentChannelClaimProto.getChannel().toString());
    assertThat(paymentChannelClaim.publicKey().get())
        .isEqualTo(paymentChannelClaimProto.getPublicKey().toString());
    assertThat(paymentChannelClaim.signature().get())
        .isEqualTo(paymentChannelClaimProto.getPaymentChannelSignature().toString());
  }

  @Test
  public void paymentChannelClaimMissingChannel() {
    // GIVEN a PaymentChannelClaim protocol buffer that's missing a channel.
    final PaymentChannelClaim paymentChannelClaimProto = FakeXrpTransactionProtobufs
        .invalidPaymentChannelClaimMissingChannel;

    // WHEN the protocol buffer is converted to a native Java object.
    final XrpPaymentChannelClaim paymentChannelClaim = XrpPaymentChannelClaim.from(paymentChannelClaimProto);

    // THEN the conversion returns null;
    assertThat(paymentChannelClaim).isNull();
  }
}
