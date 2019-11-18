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
 * Unit tests for {@link XpringClient}.
 */
public class XpringClientTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    /** The XpringClient under test. */
    private XpringClient client;

    /** An address on the XRP Ledger. */
    private static final String XRPL_ADDRESS = "XVwDxLQ4SN9pEBQagTNHwqpFkPgGppXqrMoTmUcSKdCtcK5";

    /** Mocked values in responses from the gRPC server. */
    private static final String DROPS_OF_XRP_IN_ACCOUNT = "10";
    private static final String DROPS_OF_XRP_FOR_FEE = "20";
    private static final String TRANSACTION_BLOB = "DEADBEEF";
    private static final String GENERIC_ERROR = "Mocked network error";

    /** The seed for a wallet with funds on the XRP Ledger test net. */
    private static final String WALLET_SEED = "snYP7oArxKepd3GPDcrjMsJYiJeJB";

    /** Drops of XRP to send. */
    private static final BigInteger AMOUNT = new BigInteger("1");

    @Test
    public void getBalanceTest() throws IOException, XpringKitException {
        // GIVEN a XpringClient with mocked networking which will succeed.
        XpringClient client = getClient();

        // WHEN the balance is retrieved.
        BigInteger balance = client.getBalance(XRPL_ADDRESS);

        // THEN the balance returned is the the same as the mocked response.
        assertThat(balance.toString()).isEqualTo(DROPS_OF_XRP_IN_ACCOUNT);
    }

    @Test
    public void getBalanceWithClassicAddressTest() throws IOException, XpringKitException {
        // GIVEN a classic address.
        ClassicAddress classicAddress = Utils.decodeXAddress(XRPL_ADDRESS);
        XpringClient client = getClient();

        // WHEN the balance for the classic address is retrieved THEN an error is thrown.
        expectedException.expect(XpringKitException.class);
        client.getBalance(classicAddress.address());
    }

    @Test
    public void getBalanceTestWithFailedAccountInfo() throws IOException, XpringKitException {
        // GIVEN a XpringClient with mocked networking which will fail to retrieve account info.
        GRPCResult<AccountInfo> accountInfoResult = GRPCResult.error(GENERIC_ERROR);
        XpringClient client = getClient(
                accountInfoResult,
                GRPCResult.ok(makeFee(DROPS_OF_XRP_FOR_FEE)),
                GRPCResult.ok(makeSubmitSignedTransactionResponse(TRANSACTION_BLOB)),
                GRPCResult.ok(makeLedgerSequence())
        );

        // WHEN the balance is retrieved THEN an error is thrown.
        expectedException.expect(Exception.class);
        client.getBalance(XRPL_ADDRESS);
    }

    @Test
    public void submitTransactionTest() throws IOException, XpringKitException {
        // GIVEN a XpringClient with mocked networking which will succeed.
        XpringClient client = getClient();
        Wallet wallet = new Wallet(WALLET_SEED);

        // WHEN a transaction is sent.
        String transactionHash = client.send(AMOUNT, XRPL_ADDRESS, wallet);

        // THEN the transaction hash is the same as the hash of the mocked transaction blob in the response.
        String expectedTransactionHash = Utils.toTransactionHash(TRANSACTION_BLOB);
        assertThat(transactionHash).isEqualTo(expectedTransactionHash);
    }

    @Test
    public void submitTransactionWithClassicAddress() throws IOException, XpringKitException {
        // GIVEN a classic address.
        XpringClient client = getClient();
        ClassicAddress classicAddress = Utils.decodeXAddress(XRPL_ADDRESS);
        Wallet wallet = new Wallet(WALLET_SEED);

        // WHEN XRP is sent to the classic address THEN an error is thrown.
        expectedException.expect(XpringKitException.class);
        client.send(AMOUNT, classicAddress.address(), wallet);
    }

    @Test
    public void submitTransactionWithFailedAccountInfo() throws IOException, XpringKitException {
        // GIVEN a XpringClient which will fail to return account info.
        GRPCResult<AccountInfo> accountInfoResult = GRPCResult.error(GENERIC_ERROR);
        XpringClient client = getClient(
                accountInfoResult,
                GRPCResult.ok(makeFee(DROPS_OF_XRP_FOR_FEE)),
                GRPCResult.ok(makeSubmitSignedTransactionResponse(TRANSACTION_BLOB)),
                GRPCResult.ok(makeLedgerSequence())
        );
        Wallet wallet = new Wallet(WALLET_SEED);

        // WHEN XRP is sent then THEN an error is thrown.
        expectedException.expect(Exception.class);
        client.send(AMOUNT, XRPL_ADDRESS, wallet);
    }

    @Test
    public void submitTransactionWithFailedFee() throws IOException, XpringKitException {
        // GIVEN a XpringClient which will fail to retrieve a fee.
        GRPCResult<Fee> feeResult = GRPCResult.error(GENERIC_ERROR);
        XpringClient client = getClient(
                GRPCResult.ok(makeAccountInfo(DROPS_OF_XRP_IN_ACCOUNT)),
                feeResult,
                GRPCResult.ok(makeSubmitSignedTransactionResponse(TRANSACTION_BLOB)),
                GRPCResult.ok(makeLedgerSequence())
        );
        Wallet wallet = new Wallet(WALLET_SEED);

        // WHEN XRP is sent then THEN an error is thrown.
        expectedException.expect(Exception.class);
        client.send(AMOUNT, XRPL_ADDRESS, wallet);
    }

    @Test
    public void submitTransactionWithFailedLatestValidatedLedgerSequence() throws IOException, XpringKitException {
        // GIVEN a XpringClient which will fail to retrieve a fee.
        GRPCResult<LedgerSequence> ledgerSequence = GRPCResult.error(GENERIC_ERROR);
        XpringClient client = getClient(
                GRPCResult.ok(makeAccountInfo(DROPS_OF_XRP_IN_ACCOUNT)),
                GRPCResult.ok(makeFee(DROPS_OF_XRP_FOR_FEE)),
                GRPCResult.ok(makeSubmitSignedTransactionResponse(TRANSACTION_BLOB)),
                ledgerSequence
        );
        Wallet wallet = new Wallet(WALLET_SEED);

        // WHEN XRP is sent then THEN an error is thrown.
        expectedException.expect(Exception.class);
        client.send(AMOUNT, XRPL_ADDRESS, wallet);
    }

    @Test
    public void submitTransactionWithFailedSubmit() throws IOException, XpringKitException {
        // GIVEN a XpringClient which will fail to submit a transaction.
        GRPCResult<SubmitSignedTransactionResponse> submitResult = GRPCResult.error(GENERIC_ERROR);
        XpringClient client = getClient(
                GRPCResult.ok(makeAccountInfo(DROPS_OF_XRP_IN_ACCOUNT)),
                GRPCResult.ok(makeFee(DROPS_OF_XRP_FOR_FEE)),
                submitResult,
                GRPCResult.ok(makeLedgerSequence())
        );
        Wallet wallet = new Wallet(WALLET_SEED);

        // WHEN XRP is sent then THEN an error is thrown.
        expectedException.expect(Exception.class);
        client.send(AMOUNT, XRPL_ADDRESS, wallet);
    }

    /**
     * Convenience method to get a XpringClient which has successful network calls.
     */
    private XpringClient getClient() throws IOException {
        return getClient(
                GRPCResult.ok(makeAccountInfo(DROPS_OF_XRP_IN_ACCOUNT)),
                GRPCResult.ok(makeFee(DROPS_OF_XRP_FOR_FEE)),
                GRPCResult.ok(makeSubmitSignedTransactionResponse(TRANSACTION_BLOB)),
                GRPCResult.ok(makeLedgerSequence())
        );
    }

    /**
     * Return a XpringClient which returns the given results for network calls.
     */
    private XpringClient getClient(GRPCResult<AccountInfo> accountInfoResult, GRPCResult<Fee> feeResult, GRPCResult<SubmitSignedTransactionResponse> submitResult, GRPCResult<LedgerSequence> latestValidatedLedgerSequenceResult) throws IOException {
        XRPLedgerAPIGrpc.XRPLedgerAPIImplBase serviceImpl = getService(accountInfoResult, feeResult, submitResult, latestValidatedLedgerSequenceResult);

        // Generate a unique in-process server name.
        String serverName = InProcessServerBuilder.generateName();

        // Create a server, add service, start, and register for automatic graceful shutdown.
        grpcCleanup.register(InProcessServerBuilder
                .forName(serverName).directExecutor().addService(serviceImpl).build().start());

        // Create a client channel and register for automatic graceful shutdown.
        ManagedChannel channel = grpcCleanup.register(
                InProcessChannelBuilder.forName(serverName).directExecutor().build());

        // Create a new XpringClient using the in-process channel;
        return new XpringClient(channel);
    }

    /**
     * Return a XRPLedgerService implementation which returns the given results for network calls.
     */
    private XRPLedgerAPIGrpc.XRPLedgerAPIImplBase getService(GRPCResult<AccountInfo> accountInfoResult, GRPCResult<Fee> feeResult, GRPCResult<SubmitSignedTransactionResponse> submitResult, GRPCResult<LedgerSequence> latestValidatedLedgerSequenceResult) {
        return mock(XRPLedgerAPIGrpc.XRPLedgerAPIImplBase.class, delegatesTo(
                new XRPLedgerAPIGrpc.XRPLedgerAPIImplBase() {
                    @Override
                    public void getAccountInfo(io.xpring.proto.GetAccountInfoRequest request,
                                               io.grpc.stub.StreamObserver<io.xpring.proto.AccountInfo> responseObserver) {
                        if (accountInfoResult.isError()) {
                            responseObserver.onError(new Throwable(accountInfoResult.getError()));
                        } else {
                            responseObserver.onNext(accountInfoResult.getValue());
                            responseObserver.onCompleted();
                        }
                    }

                    @Override
                    public void getFee(io.xpring.proto.GetFeeRequest request,
                                       io.grpc.stub.StreamObserver<io.xpring.proto.Fee> responseObserver) {
                        if (feeResult.isError()) {
                            responseObserver.onError(new Throwable(feeResult.getError()));
                        } else {
                            responseObserver.onNext(feeResult.getValue());
                            responseObserver.onCompleted();
                        }
                    }

                    @Override
                    public void submitSignedTransaction(io.xpring.proto.SubmitSignedTransactionRequest request,
                                                        io.grpc.stub.StreamObserver<io.xpring.proto.SubmitSignedTransactionResponse> responseObserver) {
                        if (submitResult.isError()) {
                            responseObserver.onError(new Throwable(submitResult.getError()));
                        } else {
                            responseObserver.onNext(submitResult.getValue());
                            responseObserver.onCompleted();
                        }
                    }

                    @Override
                    public void getLatestValidatedLedgerSequence(io.xpring.proto.GetLatestValidatedLedgerSequenceRequest request,
                                                                 io.grpc.stub.StreamObserver<io.xpring.proto.LedgerSequence> responseObserver) {
                        if (latestValidatedLedgerSequenceResult.isError()) {
                            responseObserver.onError(new Throwable(latestValidatedLedgerSequenceResult.getError()));
                        } else {
                            responseObserver.onNext(latestValidatedLedgerSequenceResult.getValue());
                            responseObserver.onCompleted();
                        }
                    }
                }
                )
        );
    }

    /**
     * Make an AccountInfo protocol buffer with the given balance.
     */
    private AccountInfo makeAccountInfo(String balance) {
        XRPAmount balanceAmount = XRPAmount.newBuilder().setDrops(balance).build();
        return AccountInfo.newBuilder().setBalance(balanceAmount).build();
    }

    /**
     * Make an Fee protocol buffer with the given fee.
     */
    private Fee makeFee(String fee) {
        XRPAmount feeAmount = XRPAmount.newBuilder().setDrops(fee).build();
        return Fee.newBuilder().setAmount(feeAmount).build();

    }

    /**
     * Make a SubmitSignedTransactionResponse with the given transaction blob.
     */
    private SubmitSignedTransactionResponse makeSubmitSignedTransactionResponse(String transactionBlob) {
        return SubmitSignedTransactionResponse.newBuilder().setTransactionBlob(TRANSACTION_BLOB).build();
    }

    /**
     * Make a Ledger Sequence.
     */
    private LedgerSequence makeLedgerSequence() {
        return LedgerSequence.newBuilder().setIndex(12).build();
    }
}
