package io.xpring.xrpl.model;

import com.google.protobuf.ByteString;
import io.xpring.xrpl.TransactionType;
import org.xrpl.rpc.v1.Payment;
import org.xrpl.rpc.v1.Transaction;
import org.immutables.value.Value;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A transaction on the XRP Ledger.
 *
 * @see "https://xrpl.org/transaction-formats.html"
 */
// TODO(amiecorso): Modify this object to use X-Address format.
@Value.Immutable
public interface XRPTransaction {
    static ImmutableXRPTransaction.Builder builder() {
        return ImmutableXRPTransaction.builder();
    }

    /**
     * @return The unique address of the account that initiated the transaction.
     */
    String account();

    /**
     * @return (Optional) Hash value identifying another transaction.
     * If provided, this transaction is only valid if the sending account's previously-sent
     * transaction matches the provided hash.
     */
    @Nullable
    byte[] accountTransactionID();

    /**
     * @return Integer amount of XRP, in drops, to be destroyed as a cost for distributing
     * this transaction to the network.
     */
    Long fee();

    /**
     * @return (Optional) Set of bit-flags for this transaction.
     */
    @Nullable
    Integer flags();

    /**
     * @return (Optional; strongly recommended) Highest ledger index this transaction can appear in.
     * Specifying this field places a strict upper limit on how long the transaction can wait
     * to be validated or rejected.
     */
    @Nullable
    Integer lastLedgerSequence();

    /**
     * @return (Optional) Additional arbitrary information used to identify this transaction.
     */
    @Nullable
    List<XRPMemo> memos();

    /**
     * @return The sequence number of the account sending the transaction.
     * A transaction is only valid if the Sequence number is exactly 1 greater
     * than the previous transaction from the same account.
     */
    Integer sequence();

    /**
     * @return (Optional) Array of objects that represent a multi-signature which authorizes this transaction.
     */
    @Nullable
    List<XRPSigner> signers();

    /**
     * @return Hex representation of the public key that corresponds to the private key used to sign this transaction.
     * If an empty string, indicates a multi-signature is present in the Signers field instead.
     */
    byte[] signingPublicKey();

    /**
     * @return (Optional) Arbitrary integer used to identify the reason for this payment,
     * or a sender on whose behalf this transaction is made.
     * Conventionally, a refund should specify the initial payment's SourceTag as the refund payment's DestinationTag.
     */
    @Nullable
    Integer sourceTag();

    /**
     * @return The signature that verifies this transaction as originating from the account it says it is from.
     */
    byte[] transactionSignature();

    /**
     * @return The type of transaction.
     */
    TransactionType type();

    /**
     * @return an XRPPayment object representing the additional fields present in a PAYMENT transaction.
     * @see "https://xrpl.org/payment.html#payment-fields"
     */
    XRPPayment paymentFields();

    /**
     * Constructs an {@link XRPTransaction} from a {@link Transaction}
     * @see <a href="https://github.com/ripple/rippled/blob/develop/src/ripple/proto/org/xrpl/rpc/v1/transaction.proto#L13">
     *     Transaction protocol buffer</a>
     *
     * @param transaction a {@link Transaction} (protobuf object) whose field values will be used
     *                 to construct an {@link XRPTransaction}
     * @return an {@link XRPTransaction} with its fields set via the analogous protobuf fields.
     */
    static XRPTransaction from(Transaction transaction) {
        String account = transaction.getAccount().getValue().getAddress();

        ByteString accountTxnIDByteString = transaction.getAccountTransactionId().getValue();
        byte[] accountTransactionID = accountTxnIDByteString.equals(ByteString.EMPTY) ?
                null : accountTxnIDByteString.toByteArray();

        Long fee = transaction.getFee().getDrops();

        Integer flags = transaction.getFlags().getValue();

        Integer lastLedgerSequence = transaction.getLastLedgerSequence().getValue();

        List<XRPMemo> memos = transaction.getMemosList()
                .stream()
                .map(memo -> XRPMemo.from(memo))
                .collect(Collectors.toList());
        if (memos.isEmpty()) {
            memos = null;
        }

        Integer sequence = transaction.getSequence().getValue();

        List<XRPSigner> signers = transaction.getSignersList()
                .stream()
                .map(signer -> XRPSigner.from(signer))
                .collect(Collectors.toList());
        if (signers.isEmpty()) {
            signers = null;
        }

        byte[] signingPublicKey = transaction.getSigningPublicKey().getValue().toByteArray();

        Integer sourceTag = transaction.getSourceTag().getValue();

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
