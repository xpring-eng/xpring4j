package io.xpring.xrpl;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import io.xpring.xrpl.model.XrpCurrencyAmount;
import io.xpring.xrpl.model.XrpOfferCreate;
import org.junit.Test;
import org.xrpl.rpc.v1.OfferCreate;

public class XrpOfferCreateProtoConversionTest {
  @Test
  public void offerCreateRequiredFieldsTest() {
    // GIVEN a valid OfferCreate protocol buffer with required fields set.
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

  @Test
  public void offerCreateAllFieldsTest() {
    // GIVEN a valid OfferCreate protocol buffer with all fields set.
    final OfferCreate offerCreateProto = FakeXrpTransactionProtobufs.offerCreateWithAllFields;

    // WHEN the protocol buffer is converted to a native Java object.
    final XrpOfferCreate offerCreate = XrpOfferCreate.from(offerCreateProto);

    // THEN the OfferCreate converted as expected.
    assertThat(offerCreate.expiration().get()).isEqualTo(offerCreateProto.getExpiration().getValue());
    assertThat(offerCreate.offerSequence().get()).isEqualTo(offerCreateProto.getOfferSequence().getValue());
    assertThat(offerCreate.takerGets())
        .isEqualTo(XrpCurrencyAmount.from(offerCreateProto.getTakerGets().getValue()));
    assertThat(offerCreate.takerPays())
        .isEqualTo(XrpCurrencyAmount.from(offerCreateProto.getTakerPays().getValue()));
  }

  @Test
  public void offerCreateInvalidTakerGetsTest() {
    // GIVEN a OfferCreate protocol buffer with an invalid takerGets field.
    final OfferCreate offerCreateProto = FakeXrpTransactionProtobufs.invalidOfferCreateInvalidTakerGets;

    // WHEN the protocol buffer is converted to a native Java object.
    final XrpOfferCreate offerCreate = XrpOfferCreate.from(offerCreateProto);

    // THEN the result is null.
    assertThat(offerCreate).isNull();
  }

  @Test
  public void offerCreateInvalidTakerPaysTest() {
    // GIVEN a OfferCreate protocol buffer with an invalid takerPays field.
    final OfferCreate offerCreateProto = FakeXrpTransactionProtobufs.invalidOfferCreateInvalidTakerPays;

    // WHEN the protocol buffer is converted to a native Java object.
    final XrpOfferCreate offerCreate = XrpOfferCreate.from(offerCreateProto);

    // THEN the result is null.
    assertThat(offerCreate).isNull();
  }

  @Test
  public void offerCreateMissingTakerGetsTest() {
    // GIVEN a OfferCreate protocol buffer missing the takerGets field.
    final OfferCreate offerCreateProto = FakeXrpTransactionProtobufs.invalidOfferCreateMissingTakerGets;

    // WHEN the protocol buffer is converted to a native Java object.
    final XrpOfferCreate offerCreate = XrpOfferCreate.from(offerCreateProto);

    // THEN the result is null.
    assertThat(offerCreate).isNull();
  }

  @Test
  public void offerCreateMissingTakerPays() {
    // GIVEN a OfferCreate protocol buffer missing the takerPays field.
    final OfferCreate offerCreateProto = FakeXrpTransactionProtobufs.invalidOfferCreateMissingTakerPays;

    // WHEN the protocol buffer is converted to a native Java object.
    final XrpOfferCreate offerCreate = XrpOfferCreate.from(offerCreateProto);

    // THEN the result is null.
    assertThat(offerCreate).isNull();
  }
}
