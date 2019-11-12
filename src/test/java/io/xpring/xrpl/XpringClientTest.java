package io.xpring.xrpl;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import io.xpring.Utils;
import io.xpring.proto.*;
import io.xpring.Wallet;
import io.xpring.XpringKitException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.grpc.testing.GrpcCleanupRule;
import io.grpc.inprocess.InProcessServerBuilder;
import static org.mockito.Mockito.mock;
import static org.mockito.AdditionalAnswers.delegatesTo;
import io.grpc.ManagedChannel;
import io.grpc.inprocess.InProcessChannelBuilder;

import java.math.BigInteger;

/**
 * Unit tests for {@link XpringClient}.
 */
public class XpringClientTest {
    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    /** The XpringClient under test. */
    private XpringClient client;

    /** An address on the XRP Ledger. */
    private static final String XRPL_ADDRESS = "rD7zai6QQQVvWc39ZVAhagDgtH5xwEoeXD";

    /** Mocked values in responses from the gRPC server. */
    private static final String DROPS_OF_XRP_IN_ACCOUNT = "10";
    private static final String DROPS_OF_XRP_FOR_FEE = "20";
    private static final String TRANSACTION_BLOB = "DEADBEEF";

    /** A mock implementation of the gRPC server. */
    private final XRPLedgerAPIGrpc.XRPLedgerAPIImplBase serviceImpl =
            mock(XRPLedgerAPIGrpc.XRPLedgerAPIImplBase.class, delegatesTo(
                new XRPLedgerAPIGrpc.XRPLedgerAPIImplBase() {
                    @Override
                    public void getAccountInfo(io.xpring.proto.GetAccountInfoRequest request,
                                               io.grpc.stub.StreamObserver<io.xpring.proto.AccountInfo> responseObserver) {
                        XRPAmount balanceAmount = XRPAmount.newBuilder().setDrops(DROPS_OF_XRP_IN_ACCOUNT).build();
                        AccountInfo accountInfo = AccountInfo.newBuilder().setBalance(balanceAmount).build();

                        responseObserver.onNext(accountInfo);
                        responseObserver.onCompleted();
                    }

                    @Override
                    public void getFee(io.xpring.proto.GetFeeRequest request,
                                       io.grpc.stub.StreamObserver<io.xpring.proto.Fee> responseObserver) {
                        XRPAmount feeAmount = XRPAmount.newBuilder().setDrops(DROPS_OF_XRP_FOR_FEE).build();
                        Fee fee = Fee.newBuilder().setAmount(feeAmount).build();

                        responseObserver.onNext(fee);
                        responseObserver.onCompleted();
                    }

                    @Override
                    public void submitSignedTransaction(io.xpring.proto.SubmitSignedTransactionRequest request,
                                                        io.grpc.stub.StreamObserver<io.xpring.proto.SubmitSignedTransactionResponse> responseObserver) {
                        SubmitSignedTransactionResponse response = SubmitSignedTransactionResponse.newBuilder().setTransactionBlob(TRANSACTION_BLOB).build();

                        responseObserver.onNext(response);
                        responseObserver.onCompleted();
                    }
                }
            )
    );

    /** Mocks gRPC networking inside of XpringClient. */
    @Before
    public void setUp() throws Exception {
        // Generate a unique in-process server name.
        String serverName = InProcessServerBuilder.generateName();

        // Create a server, add service, start, and register for automatic graceful shutdown.
        grpcCleanup.register(InProcessServerBuilder
                .forName(serverName).directExecutor().addService(serviceImpl).build().start());

        // Create a client channel and register for automatic graceful shutdown.
        ManagedChannel channel = grpcCleanup.register(
                InProcessChannelBuilder.forName(serverName).directExecutor().build());

        // Create a new XpringClient using the in-process channel;
        client = new XpringClient(channel);
    }

    @Test
    public void getBalanceTest() {
        // GIVEN a XpringClient with mocked networking WHEN the balance is retrieved.
        BigInteger balance = client.getBalance(XRPL_ADDRESS);

        // THEN the balance returned is the the same as the mocked response.
        assertThat(balance.toString()).isEqualTo(DROPS_OF_XRP_IN_ACCOUNT);
    }

    @Test
    public void submitTransactionTest() throws XpringKitException {
        // GIVEN a XpringClient with mocked networking WHEN a transaction is sent.
        Wallet wallet = new Wallet("snYP7oArxKepd3GPDcrjMsJYiJeJB");
        String transactionHash = client.send(new BigInteger("30"), XRPL_ADDRESS, wallet);

        // THEN the transaction hash is the same as the hash of the mocked transaction blob in the response.
        String expectedTransactionHash = Utils.toTransactionHash(TRANSACTION_BLOB);
        assertThat(transactionHash).isEqualTo(expectedTransactionHash);
    }
}
