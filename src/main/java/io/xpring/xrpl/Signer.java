package io.xpring.xrpl;

import com.google.common.io.BaseEncoding;
import com.ripple.core.types.known.tx.txns.AccountSet;
import com.ripple.core.types.known.tx.txns.Payment;
import io.xpring.xrpl.converters.AccountSetConverter;
import io.xpring.xrpl.converters.PaymentConverter;
import org.xrpl.rpc.v1.Transaction;

public class Signer {

  private static final PaymentConverter paymentConverter = new PaymentConverter();
  private static final AccountSetConverter accountSetConverter = new AccountSetConverter();

  /**
   * Please do not instantiate this static utility class.
   */
  private Signer() {
  }

  /**
   * Sign the given transaction with the given wallet.
   *
   * @param transaction The transaction to sign.
   * @param wallet The wallet that will sign the transaction.
   * @return A `SignedTransaction`.
   */
  public static byte[] signTransaction(Transaction transaction, Wallet wallet) {
    if (transaction.hasPayment()) {
      return signPayment(transaction, wallet);
    } else if (transaction.hasAccountSet()) {
      return signAccountSet(transaction, wallet);
    }
    throw new IllegalArgumentException("unsupported transaction " + transaction);
  }

  /**
   * Sign the given transaction with the given wallet.
   *
   * @param transaction The transaction to sign.
   * @param wallet The wallet that will sign the transaction.
   * @return A `SignedTransaction`.
   */
  public static byte[] signPayment(Transaction transaction, Wallet wallet) {
    try {
      Payment convertedPayment = paymentConverter.convert(transaction);
      String txBlob = convertedPayment.sign(wallet.getWalletGenerationResult().getKeyPair()).tx_blob;
      return BaseEncoding.base16().decode(txBlob);
    } catch (Exception exception) {
      throw new RuntimeException(exception);
    }
  }

  /**
   * Sign the given transaction with the given wallet.
   *
   * @param transaction The transaction to sign.
   * @param wallet The wallet that will sign the transaction.
   * @return A `SignedTransaction`.
   */
  public static byte[] signAccountSet(Transaction transaction, Wallet wallet) {
    try {
      AccountSet convertedPayment = accountSetConverter.convert(transaction);
      String txBlob = convertedPayment.sign(wallet.getWalletGenerationResult().getKeyPair()).tx_blob;
      return BaseEncoding.base16().decode(txBlob);
    } catch (Exception exception) {
      throw new RuntimeException(exception);
    }
  }

}
