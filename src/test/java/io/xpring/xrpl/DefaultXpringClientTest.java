package io.xpring.xrpl;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
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
        // GIVEN a DefaultXpringClient with mocked networking which will succeed.
        DefaultXpringClient client = getClient();

        // WHEN the balance is retrieved.
        BigInteger balance = client.getBalance(XRPL_ADDRESS);

        // THEN the balance returned is the the same as the mocked response.
        assertThat(balance).isEqualTo(BigInteger.valueOf(DROPS_OF_XRP_IN_ACCOUNT));
    }

    @Test
    public void getBalanceWithClassicAddressTest() throws IOException, XpringException {
        // GIVEN a classic address.
        ClassicAddress classicAddress = Utils.decodeXAddress(XRPL_ADDRESS);
        DefaultXpringClient client = getClient();

        // WHEN the balance for the classic address is retrieved THEN an error is thrown.
        expectedException.expect(XpringException.class);
        client.getBalance(classicAddress.address());
    }

    @Test
    public void getBalanceTestWithFailedAccountInfo() throws IOException, XpringException {
        // GIVEN a XpringClient with mocked networking which will fail to retrieve account info.
        GRPCResult<GetAccountInfoResponse> accountInfoResult = GRPCResult.error(GENERIC_ERROR);
        DefaultXpringClient client = getClient(
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
    public void transactionStatusWithUnvalidatedTransactionAndFailureCode() throws IOException, XpringException {
        // Iterate over different types of transaction status codes which represent failures.
        for (String transactionFailureCode : TRANSACTION_FAILURE_STATUS_CODES) {
            // GIVEN a XpringClient which will return an unvalidated transaction with a failed code.
            DefaultXpringClient client = getClient(
                    GRPCResult.ok(makeGetAccountInfoResponse(DROPS_OF_XRP_IN_ACCOUNT)),
                    GRPCResult.ok(makeTransactionStatus(false, transactionFailureCode)),
                    GRPCResult.ok(makeGetFeeResponse(MINIMUM_FEE, LAST_LEDGER_SEQUENCE)),
                    GRPCResult.ok(makeSubmitTransactionResponse(TRANSACTION_HASH))
            );

            // WHEN the transaction status is retrieved.
            io.xpring.xrpl.TransactionStatus transactionStatus = client.getTransactionStatus(TRANSACTION_HASH);

            // THEN the status is PENDING.
            assertThat(transactionStatus).isEqualTo(io.xpring.xrpl.TransactionStatus.PENDING);
        }
    }

    @Test
    public void transactionStatusWithUnvalidatedTransactionAndSuccessCode() throws IOException, XpringException {
        // GIVEN a XpringClient which will return an unvalidated transaction with a success code.
        DefaultXpringClient client = getClient(
                GRPCResult.ok(makeGetAccountInfoResponse(DROPS_OF_XRP_IN_ACCOUNT)),
                GRPCResult.ok(makeTransactionStatus(false, TRANSACTION_STATUS_SUCCESS)),
                GRPCResult.ok(makeGetFeeResponse(MINIMUM_FEE, LAST_LEDGER_SEQUENCE)),
                GRPCResult.ok(makeSubmitTransactionResponse(TRANSACTION_HASH))
        );

        // WHEN the transaction status is retrieved.
        io.xpring.xrpl.TransactionStatus transactionStatus = client.getTransactionStatus(TRANSACTION_HASH);

        // THEN the status is PENDING.
        assertThat(transactionStatus).isEqualTo(io.xpring.xrpl.TransactionStatus.PENDING);
    }

    @Test
    public void transactionStatusWithValidatedTransactionAndFailureCode() throws IOException, XpringException {
        // Iterate over different types of transaction status codes which represent failures.
        for (String transactionFailureCode : TRANSACTION_FAILURE_STATUS_CODES) {
            // GIVEN a XpringClient which will return an validated transaction with a failed code.
            DefaultXpringClient client = getClient(
                    GRPCResult.ok(makeGetAccountInfoResponse(DROPS_OF_XRP_IN_ACCOUNT)),
                    GRPCResult.ok(makeTransactionStatus(true, transactionFailureCode)),
                    GRPCResult.ok(makeGetFeeResponse(MINIMUM_FEE, LAST_LEDGER_SEQUENCE)),
                    GRPCResult.ok(makeSubmitTransactionResponse(TRANSACTION_HASH))
            );

            // WHEN the transaction status is retrieved.
            io.xpring.xrpl.TransactionStatus transactionStatus = client.getTransactionStatus(TRANSACTION_HASH);

            // THEN the status is FAILED.
            assertThat(transactionStatus).isEqualTo(io.xpring.xrpl.TransactionStatus.FAILED);
        }
    }

    @Test
    public void transactionStatusWithValidatedTransactionAndSuccessCode() throws IOException, XpringException {
        // GIVEN a XpringClient which will return an validated transaction with a success code.
        DefaultXpringClient client = getClient(
                GRPCResult.ok(makeGetAccountInfoResponse(DROPS_OF_XRP_IN_ACCOUNT)),
                GRPCResult.ok(makeTransactionStatus(true, TRANSACTION_STATUS_SUCCESS)),
                GRPCResult.ok(makeGetFeeResponse(MINIMUM_FEE, LAST_LEDGER_SEQUENCE)),
                GRPCResult.ok(makeSubmitTransactionResponse(TRANSACTION_HASH))
        );

        // WHEN the transaction status is retrieved.
        io.xpring.xrpl.TransactionStatus transactionStatus = client.getTransactionStatus(TRANSACTION_HASH);

        // THEN the status is SUCCEEDED.
        assertThat(transactionStatus).isEqualTo(io.xpring.xrpl.TransactionStatus.SUCCEEDED);
    }

    @Test
    public void transactionStatusWithNodeError() throws IOException, XpringException {
        // GIVEN a XpringClient which will error when a transaction status is requested..
        DefaultXpringClient client = getClient(
                GRPCResult.ok(makeGetAccountInfoResponse(DROPS_OF_XRP_IN_ACCOUNT)),
                GRPCResult.error(GENERIC_ERROR),
                GRPCResult.ok(makeGetFeeResponse(MINIMUM_FEE, LAST_LEDGER_SEQUENCE)),
                GRPCResult.ok(makeSubmitTransactionResponse(TRANSACTION_HASH))
        );

        // WHEN the transaction status is retrieved THEN an error is thrown..
        expectedException.expect(Exception.class);
        client.getTransactionStatus(TRANSACTION_HASH);
    }

    @Test
    public void submitTransactionTest() throws IOException, XpringException {
        // GIVEN a XpringClient with mocked networking which will succeed.
        DefaultXpringClient client = getClient();
        Wallet wallet = new Wallet(WALLET_SEED);

        // WHEN a transaction is sent.
        String transactionHash = client.send(AMOUNT, XRPL_ADDRESS, wallet);

        // THEN the transaction hash is the same as the hash of the mocked transaction blob in the response.
        assertThat(transactionHash).isEqualTo(TRANSACTION_HASH.toLowerCase());
    }

    @Test
    public void submitTransactionWithClassicAddress() throws IOException, XpringException {
        // GIVEN a classic address.
        DefaultXpringClient client = getClient();
        ClassicAddress classicAddress = Utils.decodeXAddress(XRPL_ADDRESS);
        Wallet wallet = new Wallet(WALLET_SEED);

        // WHEN XRP is sent to the classic address THEN an error is thrown.
        expectedException.expect(XpringException.class);
        client.send(AMOUNT, classicAddress.address(), wallet);
    }

    @Test
    public void submitTransactionWithFailedAccountInfo() throws IOException, XpringException {
        // GIVEN a XpringClient which will fail to return account info.
        GRPCResult<GetAccountInfoResponse> accountInfoResult = GRPCResult.error(GENERIC_ERROR);
        DefaultXpringClient client = getClient(
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
        // GIVEN a XpringClient which will fail to retrieve a fee.
        GRPCResult<GetFeeResponse> feeResult = GRPCResult.error(GENERIC_ERROR);
        DefaultXpringClient client = getClient(
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
        // GIVEN a XpringClient which will fail to submit a transaction.
        GRPCResult<SubmitTransactionResponse> submitResult = GRPCResult.error(GENERIC_ERROR);
        DefaultXpringClient client = getClient(
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

    /**
     * Convenience method to get a XpringClient which has successful network calls.
     */
    private DefaultXpringClient getClient() throws IOException {
        return getClient(
                GRPCResult.ok(makeGetAccountInfoResponse(DROPS_OF_XRP_IN_ACCOUNT)),
                GRPCResult.ok(makeTransactionStatus(true, TRANSACTION_STATUS_SUCCESS)),
                GRPCResult.ok(makeGetFeeResponse(MINIMUM_FEE, LAST_LEDGER_SEQUENCE)),
                GRPCResult.ok(makeSubmitTransactionResponse(TRANSACTION_HASH))
        );
    }

    /**
     * Return a XpringClient which returns the given results for network calls.
     */

    private DefaultXpringClient getClient(
            GRPCResult<GetAccountInfoResponse> getAccountInfoResponseResult,
            GRPCResult<GetTransactionResponse> GetTransactionResponseResult,
            GRPCResult<GetFeeResponse> getFeeResult,
            GRPCResult<SubmitTransactionResponse> submitTransactionResult
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

        // Create a new XpringClient using the in-process channel;
        return new DefaultXpringClient(channel);
    }


    /**
     * Return a XRPLedgerService implementation which returns the given results for network calls.
     */
    private XRPLedgerAPIServiceGrpc.XRPLedgerAPIServiceImplBase getService(
        GRPCResult<GetAccountInfoResponse> getAccountInfoResult,
        GRPCResult<GetTransactionResponse> GetTransactionResponseResult,
        GRPCResult<GetFeeResponse> getFeeResult,
        GRPCResult<SubmitTransactionResponse> submitTransactionResult
    ) {
        return mock(XRPLedgerAPIServiceGrpc.XRPLedgerAPIServiceImplBase.class, delegatesTo(
                new XRPLedgerAPIServiceGrpc.XRPLedgerAPIServiceImplBase() {
                    @Override
                    public void getAccountInfo(GetAccountInfoRequest request, StreamObserver<GetAccountInfoResponse> responseObserver) {
                        if (getAccountInfoResult.isError()) {
                            responseObserver.onError(new Throwable(getAccountInfoResult.getError()));
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
                            responseObserver.onError(new Throwable(getFeeResult.getError()));
                        } else {
                            responseObserver.onNext(getFeeResult.getValue());
                            responseObserver.onCompleted();
                        }
                    }

                    @Override
                    public void submitTransaction(SubmitTransactionRequest request,
                                                  StreamObserver<SubmitTransactionResponse> responseObserver) {
                        if (submitTransactionResult.isError()) {
                            responseObserver.onError(new Throwable(submitTransactionResult.getError()));
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
