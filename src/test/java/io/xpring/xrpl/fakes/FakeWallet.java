package io.xpring.xrpl.fakes;

import io.xpring.xrpl.Wallet;
import io.xpring.xrpl.XRPException;
import io.xpring.xrpl.javascript.JavaScriptWallet;
import io.xpring.xrpl.javascript.JavaScriptWalletFactory;

/**
 * A fake {@link Wallet} which always produces the given signature.
 */
public class FakeWallet extends Wallet {
    /**
    * A public key to default to.
    */
    public static final String DEFAULT_PUBLIC_KEY = "031D68BC1A142E6766B2BDFB006CCFE135EF2E0E2E94ABB5CF5C9AB6104776FBAE";

    /**
     * A private key to default to.
     */
    public static final String DEFAULT_PRIVATE_KEY = "0090802A50AA84EFB6CDB225F17C27616EA94048C179142FECF03F4712A07EA7A4";

    /** The signature that this wallet will always produce. */
    private String signature;

    /**
     * Initialize a wallet which will always produce the same signature when asked to sign inputs.
     *
     * The wallet will use DEFAULT_PUBLIC_KEY and DEFAULT_PRIVATE_KEY as a set of keys.
     *
     * @param signature The signature this wallet will produce.
     */
    public FakeWallet(String signature) throws XRPException {
        this(signature, DEFAULT_PUBLIC_KEY, DEFAULT_PRIVATE_KEY);
    }

    /**
     * Initialize a wallet which will always produce the same signature when asked to sign inputs.
     *
     * @param signature The signature this wallet will produce.
     * @param publicKey A hex encoded string representing a public key.
     * @param privateKey A hex encoded string representing a private key.
     */
    public FakeWallet(String signature, String publicKey, String privateKey) throws XRPException {
        super(JavaScriptWalletFactory.get().walletFromKeys(publicKey, privateKey, true));
        this.signature = signature;
    }

    /**
     * Return a fake signature for any input.
     *
     * @param hex The hex to sign.
     */
    @Override
    public String sign(String hex) {
        return this.signature;
    }
}
