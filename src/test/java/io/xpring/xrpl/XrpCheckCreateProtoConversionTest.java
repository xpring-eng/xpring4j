package io.xpring.xrpl;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import io.xpring.common.XrplNetwork;
import io.xpring.xrpl.model.XrpCheckCash;
import io.xpring.xrpl.model.XrpCheckCreate;
import io.xpring.xrpl.model.XrpCurrencyAmount;
import org.junit.Test;
import org.xrpl.rpc.v1.CheckCash;
import org.xrpl.rpc.v1.CheckCreate;

public class XrpCheckCreateProtoConversionTest {
  @Test
  public void checkCreateAllFieldsSetTest() {
    // GIVEN a CheckCreate protocol buffer with all fields set.
    final CheckCreate checkCreateProto = FakeXrpTransactionProtobufs.allFieldsCheckCreateProto;

    // WHEN the protocol buffer is converted to a native Java object.
    final XrpCheckCreate checkCreate = XrpCheckCreate.from(checkCreateProto, XrplNetwork.TEST);

    ClassicAddress classicAddress = ImmutableClassicAddress.builder()
        .address(checkCreateProto.getDestination().getValue().getAddress())
        .tag(checkCreateProto.getDestinationTag().getValue())
        .isTest(true)
        .build();
    final String destinationXAddress = Utils.encodeXAddress(classicAddress);

    // THEN the CheckCreate converted as expected.
    assertThat(checkCreate.destinationXAddress()).isEqualTo(destinationXAddress);
    assertThat(checkCreate.expiration().get()).isEqualTo(checkCreateProto.getExpiration().getValue());
    assertThat(checkCreate.invoiceId().get()).isEqualTo(checkCreateProto.getInvoiceId().getValue().toString());
    assertThat(checkCreate.sendMax().get()).isEqualTo(XrpCurrencyAmount.from(checkCreateProto.getSendMax().getValue()));
  }

  @Test
  public void checkCreateMandatoryFieldsSetTest() {
    // GIVEN a CheckCreate protocol buffer with only mandatory fields set.
    final CheckCreate checkCreateProto = FakeXrpTransactionProtobufs.checkCreateProtoWithMandatoryFields;

    // WHEN the protocol buffer is converted to a native Java object.
    final XrpCheckCreate checkCreate = XrpCheckCreate.from(checkCreateProto, XrplNetwork.TEST);

    ClassicAddress classicAddress = ImmutableClassicAddress.builder()
        .address(checkCreateProto.getDestination().getValue().getAddress())
        .isTest(true)
        .build();
    final String destinationXAddress = Utils.encodeXAddress(classicAddress);

    // THEN the CheckCreate converted as expected.
    assertThat(checkCreate.destinationXAddress()).isEqualTo(destinationXAddress);
    assertThat(checkCreate.expiration()).isEmpty();
    assertThat(checkCreate.invoiceId()).isEmpty();
    assertThat(checkCreate.sendMax().get()).isEqualTo(XrpCurrencyAmount.from(checkCreateProto.getSendMax().getValue()));
  }

  @Test
  public void invalidCheckCreateMissingDestinationTest() {
    // GIVEN an invalid CheckCreate protocol buffer missing the destination field.
    final CheckCreate checkCreateProto = FakeXrpTransactionProtobufs.invalidCheckCreateProto;

    // WHEN the protocol buffer is converted to a native Java object.
    final XrpCheckCreate checkCreate = XrpCheckCreate.from(checkCreateProto, XrplNetwork.TEST);

    // THEN the result is null.
    assertThat(checkCreate).isNull();
  }

}
