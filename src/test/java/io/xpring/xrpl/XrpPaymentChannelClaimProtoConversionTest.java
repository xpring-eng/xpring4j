package io.xpring.xrpl;

import io.xpring.common.XrplNetwork;
import io.xpring.xrpl.model.XrpEscrowFinish;
import io.xpring.xrpl.model.XrpPaymentChannelClaim;
import org.junit.Test;
import org.xrpl.rpc.v1.EscrowFinish;
import org.xrpl.rpc.v1.PaymentChannelClaim;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class XrpPaymentChannelClaimProtoConversionTest {
  @Test
  public void paymentChannelClaimMissingChannel() {
    // GIVEN a PaymentChannelClaim protocol buffer that's missing a channel.
    final PaymentChannelClaim paymentChannelClaimProto = FakeXrpTransactionProtobufs.invalidPaymentChannelClaimMissingChannel;

    // WHEN the protocol buffer is converted to a native Java object.
    final XrpPaymentChannelClaim paymentChannelClaim = XrpPaymentChannelClaim.from(paymentChannelClaimProto);

    // THEN the conversion returns null;
    assertThat(paymentChannelClaim).isNull();
  }
}
