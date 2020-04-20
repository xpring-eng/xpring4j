package io.xpring.xrpl.model;

import com.google.protobuf.ByteString;
import io.xpring.xrpl.TransactionType;
import org.immutables.value.Value;
import org.xrpl.rpc.v1.Payment;
import org.xrpl.rpc.v1.Transaction;

import java.util.List;
import java.util.Optional;
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
   * The unique address of the account that initiated the transaction.
   *
   * @return A {@link String} containing the unique address of the account that initiated the transaction.
   */
  String account();

  /**
   * (Optional) Hash value identifying another transaction.
   * If provided, this transaction is only valid if the sending account's previously-sent transaction matches the
   * provided hash.
   *
   * @return A byte array containing the hash value of another transaction.
   */
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
  Optional<Integer> flags();

  /**
   * (Optional; strongly recommended) Highest ledger index this transaction can appear in.
   * Specifying this field places a strict upper limit on how long the transaction can wait to be validated or rejected.
   *
   * @return An {@link Integer} representing the highest ledger index this transaction can appear in.
   */
  Optional<Integer> lastLedgerSequence();

  /**
   * (Optional) Additional arbitrary information used to identify this transaction.
   *
   * @return A {@link List} of {@link XRPMemo}s containing additional information for this transaction.
   */
  List<XRPMemo> memos();

  /**
   * The sequence number of the account sending the transaction.
   * A transaction is only valid if the Sequence number is exactly 1 greater than the previous transaction from the same
   * account.
   *
   * @return An {@link Integer} representing the sequence number of the account sending the transaction.
   */
  Integer sequence();

  /**
   * (Optional) A collection of signers that represent a multi-signature which authorizes this transaction.
   *
   * @return An optional {@link List} of {@link XRPSigner}s that represent a multi-signature which
   *          authorizes this transaction.
   */
  List<XRPSigner> signers();

  /**
   * Hex representation of the public key that corresponds to the private key used to sign this transaction.
   * If an empty string, indicates a multi-signature is present in the Signers field instead.
   *
   * @return A byte array containing the public key.
   */
  byte[] signingPublicKey();

  /**
   * (Optional) Arbitrary integer used to identify the reason for this payment or a sender on whose behalf this
   * transaction is made.
   * Conventionally, a refund should specify the initial payment's SourceTag as the refund payment's DestinationTag.
   *
   * @return An {@link Integer} representing the source tag of this transaction.
   */
  Optional<Integer> sourceTag();

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
   * Constructs an {@link XRPTransaction} from a {@link Transaction}.
   *
   * @param transaction a {@link Transaction} (protobuf object) whose field values will be used
   *                    to construct an {@link XRPTransaction}
   * @return an {@link XRPTransaction} with its fields set via the analogous protobuf fields.
   * @see <a href="https://github.com/ripple/rippled/blob/develop/src/ripple/proto/org/xrpl/rpc/v1/transaction.proto#L13">
   * Transaction protocol buffer</a>
   */
  static XRPTransaction from(Transaction transaction) {
    final String account = transaction.getAccount().getValue().getAddress();

    byte[] accountTransactionID = transaction.getAccountTransactionId().getValue().toByteArray();

    final Long fee = transaction.getFee().getDrops();

    Optional<Integer> flags = Optional.empty();
    if (transaction.hasFlags()) {
      flags = Optional.of(transaction.getFlags().getValue());
    }

    Optional<Integer> lastLedgerSequence = Optional.empty();
    if (transaction.hasLastLedgerSequence()) {
      lastLedgerSequence = Optional.of(transaction.getLastLedgerSequence().getValue());
    }

    List<XRPMemo> memos = transaction.getMemosList()
        .stream()
        .map(XRPMemo::from)
        .collect(Collectors.toList());

    Integer sequence = transaction.getSequence().getValue();

    List<XRPSigner> signers = transaction.getSignersList()
        .stream()
        .map(XRPSigner::from)
        .collect(Collectors.toList());

    byte[] signingPublicKey = transaction.getSigningPublicKey().getValue().toByteArray();

    Optional<Integer> sourceTag = Optional.empty();
    if (transaction.hasSourceTag()) {
      sourceTag = Optional.of(transaction.getSourceTag().getValue());
    }

    byte[] transactionSignature = transaction.getTransactionSignature().getValue().toByteArray();

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

    return XRPTransaction.builder()
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
        .build();
  }
}
