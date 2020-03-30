package io.xpring.xrpl.model;

import com.google.protobuf.ByteString;
import io.xpring.xrpl.RippledFlags;
import io.xpring.xrpl.TransactionType;
import org.xrpl.rpc.v1.Payment;
import org.xrpl.rpc.v1.Transaction;
import org.immutables.value.Value;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;


@Value.Immutable
public interface XRPTransaction {
    static ImmutableXRPTransaction.Builder builder() {
        return ImmutableXRPTransaction.builder();
    }

    String account();
    byte[] accountTransactionID();
    Long fee();
    Integer flags();
    Integer lastLedgerSequence();
    List<XRPMemo> memos();
    Number sequence();
    List<XRPSigner> signers();
    byte[] signingPublicKey();
    Integer sourceTag();
    byte[] transactionSignature();
    TransactionType type();
    XRPPayment paymentFields();

    static XRPTransaction from(Transaction transaction) {
        String account = transaction.getAccount().getValue().getAddress();

        byte[] accountTransactionID = transaction.getAccountTransactionId().getValue().toByteArray();

        Long fee = transaction.getFee().getDrops();

        Integer flags = transaction.getFlags().getValue();

        Integer lastLedgerSequence = transaction.getLastLedgerSequence().getValue();

        List<XRPMemo> memos = transaction.getMemosList()
                .stream()
                .map(memo -> XRPMemo.from(memo))
                .collect(Collectors.toList());

        Integer sequence = transaction.getSequence().getValue();

        List<XRPSigner> signers = transaction.getSignersList()
                .stream()
                .map(signer -> XRPSigner.from(signer))
                .collect(Collectors.toList());

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
