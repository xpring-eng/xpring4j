package io.xpring.xrpl.ilp;

import org.interledger.spsp.server.grpc.AccountServiceGrpc;
import org.interledger.spsp.server.grpc.BalanceServiceGrpc;
import org.interledger.spsp.server.grpc.CreateAccountRequest;
import org.interledger.spsp.server.grpc.CreateAccountResponse;
import org.interledger.spsp.server.grpc.GetBalanceRequest;
import org.interledger.spsp.server.grpc.GetBalanceResponse;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.xpring.xrpl.XpringKitException;
import io.xpring.xrpl.ilp.grpc.IlpJwtCallCredentials;
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

    public DefaultIlpClient(final ManagedChannel channel) {
        this.balanceServiceStub = BalanceServiceGrpc.newBlockingStub(channel);
        this.accountServiceStub = AccountServiceGrpc.newBlockingStub(channel);
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
    public CreateAccountResponse createAccount() throws XpringKitException {
        return this.createAccount(Optional.empty(), Optional.empty());
    }

    @Override
    public CreateAccountResponse createAccount(Optional<CreateAccountRequest> createAccountRequest, Optional<String> bearerToken) throws XpringKitException {
        return this.createAccount(createAccountRequest.orElse(null), bearerToken.orElse(null));
    }

    @Override
    public CreateAccountResponse createAccount(CreateAccountRequest createAccountRequest, String bearerToken) throws XpringKitException {
        try {
            return this.accountServiceStub
              .withCallCredentials(IlpJwtCallCredentials.build(bearerToken))
              .createAccount(createAccountRequest);
        } catch (StatusRuntimeException e) {
            throw new XpringKitException("Unable to create an account. " + e.getStatus());
        }
    }
}
