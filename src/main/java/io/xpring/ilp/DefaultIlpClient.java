package io.xpring.ilp;

import org.interledger.spsp.server.grpc.AccountServiceGrpc;
import org.interledger.spsp.server.grpc.BalanceServiceGrpc;
import org.interledger.spsp.server.grpc.CreateAccountRequest;
import org.interledger.spsp.server.grpc.CreateAccountResponse;
import org.interledger.spsp.server.grpc.GetAccountRequest;
import org.interledger.spsp.server.grpc.GetAccountResponse;
import org.interledger.spsp.server.grpc.GetBalanceRequest;
import org.interledger.spsp.server.grpc.GetBalanceResponse;
import org.interledger.spsp.server.grpc.IlpOverHttpServiceGrpc;
import org.interledger.spsp.server.grpc.SendPaymentRequest;
import org.interledger.spsp.server.grpc.SendPaymentResponse;

import com.google.common.annotations.VisibleForTesting;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.xpring.ilp.grpc.IlpJwtCallCredentials;
import io.xpring.xrpl.XpringKitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;
import java.util.Properties;

/**
 * A client that can get balances on a connector and send ILP payments.
 *
 * NOTE: This is where we'll interact with the generated client from (maybe) Swagger.
 * {@link IlpClient} should use this as its decorated client so that it can hide the implementation details
 * of the client
 *
 */
public class DefaultIlpClient implements IlpClientDecorator {

    private static String XPRING_ILP_GRPC_URL;
    private static final Logger logger = LoggerFactory.getLogger(DefaultIlpClient.class);

    // Bring in configuration properties such as GRPC url
    static {
        String propertiesFileName = "client.properties";
        Properties properties = new Properties();

        try {
            // Make sure that the configuration file exists
            URL res = DefaultIlpClient.class.getClassLoader().getResource(propertiesFileName);
            InputStream inputStream = new FileInputStream(res.getFile());
            // load the properties file
            properties.load(inputStream);

            XPRING_ILP_GRPC_URL = properties.getProperty("ilp.grpc.url");
        } catch (Exception e) {
            logger.info(String.format("Problem loading config file named %s. Using default configuration.", propertiesFileName));
            XPRING_ILP_GRPC_URL = "http://localhost:6565";
        }
    };

    private final BalanceServiceGrpc.BalanceServiceBlockingStub balanceServiceStub;
    private final AccountServiceGrpc.AccountServiceBlockingStub accountServiceStub;
    private final IlpOverHttpServiceGrpc.IlpOverHttpServiceBlockingStub ilpOverHttpServiceStub;

    /**
     * No-args Constructor.
     */
    public DefaultIlpClient() {
        this(ManagedChannelBuilder
          .forTarget(XPRING_ILP_GRPC_URL)
          // Let's use plaintext communication because we don't have certs
          // TODO: Use TLS!
          .usePlaintext()
          .build()
        );
    }

    @VisibleForTesting
    public DefaultIlpClient(String grpcUrl) {
        this(ManagedChannelBuilder
          .forTarget(grpcUrl)
          // Let's use plaintext communication because we don't have certs
          // TODO: Use TLS!
          .usePlaintext()
          .build()
        );
    }

    public DefaultIlpClient(final ManagedChannel channel) {
        this.balanceServiceStub = BalanceServiceGrpc.newBlockingStub(channel);
        this.accountServiceStub = AccountServiceGrpc.newBlockingStub(channel);
        this.ilpOverHttpServiceStub = IlpOverHttpServiceGrpc.newBlockingStub(channel);
    }

    @Override
    public CreateAccountResponse createAccount() throws XpringKitException {
        return this.createAccount(Optional.empty(),
          Optional.empty(),
          Optional.empty(),
          Optional.empty(),
          Optional.empty());
    }

