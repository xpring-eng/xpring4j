package io.xpring.xrpl.model;

import com.google.protobuf.ByteString;
import io.xpring.xrpl.TransactionType;
import io.xpring.xrpl.javascript.JavaScriptLoaderException;
import io.xpring.xrpl.javascript.JavaScriptUtils;
import org.immutables.value.Value;
import org.xrpl.rpc.v1.CurrencyAmount;
import org.xrpl.rpc.v1.GetTransactionResponse;
import org.xrpl.rpc.v1.Payment;
import org.xrpl.rpc.v1.Transaction;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A transaction on the XRP Ledger.
 *
 * @see "https://xrpl.org/transaction-formats.html"
 */
// TODO(amiecorso): Modify this object to use X-Address format.
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
@Value.Immutable
public interface XRPTransaction {
  static ImmutableXRPTransaction.Builder builder() {
    return ImmutableXRPTransaction.builder();
  }

  /**
   * The identifying hash of the transaction.
   */
  String hash();

  /**
   * The unique address of the account that initiated the transaction.
   *
   * @return A {@link String} containing the unique address of the account that initiated the transaction.
   */
  String account();

  /**
   * (Optional) Hash value identifying another transaction.
   * <p>
   * If provided, this transaction is only valid if the sending account's previously-sent transaction matches the
   * provided hash.
   * </p>
   *
   * @return A byte array containing the hash value of another transaction.
   */
  @Nullable
  byte[] accountTransactionID();

  /**
   * The amount of XRP, in drops, to be destroyed as a cost for distributing this transaction to the network.
   *
   * @return A {@link Long} representing amount of XRP, in drops, to be destroyed as a cost for distributing
   *          this transaction to the network.
   */
  Long fee();

  /**
   * (Optional) set of bit-flags for this transaction.
   *
   * @return An {@link Integer} representing the set of bit-flags for this transaction.
   */
  @Nullable
  Integer flags();

  /**
   * (Optional; strongly recommended) Highest ledger index this transaction can appear in.
   * <p>
   * Specifying this field places a strict upper limit on how long the transaction can wait to be validated or rejected.
   * </p>
   *
   * @return An {@link Integer} representing the highest ledger index this transaction can appear in.
   */
  @Nullable
  Integer lastLedgerSequence();

  /**
   * (Optional) Additional arbitrary information used to identify this transaction.
   *
   * @return A {@link List} of {@link XRPMemo}s containing additional information for this transaction.
   */
  @Nullable
  List<XRPMemo> memos();

  /**
   * The sequence number of the account sending the transaction.
   * <p>
   * A transaction is only valid if the Sequence number is exactly 1 greater than the previous transaction from the same
   * account.
   * </p>
   *
   * @return An {@link Integer} representing the sequence number of the account sending the transaction.
   */
  Integer sequence();

  /**
   * A collection of signers that represent a multi-signature which authorizes this transaction.
   *
   * @return An optional {@link List} of {@link XRPSigner}s that represent a multi-signature which
   *          authorizes this transaction.
   */
  @Nullable
  List<XRPSigner> signers();

  /**
   * Hex representation of the public key that corresponds to the private key used to sign this transaction.
   * <p>
   * If an empty string, indicates a multi-signature is present in the Signers field instead.
   * </p>
   *
   * @return A byte array containing the public key.
   */
  byte[] signingPublicKey();

  /**
   * (Optional) Arbitrary integer used to identify the reason for this payment or a sender on whose behalf this
   * transaction is made.
   * <p>
   * Conventionally, a refund should specify the initial payment's SourceTag as the refund payment's DestinationTag.
   * </p>
   *
   * @return An {@link Integer} representing the source tag of this transaction.
   */
  @Nullable
  Integer sourceTag();

  /**
   * The signature that verifies this transaction as originating from the account it says it is from.
   *
   * @return A byte array containing the signature that verifies this transaction as originating from
   *          the account it says it is from.
   */
  byte[] transactionSignature();

  /**
   * The type of this {@link TransactionType}.
   *
   * @return A {@link TransactionType} representing the type of transaction.
   */
  TransactionType type();

  /**
   * Additional fields present in an {@link XRPPayment}.
   *
   * @return A {@link XRPPayment} representing the additional fields present in an {@link XRPPayment}.
   * @see "https://xrpl.org/payment.html#payment-fields"
   */
  XRPPayment paymentFields();

  /**
   * The timestamp of the transaction reported in Unix time (seconds).
   *
   * @see "https://xrpl.org/basic-data-types.html#specifying-time"
   */
  @Nullable
  Integer timestamp();

  /**
   * (Omitted for non-Payment transactions) The Currency Amount actually received by the Destination account.
   * Use this field to determine how much was delivered, regardless of whether the transaction is a partial payment.
   *
   * @see "https://xrpl.org/transaction-metadata.html#delivered_amount"
   */
  @Nullable
  String deliveredAmount();

