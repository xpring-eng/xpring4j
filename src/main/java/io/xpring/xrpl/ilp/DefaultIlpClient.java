package io.xpring.xrpl.ilp;

import org.interledger.spsp.server.grpc.GetBalanceRequest;
import org.interledger.spsp.server.grpc.GetBalanceResponse;
import org.interledger.stream.proto.BalanceServiceGrpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.xpring.xrpl.XpringKitException;
import io.xpring.xrpl.ilp.grpc.IlpJwtCallCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
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
            XPRING_ILP_GRPC_URL = "https://localhost:6565";
        }
    };

    private final BalanceServiceGrpc.BalanceServiceBlockingStub stub;

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
        this.stub = BalanceServiceGrpc.newBlockingStub(channel);
    }

    /**
     * Get the balance of the specified account on the connector.
     *
     * @param accountId The account ID to get the balance for.
     * @param bearerToken Authentication bearer token.
     * @return A {@link BigInteger} with the number of drops in this account.
     * @throws XpringKitException If the given inputs were invalid.
     */
    public GetBalanceResponse getBalance(final String accountId, final String bearerToken) throws XpringKitException {
        GetBalanceRequest request = GetBalanceRequest.newBuilder()
          .setAccountId(accountId)
          .build();

        return this.stub
          .withCallCredentials(IlpJwtCallCredentials.build(bearerToken))
          .getBalance(request);
    }
}
