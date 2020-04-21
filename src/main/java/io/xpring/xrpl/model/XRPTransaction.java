package io.xpring.xrpl.model;

import io.xpring.xrpl.TransactionType;
import io.xpring.xrpl.Utils;
import org.immutables.value.Value;
import org.xrpl.rpc.v1.CurrencyAmount;
import org.xrpl.rpc.v1.GetTransactionResponse;
import org.xrpl.rpc.v1.Payment;
import org.xrpl.rpc.v1.Transaction;

import java.util.ArrayList;
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
   * The identifying hash of the transaction.
   *
   * @return a {@link String} representation of the hash of the transaction.
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
   * If provided, this transaction is only valid if the sending account's previously-sent transaction matches the
   * provided hash.
   *
   * @return A byte array containing the hash value of another transaction.
   */
  @Value.Default
  default byte[] accountTransactionID() {
    return new byte[0];
  }

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
   * @return An Optional {@link Integer} representing the set of bit-flags for this transaction.
   */
  Optional<Integer> flags();

  /**
   * (Optional; strongly recommended) Highest ledger index this transaction can appear in.
   * Specifying this field places a strict upper limit on how long the transaction can wait to be validated or rejected.
   *
   * @return An Optional {@link Integer} representing the highest ledger index this transaction can appear in.
   */
  Optional<Integer> lastLedgerSequence();

  /**
   * (Optional) Additional arbitrary information used to identify this transaction.
   *
   * @return A {@link List} of {@link XRPMemo}s containing additional information for this transaction.
   */
  @Value.Default
  default List<XRPMemo> memos() {
    return new ArrayList<>();
  }

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
  @Value.Default
  default List<XRPSigner> signers() {
    return new ArrayList<>();
  }

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
   * @return An Optional {@link Integer} representing the source tag of this transaction.
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
   * (Optional) The timestamp of the transaction reported in Unix time (seconds).
   *
   * @return An {@link Integer} representing the timestamp of the transaction.
   * @see "https://xrpl.org/basic-data-types.html#specifying-time"
   */
  Optional<Integer> timestamp();

  /**
   * (Optional, omitted for non-Payment transactions) The Currency Amount actually received by the Destination account.
   * Use this field to determine how much was delivered, regardless of whether the transaction is a partial payment.
   *
   * @return A {@link String} representation of the amount actually delivered by this transaction.
   * @see "https://xrpl.org/transaction-metadata.html#delivered_amount"
   */
  Optional<String> deliveredAmount();

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
    final Transaction transaction = getTransactionResponse.getTransaction();
    if (transaction == null) {
      return null;
    }

    byte[] transactionHashBytes = getTransactionResponse.getHash().toByteArray();
    final String hash = Utils.byteArrayToHex(transactionHashBytes);

    final String account = transaction.getAccount().getValue().getAddress();

    final byte[] accountTransactionID = transaction.getAccountTransactionId().getValue().toByteArray();

    final Long fee = transaction.getFee().getDrops();

    Optional<Integer> flags = Optional.empty();
    if (transaction.hasFlags()) {
      flags = Optional.of(transaction.getFlags().getValue());
    }

    Optional<Integer> lastLedgerSequence = Optional.empty();
    if (transaction.hasLastLedgerSequence()) {
      lastLedgerSequence = Optional.of(transaction.getLastLedgerSequence().getValue());
    }

    final List<XRPMemo> memos = transaction.getMemosList()
        .stream()
        .map(XRPMemo::from)
        .collect(Collectors.toList());

    final Integer sequence = transaction.getSequence().getValue();

    final List<XRPSigner> signers = transaction.getSignersList()
        .stream()
        .map(XRPSigner::from)
        .collect(Collectors.toList());

    final byte[] signingPublicKey = transaction.getSigningPublicKey().getValue().toByteArray();

    Optional<Integer> sourceTag = Optional.empty();
    if (transaction.hasSourceTag()) {
      sourceTag = Optional.of(transaction.getSourceTag().getValue());
    }

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
    Optional<Integer> timestamp = Optional.empty();
    if (getTransactionResponse.hasDate()) {
      Integer rippleTransactionDate = getTransactionResponse.getDate().getValue();
      if (rippleTransactionDate != null) {
        timestamp = Optional.of(rippleTransactionDate + 946684800);
      }
    }

    Optional<String> deliveredAmount = Optional.empty();
    if (getTransactionResponse.getMeta().hasDeliveredAmount()) {
      CurrencyAmount currencyAmountDelivered = getTransactionResponse.getMeta().getDeliveredAmount().getValue();
      switch (currencyAmountDelivered.getAmountCase()) {
        case XRP_AMOUNT:
          deliveredAmount = Optional.of(Long.toString(currencyAmountDelivered.getXrpAmount().getDrops()));
          break;
        case ISSUED_CURRENCY_AMOUNT:
          deliveredAmount = Optional.of(Long.toString(currencyAmountDelivered.getXrpAmount().getDrops()));
          break;
        default: // there MUST be one of XRP drops or issued currency
          return null;
      }
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
