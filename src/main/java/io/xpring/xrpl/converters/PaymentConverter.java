package io.xpring.xrpl.converters;

import com.ripple.core.coretypes.AccountID;
import com.ripple.core.coretypes.Amount;
import com.ripple.core.coretypes.STArray;
import com.ripple.core.coretypes.STObject;
import com.ripple.core.coretypes.uint.UInt32;
import com.ripple.core.types.known.tx.txns.Payment;
import io.xpring.xrpl.ClassicAddress;
import org.xrpl.rpc.v1.Memo;
import org.xrpl.rpc.v1.Transaction;

public class PaymentConverter extends BaseConverter {

  /**
   * Converts a GRPC transaction to a Payment instance.
   * @param transaction to be converted.
   * @return converted value.
   */
  public Payment convert(Transaction transaction) {
    Payment payment = new Payment();
    if (transaction.hasFlags()) {
      payment.flags(new UInt32(Integer.toUnsignedLong(transaction.getFlags().getValue())));
    }
    payment.lastLedgerSequence(new UInt32(transaction.getLastLedgerSequence().getValue()));
    convert(transaction.getAccountTransactionId().getValue()).ifPresent(payment::accountTxnID);
    payment.sequence(new UInt32(transaction.getSequence().getValue()));
    payment.fee(Amount.fromDropString(transaction.getFee().getDrops() + ""));

    org.xrpl.rpc.v1.Payment fromPayment = transaction.getPayment();
    convertDrops(fromPayment.getAmount().getValue()).ifPresent(payment::amount);
    convertDrops(fromPayment.getDeliverMin().getValue()).ifPresent(payment::deliverMin);
    convertDrops(fromPayment.getSendMax().getValue()).ifPresent(payment::sendMax);
    convert(fromPayment.getInvoiceId().getValue()).ifPresent(payment::invoiceID);

    ClassicAddress sourceAddress = convertSourceAddress(transaction);
    payment.account(AccountID.fromAddress(sourceAddress.address()));

    ClassicAddress destinationAddress = convertDestinationAddress(fromPayment);
    payment.destination(AccountID.fromAddress(destinationAddress.address()));
    destinationAddress.tag().ifPresent(tag -> payment.destinationTag(new UInt32(tag)));

    if (transaction.getMemosCount() > 0) {
      STArray memoArray = new STArray();
      for (Memo memo : transaction.getMemosList()) {
        String json = convertMemo(memo);
        memoArray.add(STObject.fromJSON(json));
      }
      payment.put(STArray.Memos, memoArray);
    }
    return payment;
  }

}
