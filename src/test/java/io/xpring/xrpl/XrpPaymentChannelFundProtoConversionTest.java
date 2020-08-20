package io.xpring.xrpl;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import io.xpring.xrpl.model.XrpPaymentChannelFund;
import org.junit.Test;
import org.xrpl.rpc.v1.PaymentChannelFund;

public class XrpPaymentChannelFundProtoConversionTest {
  @Test
  public void paymentChannelFundRequiredFieldsTest() {
    // GIVEN a PaymentChannelFund protocol buffer with all fields set.
    final PaymentChannelFund paymentChannelFundProto = FakeXrpTransactionProtobufs.paymentChannelFundWithRequiredFields;

    // WHEN the protocol buffer is converted to a native Java object.
    final XrpPaymentChannelFund paymentChannelFund = XrpPaymentChannelFund.from(paymentChannelFundProto);

    // THEN the PaymentChannelFund converted as expected.
  }

  @Test
  public void paymentChannelFundAllFieldsTest() {
    // GIVEN a PaymentChannelFund protocol buffer with all fields set.
    final PaymentChannelFund paymentChannelFundProto = FakeXrpTransactionProtobufs.paymentChannelFundWithAllFields;

    // WHEN the protocol buffer is converted to a native Java object.
    final XrpPaymentChannelFund paymentChannelFund = XrpPaymentChannelFund.from(paymentChannelFundProto);

    // THEN the PaymentChannelFund converted as expected.
  }

  @Test
  public void paymentChannelFundMissingFieldsTest() {
    // GIVEN a PaymentChannelFund protocol buffer with missing fields.
    final PaymentChannelFund paymentChannelFundProto = FakeXrpTransactionProtobufs.invalidChannelFundWithMissingFields;

    // WHEN the protocol buffer is converted to a native Java object.
    final XrpPaymentChannelFund paymentChannelFund = XrpPaymentChannelFund.from(paymentChannelFundProto);

    // THEN the PaymentChannelFund is null.
    assertThat(paymentChannelFund).isNull();
  }
}
