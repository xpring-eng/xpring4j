package io.xpring.xrpl.model;

import io.xpring.common.XrplNetwork;
import io.xpring.xrpl.ClassicAddress;
import io.xpring.xrpl.ImmutableClassicAddress;
import io.xpring.xrpl.Utils;
import org.immutables.value.Value;
import org.xrpl.rpc.v1.EscrowCancel;

@Value.Immutable
public interface XrpEscrowCancel {
  static ImmutableXrpEscrowCancel.Builder builder() {
    return ImmutableXrpEscrowCancel.builder();
  }

  String ownerXAddress();
  Integer offerSequence();

  static XrpEscrowCancel from(EscrowCancel escrowCancel, XrplNetwork xrplNetwork) {
    if (!escrowCancel.hasOwner() || !escrowCancel.hasOfferSequence()) {
      return null;
    }

    ClassicAddress ownerClassicAddress = ImmutableClassicAddress.builder()
      .address(escrowCancel.getOwner().getValue().getAddress())
      .isTest(xrplNetwork == XrplNetwork.TEST || xrplNetwork == XrplNetwork.DEV)
      .build();

    final String ownerXAddress = Utils.encodeXAddress(ownerClassicAddress);

    return XrpEscrowCancel.builder()
      .ownerXAddress(ownerXAddress)
      .build();
  }
}
