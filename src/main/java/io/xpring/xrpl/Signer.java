package io.xpring.xrpl;

import io.xpring.xrpl.javascript.JavaScriptLoaderException;
import io.xpring.xrpl.javascript.JavaScriptSigner;
import org.xrpl.rpc.v1.Transaction;

public class Signer {
  private static final JavaScriptSigner javascriptSigner;

  static {
    try {
      javascriptSigner = new JavaScriptSigner();
    } catch (JavaScriptLoaderException e) {
      throw new RuntimeException(e);
    }
  }

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
    try {
      return javascriptSigner.signTransaction(transaction, wallet);
    } catch (Exception exception) {
      throw new RuntimeException(exception);
    }
  }
}
