package io.xpring.xrpl.model;

import io.xpring.common.XrplNetwork;
import io.xpring.xrpl.ClassicAddress;
import io.xpring.xrpl.ImmutableClassicAddress;
import io.xpring.xrpl.Utils;
import org.immutables.value.Value;
import org.xrpl.rpc.v1.AccountDelete;

import java.util.Optional;

/**
 * Represents an AccountDelete transaction on the XRP Ledger.
 * <p>
 * An AccountDelete transaction deletes an account and any objects it owns in the XRP Ledger,
 * if possible, sending the account's remaining XRP to a specified destination account.
 * </p>
 *
 * @see "https://xrpl.org/accountdelete.html"
 */
@Value.Immutable
public interface XrpAccountDelete {
  static ImmutableXrpAccountDelete.Builder builder() {
    return ImmutableXrpAccountDelete.builder();
  }

  /**
   * The address and destination tag of an account to receive any leftover XRP after deleting the
   * sending account, encoded as an X-address.
   * <p>
   * Must be a funded account in the ledger, and must not be the sending account.
   * </p>
   *
   * @return A {@link String} representing the address and destination tag of an account to receive any
   *         leftover XRP after deleting the sending account, encoded as an X-address.
   *
   * @see "https://xrpaddress.info"
   */
  String destinationXAddress();

  /**
   * Constructs an XrpAccountDelete from an AccountDelete protocol buffer.
   *
   * @param accountDelete An {@link AccountDelete} (protobuf object) whose field values will be used
   *                      to construct an XrpAccountDelete
   *
   * @return An {@link XrpAccountDelete} with its fields set via the analogous protobuf fields.
   *
   * @see <a href="https://github.com/ripple/rippled/blob/3d86b49dae8173344b39deb75e53170a9b6c5284/src/ripple/proto/org/xrpl/rpc/v1/transaction.proto#L118">
   * AccountDelete protocol buffer</a>
   */
  static XrpAccountDelete from(AccountDelete accountDelete, XrplNetwork xrplNetwork) {
    // Destination is required
    if (!accountDelete.hasDestination() || accountDelete.getDestination().getValue().getAddress().isEmpty()) {
      return null;
    }
    final String destination = accountDelete.getDestination().getValue().getAddress();

    Optional<Integer> destinationTag = accountDelete.hasDestinationTag()
        ? Optional.of(accountDelete.getDestinationTag().getValue())
        : Optional.empty();

    ClassicAddress classicAddress = ImmutableClassicAddress.builder()
        .address(destination)
        .tag(destinationTag)
        .isTest(xrplNetwork == XrplNetwork.TEST)
        .build();

    final String destinationXAddress = Utils.encodeXAddress(classicAddress);

    return builder()
      .destinationXAddress(destinationXAddress)
      .build();
  }
}
