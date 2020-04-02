package io.xpring.xrpl.legacy;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import io.xpring.GRPCResult;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.xpring.proto.*;
import io.xpring.xrpl.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import io.grpc.testing.GrpcCleanupRule;
import io.grpc.inprocess.InProcessServerBuilder;
import static org.mockito.Mockito.mock;
import static org.mockito.AdditionalAnswers.delegatesTo;
import io.grpc.ManagedChannel;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.xpring.xrpl.Utils;
import io.xpring.xrpl.ClassicAddress;
import io.xpring.xrpl.Wallet;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Optional;

/**
 * Unit tests for {@link io.xpring.xrpl.DefaultXRPClient}.
 */
public class LegacyDefaultXRPClientTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    /** The DefaultXRPClient under test. */
    private LegacyDefaultXRPClient client;

    /** An address on the XRP Ledger. */
    private static final String XRPL_ADDRESS = "XVwDxLQ4SN9pEBQagTNHwqpFkPgGppXqrMoTmUcSKdCtcK5";

    /** Mocked values in responses from the gRPC server. */
    private static final String DROPS_OF_XRP_IN_ACCOUNT = "10";
    private static final String DROPS_OF_XRP_FOR_FEE = "20";
    private static final String TRANSACTION_BLOB = "DEADBEEF";
    private static final Throwable GENERIC_ERROR = new Throwable("Mocked network error");
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
    public void getBalanceTest() throws IOException, XRPException {
        // GIVEN a DefaultXRPClient with mocked networking which will succeed.
        LegacyDefaultXRPClient client = getClient();

        // WHEN the balance is retrieved.
        BigInteger balance = client.getBalance(XRPL_ADDRESS);

        // THEN the balance returned is the the same as the mocked response.
        assertThat(balance.toString()).isEqualTo(DROPS_OF_XRP_IN_ACCOUNT);
    }

    @Test
    public void getBalanceWithClassicAddressTest() throws IOException, XRPException {
        // GIVEN a classic address.
        ClassicAddress classicAddress = Utils.decodeXAddress(XRPL_ADDRESS);
        LegacyDefaultXRPClient client = getClient();

        // WHEN the balance for the classic address is retrieved THEN an error is thrown.
        expectedException.expect(XRPException.class);
        client.getBalance(classicAddress.address());
    }

    @Test
    public void getBalanceTestWithFailedAccountInfo() throws IOException, XRPException {
        // GIVEN a XRPClient with mocked networking which will fail to retrieve account info.
        GRPCResult<AccountInfo> accountInfoResult = GRPCResult.error(GENERIC_ERROR);
        LegacyDefaultXRPClient client = getClient(
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
    public void submitTransactionTest() throws IOException, XRPException {
        // GIVEN a XRPClient with mocked networking which will succeed.
        LegacyDefaultXRPClient client = getClient();
        Wallet wallet = new Wallet(WALLET_SEED);

        // WHEN a transaction is sent.
        String transactionHash = client.send(AMOUNT, XRPL_ADDRESS, wallet);

        // THEN the transaction hash is the same as the hash of the mocked transaction blob in the response.
        String expectedTransactionHash = Utils.toTransactionHash(TRANSACTION_BLOB);
        assertThat(transactionHash).isEqualTo(expectedTransactionHash);
    }

    @Test
    public void submitTransactionWithClassicAddress() throws IOException, XRPException {
        // GIVEN a classic address.
        LegacyDefaultXRPClient client = getClient();
        ClassicAddress classicAddress = Utils.decodeXAddress(XRPL_ADDRESS);
        Wallet wallet = new Wallet(WALLET_SEED);

        // WHEN XRP is sent to the classic address THEN an error is thrown.
        expectedException.expect(XRPException.class);
        client.send(AMOUNT, classicAddress.address(), wallet);
    }

    @Test
    public void submitTransactionWithFailedAccountInfo() throws IOException, XRPException {
        // GIVEN a XRPClient which will fail to return account info.
        GRPCResult<AccountInfo> accountInfoResult = GRPCResult.error(GENERIC_ERROR);
        LegacyDefaultXRPClient client = getClient(
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
    public void submitTransactionWithFailedFee() throws IOException, XRPException {
        // GIVEN a XRPClient which will fail to retrieve a fee.
        GRPCResult<Fee> feeResult = GRPCResult.error(GENERIC_ERROR);
        LegacyDefaultXRPClient client = getClient(
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
    public void submitTransactionWithFailedLatestValidatedLedgerSequence() throws IOException, XRPException {
        // GIVEN a XRPClient which will fail to retrieve a fee.
        GRPCResult<LedgerSequence> ledgerSequence = GRPCResult.error(GENERIC_ERROR);
        LegacyDefaultXRPClient client = getClient(
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
    public void submitTransactionWithFailedSubmit() throws IOException, XRPException {
        // GIVEN a XRPClient which will fail to submit a transaction.
        GRPCResult<SubmitSignedTransactionResponse> submitResult = GRPCResult.error(GENERIC_ERROR);
        LegacyDefaultXRPClient client = getClient(
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
    public void paymentStatusWithUnvalidatedTransactionAndFailureCode() throws IOException {
        // Iterate over different types of transaction status codes which represent failures.
        for (String transactionFailureCode : TRANSACTION_FAILURE_STATUS_CODES) {
            // GIVEN an XRPClient which will return an invalidated transaction with a failed code.
            io.xpring.proto.TransactionStatus transactionStatusResponse = io.xpring.proto.TransactionStatus.newBuilder().setValidated(false).setTransactionStatusCode(transactionFailureCode).build();
            LegacyDefaultXRPClient client = getClient(
                    GRPCResult.ok(makeAccountInfo(DROPS_OF_XRP_IN_ACCOUNT)),
                    GRPCResult.ok(makeFee(DROPS_OF_XRP_FOR_FEE)),
                    GRPCResult.ok(makeSubmitSignedTransactionResponse(TRANSACTION_BLOB)),
                    GRPCResult.ok(makeLedgerSequence()),
                    GRPCResult.ok(transactionStatusResponse)
            );

            // WHEN the payment status is retrieved.
            io.xpring.xrpl.TransactionStatus paymentStatus = client.getPaymentStatus(TRANSACTION_HASH);

            // THEN the status is PENDING.
            assertThat(paymentStatus).isEqualTo(io.xpring.xrpl.TransactionStatus.PENDING);
        }
    }

    @Test
    public void paymentStatusWithUnvalidatedTransactionAndSuccessCode() throws IOException {
        // GIVEN an XRPClient which will return an unvalidated transaction with a success code.
        io.xpring.proto.TransactionStatus transactionStatusResponse = io.xpring.proto.TransactionStatus.newBuilder().setValidated(false).setTransactionStatusCode(TRANSACTION_STATUS_SUCCESS).build();
        LegacyDefaultXRPClient client = getClient(
                GRPCResult.ok(makeAccountInfo(DROPS_OF_XRP_IN_ACCOUNT)),
                GRPCResult.ok(makeFee(DROPS_OF_XRP_FOR_FEE)),
                GRPCResult.ok(makeSubmitSignedTransactionResponse(TRANSACTION_BLOB)),
                GRPCResult.ok(makeLedgerSequence()),
                GRPCResult.ok(transactionStatusResponse)
        );

        // WHEN the payment status is retrieved.
        io.xpring.xrpl.TransactionStatus paymentStatus = client.getPaymentStatus(TRANSACTION_HASH);

        // THEN the status is PENDING.
        assertThat(paymentStatus).isEqualTo(io.xpring.xrpl.TransactionStatus.PENDING);
    }

    @Test
    public void paymentStatusWithValidatedTransactionAndFailureCode() throws IOException {
        // Iterate over different types of transaction status codes which represent failures.
        for (String transactionFailureCode : TRANSACTION_FAILURE_STATUS_CODES) {
            // GIVEN an XRPClient which will return an validated transaction with a failed code.
            io.xpring.proto.TransactionStatus transactionStatusResponse = io.xpring.proto.TransactionStatus.newBuilder().setValidated(true).setTransactionStatusCode(transactionFailureCode).build();
            LegacyDefaultXRPClient client = getClient(
                    GRPCResult.ok(makeAccountInfo(DROPS_OF_XRP_IN_ACCOUNT)),
                    GRPCResult.ok(makeFee(DROPS_OF_XRP_FOR_FEE)),
                    GRPCResult.ok(makeSubmitSignedTransactionResponse(TRANSACTION_BLOB)),
                    GRPCResult.ok(makeLedgerSequence()),
                    GRPCResult.ok(transactionStatusResponse)
            );

            // WHEN the payment status is retrieved.
            io.xpring.xrpl.TransactionStatus paymentStatus = client.getPaymentStatus(TRANSACTION_HASH);

            // THEN the status is FAILED.
            assertThat(paymentStatus).isEqualTo(io.xpring.xrpl.TransactionStatus.FAILED);
        }
    }

    @Test
    public void paymentStatusWithValidatedTransactionAndSuccessCode() throws IOException {
        // GIVEN an XRPClient which will return an validated transaction with a success code.
        io.xpring.proto.TransactionStatus transactionStatusResponse = io.xpring.proto.TransactionStatus.newBuilder().setValidated(true).setTransactionStatusCode(TRANSACTION_STATUS_SUCCESS).build();
        LegacyDefaultXRPClient client = getClient(
                GRPCResult.ok(makeAccountInfo(DROPS_OF_XRP_IN_ACCOUNT)),
                GRPCResult.ok(makeFee(DROPS_OF_XRP_FOR_FEE)),
                GRPCResult.ok(makeSubmitSignedTransactionResponse(TRANSACTION_BLOB)),
                GRPCResult.ok(makeLedgerSequence()),
                GRPCResult.ok(transactionStatusResponse)
        );

        // WHEN the payment status is retrieved.
        io.xpring.xrpl.TransactionStatus paymentStatus = client.getPaymentStatus(TRANSACTION_HASH);

        // THEN the status is SUCCEEDED.
        assertThat(paymentStatus).isEqualTo(io.xpring.xrpl.TransactionStatus.SUCCEEDED);
    }

    @Test
    public void paymentStatusWithNodeError() throws IOException {
        // GIVEN an XRPClient which will error when a transaction status is requested..
        io.xpring.proto.TransactionStatus transactionStatusResponse = io.xpring.proto.TransactionStatus.newBuilder().setValidated(true).setTransactionStatusCode(TRANSACTION_STATUS_SUCCESS).build();
        LegacyDefaultXRPClient client = getClient(
                GRPCResult.ok(makeAccountInfo(DROPS_OF_XRP_IN_ACCOUNT)),
                GRPCResult.ok(makeFee(DROPS_OF_XRP_FOR_FEE)),
                GRPCResult.ok(makeSubmitSignedTransactionResponse(TRANSACTION_BLOB)),
                GRPCResult.ok(makeLedgerSequence()),
                GRPCResult.error(GENERIC_ERROR)
        );

        // WHEN the payment status is retrieved THEN an error is thrown..
        expectedException.expect(Exception.class);
        client.getPaymentStatus(TRANSACTION_HASH);
    }

    @Test
    public void accountExistsTest() throws IOException, XpringException {
        // GIVEN a DefaultXRPClient with mocked networking which will succeed.
        LegacyDefaultXRPClient client = getClient();

        // WHEN the account is checked
        boolean exists = client.accountExists(XRPL_ADDRESS);

        // THEN the existence of the account is the the same as the mocked response.
        assertThat(exists).isEqualTo(true);
    }

    @Test
    public void accountExistsWithClassicAddressTest() throws IOException, XpringException {
        // GIVEN a classic address.
        ClassicAddress classicAddress = Utils.decodeXAddress(XRPL_ADDRESS);
        LegacyDefaultXRPClient client = getClient();

        // WHEN the existence of the account is checked for the classic address THEN an error is thrown.
        expectedException.expect(XpringException.class);
        client.accountExists(classicAddress.address());
    }

    @Test
    public void accountExistsTestWithNotFoundError() throws IOException, XpringException {
        // GIVEN an XRPClient with mocked networking which will fail to retrieve account info w/ NOT_FOUND error code.
        StatusRuntimeException notFoundError = new StatusRuntimeException(Status.NOT_FOUND);
        GRPCResult<AccountInfo> accountInfoResult = GRPCResult.error(notFoundError);
        LegacyDefaultXRPClient client = getClient(
                accountInfoResult,
                GRPCResult.ok(makeFee(DROPS_OF_XRP_FOR_FEE)),
                GRPCResult.ok(makeSubmitSignedTransactionResponse(TRANSACTION_BLOB)),
                GRPCResult.ok(makeLedgerSequence()),
                GRPCResult.ok(makeTransactionStatus(true, TRANSACTION_STATUS_SUCCESS))
        );

        // WHEN the existence of the account is checked
        boolean exists = client.accountExists(XRPL_ADDRESS);

        // THEN false is returned.
        assertThat(exists).isEqualTo(false);
    }

    @Test
    public void accountExistsTestWithUnknownError() throws IOException, XpringException {
        // GIVEN an XRPClient with mocked networking which will fail to retrieve account info w/ UNKNOWN error code.
        StatusRuntimeException unknownError = new StatusRuntimeException(Status.UNKNOWN);
        GRPCResult<AccountInfo> accountInfoResult = GRPCResult.error(unknownError);
        LegacyDefaultXRPClient client = getClient(
                accountInfoResult,
                GRPCResult.ok(makeFee(DROPS_OF_XRP_FOR_FEE)),
                GRPCResult.ok(makeSubmitSignedTransactionResponse(TRANSACTION_BLOB)),
                GRPCResult.ok(makeLedgerSequence()),
                GRPCResult.ok(makeTransactionStatus(true, TRANSACTION_STATUS_SUCCESS))
        );

        // WHEN the existence of the account is checked
        boolean exists = client.accountExists(XRPL_ADDRESS);

        // THEN false is returned. (Legacy protobufs return UNKNOWN error code in case of now found account info)
        assertThat(exists).isEqualTo(false);
    }

    @Test
    public void accountExistsTestWithCancelledError() throws IOException, XpringException {
        // GIVEN an XRPClient with mocked networking which will fail to retrieve account info w/ CANCELLED error code.
        StatusRuntimeException cancelledError = new StatusRuntimeException(Status.CANCELLED);
        GRPCResult<AccountInfo> accountInfoResult = GRPCResult.error(cancelledError);
        LegacyDefaultXRPClient client = getClient(
                accountInfoResult,
                GRPCResult.ok(makeFee(DROPS_OF_XRP_FOR_FEE)),
                GRPCResult.ok(makeSubmitSignedTransactionResponse(TRANSACTION_BLOB)),
                GRPCResult.ok(makeLedgerSequence()),
                GRPCResult.ok(makeTransactionStatus(true, TRANSACTION_STATUS_SUCCESS))
        );

        // WHEN the existence of the account is checked THEN the error is re-thrown
        expectedException.expect(StatusRuntimeException.class);
        client.accountExists(XRPL_ADDRESS);
    }


    /**
     * Convenience method to get an XRPClient which has successful network calls.
     */
    private LegacyDefaultXRPClient getClient() throws IOException {
        return getClient(
                GRPCResult.ok(makeAccountInfo(DROPS_OF_XRP_IN_ACCOUNT)),
                GRPCResult.ok(makeFee(DROPS_OF_XRP_FOR_FEE)),
                GRPCResult.ok(makeSubmitSignedTransactionResponse(TRANSACTION_BLOB)),
                GRPCResult.ok(makeLedgerSequence()),
                GRPCResult.ok(makeTransactionStatus(true, TRANSACTION_STATUS_SUCCESS))
        );
    }

    /**
     * Return an XRPClient which returns the given results for network calls.
     */

    private LegacyDefaultXRPClient getClient(GRPCResult<AccountInfo> accountInfoResult, GRPCResult<Fee> feeResult, GRPCResult<SubmitSignedTransactionResponse> submitResult, GRPCResult<LedgerSequence> latestValidatedLedgerSequenceResult, GRPCResult<io.xpring.proto.TransactionStatus> transactionStatusResult) throws IOException {
        XRPLedgerAPIGrpc.XRPLedgerAPIImplBase serviceImpl = getService(accountInfoResult, feeResult, submitResult, latestValidatedLedgerSequenceResult, transactionStatusResult);

        // Generate a unique in-process server name.
        String serverName = InProcessServerBuilder.generateName();

        // Create a server, add service, start, and register for automatic graceful shutdown.
        grpcCleanup.register(InProcessServerBuilder
                .forName(serverName).directExecutor().addService(serviceImpl).build().start());

        // Create a client channel and register for automatic graceful shutdown.
        ManagedChannel channel = grpcCleanup.register(
                InProcessChannelBuilder.forName(serverName).directExecutor().build());

        // Create a new XRPClient using the in-process channel;
        return new LegacyDefaultXRPClient(channel);
    }

    /**
     * Return an XRPLedgerService implementation which returns the given results for network calls.
     */
    private XRPLedgerAPIGrpc.XRPLedgerAPIImplBase getService(GRPCResult<AccountInfo> accountInfoResult, GRPCResult<Fee> feeResult, GRPCResult<SubmitSignedTransactionResponse> submitResult, GRPCResult<LedgerSequence> latestValidatedLedgerSequenceResult, GRPCResult<io.xpring.proto.TransactionStatus> transactionStatusResult) {
        return mock(XRPLedgerAPIGrpc.XRPLedgerAPIImplBase.class, delegatesTo(
                new XRPLedgerAPIGrpc.XRPLedgerAPIImplBase() {
                    @Override
                    public void getAccountInfo(io.xpring.proto.GetAccountInfoRequest request,
                                               io.grpc.stub.StreamObserver<io.xpring.proto.AccountInfo> responseObserver) {
                        if (accountInfoResult.isError()) {
                            responseObserver.onError(accountInfoResult.getError());
                        } else {
                            responseObserver.onNext(accountInfoResult.getValue());
                            responseObserver.onCompleted();
                        }
                    }

                    @Override
                    public void getFee(io.xpring.proto.GetFeeRequest request,
                                       io.grpc.stub.StreamObserver<io.xpring.proto.Fee> responseObserver) {
                        if (feeResult.isError()) {
                            responseObserver.onError(feeResult.getError());
                        } else {
                            responseObserver.onNext(feeResult.getValue());
                            responseObserver.onCompleted();
                        }
                    }

                    @Override
                    public void submitSignedTransaction(io.xpring.proto.SubmitSignedTransactionRequest request,
                                                        io.grpc.stub.StreamObserver<io.xpring.proto.SubmitSignedTransactionResponse> responseObserver) {
                        if (submitResult.isError()) {
                            responseObserver.onError(submitResult.getError());
                        } else {
                            responseObserver.onNext(submitResult.getValue());
                            responseObserver.onCompleted();
                        }
                    }

                    @Override
                    public void getLatestValidatedLedgerSequence(io.xpring.proto.GetLatestValidatedLedgerSequenceRequest request,
                                                                 io.grpc.stub.StreamObserver<io.xpring.proto.LedgerSequence> responseObserver) {
                        if (latestValidatedLedgerSequenceResult.isError()) {
                            responseObserver.onError(latestValidatedLedgerSequenceResult.getError());
                        } else {
                            responseObserver.onNext(latestValidatedLedgerSequenceResult.getValue());
                            responseObserver.onCompleted();
                        }
                    }

                    @Override
                    public void getTransactionStatus(io.xpring.proto.GetTransactionStatusRequest request,
                                                     io.grpc.stub.StreamObserver<io.xpring.proto.TransactionStatus> responseObserver) {
                        if (transactionStatusResult.isError()) {
                            responseObserver.onError(transactionStatusResult.getError());
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
