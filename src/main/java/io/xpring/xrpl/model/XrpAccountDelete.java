package io.xpring.xrpl.model;

import org.immutables.value.Value;
import org.xrpl.rpc.v1.AccountDelete;


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
}
