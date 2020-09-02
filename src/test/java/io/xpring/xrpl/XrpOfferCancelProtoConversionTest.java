package io.xpring.xrpl;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import io.xpring.xrpl.model.XrpOfferCancel;
import org.junit.Test;
import org.xrpl.rpc.v1.OfferCancel;

public class XrpOfferCancelProtoConversionTest {
  @Test
  public void offerCancelConversionTest() {
    // GIVEN an OfferCancel protocol buffer with offerSequence field set.
    final OfferCancel offerCancelProto = FakeXrpTransactionProtobufs.offerCancelProto;

    // WHEN the protocol buffer is converted to a native Java object.
    final XrpOfferCancel offerCancel = XrpOfferCancel.from(offerCancelProto);

    // THEN the OfferCancel converted as expected.
    assertThat(offerCancel.offerSequence()).isEqualTo(offerCancelProto.getOfferSequence().getValue());
  }

  @Test
  public void offerCancelMissingOfferSequenceTest() {
    // GIVEN an OfferCancel protocol buffer missing the offerSequence field.
    final OfferCancel offerCancelProto = FakeXrpTransactionProtobufs.invalidOfferCancelMissingOfferSequence;

    // WHEN the protocol buffer is converted to a native Java object.
    final XrpOfferCancel offerCancel = XrpOfferCancel.from(offerCancelProto);

    // THEN the result is null.
    assertThat(offerCancel).isNull();
  }
}
