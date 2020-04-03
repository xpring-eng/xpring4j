package io.xpring.xrpl;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class WalletTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testGenerateMainNetWalletFromSeed() throws XRPException {
        Wallet wallet = new Wallet("snYP7oArxKepd3GPDcrjMsJYiJeJB");
        assertEquals(wallet.getAddress(), "XVnJMYQFqA8EAijpKh5EdjEY5JqyxykMKKSbrUX8uchF6U8");
    }

    @Test
    public void testGenerateTestNetWalletFromSeed() throws XRPException {
        Wallet wallet = new Wallet("snYP7oArxKepd3GPDcrjMsJYiJeJB", true);
        assertEquals(wallet.getAddress(), "T7zFmeZo6uLHP4Vd21TpXjrTBk487ZQPGVQsJ1mKWGCD5rq");
    }

    @Test
    public void testGenerateWalletFromInvalidSeed() throws XRPException {
        expectedException.expect(XRPException.class);
        new Wallet("xrp");
    }

    @Test
    public void testGenerateRandomWallet() throws XRPException {
        WalletGenerationResult generationResult = Wallet.generateRandomWallet();

        assertNotNull(generationResult.getWallet());
        assertNotNull(generationResult.getMnemonic());
        assertNotNull(generationResult.getDerivationPath());

        Wallet recreatedWallet = new Wallet(generationResult.getMnemonic(), generationResult.getDerivationPath());

        assertEquals(generationResult.getWallet().getAddress(), recreatedWallet.getAddress());
        assertEquals(generationResult.getWallet().getPublicKey(), recreatedWallet.getPublicKey());
        assertEquals(generationResult.getWallet().getPrivateKey(), recreatedWallet.getPrivateKey());
    }

    @Test
    public void testGenerateWalletFromMnemonicNoDerivationPath() throws XRPException {
        String mnemonic = "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about";
        Wallet wallet = new Wallet(mnemonic, null);

        assertEquals(wallet.getPublicKey(), "031D68BC1A142E6766B2BDFB006CCFE135EF2E0E2E94ABB5CF5C9AB6104776FBAE");
        assertEquals(wallet.getPrivateKey(), "0090802A50AA84EFB6CDB225F17C27616EA94048C179142FECF03F4712A07EA7A4");
        assertEquals(wallet.getAddress(), "XVMFQQBMhdouRqhPMuawgBMN1AVFTofPAdRsXG5RkPtUPNQ");
    }

    @Test
    public void testGenerateMainNetWalletFromMnemonicDerivationPath0() throws XRPException {
        String mnemonic = "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about";
        String derivationPath = "m/44'/144'/0'/0/0";
        Wallet wallet = new Wallet(mnemonic, derivationPath);

        assertEquals(wallet.getPublicKey(), "031D68BC1A142E6766B2BDFB006CCFE135EF2E0E2E94ABB5CF5C9AB6104776FBAE");
        assertEquals(wallet.getPrivateKey(), "0090802A50AA84EFB6CDB225F17C27616EA94048C179142FECF03F4712A07EA7A4");
        assertEquals(wallet.getAddress(), "XVMFQQBMhdouRqhPMuawgBMN1AVFTofPAdRsXG5RkPtUPNQ");
    }

    @Test
    public void testGenerateTestNetWalletFromMnemonicDerivationPath0() throws XRPException {
        String mnemonic = "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about";
        String derivationPath = "m/44'/144'/0'/0/0";
        Wallet wallet = new Wallet(mnemonic, derivationPath, true);

        assertEquals(wallet.getPublicKey(), "031D68BC1A142E6766B2BDFB006CCFE135EF2E0E2E94ABB5CF5C9AB6104776FBAE");
        assertEquals(wallet.getPrivateKey(), "0090802A50AA84EFB6CDB225F17C27616EA94048C179142FECF03F4712A07EA7A4");
        assertEquals(wallet.getAddress(), "TVHLFWLKvbMv1LFzd6FA2Bf9MPpcy4mRto4VFAAxLuNpvdW");
    }

    @Test
    public void testGenerateWalletFromMnemonicDerivationPath1() throws XRPException {
        String mnemonic = "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about";
        String derivationPath = "m/44'/144'/0'/0/1";
        Wallet wallet = new Wallet(mnemonic, derivationPath);

        assertEquals(wallet.getPublicKey(), "038BF420B5271ADA2D7479358FF98A29954CF18DC25155184AEAD05796DA737E89");
        assertEquals(wallet.getPrivateKey(), "000974B4CFE004A2E6C4364CBF3510A36A352796728D0861F6B555ED7E54A70389");
        assertEquals(wallet.getAddress(), "X7uRz9jfzHUFEjZTZ7rMVzFuTGZTHWcmkKjvGkNqVbfMhca");
    }

    @Test
    public void  testGenerateWalletFromMnemonicInvalidDerivationPath() throws XRPException {
        expectedException.expect(XRPException.class);
        String mnemonic = "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about";
        String derivationPath = "invalid_path";
        new Wallet(mnemonic, derivationPath);
    }

    @Test
    public void  testGenerateWalletFromMnemonicInvalidMnemonic() throws XRPException {
        expectedException.expect(XRPException.class);
        String mnemonic = "xrp xrp xrp xrp xrp xrp xrp xrp xrp xrp xrp xrp";
        String derivationPath = "m/44'/144'/0'/0/1";
        new Wallet(mnemonic, derivationPath);
    }

    @Test
    public void testSign() throws XRPException {
        String mnemonic = "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about";
        String derivationPath = "m/44'/144'/0'/0/0";
        Wallet wallet = new Wallet(mnemonic, derivationPath);

        String result = wallet.sign("74657374206d657373616765");
        assertEquals(result, "3045022100E10177E86739A9C38B485B6AA04BF2B9AA00E79189A1132E7172B70F400ED1170220566BD64AA3F01DDE8D99DFFF0523D165E7DD2B9891ABDA1944E2F3A52CCCB83A");
    }

    @Test
    public void testSignInvalidHex() throws XRPException {
        expectedException.expect(XRPException.class);
        String mnemonic = "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about";
        String derivationPath = "m/44'/144'/0'/0/0";
        Wallet wallet = new Wallet(mnemonic, derivationPath);

        wallet.sign("xrp");
    }

    @Test
    public void testVerify() throws XRPException {
        String mnemonic = "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about";
        String derivationPath = "m/44'/144'/0'/0/0";
        Wallet wallet = new Wallet(mnemonic, derivationPath);

        String message = "74657374206d657373616765";
        String signature = "3045022100E10177E86739A9C38B485B6AA04BF2B9AA00E79189A1132E7172B70F400ED1170220566BD64AA3F01DDE8D99DFFF0523D165E7DD2B9891ABDA1944E2F3A52CCCB83A";

        assertTrue(wallet.verify(message, signature));
    }

    @Test
    public void testVerifyInvalidSignature() throws XRPException {
        String mnemonic = "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about";
        String derivationPath = "m/44'/144'/0'/0/0";
        Wallet wallet = new Wallet(mnemonic, derivationPath);

        String message = "74657374206d657373616765";
        String signature = "DEADBEEF";

        assertFalse(wallet.verify(message, signature));
    }

    @Test
    public void testVerifyBadMessage() throws XRPException {
        String mnemonic = "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about";
        String derivationPath = "m/44'/144'/0'/0/0";
        Wallet wallet = new Wallet(mnemonic, derivationPath);

        String message = "xrp";
        String signature = "3045022100E10177E86739A9C38B485B6AA04BF2B9AA00E79189A1132E7172B70F400ED1170220566BD64AA3F01DDE8D99DFFF0523D165E7DD2B9891ABDA1944E2F3A52CCCB83A";

        assertFalse(wallet.verify(message, signature));
    }

    @Test
    public void testSignAndVerifyEmptyString() throws XRPException {
        String mnemonic = "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about";
        String derivationPath = "m/44'/144'/0'/0/0";
        Wallet wallet = new Wallet(mnemonic, derivationPath);

        String message = "";
        String signature = wallet.sign(message);

        assertTrue(wallet.verify(message, signature));
    }

    @Test
    public void testVerifyEmptyStringBadSignature() throws XRPException {
        String mnemonic = "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about";
        String derivationPath = "m/44'/144'/0'/0/0";
        Wallet wallet = new Wallet(mnemonic, derivationPath);

        String message = "";
        String signature = "DEADBEEF";

        assertFalse(wallet.verify(message, signature));
    }
}
