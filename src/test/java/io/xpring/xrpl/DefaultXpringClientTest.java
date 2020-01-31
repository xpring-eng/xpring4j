package io.xpring.xrpl;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import io.grpc.stub.StreamObserver;
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
import rpc.v1.Amount.XRPDropsAmount;
import rpc.v1.AccountInfo;
import rpc.v1.LedgerObjects.AccountRoot;
import rpc.v1.XRPLedgerAPIServiceGrpc;
import rpc.v1.AccountInfo.GetAccountInfoResponse;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Optional;


/**
 * Represents the result of a gRPC network call for an object of type T or an error.
 */
class GRPCResult<T> {
    private Optional<T> value;
    private Optional<String> error;

    private GRPCResult(T value, String error) {
        this.value = Optional.ofNullable(value);
        this.error = Optional.ofNullable(error);
    }

    public static <U> GRPCResult<U> ok(U value) {
        return new GRPCResult<>(value, null);
    }

    public static <U> GRPCResult<U> error(String error) {
        return new GRPCResult<>(null, error);
    }

    public boolean isError() {
        return error.isPresent();
    }

    public T getValue() {
        return value.get();
    }

    public String getError() {
        return error.get();
    }
}

/**
 * Unit tests for {@link DefaultXpringClient}.
 */
public class DefaultXpringClientTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    /** The DefaultXpringClient under test. */
    private DefaultXpringClient client;

    /** An address on the XRP Ledger. */
    private static final String XRPL_ADDRESS = "XVwDxLQ4SN9pEBQagTNHwqpFkPgGppXqrMoTmUcSKdCtcK5";

    /** Mocked values in responses from the gRPC server. */
    private static final long DROPS_OF_XRP_IN_ACCOUNT = 10;
    private static final String TRANSACTION_BLOB = "DEADBEEF";
    private static final String GENERIC_ERROR = "Mocked network error";

    /** The seed for a wallet with funds on the XRP Ledger test net. */
    private static final String WALLET_SEED = "snYP7oArxKepd3GPDcrjMsJYiJeJB";

    /** Drops of XRP to send. */
    private static final BigInteger AMOUNT = new BigInteger("1");

    @Test
    public void getBalanceTest() throws IOException, XpringKitException {
        // GIVEN a DefaultXpringClient with mocked networking which will succeed.
        DefaultXpringClient client = getClient();

        // WHEN the balance is retrieved.
        BigInteger balance = client.getBalance(XRPL_ADDRESS);

        // THEN the balance returned is the the same as the mocked response.
        assertThat(balance).isEqualTo(BigInteger.valueOf(DROPS_OF_XRP_IN_ACCOUNT));
    }

    @Test
    public void getBalanceWithClassicAddressTest() throws IOException, XpringKitException {
        // GIVEN a classic address.
        ClassicAddress classicAddress = Utils.decodeXAddress(XRPL_ADDRESS);
        DefaultXpringClient client = getClient();

        // WHEN the balance for the classic address is retrieved THEN an error is thrown.
        expectedException.expect(XpringKitException.class);
        client.getBalance(classicAddress.address());
    }

    @Test
    public void getBalanceTestWithFailedAccountInfo() throws IOException, XpringKitException {
        // GIVEN a XpringClient with mocked networking which will fail to retrieve account info.
        GRPCResult<GetAccountInfoResponse> accountInfoResult = GRPCResult.error(GENERIC_ERROR);
        DefaultXpringClient client = getClient(
                accountInfoResult
        );

        // WHEN the balance is retrieved THEN an error is thrown.
        expectedException.expect(Exception.class);
        client.getBalance(XRPL_ADDRESS);
    }

    /**
     * Convenience method to get a XpringClient which has successful network calls.
     */
    private DefaultXpringClient getClient() throws IOException {
        return getClient(
                GRPCResult.ok(makeGetAccountInfoResponse(DROPS_OF_XRP_IN_ACCOUNT))
        );
    }

    /**
     * Return a XpringClient which returns the given results for network calls.
     */

    private DefaultXpringClient getClient(
            GRPCResult<GetAccountInfoResponse> GetAccountInfoResponseResult
    ) throws IOException {
        XRPLedgerAPIServiceGrpc.XRPLedgerAPIServiceImplBase serviceImpl = getService(GetAccountInfoResponseResult);

        // Generate a unique in-process server name.
        String serverName = InProcessServerBuilder.generateName();

        // Create a server, add service, start, and register for automatic graceful shutdown.
        grpcCleanup.register(InProcessServerBuilder
                .forName(serverName).directExecutor().addService(serviceImpl).build().start());

        // Create a client channel and register for automatic graceful shutdown.
        ManagedChannel channel = grpcCleanup.register(
                InProcessChannelBuilder.forName(serverName).directExecutor().build());

        // Create a new XpringClient using the in-process channel;
        return new DefaultXpringClient(channel);
    }


    /**
     * Return a XRPLedgerService implementation which returns the given results for network calls.
     */
    private XRPLedgerAPIServiceGrpc.XRPLedgerAPIServiceImplBase getService(
        GRPCResult<GetAccountInfoResponse> getAccountInfoResult
    ) {
        return mock(XRPLedgerAPIServiceGrpc.XRPLedgerAPIServiceImplBase.class, delegatesTo(
                new XRPLedgerAPIServiceGrpc.XRPLedgerAPIServiceImplBase() {
                    @Override
                    public void getAccountInfo(AccountInfo.GetAccountInfoRequest request, StreamObserver<GetAccountInfoResponse> responseObserver) {
                        if (getAccountInfoResult.isError()) {
                            responseObserver.onError(new Throwable(getAccountInfoResult.getError()));
                        } else {
                            responseObserver.onNext(getAccountInfoResult.getValue());
                            responseObserver.onCompleted();
                        }
                    }
                }
                )
        );
    }

    /**
     * Make an GetAccountInfoResponse protocol buffer with the given balance.
     */
    private GetAccountInfoResponse makeGetAccountInfoResponse(long balance) {
        XRPDropsAmount accountBalance = XRPDropsAmount.newBuilder().setDrops(balance).build();
        AccountRoot accountData = AccountRoot.newBuilder().setBalance(accountBalance).build();
        return GetAccountInfoResponse.newBuilder().setAccountData(accountData).build();
    }
}
