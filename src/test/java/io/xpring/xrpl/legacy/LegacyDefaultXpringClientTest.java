package io.xpring.xrpl.legacy;

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
import io.xpring.xrpl.XpringException;
import io.xpring.xrpl.Utils;
import io.xpring.xrpl.ClassicAddress;
import io.xpring.xrpl.Wallet;

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
public class LegacyDefaultXpringClientTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    /** The DefaultXpringClient under test. */
    private LegacyDefaultXpringClient client;

    /** An address on the XRP Ledger. */
    private static final String XRPL_ADDRESS = "XVwDxLQ4SN9pEBQagTNHwqpFkPgGppXqrMoTmUcSKdCtcK5";

    /** Mocked values in responses from the gRPC server. */
    private static final String DROPS_OF_XRP_IN_ACCOUNT = "10";
    private static final String DROPS_OF_XRP_FOR_FEE = "20";
    private static final String TRANSACTION_BLOB = "DEADBEEF";
    private static final String GENERIC_ERROR = "Mocked network error";
    private static final String TRANSACTION_STATUS_SUCCESS = "tesSUCCESS";
    private static final String TRANSACTION_HASH = "DEADBEEF";
    private static final String [] TRANSACTION_FAILURE_STATUS_CODES = {
            "tefFAILURE",
            "tecCLAIM",
            "telBAD_PUBLIC_KEY",
            "temBAD_FEE",
            "terRETRY"
    };

    /** The seed for a wallet with funds on the XRP Ledger test net. */
    private static final String WALLET_SEED = "snYP7oArxKepd3GPDcrjMsJYiJeJB";

    /** Drops of XRP to send. */
    private static final BigInteger AMOUNT = new BigInteger("1");

    @Test
    public void getBalanceTest() throws IOException, XpringException {
        // GIVEN a DefaultXpringClient with mocked networking which will succeed.
        LegacyDefaultXpringClient client = getClient();

        // WHEN the balance is retrieved.
        BigInteger balance = client.getBalance(XRPL_ADDRESS);

        // THEN the balance returned is the the same as the mocked response.
        assertThat(balance.toString()).isEqualTo(DROPS_OF_XRP_IN_ACCOUNT);
    }

    @Test
    public void getBalanceWithClassicAddressTest() throws IOException, XpringException {
        // GIVEN a classic address.
        ClassicAddress classicAddress = Utils.decodeXAddress(XRPL_ADDRESS);
        LegacyDefaultXpringClient client = getClient();

        // WHEN the balance for the classic address is retrieved THEN an error is thrown.
        expectedException.expect(XpringException.class);
        client.getBalance(classicAddress.address());
    }

    @Test
    public void getBalanceTestWithFailedAccountInfo() throws IOException, XpringException {
        // GIVEN a XpringClient with mocked networking which will fail to retrieve account info.
        GRPCResult<AccountInfo> accountInfoResult = GRPCResult.error(GENERIC_ERROR);
        LegacyDefaultXpringClient client = getClient(
                accountInfoResult,
                GRPCResult.ok(makeFee(DROPS_OF_XRP_FOR_FEE)),
                GRPCResult.ok(makeSubmitSignedTransactionResponse(TRANSACTION_BLOB)),
                GRPCResult.ok(makeLedgerSequence()),
                GRPCResult.ok(makeTransactionStatus(true, TRANSACTION_STATUS_SUCCESS))
        );

        // WHEN the balance is retrieved THEN an error is thrown.
        expectedException.expect(Exception.class);
        client.getBalance(XRPL_ADDRESS);
    }

    @Test
    public void submitTransactionTest() throws IOException, XpringException {
        // GIVEN a XpringClient with mocked networking which will succeed.
        LegacyDefaultXpringClient client = getClient();
        Wallet wallet = new Wallet(WALLET_SEED);

        // WHEN a transaction is sent.
        String transactionHash = client.send(AMOUNT, XRPL_ADDRESS, wallet);

        // THEN the transaction hash is the same as the hash of the mocked transaction blob in the response.
        String expectedTransactionHash = Utils.toTransactionHash(TRANSACTION_BLOB);
        assertThat(transactionHash).isEqualTo(expectedTransactionHash);
    }

    @Test
    public void submitTransactionWithClassicAddress() throws IOException, XpringException {
        // GIVEN a classic address.
        LegacyDefaultXpringClient client = getClient();
        ClassicAddress classicAddress = Utils.decodeXAddress(XRPL_ADDRESS);
        Wallet wallet = new Wallet(WALLET_SEED);

        // WHEN XRP is sent to the classic address THEN an error is thrown.
        expectedException.expect(XpringException.class);
        client.send(AMOUNT, classicAddress.address(), wallet);
    }

    @Test
    public void submitTransactionWithFailedAccountInfo() throws IOException, XpringException {
        // GIVEN a XpringClient which will fail to return account info.
        GRPCResult<AccountInfo> accountInfoResult = GRPCResult.error(GENERIC_ERROR);
        LegacyDefaultXpringClient client = getClient(
                accountInfoResult,
                GRPCResult.ok(makeFee(DROPS_OF_XRP_FOR_FEE)),
                GRPCResult.ok(makeSubmitSignedTransactionResponse(TRANSACTION_BLOB)),
                GRPCResult.ok(makeLedgerSequence()),
                GRPCResult.ok(makeTransactionStatus(true, TRANSACTION_STATUS_SUCCESS))
        );
        Wallet wallet = new Wallet(WALLET_SEED);

        // WHEN XRP is sent then THEN an error is thrown.
        expectedException.expect(Exception.class);
        client.send(AMOUNT, XRPL_ADDRESS, wallet);
    }

    @Test
    public void submitTransactionWithFailedFee() throws IOException, XpringException {
        // GIVEN a XpringClient which will fail to retrieve a fee.
        GRPCResult<Fee> feeResult = GRPCResult.error(GENERIC_ERROR);
        LegacyDefaultXpringClient client = getClient(
                GRPCResult.ok(makeAccountInfo(DROPS_OF_XRP_IN_ACCOUNT)),
                feeResult,
                GRPCResult.ok(makeSubmitSignedTransactionResponse(TRANSACTION_BLOB)),
                GRPCResult.ok(makeLedgerSequence()),
                GRPCResult.ok(makeTransactionStatus(true, TRANSACTION_STATUS_SUCCESS))
        );
        Wallet wallet = new Wallet(WALLET_SEED);

        // WHEN XRP is sent then THEN an error is thrown.
        expectedException.expect(Exception.class);
        client.send(AMOUNT, XRPL_ADDRESS, wallet);
    }

    @Test
    public void submitTransactionWithFailedLatestValidatedLedgerSequence() throws IOException, XpringException {
        // GIVEN a XpringClient which will fail to retrieve a fee.
        GRPCResult<LedgerSequence> ledgerSequence = GRPCResult.error(GENERIC_ERROR);
        LegacyDefaultXpringClient client = getClient(
                GRPCResult.ok(makeAccountInfo(DROPS_OF_XRP_IN_ACCOUNT)),
                GRPCResult.ok(makeFee(DROPS_OF_XRP_FOR_FEE)),
                GRPCResult.ok(makeSubmitSignedTransactionResponse(TRANSACTION_BLOB)),
                ledgerSequence,
                GRPCResult.ok(makeTransactionStatus(true, TRANSACTION_STATUS_SUCCESS))
        );
        Wallet wallet = new Wallet(WALLET_SEED);

        // WHEN XRP is sent then THEN an error is thrown.
        expectedException.expect(Exception.class);
        client.send(AMOUNT, XRPL_ADDRESS, wallet);
    }

    @Test
    public void submitTransactionWithFailedSubmit() throws IOException, XpringException {
        // GIVEN a XpringClient which will fail to submit a transaction.
        GRPCResult<SubmitSignedTransactionResponse> submitResult = GRPCResult.error(GENERIC_ERROR);
        LegacyDefaultXpringClient client = getClient(
                GRPCResult.ok(makeAccountInfo(DROPS_OF_XRP_IN_ACCOUNT)),
                GRPCResult.ok(makeFee(DROPS_OF_XRP_FOR_FEE)),
                submitResult,
                GRPCResult.ok(makeLedgerSequence()),
                GRPCResult.ok(makeTransactionStatus(true, TRANSACTION_STATUS_SUCCESS))
        );
        Wallet wallet = new Wallet(WALLET_SEED);

        // WHEN XRP is sent then THEN an error is thrown.
        expectedException.expect(Exception.class);
        client.send(AMOUNT, XRPL_ADDRESS, wallet);
    }

    @Test
    public void transactionStatusWithUnvalidatedTransactionAndFailureCode() throws IOException {
        // Iterate over different types of transaction status codes which represent failures.
        for (String transactionFailureCode : TRANSACTION_FAILURE_STATUS_CODES) {
            // GIVEN a XpringClient which will return an invalidated transaction with a failed code.
            io.xpring.proto.TransactionStatus transactionStatusResponse = io.xpring.proto.TransactionStatus.newBuilder().setValidated(false).setTransactionStatusCode(transactionFailureCode).build();
            LegacyDefaultXpringClient client = getClient(
                    GRPCResult.ok(makeAccountInfo(DROPS_OF_XRP_IN_ACCOUNT)),
                    GRPCResult.ok(makeFee(DROPS_OF_XRP_FOR_FEE)),
                    GRPCResult.ok(makeSubmitSignedTransactionResponse(TRANSACTION_BLOB)),
                    GRPCResult.ok(makeLedgerSequence()),
                    GRPCResult.ok(transactionStatusResponse)
            );

            // WHEN the transaction status is retrieved.
            io.xpring.xrpl.TransactionStatus transactionStatus = client.getTransactionStatus(TRANSACTION_HASH);

            // THEN the status is PENDING.
            assertThat(transactionStatus).isEqualTo(io.xpring.xrpl.TransactionStatus.PENDING);
        }
    }

    @Test
    public void transactionStatusWithUnvalidatedTransactionAndSuccessCode() throws IOException {
        // GIVEN a XpringClient which will return an unvalidated transaction with a success code.
        io.xpring.proto.TransactionStatus transactionStatusResponse = io.xpring.proto.TransactionStatus.newBuilder().setValidated(false).setTransactionStatusCode(TRANSACTION_STATUS_SUCCESS).build();
        LegacyDefaultXpringClient client = getClient(
                GRPCResult.ok(makeAccountInfo(DROPS_OF_XRP_IN_ACCOUNT)),
                GRPCResult.ok(makeFee(DROPS_OF_XRP_FOR_FEE)),
                GRPCResult.ok(makeSubmitSignedTransactionResponse(TRANSACTION_BLOB)),
                GRPCResult.ok(makeLedgerSequence()),
                GRPCResult.ok(transactionStatusResponse)
        );

        // WHEN the transaction status is retrieved.
        io.xpring.xrpl.TransactionStatus transactionStatus = client.getTransactionStatus(TRANSACTION_HASH);

        // THEN the status is PENDING.
        assertThat(transactionStatus).isEqualTo(io.xpring.xrpl.TransactionStatus.PENDING);
    }

    @Test
    public void transactionStatusWithValidatedTransactionAndFailureCode() throws IOException {
        // Iterate over different types of transaction status codes which represent failures.
        for (String transactionFailureCode : TRANSACTION_FAILURE_STATUS_CODES) {
            // GIVEN a XpringClient which will return an validated transaction with a failed code.
            io.xpring.proto.TransactionStatus transactionStatusResponse = io.xpring.proto.TransactionStatus.newBuilder().setValidated(true).setTransactionStatusCode(transactionFailureCode).build();
            LegacyDefaultXpringClient client = getClient(
                    GRPCResult.ok(makeAccountInfo(DROPS_OF_XRP_IN_ACCOUNT)),
                    GRPCResult.ok(makeFee(DROPS_OF_XRP_FOR_FEE)),
                    GRPCResult.ok(makeSubmitSignedTransactionResponse(TRANSACTION_BLOB)),
                    GRPCResult.ok(makeLedgerSequence()),
                    GRPCResult.ok(transactionStatusResponse)
            );

            // WHEN the transaction status is retrieved.
            io.xpring.xrpl.TransactionStatus transactionStatus = client.getTransactionStatus(TRANSACTION_HASH);

            // THEN the status is FAILED.
            assertThat(transactionStatus).isEqualTo(io.xpring.xrpl.TransactionStatus.FAILED);
        }
    }

    @Test
    public void transactionStatusWithValidatedTransactionAndSuccessCode() throws IOException {
        // GIVEN a XpringClient which will return an validated transaction with a success code.
        io.xpring.proto.TransactionStatus transactionStatusResponse = io.xpring.proto.TransactionStatus.newBuilder().setValidated(true).setTransactionStatusCode(TRANSACTION_STATUS_SUCCESS).build();
        LegacyDefaultXpringClient client = getClient(
                GRPCResult.ok(makeAccountInfo(DROPS_OF_XRP_IN_ACCOUNT)),
                GRPCResult.ok(makeFee(DROPS_OF_XRP_FOR_FEE)),
                GRPCResult.ok(makeSubmitSignedTransactionResponse(TRANSACTION_BLOB)),
                GRPCResult.ok(makeLedgerSequence()),
                GRPCResult.ok(transactionStatusResponse)
        );

        // WHEN the transaction status is retrieved.
        io.xpring.xrpl.TransactionStatus transactionStatus = client.getTransactionStatus(TRANSACTION_HASH);

        // THEN the status is SUCCEEDED.
        assertThat(transactionStatus).isEqualTo(io.xpring.xrpl.TransactionStatus.SUCCEEDED);
    }

    @Test
    public void transactionStatusWithNodeError() throws IOException {
        // GIVEN a XpringClient which will error when a transaction status is requested..
        io.xpring.proto.TransactionStatus transactionStatusResponse = io.xpring.proto.TransactionStatus.newBuilder().setValidated(true).setTransactionStatusCode(TRANSACTION_STATUS_SUCCESS).build();
        LegacyDefaultXpringClient client = getClient(
                GRPCResult.ok(makeAccountInfo(DROPS_OF_XRP_IN_ACCOUNT)),
                GRPCResult.ok(makeFee(DROPS_OF_XRP_FOR_FEE)),
                GRPCResult.ok(makeSubmitSignedTransactionResponse(TRANSACTION_BLOB)),
                GRPCResult.ok(makeLedgerSequence()),
                GRPCResult.error(GENERIC_ERROR)
        );

        // WHEN the transaction status is retrieved THEN an error is thrown..
        expectedException.expect(Exception.class);
        client.getTransactionStatus(TRANSACTION_HASH);
    }

    /**
     * Convenience method to get a XpringClient which has successful network calls.
     */
    private LegacyDefaultXpringClient getClient() throws IOException {
        return getClient(
                GRPCResult.ok(makeAccountInfo(DROPS_OF_XRP_IN_ACCOUNT)),
                GRPCResult.ok(makeFee(DROPS_OF_XRP_FOR_FEE)),
                GRPCResult.ok(makeSubmitSignedTransactionResponse(TRANSACTION_BLOB)),
                GRPCResult.ok(makeLedgerSequence()),
                GRPCResult.ok(makeTransactionStatus(true, TRANSACTION_STATUS_SUCCESS))
        );
    }

    /**
     * Return a XpringClient which returns the given results for network calls.
     */

    private LegacyDefaultXpringClient getClient(GRPCResult<AccountInfo> accountInfoResult, GRPCResult<Fee> feeResult, GRPCResult<SubmitSignedTransactionResponse> submitResult, GRPCResult<LedgerSequence> latestValidatedLedgerSequenceResult, GRPCResult<io.xpring.proto.TransactionStatus> transactionStatusResult) throws IOException {
        XRPLedgerAPIGrpc.XRPLedgerAPIImplBase serviceImpl = getService(accountInfoResult, feeResult, submitResult, latestValidatedLedgerSequenceResult, transactionStatusResult);

        // Generate a unique in-process server name.
        String serverName = InProcessServerBuilder.generateName();

        // Create a server, add service, start, and register for automatic graceful shutdown.
        grpcCleanup.register(InProcessServerBuilder
                .forName(serverName).directExecutor().addService(serviceImpl).build().start());

        // Create a client channel and register for automatic graceful shutdown.
        ManagedChannel channel = grpcCleanup.register(
                InProcessChannelBuilder.forName(serverName).directExecutor().build());

        // Create a new XpringClient using the in-process channel;
        return new LegacyDefaultXpringClient(channel);
    }

    /**
     * Return a XRPLedgerService implementation which returns the given results for network calls.
     */
    private XRPLedgerAPIGrpc.XRPLedgerAPIImplBase getService(GRPCResult<AccountInfo> accountInfoResult, GRPCResult<Fee> feeResult, GRPCResult<SubmitSignedTransactionResponse> submitResult, GRPCResult<LedgerSequence> latestValidatedLedgerSequenceResult, GRPCResult<io.xpring.proto.TransactionStatus> transactionStatusResult) {
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

                    @Override
                    public void getTransactionStatus(io.xpring.proto.GetTransactionStatusRequest request,
                                                     io.grpc.stub.StreamObserver<io.xpring.proto.TransactionStatus> responseObserver) {
                        if (transactionStatusResult.isError()) {
                            responseObserver.onError(new Throwable(transactionStatusResult.getError()));
                        } else {
                            responseObserver.onNext(transactionStatusResult.getValue());
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

    /**
     * Make a Transaction Status.
     */
    private io.xpring.proto.TransactionStatus makeTransactionStatus(Boolean validated, String transactionStatusCode) {
        return io.xpring.proto.TransactionStatus.newBuilder().setValidated(validated).setTransactionStatusCode(transactionStatusCode).build();
    }
}
