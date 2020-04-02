package io.xpring.xrpl;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import io.xpring.GRPCResult;
import io.xpring.xrpl.helpers.XRPTestUtils;
import io.xpring.xrpl.model.XRPTransaction;
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
import org.xrpl.rpc.v1.*;
import org.xrpl.rpc.v1.Common.*;
import org.xrpl.rpc.v1.Common.Amount;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

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
        // GIVEN an XRPClient with mocked networking which will fail to retrieve account info.
        GRPCResult<GetAccountInfoResponse> accountInfoResult = GRPCResult.error(GENERIC_ERROR);
        DefaultXRPClient client = getClient(
                accountInfoResult,
                GRPCResult.ok(makeTransactionStatus(true, TRANSACTION_STATUS_SUCCESS)),
                GRPCResult.ok(makeGetFeeResponse(MINIMUM_FEE, LAST_LEDGER_SEQUENCE)),
                GRPCResult.ok(makeSubmitTransactionResponse(TRANSACTION_HASH)),
                GRPCResult.ok(makeGetAccountTransactionHistoryResponse())
        );

        // WHEN the balance is retrieved THEN an error is thrown.
        expectedException.expect(Exception.class);
        client.getBalance(XRPL_ADDRESS);
    }


    @Test
    public void paymentStatusWithUnvalidatedTransactionAndFailureCode() throws IOException, XpringException {
        // Iterate over different types of transaction status codes which represent failures.
        for (String transactionFailureCode : TRANSACTION_FAILURE_STATUS_CODES) {
            // GIVEN an XRPClient which will return an unvalidated transaction with a failed code.
            DefaultXRPClient client = getClient(
                    GRPCResult.ok(makeGetAccountInfoResponse(DROPS_OF_XRP_IN_ACCOUNT)),
                    GRPCResult.ok(makeTransactionStatus(false, transactionFailureCode)),
                    GRPCResult.ok(makeGetFeeResponse(MINIMUM_FEE, LAST_LEDGER_SEQUENCE)),
                    GRPCResult.ok(makeSubmitTransactionResponse(TRANSACTION_HASH)),
                    GRPCResult.ok(makeGetAccountTransactionHistoryResponse())
            );

            // WHEN the payment status is retrieved.
            io.xpring.xrpl.TransactionStatus paymentStatus = client.getPaymentStatus(TRANSACTION_HASH);

            // THEN the status is PENDING.
            assertThat(paymentStatus).isEqualTo(io.xpring.xrpl.TransactionStatus.PENDING);
        }
    }

    @Test
    public void paymentStatusWithUnvalidatedTransactionAndSuccessCode() throws IOException, XpringException {
        // GIVEN an XRPClient which will return an unvalidated transaction with a success code.
        DefaultXRPClient client = getClient(
                GRPCResult.ok(makeGetAccountInfoResponse(DROPS_OF_XRP_IN_ACCOUNT)),
                GRPCResult.ok(makeTransactionStatus(false, TRANSACTION_STATUS_SUCCESS)),
                GRPCResult.ok(makeGetFeeResponse(MINIMUM_FEE, LAST_LEDGER_SEQUENCE)),
                GRPCResult.ok(makeSubmitTransactionResponse(TRANSACTION_HASH)),
                GRPCResult.ok(makeGetAccountTransactionHistoryResponse())
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
            // GIVEN an XRPClient which will return an validated transaction with a failed code.
            DefaultXRPClient client = getClient(
                    GRPCResult.ok(makeGetAccountInfoResponse(DROPS_OF_XRP_IN_ACCOUNT)),
                    GRPCResult.ok(makeTransactionStatus(true, transactionFailureCode)),
                    GRPCResult.ok(makeGetFeeResponse(MINIMUM_FEE, LAST_LEDGER_SEQUENCE)),
                    GRPCResult.ok(makeSubmitTransactionResponse(TRANSACTION_HASH)),
                    GRPCResult.ok(makeGetAccountTransactionHistoryResponse())
            );

            // WHEN the payment status is retrieved.
            io.xpring.xrpl.TransactionStatus paymentStatus = client.getPaymentStatus(TRANSACTION_HASH);

            // THEN the status is FAILED.
            assertThat(paymentStatus).isEqualTo(io.xpring.xrpl.TransactionStatus.FAILED);
        }
    }

    @Test
    public void paymentStatusWithValidatedTransactionAndSuccessCode() throws IOException, XpringException {
        // GIVEN an XRPClient which will return an validated transaction with a success code.
        DefaultXRPClient client = getClient(
                GRPCResult.ok(makeGetAccountInfoResponse(DROPS_OF_XRP_IN_ACCOUNT)),
                GRPCResult.ok(makeTransactionStatus(true, TRANSACTION_STATUS_SUCCESS)),
                GRPCResult.ok(makeGetFeeResponse(MINIMUM_FEE, LAST_LEDGER_SEQUENCE)),
                GRPCResult.ok(makeSubmitTransactionResponse(TRANSACTION_HASH)),
                GRPCResult.ok(makeGetAccountTransactionHistoryResponse())
        );

        // WHEN the payment status is retrieved.
        io.xpring.xrpl.TransactionStatus paymentStatus = client.getPaymentStatus(TRANSACTION_HASH);

        // THEN the status is SUCCEEDED.
        assertThat(paymentStatus).isEqualTo(io.xpring.xrpl.TransactionStatus.SUCCEEDED);
    }

    @Test
    public void paymentStatusWithNodeError() throws IOException, XpringException {
        // GIVEN an XRPClient which will error when a transaction status is requested..
        DefaultXRPClient client = getClient(
                GRPCResult.ok(makeGetAccountInfoResponse(DROPS_OF_XRP_IN_ACCOUNT)),
                GRPCResult.error(GENERIC_ERROR),
                GRPCResult.ok(makeGetFeeResponse(MINIMUM_FEE, LAST_LEDGER_SEQUENCE)),
                GRPCResult.ok(makeSubmitTransactionResponse(TRANSACTION_HASH)),
                GRPCResult.ok(makeGetAccountTransactionHistoryResponse())
        );

        // WHEN the payment status is retrieved THEN an error is thrown..
        expectedException.expect(Exception.class);
        client.getPaymentStatus(TRANSACTION_HASH);
    }

    @Test
    public void submitTransactionTest() throws IOException, XpringException {
        // GIVEN an XRPClient with mocked networking which will succeed.
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
        // GIVEN an XRPClient which will fail to return account info.
        GRPCResult<GetAccountInfoResponse> accountInfoResult = GRPCResult.error(GENERIC_ERROR);
        DefaultXRPClient client = getClient(
                accountInfoResult,
                GRPCResult.ok(makeTransactionStatus(true, TRANSACTION_STATUS_SUCCESS)),
                GRPCResult.ok(makeGetFeeResponse(MINIMUM_FEE, LAST_LEDGER_SEQUENCE)),
                GRPCResult.ok(makeSubmitTransactionResponse(TRANSACTION_HASH)),
                GRPCResult.ok(makeGetAccountTransactionHistoryResponse())
        );
        Wallet wallet = new Wallet(WALLET_SEED);

        // WHEN XRP is sent then THEN an error is thrown.
        expectedException.expect(Exception.class);
        client.send(AMOUNT, XRPL_ADDRESS, wallet);
    }

    @Test
    public void submitTransactionWithFailedFee() throws IOException, XpringException {
        // GIVEN an XRPClient which will fail to retrieve a fee.
        GRPCResult<GetFeeResponse> feeResult = GRPCResult.error(GENERIC_ERROR);
        DefaultXRPClient client = getClient(
                GRPCResult.ok(makeGetAccountInfoResponse(DROPS_OF_XRP_IN_ACCOUNT)),
                GRPCResult.ok(makeTransactionStatus(true, TRANSACTION_STATUS_SUCCESS)),
                feeResult,
                GRPCResult.ok(makeSubmitTransactionResponse(TRANSACTION_HASH)),
                GRPCResult.ok(makeGetAccountTransactionHistoryResponse())
        );
        Wallet wallet = new Wallet(WALLET_SEED);

        // WHEN XRP is sent then THEN an error is thrown.
        expectedException.expect(Exception.class);
        client.send(AMOUNT, XRPL_ADDRESS, wallet);
    }


    @Test
    public void submitTransactionWithFailedSubmit() throws IOException, XpringException {
        // GIVEN an XRPClient which will fail to submit a transaction.
        GRPCResult<SubmitTransactionResponse> submitResult = GRPCResult.error(GENERIC_ERROR);
        DefaultXRPClient client = getClient(
                GRPCResult.ok(makeGetAccountInfoResponse(DROPS_OF_XRP_IN_ACCOUNT)),
                GRPCResult.ok(makeTransactionStatus(true, TRANSACTION_STATUS_SUCCESS)),
                GRPCResult.ok(makeGetFeeResponse(MINIMUM_FEE, LAST_LEDGER_SEQUENCE)),
                submitResult,
                GRPCResult.ok(makeGetAccountTransactionHistoryResponse())
        );
        Wallet wallet = new Wallet(WALLET_SEED);

        // WHEN XRP is sent then THEN an error is thrown.
        expectedException.expect(Exception.class);
        client.send(AMOUNT, XRPL_ADDRESS, wallet);
    }

    @Test
    public void paymentHistoryWithSuccessfulResponseTest() throws IOException, XpringException {
        // GIVEN a DefaultXRPClient with mocked networking that will succeed.
        DefaultXRPClient xrpClient = getClient();

        // WHEN the payment history for an address is requested.
        List<XRPTransaction> paymentHistory = xrpClient.paymentHistory(XRPL_ADDRESS);

        List<XRPTransaction> expectedPaymentHistory = XRPTestUtils.transactionHistoryToPaymentsList(
                                                                        makeGetAccountTransactionHistoryResponse());

        // THEN the payment history is returned as expected.
        assertThat(paymentHistory).isEqualTo(expectedPaymentHistory);
    }

    @Test
    public void paymentHistoryWithClassicAddressTest() throws IOException, XpringException {
        // GIVEN an XRPClient and a classic address
        DefaultXRPClient xrpClient = getClient();
        ClassicAddress classicAddress = Utils.decodeXAddress(XRPL_ADDRESS);

        // WHEN the payment history for an account is requested THEN an error to use X-Addresses is thrown.
        expectedException.expect(XpringException.class);
        xrpClient.paymentHistory(classicAddress.address());
    }

    @Test
    public void paymentHistoryWithNetworkFailureTest() throws IOException, XpringException {
        // GIVEN an XRPClient which will return a network error when calling paymentHistory.
        GRPCResult<GetAccountTransactionHistoryResponse> getAccountTransactionHistoryResponse =
                                                                                        GRPCResult.error(GENERIC_ERROR);
        DefaultXRPClient xrpClient = getClient(
                GRPCResult.ok(makeGetAccountInfoResponse(DROPS_OF_XRP_IN_ACCOUNT)),
                GRPCResult.ok(makeTransactionStatus(true, TRANSACTION_STATUS_SUCCESS)),
                GRPCResult.ok(makeGetFeeResponse(MINIMUM_FEE, LAST_LEDGER_SEQUENCE)),
                GRPCResult.ok(makeSubmitTransactionResponse(TRANSACTION_HASH)),
                getAccountTransactionHistoryResponse);

        // WHEN the payment history is requested THEN an error is propagated.
        expectedException.expect(Exception.class);
        xrpClient.paymentHistory(XRPL_ADDRESS);
    }

    @Test
    public void paymentHistoryWithSomeNonPaymentTransactionsTest() throws IOException, XpringException {
        // GIVEN an XRPClient client which will return a transaction history which contains non-payment transactions.
        GRPCResult<GetAccountTransactionHistoryResponse> getAccountTransactionHistoryResponse =
                                            GRPCResult.ok(FakeXRPProtobufs.mixedGetAccountTransactionHistoryResponse);

        DefaultXRPClient xrpClient = getClient(
                GRPCResult.ok(makeGetAccountInfoResponse(DROPS_OF_XRP_IN_ACCOUNT)),
                GRPCResult.ok(makeTransactionStatus(true, TRANSACTION_STATUS_SUCCESS)),
                GRPCResult.ok(makeGetFeeResponse(MINIMUM_FEE, LAST_LEDGER_SEQUENCE)),
                GRPCResult.ok(makeSubmitTransactionResponse(TRANSACTION_HASH)),
                getAccountTransactionHistoryResponse);

        // WHEN the transactionHistory is requested.
        List<XRPTransaction> transactionHistory = xrpClient.paymentHistory(XRPL_ADDRESS);

        // THEN the returned transactions are conversions of the inputs with non-payment transactions filtered.
        List<XRPTransaction> expectedTransactionHistory = XRPTestUtils.transactionHistoryToPaymentsList(
                                                            FakeXRPProtobufs.mixedGetAccountTransactionHistoryResponse);
    }

    @Test
    public void paymentHistoryWithInvalidPaymentTest() throws IOException, XpringException {
        // GIVEN an XRPClient client which will return a transaction history which contains a malformed payment.
        GRPCResult<GetAccountTransactionHistoryResponse> getAccountTransactionHistoryResponse =
                GRPCResult.ok(FakeXRPProtobufs.invalidPaymentGetAccountTransactionHistoryResponse);

        DefaultXRPClient xrpClient = getClient(
                GRPCResult.ok(makeGetAccountInfoResponse(DROPS_OF_XRP_IN_ACCOUNT)),
                GRPCResult.ok(makeTransactionStatus(true, TRANSACTION_STATUS_SUCCESS)),
                GRPCResult.ok(makeGetFeeResponse(MINIMUM_FEE, LAST_LEDGER_SEQUENCE)),
                GRPCResult.ok(makeSubmitTransactionResponse(TRANSACTION_HASH)),
                getAccountTransactionHistoryResponse);

        // WHEN the transactionHistory is requested THEN a conversion error is thrown.
        expectedException.expect(XpringException.class);
        xrpClient.paymentHistory(XRPL_ADDRESS);
    }

    /**
     * Convenience method to get an XRPClient which has successful network calls.
     */
    private DefaultXRPClient getClient() throws IOException {
        return getClient(
                GRPCResult.ok(makeGetAccountInfoResponse(DROPS_OF_XRP_IN_ACCOUNT)),
                GRPCResult.ok(makeTransactionStatus(true, TRANSACTION_STATUS_SUCCESS)),
                GRPCResult.ok(makeGetFeeResponse(MINIMUM_FEE, LAST_LEDGER_SEQUENCE)),
                GRPCResult.ok(makeSubmitTransactionResponse(TRANSACTION_HASH)),
                GRPCResult.ok(makeGetAccountTransactionHistoryResponse())
        );
    }

    /**
     * Return an XRPClient which returns the given results for network calls.
     */
    private DefaultXRPClient getClient(
            GRPCResult<GetAccountInfoResponse> getAccountInfoResponseResult,
            GRPCResult<GetTransactionResponse> GetTransactionResponseResult,
            GRPCResult<GetFeeResponse> getFeeResult,
            GRPCResult<SubmitTransactionResponse> submitTransactionResult,
            GRPCResult<GetAccountTransactionHistoryResponse> getAccountTransactionHistoryResult
    ) throws IOException {
        XRPLedgerAPIServiceGrpc.XRPLedgerAPIServiceImplBase serviceImpl = getService(
                getAccountInfoResponseResult,
                GetTransactionResponseResult,
                getFeeResult,
                submitTransactionResult,
                getAccountTransactionHistoryResult
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
     * Return an XRPLedgerService implementation which returns the given results for network calls.
     */
    private XRPLedgerAPIServiceGrpc.XRPLedgerAPIServiceImplBase getService(
        GRPCResult<GetAccountInfoResponse> getAccountInfoResult,
        GRPCResult<GetTransactionResponse> GetTransactionResponseResult,
        GRPCResult<GetFeeResponse> getFeeResult,
        GRPCResult<SubmitTransactionResponse> submitTransactionResult,
        GRPCResult<GetAccountTransactionHistoryResponse> getTransactionHistoryResult
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

                    @Override
                    public void getAccountTransactionHistory(GetAccountTransactionHistoryRequest request,
                                                  StreamObserver<GetAccountTransactionHistoryResponse> responseObserver) {
                        if (getTransactionHistoryResult.isError()) {
                            responseObserver.onError(getTransactionHistoryResult.getError());
                        } else {
                            responseObserver.onNext(getTransactionHistoryResult.getValue());
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

    /**
     * Make a GetAccountTransactionHistoryResponse.
     *
     * Note: Delegates to FakeXRPProtobufs for re-usability.
     */
    private GetAccountTransactionHistoryResponse makeGetAccountTransactionHistoryResponse() {
        return FakeXRPProtobufs.paymentOnlyGetAccountTransactionHistoryResponse;
    }
}
