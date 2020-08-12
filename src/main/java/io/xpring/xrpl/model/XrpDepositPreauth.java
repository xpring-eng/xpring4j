package io.xpring.xrpl.model;

import io.xpring.common.XrplNetwork;
import io.xpring.xrpl.ClassicAddress;
import io.xpring.xrpl.ImmutableClassicAddress;
import io.xpring.xrpl.Utils;
import org.immutables.value.Value;
import org.xrpl.rpc.v1.DepositPreauth;

import java.util.Optional;

/**
 * Represents a DepositPreauth transaction on the XRP Ledger.
 * A DepositPreauth transaction gives another account pre-approval to deliver payments to the sender of this transaction.
 * This is only useful if the sender of this transaction is using (or plans to use) Deposit Authorization.
 *
 * @see "https://xrpl.org/depositpreauth.html"
 */
@Value.Immutable
public interface XrpDepositPreauth {
  static ImmutableXrpDepositPreauth.Builder builder() {
    return ImmutableXrpDepositPreauth.builder();
  }

  /**
   * (Optional) The XRP Ledger address of the sender to preauthorize, encoded as an X-address.
   *
   * @return A {@link String} representing the XRP Ledger address of the sender to preauthorize, encoded as an
   *         X-address.
   */
  Optional<String> authorizeXAddress();

  /**
   * (Optional) The XRP Ledger address of a sender whose preauthorization should be revoked, encoded as an X-address.
   *
   * @return A {@link String} representing the XRP Ledger address of a sender whose preauthorization should be revoked,
   * encoded as an X-address.
   */
  Optional<String> unauthorizeXAddress();

  /**
   * Constructs an XrpDepositPreauth from a DepositPreauth protocol buffer.
   *
   * @param depositPreauth A {@link DepositPreauth} (protobuf object) whose field values will be used to construct an
   *                       XrpDepositPreauth
   * @param xrplNetwork The network that this transaction should occur on.
   * @return An XrpDepositPreauth with its fields set via the analogous protobuf fields.
   * @see "<https://github.com/ripple/rippled/blob/3d86b49dae8173344b39deb75e53170a9b6c5284/src/ripple/proto/org/xrpl/rpc/v1/transaction.proto#L159"
   */
  static XrpDepositPreauth from(DepositPreauth depositPreauth, XrplNetwork xrplNetwork) {
    final String authorize = depositPreauth.getAuthorize().getValue().getAddress();
    final String unauthorize = depositPreauth.getUnauthorize().getValue().getAddress();
    final boolean isTestNetwork = xrplNetwork == XrplNetwork.TEST || xrplNetwork == XrplNetwork.DEV;

    Optional<String> authorizeXAddress = Optional.empty();
    Optional<String> unauthorizeXAddress = Optional.empty();
    if (authorize != null) {
      ClassicAddress authorizeClassicAddress = ImmutableClassicAddress.builder()
          .address(authorize)
          .isTest(isTestNetwork)
          .build();
      authorizeXAddress = Optional.of(Utils.encodeXAddress(authorizeClassicAddress));
    } else if (unauthorize != null) {
      ClassicAddress unauthorizeClassicAddress = ImmutableClassicAddress.builder()
          .address(unauthorize)
          .isTest(isTestNetwork)
          .build();
      unauthorizeXAddress = Optional.of(Utils.encodeXAddress(unauthorizeClassicAddress));
    }

    return XrpDepositPreauth.builder()
        .authorizeXAddress(authorizeXAddress)
        .unauthorizeXAddress(unauthorizeXAddress)
        .build();
  }
}
