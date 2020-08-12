package io.xpring.xrpl;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import io.xpring.common.XrplNetwork;
import io.xpring.xrpl.model.XrpDepositPreauth;
import org.junit.Test;
import org.xrpl.rpc.v1.DepositPreauth;

public class XrpDepositPreAuthProtoConversionTest {
  @Test
  public void depositPreauthAuthorizeSetTest() {
    // GIVEN a DepositPreauth protocol buffer with authorize field set.
    final DepositPreauth depositPreauthProto = FakeXrpTransactionProtobufs.depositPreauthWithAuthorize;

    // WHEN the protocol buffer is converted to a native Java object.
    final XrpDepositPreauth depositPreauth = XrpDepositPreauth.from(depositPreauthProto, XrplNetwork.TEST);

    // THEN the DepositPreauth converted as expected.
    ClassicAddress authorizeClassicAddress = ImmutableClassicAddress.builder()
      .address(depositPreauthProto.getAuthorize().getValue().getAddress())
      .isTest(true)
      .build();
    final String authorizeXAddress = Utils.encodeXAddress(authorizeClassicAddress);

    assertThat(depositPreauth.authorizeXAddress().get()).isEqualTo(authorizeXAddress);
  }

  @Test
  public void depositPreauthUnauthorizeSetTest() {
    // GIVEN a DepositPreauth protocol buffer with unauthorize field set.
    final DepositPreauth depositPreauthProto = FakeXrpTransactionProtobufs.depositPreauthWithUnauthorize;

    // WHEN the protocol buffer is converted to a native Java object.
    final XrpDepositPreauth depositPreauth = XrpDepositPreauth.from(depositPreauthProto, XrplNetwork.TEST);

    // THEN the DepositPreauth converted as expected.
    ClassicAddress unauthorizeClassicAddress = ImmutableClassicAddress.builder()
      .address(depositPreauthProto.getUnauthorize().getValue().getAddress())
      .isTest(true)
      .build();
    final String unauthorizeXAddress = Utils.encodeXAddress(unauthorizeClassicAddress);

    assertThat(depositPreauth.unauthorizeXAddress().get()).isEqualTo(unauthorizeXAddress);
  }
}
