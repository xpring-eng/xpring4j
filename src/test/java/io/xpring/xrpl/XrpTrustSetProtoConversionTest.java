package io.xpring.xrpl;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import io.xpring.xrpl.model.XrpCurrencyAmount;
import io.xpring.xrpl.model.XrpTrustSet;
import org.junit.Test;
import org.xrpl.rpc.v1.TrustSet;

public class XrpTrustSetProtoConversionTest {
  @Test
  public void trustSetRequiredFieldsTest() {
    // GIVEN a TrustSet protocol buffer with required fields set.
    final TrustSet trustSetProto = FakeXrpTransactionProtobufs.trustSetRequiredFields;

    // WHEN the protocol buffer is converted to a native Java object.
    final XrpTrustSet trustSet = XrpTrustSet.from(trustSetProto);

    // THEN the TrustSet converted as expected.
    assertThat(trustSet.limitAmount())
        .isEqualTo(XrpCurrencyAmount.from(trustSetProto.getLimitAmount().getValue()));

    assertThat(trustSet.qualityIn()).isEmpty();
    assertThat(trustSet.qualityOut()).isEmpty();
  }

  @Test
  public void trustSetAllFieldsTest() {
    // GIVEN a TrustSet protocol buffer with all fields set.
    final TrustSet trustSetProto = FakeXrpTransactionProtobufs.trustSetAllFields;

    // WHEN the protocol buffer is converted to a native Java object.
    final XrpTrustSet trustSet = XrpTrustSet.from(trustSetProto);

    // THEN the TrustSet converted as expected.
    assertThat(trustSet.limitAmount())
        .isEqualTo(XrpCurrencyAmount.from(trustSetProto.getLimitAmount().getValue()));
    assertThat(trustSet.qualityIn().get()).isEqualTo(trustSetProto.getQualityIn().getValue());
    assertThat(trustSet.qualityOut().get()).isEqualTo(trustSetProto.getQualityOut().getValue());
  }

  @Test
  public void invalidTrustSetMissingLimitAmountTest() {
    // GIVEN a TrustSet protocol buffer missing a limit amount.
    final TrustSet trustSetProto = FakeXrpTransactionProtobufs.invalidTrustSetMissingLimitAmount;

    // WHEN the protocol buffer is converted to a native Java object.
    final XrpTrustSet trustSet = XrpTrustSet.from(trustSetProto);

    // THEN the TrustSet is null.
    assertThat(trustSet).isNull();
  }
}
