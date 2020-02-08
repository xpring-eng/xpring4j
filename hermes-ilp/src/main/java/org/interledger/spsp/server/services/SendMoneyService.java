package org.interledger.spsp.server.services;

import org.interledger.codecs.ilp.InterledgerCodecContextFactory;
import org.interledger.connector.accounts.AccountId;
import org.interledger.connector.accounts.AccountNotFoundProblem;
import org.interledger.connector.accounts.AccountSettings;
import org.interledger.connector.client.ConnectorAdminClient;
import org.interledger.core.InterledgerAddress;
import org.interledger.core.SharedSecret;
import org.interledger.link.LinkId;
import org.interledger.link.http.IlpOverHttpLink;
import org.interledger.spsp.PaymentPointer;
import org.interledger.spsp.StreamConnectionDetails;
import org.interledger.spsp.client.SpspClient;
import org.interledger.stream.Denomination;
import org.interledger.stream.SendMoneyRequest;
import org.interledger.stream.SendMoneyResult;
import org.interledger.stream.crypto.JavaxStreamEncryptionService;
import org.interledger.stream.sender.FixedSenderAmountPaymentTracker;
import org.interledger.stream.sender.SimpleStreamSender;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.primitives.UnsignedLong;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.PreDestroy;

public class SendMoneyService {

  public static final int SEND_TIMEOUT = 30;
  private final SpspClient spspClient;

  private final HttpUrl connectorUrl;

  private final ObjectMapper objectMapper;

  private final ConnectorAdminClient adminClient;
  private OkHttpClient okHttpClient;
  private final ExecutorService executorService;

  public SendMoneyService(HttpUrl connectorUrl, ObjectMapper objectMapper, ConnectorAdminClient adminClient, OkHttpClient okHttpClient, SpspClient spspClient) {
    this.connectorUrl = connectorUrl;
    this.objectMapper = objectMapper;
    this.adminClient = adminClient;
    this.okHttpClient = okHttpClient;
    this.executorService = Executors.newCachedThreadPool();
    this.spspClient = spspClient;
  }

  @PreDestroy
  private void destroy() {
    executorService.shutdown();
  }

  public SendMoneyResult sendMoney(AccountId senderAccountId,
                                   String bearerToken,
                                   UnsignedLong amount,
                                   PaymentPointer destination)
    throws ExecutionException, InterruptedException {
    // Fetch shared secret and destination address using SPSP client
    StreamConnectionDetails connectionDetails = spspClient.getStreamConnectionDetails(destination);

    AccountSettings senderAccountSettings = adminClient.findAccount(senderAccountId.value())
      .orElseThrow(() -> new AccountNotFoundProblem(senderAccountId));

    final InterledgerAddress senderAddress =
      InterledgerAddress.of("test.jc.money").with(senderAccountId.value());

    // Use ILP over HTTP for our underlying link
    IlpOverHttpLink link = newIlpOverHttpLink(senderAddress, senderAccountId, bearerToken);

    // Create SimpleStreamSender for sending STREAM payments
    SimpleStreamSender simpleStreamSender = new SimpleStreamSender(new JavaxStreamEncryptionService(),
      link,
      executorService);

    // Send payment using STREAM
    return simpleStreamSender.sendMoney(
        SendMoneyRequest.builder()
          .sourceAddress(senderAddress)
          .amount(amount)
          .denomination(Denomination.builder()
            .assetCode(senderAccountSettings.assetCode())
            .assetScale((short) senderAccountSettings.assetScale())
            .build())
          .destinationAddress(connectionDetails.destinationAddress())
          .timeout(Duration.ofSeconds(SEND_TIMEOUT))
          .paymentTracker(new FixedSenderAmountPaymentTracker(amount))
          .sharedSecret(SharedSecret.of(connectionDetails.sharedSecret().value()))
          .build()
      ).get();
  }

  private IlpOverHttpLink newIlpOverHttpLink(InterledgerAddress senderAddress, AccountId senderAccountId, String bearerToken) {
    HttpUrl ilpHttpUrl = new HttpUrl.Builder()
      .scheme(connectorUrl.scheme())
      .host(connectorUrl.host())
      .port(connectorUrl.port())
      .addPathSegment("accounts")
      .addPathSegment(senderAccountId.value())
      .addPathSegment("ilp")
      .build();

    IlpOverHttpLink link = new IlpOverHttpLink(
      () -> senderAddress,
      ilpHttpUrl,
      okHttpClient,
      objectMapper,
      InterledgerCodecContextFactory.oer(),
      () -> bearerToken
    );
    link.setLinkId(LinkId.of(senderAccountId.value()));
    return link;
  }
}
