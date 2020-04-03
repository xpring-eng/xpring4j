package io.xpring.xpring;

import io.xpring.payid.PayIDClient;
import io.xpring.payid.PayIDException;
import io.xpring.xrpl.Wallet;
import io.xpring.xrpl.XRPClient;
import io.xpring.xrpl.XpringException;

import java.math.BigInteger;

/**
 * Composes interactions of Xpring services.
 */
public class XpringClient {
    /** A {@link PayIDClient} used to interact with the Pay ID protocol. */
    private PayIDClient payIDClient;

    /** A {@link XRPClient} used to interact with the Pay ID protocol. */
    private XRPClient xrpClient;

    /**
     * Create a new XpringClient.
     *
     * @param payIDClient A Pay ID Client used to interact with the Pay ID protocol.
     * @param xrpClient An XRP Client used to interact with the XRP Ledger protocol.
     */
    public XpringClient(PayIDClient payIDClient, XRPClient xrpClient) {
        // TODO(keefertaylor): Verify that given inputs are on the same network.
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
    ) throws PayIDException, XpringException {
        // Resolve the destination address to an XRP address.
        String destinationAddress = this.payIDClient.xrpAddressForPayID(destinationPayID);

        // Transact XRP to the resolved address.
        return this.xrpClient.send(amount, destinationAddress, sourceWallet);
    }
}