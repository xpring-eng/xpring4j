package io.xpring.xpring;

import io.xpring.payid.PayIDClientInterface;
import io.xpring.payid.PayIDException;
import io.xpring.xrpl.Wallet;
import io.xpring.xrpl.XRPClientInterface;
import io.xpring.xrpl.XRPException;

import java.math.BigInteger;

/**
 * Composes interactions of Xpring services.
 */
public class XpringClient {
    /** A {@link PayIDClientInterface} used to interact with the Pay ID protocol. */
    private PayIDClientInterface payIDClient;

    /** A {@link XRPClientInterface} used to interact with the Pay ID protocol. */
    private XRPClientInterface xrpClient;

    /**
     * Create a new XpringClient.
     *
     * @param payIDClient A Pay ID Client used to interact with the Pay ID protocol.
     * @param xrpClient An XRP Client used to interact with the XRP Ledger protocol.
     */
    public XpringClient(PayIDClientInterface payIDClient, XRPClientInterface xrpClient) throws XpringException {
        if (payIDClient.getNetwork() != xrpClient.getNetwork()) {
            throw XpringException.MISMATCHED_NETWORKS;
        }

        this.payIDClient = payIDClient;
        this.xrpClient = xrpClient;
    }

    /**
     * Transact XRP between two accounts on the ledger.
     *
     * @param amount The number of drops of XRP to send.
     * @param destinationPayID A destination Pay ID to send the drops to.
     * @param sourceWallet The {@link Wallet} which holds the XRP.
     * @return A transaction hash for the payment.
     * @throws PayIDException If there was a problem resolving the Pay ID.
     * @throws XpringException If there was a problem sending XRP.
     * */
    public String send(
            final BigInteger amount,
            final String destinationPayID,
            final Wallet sourceWallet
    ) throws PayIDException, XRPException {
        // Resolve the destination address to an XRP address.
        String destinationAddress = this.payIDClient.xrpAddressForPayID(destinationPayID);

        // Transact XRP to the resolved address.
        return this.xrpClient.send(amount, destinationAddress, sourceWallet);
    }
}