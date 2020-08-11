package io.xpring.xrpl;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import io.xpring.common.XrplNetwork;
import io.xpring.xrpl.model.XrpAccountDelete;
import org.junit.Test;
import org.xrpl.rpc.v1.AccountDelete;

public class XrpAccountDeleteProtoConversionTest {
  @Test
  public void allFieldsSetTest() {
    // GIVEN an AccountDelete protocol buffer with all fields set.
    final AccountDelete accountDeleteProto = FakeXrpTransactionProtobufs.allFieldsAccountDelete;

    // WHEN the protocol buffer is converted to a native Java object.
    final XrpAccountDelete accountDelete = XrpAccountDelete.from(accountDeleteProto, XrplNetwork.TEST);

    ClassicAddress classicAddress = ImmutableClassicAddress.builder()
        .address(accountDeleteProto.getDestination().getValue().getAddress())
        .tag(accountDeleteProto.getDestinationTag().getValue())
        .isTest(true)
        .build();
    final String destinationXAddress = Utils.encodeXAddress(classicAddress);

    // THEN the AccountDelete converted as expected.
    assertThat(accountDelete.destinationXAddress()).isEqualTo(destinationXAddress);
  }

  @Test
  public void destinationTagNotSetTest() {
    // GIVEN an AccountDelete protocol buffer with no destination tag.
    final AccountDelete accountDeleteProto = FakeXrpTransactionProtobufs.noDestinationTagAccountDelete;

    // WHEN the protocol buffer is converted to a native Java object.
    final XrpAccountDelete accountDelete = XrpAccountDelete.from(accountDeleteProto, XrplNetwork.TEST);

    ClassicAddress classicAddress = ImmutableClassicAddress.builder()
        .address(accountDeleteProto.getDestination().getValue().getAddress())
        .isTest(true)
        .build();
    final String destinationXAddress = Utils.encodeXAddress(classicAddress);

    // THEN the AccountDelete converted as expected.
    assertThat(accountDelete.destinationXAddress()).isEqualTo(destinationXAddress);
  }

  @Test
  public void missingDestinationTest() {
    final XrpAccountDelete accountDelete = XrpAccountDelete.from(AccountDelete.newBuilder().build(), XrplNetwork.TEST);
    assertThat(accountDelete).isNull();
  }
}
