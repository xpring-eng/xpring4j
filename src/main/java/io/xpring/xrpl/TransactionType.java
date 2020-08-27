package io.xpring.xrpl;

/**
 * Types of transactions on the XRP Ledger.
 * <p>
 * This is a partial list. Please file an issue if you have a use case that requires additional types.
 * </p>
 * See: https://xrpl.org/transaction-formats.html
 */
public enum TransactionType {
  ACCOUNTSET,
  ACCOUNTDELETE,
  CHECKCANCEL,
  CHECKCASH,
  CHECKCREATE,
  DEPOSITPREAUTH,
  ESCROWCANCEL,
  ESCROWCREATE,
  OFFERCANCEL,
  OFFERCREATE,
  PAYMENT,
  PAYMENTCHANNELCLAIM,
  PAYMENTCHANNELCREATE,
  PAYMENTCHANNELFUND,
  SETREGULARKEY,
  SIGNERLISTSET
}