    @Override
    public CreateAccountResponse createAccount(String accountId, String assetCode, Integer assetScale, String description) throws XpringKitException {
        return createAccount(Optional.empty(), Optional.of(accountId), Optional.of(assetCode), Optional.of(assetScale), Optional.of(description));
    }

    @Override
    public CreateAccountResponse createAccount(String assetCode, Integer assetScale, String description) throws XpringKitException {
        return createAccount(Optional.empty(), Optional.empty(), Optional.of(assetCode), Optional.of(assetScale), Optional.of(description));
    }

    @Override
    public CreateAccountResponse createAccount(String assetCode, Integer assetScale) throws XpringKitException {
        return createAccount(Optional.empty(), Optional.empty(), Optional.of(assetCode), Optional.of(assetScale), Optional.empty());
    }

    @Override
    public CreateAccountResponse createAccount(String bearerToken, String accountId, String assetCode, Integer assetScale, String description) throws XpringKitException {
        return createAccount(Optional.of(bearerToken), Optional.of(accountId), Optional.of(assetCode), Optional.of(assetScale), Optional.of(description));
    }

    @Override
    public CreateAccountResponse createAccount(String bearerToken, String accountId, String assetCode, Integer assetScale) throws XpringKitException {
        return createAccount(Optional.of(bearerToken), Optional.of(accountId), Optional.of(assetCode), Optional.of(assetScale), Optional.empty());
    }

    private CreateAccountResponse createAccount(Optional<String> bearerToken,
                                                Optional<String> accountId,
                                                Optional<String> assetCode,
                                                Optional<Integer> assetScale,
                                                Optional<String> description) throws XpringKitException {
        CreateAccountRequest request = CreateAccountRequest.newBuilder()
          .setAccountId(accountId.orElse(""))
          .setAssetCode(assetCode.orElse(""))
          .setAssetScale(assetScale.orElse(0))
          .setDescription(description.orElse(""))
          .build();

        try {
            return this.accountServiceStub
              .withCallCredentials(bearerToken.map(IlpJwtCallCredentials::build).orElse(null))
              .createAccount(request);
        } catch (StatusRuntimeException e) {
            throw new XpringKitException("Unable to create an account. " + e.getStatus());
        }
    }

    @Override
    public GetAccountResponse getAccount(String accountId, String bearerToken) throws XpringKitException {
        try {
            return this.accountServiceStub
              .withCallCredentials(IlpJwtCallCredentials.build(bearerToken))
              .getAccount(GetAccountRequest.newBuilder()
                .setAccountId(accountId)
                .build());
        } catch (StatusRuntimeException e) {
            throw new XpringKitException("Unable to get account with id = " + accountId + ". " + e.getStatus());
        }
    }

    @Override
    public GetBalanceResponse getBalance(final String accountId, final String bearerToken) throws XpringKitException {
        GetBalanceRequest request = GetBalanceRequest.newBuilder()
          .setAccountId(accountId)
          .build();

        try {
            return this.balanceServiceStub
              .withCallCredentials(IlpJwtCallCredentials.build(bearerToken))
              .getBalance(request);
        } catch (StatusRuntimeException e) {
            throw new XpringKitException(String.format("Unable to get balance for account %s.  %s", accountId, e.getStatus()));
        }
    }

    @Override
    public SendPaymentResponse sendPayment(String destinationPaymentPointer,
                                           long amount,
                                           String accountId,
                                           String bearerToken) throws XpringKitException {
        try {
            SendPaymentRequest request = SendPaymentRequest.newBuilder()
              .setDestinationPaymentPointer(destinationPaymentPointer)
              .setAccountId(accountId)
              .setAmount(amount)
              .build();
            return ilpOverHttpServiceStub
              .withCallCredentials(IlpJwtCallCredentials.build(bearerToken))
              .sendMoney(request);
        } catch (StatusRuntimeException e) {
            throw new XpringKitException("Unable to send payment. " + e.getStatus());
        }
    }
}