  /**
   * Constructs an {@link XRPTransaction} from a {@link GetTransactionResponse}.
   *
   * @param getTransactionResponse a {@link GetTransactionResponse} (protobuf object) whose field values will be used
   *                    to construct an {@link XRPTransaction}
   * @return an {@link XRPTransaction} with its fields set via the analogous protobuf fields.
   * @see <a href="https://github.com/ripple/rippled/blob/develop/src/ripple/proto/org/xrpl/rpc/v1/get_transaction.proto#L31">
   * GetTransactionResponse protocol buffer</a>
   */
  static XRPTransaction from(GetTransactionResponse getTransactionResponse) {
    JavaScriptUtils javaScriptUtils;
    try {
      javaScriptUtils = new JavaScriptUtils();
    } catch (JavaScriptLoaderException e) {
      throw new RuntimeException(e);
    }

    final Transaction transaction = getTransactionResponse.getTransaction();
    if (transaction == null) {
      return null;
    }

    byte[] transactionHashBytes = getTransactionResponse.getHash().toByteArray();
    final String hash = javaScriptUtils.toHex(transactionHashBytes);

    final String account = transaction.getAccount().getValue().getAddress();

    ByteString accountTransactionIDByteString = transaction.getAccountTransactionId().getValue();
    final byte[] accountTransactionID = accountTransactionIDByteString.equals(ByteString.EMPTY)
        ? null : accountTransactionIDByteString.toByteArray();

    final Long fee = transaction.getFee().getDrops();

    final Integer flags = transaction.getFlags().getValue();

    final Integer lastLedgerSequence = transaction.getLastLedgerSequence().getValue();

    List<XRPMemo> memos = transaction.getMemosList()
        .stream()
        .map(XRPMemo::from)
        .collect(Collectors.toList());
    if (memos.isEmpty()) {
      memos = null;
    }

    final Integer sequence = transaction.getSequence().getValue();

    List<XRPSigner> signers = transaction.getSignersList()
        .stream()
        .map(XRPSigner::from)
        .collect(Collectors.toList());
    if (signers.isEmpty()) {
      signers = null;
    }

    final byte[] signingPublicKey = transaction.getSigningPublicKey().getValue().toByteArray();

    final Integer sourceTag = transaction.getSourceTag().getValue();

    final byte[] transactionSignature = transaction.getTransactionSignature().getValue().toByteArray();

    TransactionType type;
    XRPPayment paymentFields;
    switch (transaction.getTransactionDataCase()) {
      case PAYMENT: {
        Payment payment = transaction.getPayment();
        paymentFields = XRPPayment.from(payment);
        if (paymentFields == null) {
          return null;
        }
        type = TransactionType.PAYMENT;
        break;
      }
      default: {
        // unsupported transaction type
        return null;
      }
    }

    // Transactions report their timestamps since the Ripple Epoch, which is 946,684,800 seconds after
    // the unix epoch. Convert transaction's timestamp to a unix timestamp.
    // See: https://xrpl.org/basic-data-types.html#specifying-time
    Integer timestamp;
    if (getTransactionResponse.hasDate()) {
      Integer rippleTransactionDate = getTransactionResponse.getDate().getValue();
      timestamp = rippleTransactionDate != null
              ? rippleTransactionDate + 946684800
              : null;
    } else {
      timestamp = null;
    }

    String deliveredAmount;
    if (getTransactionResponse.getMeta().hasDeliveredAmount()) {
      CurrencyAmount currencyAmountDelivered = getTransactionResponse.getMeta().getDeliveredAmount().getValue();
      if (currencyAmountDelivered.hasXrpAmount()) {
        deliveredAmount = Long.toString(currencyAmountDelivered.getXrpAmount().getDrops());
      } else {
        if (currencyAmountDelivered.hasIssuedCurrencyAmount()) {
          deliveredAmount = currencyAmountDelivered.getIssuedCurrencyAmount().getValue();
        } else { // there MUST be one of XRP drops or issued currency
          return null;
        }
      }
    } else { // no delivered amount (not set for non-payment transactions)
      deliveredAmount = null;
    }


    return XRPTransaction.builder()
        .hash(hash)
        .account(account)
        .accountTransactionID(accountTransactionID)
        .fee(fee)
        .flags(flags)
        .lastLedgerSequence(lastLedgerSequence)
        .memos(memos)
        .sequence(sequence)
        .signers(signers)
        .signingPublicKey(signingPublicKey)
        .sourceTag(sourceTag)
        .transactionSignature(transactionSignature)
        .type(type)
        .paymentFields(paymentFields)
        .timestamp(timestamp)
        .deliveredAmount(deliveredAmount)
        .build();
  }
}
