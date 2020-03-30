package io.xpring.xrpl.model;

import com.google.protobuf.ByteString;
import org.xrpl.rpc.v1.Signer;
import org.immutables.value.Value;

import javax.annotation.Nullable;

/**
 * Represents a signer of a transaction on the XRP Ledger.
 * @see "https://xrpl.org/transaction-common-fields.html#signers-field"
 */
@Value.Immutable
public interface XRPSigner {
    static ImmutableXRPSigner.Builder builder() {
        return ImmutableXRPSigner.builder();
    }

    /**
     * @return The address associated with this signature, as it appears in the SignerList.
     */
    String account();

    /**
     * @return The public key used to create this signature.
     */
    @Nullable byte[] signingPublicKey();

    /**
     * @return A signature for this transaction, verifiable using the SigningPubKey.
     */
    @Nullable byte[] transactionSignature();

    static XRPSigner from(Signer signer) {
        String account = signer.getAccount().getValue().getAddress();
        byte[] signingPublicKey;
        byte[] transactionSignature;
        if (account.isEmpty()) {
            account = null;
        }
        if (signer.getSigningPublicKey().getValue() == ByteString.EMPTY) {
            signingPublicKey = null;
        } else {
            signingPublicKey = signer.getSigningPublicKey().getValue().toByteArray();
        }
        if (signer.getTransactionSignature().getValue() == ByteString.EMPTY) {
            transactionSignature = null;
        } else {
            transactionSignature = signer.getTransactionSignature().getValue().toByteArray();
        }
        return XRPSigner.builder()
                .account(account)
                .signingPublicKey(signingPublicKey)
                .transactionSignature(transactionSignature)
                .build();
    }
}