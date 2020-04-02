package io.xpring.xrpl;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import io.xpring.GRPCResult;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import io.grpc.testing.GrpcCleanupRule;
import io.grpc.inprocess.InProcessServerBuilder;
import static org.mockito.Mockito.mock;
import static org.mockito.AdditionalAnswers.delegatesTo;
import io.grpc.ManagedChannel;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.Status;
import org.xrpl.rpc.v1.*;
import org.xrpl.rpc.v1.Common.*;
import java.io.IOException;
import java.math.BigInteger;

/**
 * Unit tests for {@link DefaultXRPClient}.
 */
public class DefaultXRPClientTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    /** The DefaultXRPClient under test. */
    private DefaultXRPClient client;

    /** An address on the XRP Ledger. */
    private static final String XRPL_ADDRESS = "XVwDxLQ4SN9pEBQagTNHwqpFkPgGppXqrMoTmUcSKdCtcK5";

    /** Mocked values in responses from the gRPC server. */
    private static final long DROPS_OF_XRP_IN_ACCOUNT = 10;
    private static final String TRANSACTION_BLOB = "DEADBEEF";
    private static final Throwable GENERIC_ERROR = new Throwable("Mocked network error");
    private static final String TRANSACTION_STATUS_SUCCESS = "tesSUCCESS";
    private static final String [] TRANSACTION_FAILURE_STATUS_CODES = {
            "tefFAILURE",
            "tecCLAIM",
            "telBAD_PUBLIC_KEY",
            "temBAD_FEE",
            "terRETRY"
    };
    private static final String TRANSACTION_HASH = "DEADBEEF";
    private static final long MINIMUM_FEE = 12;
    private static final int LAST_LEDGER_SEQUENCE = 20;

    /** The seed for a wallet with funds on the XRP Ledger test net. */
    private static final String WALLET_SEED = "snYP7oArxKepd3GPDcrjMsJYiJeJB";

    /** Drops of XRP to send. */
    private static final BigInteger AMOUNT = new BigInteger("1");

    @Test
    public void getBalanceTest() throws IOException, XpringException {
        // GIVEN a DefaultXRPClient with mocked networking which will succeed.
        DefaultXRPClient client = getClient();

        // WHEN the balance is retrieved.
        BigInteger balance = client.getBalance(XRPL_ADDRESS);

        // THEN the balance returned is the the same as the mocked response.
        assertThat(balance).isEqualTo(BigInteger.valueOf(DROPS_OF_XRP_IN_ACCOUNT));
    }

    @Test
    public void getBalanceWithClassicAddressTest() throws IOException, XpringException {
        // GIVEN a classic address.
        ClassicAddress classicAddress = Utils.decodeXAddress(XRPL_ADDRESS);
        DefaultXRPClient client = getClient();

        // WHEN the balance for the classic address is retrieved THEN an error is thrown.
        expectedException.expect(XpringException.class);
        client.getBalance(classicAddress.address());
    }

    @Test
    public void getBalanceTestWithFailedAccountInfo() throws IOException, XpringException {
        // GIVEN a XRPClient with mocked networking which will fail to retrieve account info.
        GRPCResult<GetAccountInfoResponse, Throwable> accountInfoResult = GRPCResult.error(GENERIC_ERROR);
        DefaultXRPClient client = getClient(
                accountInfoResult,
                GRPCResult.ok(makeTransactionStatus(true, TRANSACTION_STATUS_SUCCESS)),
                GRPCResult.ok(makeGetFeeResponse(MINIMUM_FEE, LAST_LEDGER_SEQUENCE)),
                GRPCResult.ok(makeSubmitTransactionResponse(TRANSACTION_HASH))
        );

        // WHEN the balance is retrieved THEN an error is thrown.
        expectedException.expect(Exception.class);
        client.getBalance(XRPL_ADDRESS);
    }


    @Test
    public void paymentStatusWithUnvalidatedTransactionAndFailureCode() throws IOException, XpringException {
        // Iterate over different types of transaction status codes which represent failures.
        for (String transactionFailureCode : TRANSACTION_FAILURE_STATUS_CODES) {
            // GIVEN a XRPClient which will return an unvalidated transaction with a failed code.
            DefaultXRPClient client = getClient(
                    GRPCResult.ok(makeGetAccountInfoResponse(DROPS_OF_XRP_IN_ACCOUNT)),
                    GRPCResult.ok(makeTransactionStatus(false, transactionFailureCode)),
                    GRPCResult.ok(makeGetFeeResponse(MINIMUM_FEE, LAST_LEDGER_SEQUENCE)),
                    GRPCResult.ok(makeSubmitTransactionResponse(TRANSACTION_HASH))
            );

            // WHEN the payment status is retrieved.
            io.xpring.xrpl.TransactionStatus paymentStatus = client.getPaymentStatus(TRANSACTION_HASH);

            // THEN the status is PENDING.
            assertThat(paymentStatus).isEqualTo(io.xpring.xrpl.TransactionStatus.PENDING);
        }
    }

    @Test
    public void paymentStatusWithUnvalidatedTransactionAndSuccessCode() throws IOException, XpringException {
        // GIVEN a XRPClient which will return an unvalidated transaction with a success code.
        DefaultXRPClient client = getClient(
                GRPCResult.ok(makeGetAccountInfoResponse(DROPS_OF_XRP_IN_ACCOUNT)),
                GRPCResult.ok(makeTransactionStatus(false, TRANSACTION_STATUS_SUCCESS)),
                GRPCResult.ok(makeGetFeeResponse(MINIMUM_FEE, LAST_LEDGER_SEQUENCE)),
                GRPCResult.ok(makeSubmitTransactionResponse(TRANSACTION_HASH))
        );

        // WHEN the payment status is retrieved.
        io.xpring.xrpl.TransactionStatus paymentStatus = client.getPaymentStatus(TRANSACTION_HASH);

        // THEN the status is PENDING.
        assertThat(paymentStatus).isEqualTo(io.xpring.xrpl.TransactionStatus.PENDING);
    }

    @Test
    public void paymentStatusWithValidatedTransactionAndFailureCode() throws IOException, XpringException {
        // Iterate over different types of transaction status codes which represent failures.
        for (String transactionFailureCode : TRANSACTION_FAILURE_STATUS_CODES) {
            // GIVEN a XRPClient which will return an validated transaction with a failed code.
            DefaultXRPClient client = getClient(
                    GRPCResult.ok(makeGetAccountInfoResponse(DROPS_OF_XRP_IN_ACCOUNT)),
                    GRPCResult.ok(makeTransactionStatus(true, transactionFailureCode)),
                    GRPCResult.ok(makeGetFeeResponse(MINIMUM_FEE, LAST_LEDGER_SEQUENCE)),
                    GRPCResult.ok(makeSubmitTransactionResponse(TRANSACTION_HASH))
            );

            // WHEN the payment status is retrieved.
            io.xpring.xrpl.TransactionStatus paymentStatus = client.getPaymentStatus(TRANSACTION_HASH);

            // THEN the status is FAILED.
            assertThat(paymentStatus).isEqualTo(io.xpring.xrpl.TransactionStatus.FAILED);
        }
    }

    @Test
    public void paymentStatusWithValidatedTransactionAndSuccessCode() throws IOException, XpringException {
        // GIVEN a XRPClient which will return an validated transaction with a success code.
        DefaultXRPClient client = getClient(
                GRPCResult.ok(makeGetAccountInfoResponse(DROPS_OF_XRP_IN_ACCOUNT)),
                GRPCResult.ok(makeTransactionStatus(true, TRANSACTION_STATUS_SUCCESS)),
                GRPCResult.ok(makeGetFeeResponse(MINIMUM_FEE, LAST_LEDGER_SEQUENCE)),
                GRPCResult.ok(makeSubmitTransactionResponse(TRANSACTION_HASH))
        );

        // WHEN the payment status is retrieved.
        io.xpring.xrpl.TransactionStatus paymentStatus = client.getPaymentStatus(TRANSACTION_HASH);

        // THEN the status is SUCCEEDED.
        assertThat(paymentStatus).isEqualTo(io.xpring.xrpl.TransactionStatus.SUCCEEDED);
    }

    @Test
    public void paymentStatusWithNodeError() throws IOException, XpringException {
        // GIVEN a XRPClient which will error when a transaction status is requested..
        DefaultXRPClient client = getClient(
                GRPCResult.ok(makeGetAccountInfoResponse(DROPS_OF_XRP_IN_ACCOUNT)),
                GRPCResult.error(GENERIC_ERROR),
                GRPCResult.ok(makeGetFeeResponse(MINIMUM_FEE, LAST_LEDGER_SEQUENCE)),
                GRPCResult.ok(makeSubmitTransactionResponse(TRANSACTION_HASH))
        );

        // WHEN the payment status is retrieved THEN an error is thrown..
        expectedException.expect(Exception.class);
        client.getPaymentStatus(TRANSACTION_HASH);
    }

    @Test
    public void submitTransactionTest() throws IOException, XpringException {
        // GIVEN a XRPClient with mocked networking which will succeed.
        DefaultXRPClient client = getClient();
        Wallet wallet = new Wallet(WALLET_SEED);

        // WHEN a transaction is sent.
        String transactionHash = client.send(AMOUNT, XRPL_ADDRESS, wallet);

        // THEN the transaction hash is the same as the hash of the mocked transaction blob in the response.
        assertThat(transactionHash).isEqualTo(TRANSACTION_HASH.toLowerCase());
    }

    @Test
    public void submitTransactionWithClassicAddress() throws IOException, XpringException {
        // GIVEN a classic address.
        DefaultXRPClient client = getClient();
        ClassicAddress classicAddress = Utils.decodeXAddress(XRPL_ADDRESS);
        Wallet wallet = new Wallet(WALLET_SEED);

        // WHEN XRP is sent to the classic address THEN an error is thrown.
        expectedException.expect(XpringException.class);
        client.send(AMOUNT, classicAddress.address(), wallet);
    }

    @Test
    public void submitTransactionWithFailedAccountInfo() throws IOException, XpringException {
        // GIVEN a XRPClient which will fail to return account info.
        GRPCResult<GetAccountInfoResponse, Throwable> accountInfoResult = GRPCResult.error(GENERIC_ERROR);
        DefaultXRPClient client = getClient(
                accountInfoResult,
                GRPCResult.ok(makeTransactionStatus(true, TRANSACTION_STATUS_SUCCESS)),
                GRPCResult.ok(makeGetFeeResponse(MINIMUM_FEE, LAST_LEDGER_SEQUENCE)),
                GRPCResult.ok(makeSubmitTransactionResponse(TRANSACTION_HASH))
        );
        Wallet wallet = new Wallet(WALLET_SEED);

        // WHEN XRP is sent then THEN an error is thrown.
        expectedException.expect(Exception.class);
        client.send(AMOUNT, XRPL_ADDRESS, wallet);
    }

    @Test
    public void submitTransactionWithFailedFee() throws IOException, XpringException {
        // GIVEN a XRPClient which will fail to retrieve a fee.
        GRPCResult<GetFeeResponse, Throwable> feeResult = GRPCResult.error(GENERIC_ERROR);
        DefaultXRPClient client = getClient(
                GRPCResult.ok(makeGetAccountInfoResponse(DROPS_OF_XRP_IN_ACCOUNT)),
                GRPCResult.ok(makeTransactionStatus(true, TRANSACTION_STATUS_SUCCESS)),
                feeResult,
                GRPCResult.ok(makeSubmitTransactionResponse(TRANSACTION_HASH))
        );
        Wallet wallet = new Wallet(WALLET_SEED);

        // WHEN XRP is sent then THEN an error is thrown.
        expectedException.expect(Exception.class);
        client.send(AMOUNT, XRPL_ADDRESS, wallet);
    }


    @Test
    public void submitTransactionWithFailedSubmit() throws IOException, XpringException {
        // GIVEN a XRPClient which will fail to submit a transaction.
        GRPCResult<SubmitTransactionResponse, Throwable> submitResult = GRPCResult.error(GENERIC_ERROR);
        DefaultXRPClient client = getClient(
                GRPCResult.ok(makeGetAccountInfoResponse(DROPS_OF_XRP_IN_ACCOUNT)),
                GRPCResult.ok(makeTransactionStatus(true, TRANSACTION_STATUS_SUCCESS)),
                GRPCResult.ok(makeGetFeeResponse(MINIMUM_FEE, LAST_LEDGER_SEQUENCE)),
                submitResult
        );
        Wallet wallet = new Wallet(WALLET_SEED);

        // WHEN XRP is sent then THEN an error is thrown.
        expectedException.expect(Exception.class);
        client.send(AMOUNT, XRPL_ADDRESS, wallet);
    }

    @Test
    public void accountExistsTest() throws IOException, XpringException {
        // GIVEN a DefaultXRPClient with mocked networking which will succeed.
        DefaultXRPClient client = getClient();

        // WHEN the account is checked
        boolean exists = client.accountExists(XRPL_ADDRESS);

        // THEN the existence of the account is the the same as the mocked response.
        assertThat(exists).isEqualTo(true);
    }

    @Test
    public void accountExistsWithClassicAddressTest() throws IOException, XpringException {
        // GIVEN a classic address.
        ClassicAddress classicAddress = Utils.decodeXAddress(XRPL_ADDRESS);
        DefaultXRPClient client = getClient();

        // WHEN the existence of the account is checked for the classic address THEN an error is thrown.
        expectedException.expect(XpringException.class);
        client.accountExists(classicAddress.address());
    }

    @Test
    public void accountExistsTestWithNotFoundError() throws IOException, XpringException {
        // GIVEN a XRPClient with mocked networking which will fail to retrieve account info w/ NOT_FOUND error code.
        StatusRuntimeException notFoundError = new StatusRuntimeException(Status.NOT_FOUND);
        GRPCResult<GetAccountInfoResponse> accountInfoResult = GRPCResult.error(notFoundError);
        DefaultXRPClient client = getClient(
                accountInfoResult,
                GRPCResult.ok(makeTransactionStatus(true, TRANSACTION_STATUS_SUCCESS)),
                GRPCResult.ok(makeGetFeeResponse(MINIMUM_FEE, LAST_LEDGER_SEQUENCE)),
                GRPCResult.ok(makeSubmitTransactionResponse(TRANSACTION_HASH))
        );

        // WHEN the existence of the account is checked
        boolean exists = client.accountExists(XRPL_ADDRESS);

        // THEN false is returned.
        assertThat(exists).isEqualTo(false);
    }

    @Test
    public void accountExistsTestWithUnkonwnError() throws IOException, XpringException {
        // GIVEN a XpringClient with mocked networking which will fail to retrieve account info w/ UNKNOWN error code.
        StatusRuntimeException notFoundError = new StatusRuntimeException(Status.UNKNOWN);
        GRPCResult<GetAccountInfoResponse> accountInfoResult = GRPCResult.error(notFoundError);
        DefaultXRPClient client = getClient(
                accountInfoResult,
                GRPCResult.ok(makeTransactionStatus(true, TRANSACTION_STATUS_SUCCESS)),
                GRPCResult.ok(makeGetFeeResponse(MINIMUM_FEE, LAST_LEDGER_SEQUENCE)),
                GRPCResult.ok(makeSubmitTransactionResponse(TRANSACTION_HASH))
        );

        // WHEN the existence of the account is checked THEN the error is re-thrown.
        expectedException.expect(StatusRuntimeException.class);
        client.getBalance(XRPL_ADDRESS);
    }

    /**
     * Convenience method to get a XRPClient which has successful network calls.
     */
    private DefaultXRPClient getClient() throws IOException {
        return getClient(
                GRPCResult.ok(makeGetAccountInfoResponse(DROPS_OF_XRP_IN_ACCOUNT)),
                GRPCResult.ok(makeTransactionStatus(true, TRANSACTION_STATUS_SUCCESS)),
                GRPCResult.ok(makeGetFeeResponse(MINIMUM_FEE, LAST_LEDGER_SEQUENCE)),
                GRPCResult.ok(makeSubmitTransactionResponse(TRANSACTION_HASH))
        );
    }

    /**
     * Return a XRPClient which returns the given results for network calls.
     */

    private DefaultXRPClient getClient(
            GRPCResult<GetAccountInfoResponse, Throwable> getAccountInfoResponseResult,
            GRPCResult<GetTransactionResponse, Throwable> GetTransactionResponseResult,
            GRPCResult<GetFeeResponse, Throwable> getFeeResult,
            GRPCResult<SubmitTransactionResponse, Throwable> submitTransactionResult
    ) throws IOException {
        XRPLedgerAPIServiceGrpc.XRPLedgerAPIServiceImplBase serviceImpl = getService(
                getAccountInfoResponseResult,
                GetTransactionResponseResult,
                getFeeResult,
                submitTransactionResult
        );

        // Generate a unique in-process server name.
        String serverName = InProcessServerBuilder.generateName();

        // Create a server, add service, start, and register for automatic graceful shutdown.
        grpcCleanup.register(InProcessServerBuilder
                .forName(serverName).directExecutor().addService(serviceImpl).build().start());

        // Create a client channel and register for automatic graceful shutdown.
        ManagedChannel channel = grpcCleanup.register(
                InProcessChannelBuilder.forName(serverName).directExecutor().build());

        // Create a new XRPClient using the in-process channel;
        return new DefaultXRPClient(channel);
    }


    /**
     * Return a XRPLedgerService implementation which returns the given results for network calls.
     */
    private XRPLedgerAPIServiceGrpc.XRPLedgerAPIServiceImplBase getService(
        GRPCResult<GetAccountInfoResponse, Throwable> getAccountInfoResult,
        GRPCResult<GetTransactionResponse, Throwable> GetTransactionResponseResult,
        GRPCResult<GetFeeResponse, Throwable> getFeeResult,
        GRPCResult<SubmitTransactionResponse, Throwable> submitTransactionResult
    ) {
        return mock(XRPLedgerAPIServiceGrpc.XRPLedgerAPIServiceImplBase.class, delegatesTo(
                new XRPLedgerAPIServiceGrpc.XRPLedgerAPIServiceImplBase() {
                    @Override
                    public void getAccountInfo(GetAccountInfoRequest request, StreamObserver<GetAccountInfoResponse> responseObserver) {
                        if (getAccountInfoResult.isError()) {
                            responseObserver.onError(getAccountInfoResult.getError());
                        } else {
                            responseObserver.onNext(getAccountInfoResult.getValue());
                            responseObserver.onCompleted();
                        }
                    }

                    @Override
                    public void getTransaction(GetTransactionRequest request, StreamObserver<GetTransactionResponse> responseObserver) {
                        if (GetTransactionResponseResult.isError()) {
                            responseObserver.onError(new Throwable(GetTransactionResponseResult.getError()));
                        } else {
                            responseObserver.onNext(GetTransactionResponseResult.getValue());
                            responseObserver.onCompleted();
                        }
                    }

                    @Override
                    public void getFee(GetFeeRequest request,
                                       StreamObserver<GetFeeResponse> responseObserver) {
                        if (getFeeResult.isError()) {
                            responseObserver.onError(getFeeResult.getError());
                        } else {
                            responseObserver.onNext(getFeeResult.getValue());
                            responseObserver.onCompleted();
                        }
                    }

                    @Override
                    public void submitTransaction(SubmitTransactionRequest request,
                                                  StreamObserver<SubmitTransactionResponse> responseObserver) {
                        if (submitTransactionResult.isError()) {
                            responseObserver.onError(submitTransactionResult.getError());
                        } else {
                            responseObserver.onNext(submitTransactionResult.getValue());
                            responseObserver.onCompleted();
                        }
                    }
                }
                ));
    }

    /**
     * Make a SubmitSignedTransaction response protocol buffer with the given inputs.
     */
    private SubmitTransactionResponse makeSubmitTransactionResponse(String hash) {
        ByteString bytes = ByteString.copyFrom(Utils.hexStringToByteArray(hash));
        return SubmitTransactionResponse.newBuilder().setHash(bytes).build();
    }

    /**
     * Make a GetFeeResponse protocol buffer with the given inputs.
     */
    private GetFeeResponse makeGetFeeResponse(long minimumFee, int lastLedgerSequence) {
        XRPDropsAmount minimumDrops = XRPDropsAmount.newBuilder().setDrops(minimumFee).build();
        Fee fee = Fee.newBuilder().setMinimumFee(minimumDrops).build();
        return GetFeeResponse.newBuilder().setLedgerCurrentIndex(lastLedgerSequence).setFee(fee).build();
    }

    /**
     * Make an GetAccountInfoResponse protocol buffer with the given balance.
     */
    private GetAccountInfoResponse makeGetAccountInfoResponse(long balance) {
        XRPDropsAmount xrpAccountBalance = XRPDropsAmount.newBuilder().setDrops(balance).build();
        CurrencyAmount currencyAccountBalance = CurrencyAmount.newBuilder().setXrpAmount(xrpAccountBalance).build();
        Balance accountBalance = Balance.newBuilder().setValue(currencyAccountBalance).build();

        AccountRoot accountData = AccountRoot.newBuilder().setBalance(accountBalance).build();
        return GetAccountInfoResponse.newBuilder().setAccountData(accountData).build();
    }

    /**
     * Make a GetTransactionResponse.
     */
    private GetTransactionResponse makeTransactionStatus(Boolean validated, String result) {
        TransactionResult transactionResult = TransactionResult.newBuilder()
                .setResult(result)
                .build();
        Meta meta = Meta.newBuilder().setTransactionResult(transactionResult).build();

        return GetTransactionResponse.newBuilder().setValidated(validated).setMeta(meta).build();
    }
}
