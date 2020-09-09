package io.xpring.xrpl.converters;

import com.ripple.core.coretypes.AccountID;
import com.ripple.core.coretypes.Amount;
import com.ripple.core.coretypes.uint.UInt32;
import com.ripple.core.types.known.tx.txns.AccountSet;
import org.xrpl.rpc.v1.Transaction;

public class AccountSetConverter extends BaseConverter {

  /**
   * Converts a GRPC transaction to a AccountSet instance.
   * @param transaction to be converted.
   * @return converted value.
   */
  public AccountSet convert(Transaction transaction) {
    AccountSet accountSet = new AccountSet();
    if (transaction.hasFlags()) {
      accountSet.flags(new UInt32(Integer.toUnsignedLong(transaction.getFlags().getValue())));
    }
    convert(transaction.getAccountTransactionId().getValue()).ifPresent(accountSet::accountTxnID);
    accountSet.lastLedgerSequence(new UInt32(transaction.getLastLedgerSequence().getValue()));
    accountSet.account(AccountID.fromAddress(transaction.getAccount().getValue().getAddress()));
    accountSet.sequence(new UInt32(transaction.getSequence().getValue()));
    accountSet.fee(Amount.fromDropString(transaction.getFee().getDrops() + ""));

    org.xrpl.rpc.v1.AccountSet source = transaction.getAccountSet();
    getHash128(source.getEmailHash().getValue()).ifPresent(accountSet::emailHash);
    getBlob(source.getDomain().toByteString()).ifPresent(accountSet::domain);
    getBlob(source.getMessageKey().toByteString()).ifPresent(accountSet::messageKey);
    getNonZeroValue(source.getClearFlag().getValue()).ifPresent(accountSet::clearFlag);
    getNonZeroValue(source.getSetFlag().getValue()).ifPresent(accountSet::setFlag);
    return accountSet;
  }

}
