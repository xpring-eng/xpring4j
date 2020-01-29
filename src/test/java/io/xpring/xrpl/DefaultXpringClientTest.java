package io.xpring.xrpl;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import io.xpring.proto.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import io.grpc.testing.GrpcCleanupRule;
import io.grpc.inprocess.InProcessServerBuilder;
import static org.mockito.Mockito.mock;
import static org.mockito.AdditionalAnswers.delegatesTo;
import io.grpc.ManagedChannel;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.xpring.xrpl.XpringKitException;
import io.xpring.xrpl.Utils;
import io.xpring.xrpl.ClassicAddress;
import io.xpring.xrpl.Wallet;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Optional;

/**
 * Unit tests for {@link DefaultXpringClient}.
 */
public class DefaultXpringClientTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    /** The DefaultXpringClient under test. */
    private DefaultXpringClient client;

    /** An address on the XRP Ledger. */
    private static final String XRPL_ADDRESS = "XVwDxLQ4SN9pEBQagTNHwqpFkPgGppXqrMoTmUcSKdCtcK5";

    @Test
    public void getBalanceTest() throws IOException, XpringKitException {
        // GIVEN a classic address.
        DefaultXpringClient client = new DefaultXpringClient();

        // WHEN the balance for the classic address is retrieved THEN an error is thrown.
        expectedException.expect(XpringKitException.class);
        BigInteger balance = client.getBalance(XRPL_ADDRESS);
    }
}
