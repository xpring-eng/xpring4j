package io.xpring.xrpl;

/**
 * Types of transactions on the XRP Ledger.
 * <p>
 * This is a partial list. Please file an issue if you have a use case that requires additional types.
 * </p>
 * See: https://xrpl.org/transaction-formats.html
 */
public enum TransactionType {
  ACCOUNT_SET,
  ACCOUNT_DELETE,
  CHECK_CANCEL,
  CHECK_CASH,
  CHECK_CREATE,
  DEPOSIT_PREAUTH,
  ESCROW_CANCEL,
  ESCROW_CREATE,
  OFFER_CANCEL,
  OFFER_CREATE,
  PAYMENT,
  PAYMENT_CHANNEL_CLAIM,
  PAYMENT_CHANNEL_CREATE,
  PAYMENT_CHANNEL_FUND,
  SET_REGULAR_KEY,
  SIGNER_LIST_SET
}
