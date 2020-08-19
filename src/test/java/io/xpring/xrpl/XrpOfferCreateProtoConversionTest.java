package io.xpring.xrpl;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import io.xpring.xrpl.model.XrpCurrencyAmount;
import io.xpring.xrpl.model.XrpOfferCreate;
import org.junit.Test;
import org.xrpl.rpc.v1.OfferCreate;

public class XrpOfferCreateProtoConversionTest {
  @Test
  public void offerCreateRequiredFieldsTest() {
    // GIVEN a valid CheckCash protocol buffer with required fields set.
    final OfferCreate offerCreateProto = FakeXrpTransactionProtobufs.offerCreateWithRequiredFields;

    // WHEN the protocol buffer is converted to a native Java object.
    final XrpOfferCreate offerCreate = XrpOfferCreate.from(offerCreateProto);

    // THEN the OfferCreate converted as expected.
    assertThat(offerCreate.takerGets())
      .isEqualTo(XrpCurrencyAmount.from(offerCreateProto.getTakerGets().getValue()));
    assertThat(offerCreate.takerPays())
      .isEqualTo(XrpCurrencyAmount.from(offerCreateProto.getTakerPays().getValue()));

    assertThat(offerCreate.expiration()).isEmpty();
    assertThat(offerCreate.offerSequence()).isEmpty();
  }
}
