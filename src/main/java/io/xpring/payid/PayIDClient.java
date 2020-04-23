package io.xpring.payid;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.reflect.TypeToken;
import io.xpring.common.XRPLNetwork;
import io.xpring.payid.generated.ApiClient;
import io.xpring.payid.generated.ApiException;
import io.xpring.payid.generated.ApiResponse;
import io.xpring.payid.generated.Pair;
import io.xpring.payid.generated.model.PaymentInformation;
import okhttp3.HttpUrl;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implements interaction with a PayID service.
 * Warning:  This class is experimental and should not be used in production applications.
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public class PayIDClient implements PayIDClientInterface {
  /**
   * The network this PayID client resolves on.
   */
  private XRPLNetwork network;

  /**
   * Whether to enable SSL Verification.
   */
  private boolean enableSSLVerification;

  /**
   * Used to resolve PayIDs to URLs.
   */
  private PayIDResolver payIDResolver;

  /**
   * Initialize a new PayIDClient.
   *
   * @param network The network that addresses will be resolved on.
   */
  public PayIDClient(XRPLNetwork network) {
    this(network, new DefaultPayIDResolver());
  }

  /**
   * Initialize a new PayIDClient.
   *
   * @param network The network that addresses will be resolved on.
   * @param payIDResolver The resolver used to resolve PayIDs to URLs.
   */
  public PayIDClient(XRPLNetwork network, PayIDResolver payIDResolver) {
    this.network = network;
    this.enableSSLVerification = true;
    this.payIDResolver = payIDResolver;
  }

  /**
   * Retrieve the network that addresses will be resolved on.
   *
   * @return The {@link XRPLNetwork} of this {@link PayIDClient}
   */
  public XRPLNetwork getNetwork() {
    return this.network;
  }

  /**
   * Set whether to enable or disable SSL verification.
   * <p>
   * Exposed for testing purposes.
   * </p>
   *
   * @param enableSSLVerification true if SSL should be enabled.
   */
  @VisibleForTesting
  public void setEnableSSLVerification(boolean enableSSLVerification) {
    this.enableSSLVerification = enableSSLVerification;
  }

  public String xrpAddressForPayID(PayID payID) throws PayIDException {
    HttpUrl payIDUrl = payIDResolver.resolvePayIDUrl(payID);

    ApiClient apiClient = new ApiClient();

    apiClient.setBasePath(payIDUrl.scheme() + "://" + payIDUrl.host());
    apiClient.setVerifyingSsl(enableSSLVerification);

//    String path = paymentPointer.path().substring(1);
    final String[] localVarAccepts = {
      "application/xrpl-" + this.network.getNetworkName() + "+json"
    };

    // NOTE: Swagger produces a higher level client that does not require this level of configuration,
    // however access to Accept headers is not available unless we access the underlying class.
    // TODO(keefertaylor): Factor this out to a helper function to make it clear which inputs matter.

//    String localVarPath = "/" + apiClient.escapeString(path.toString());
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    payIDUrl.queryParameterNames()
      .forEach(name ->
        localVarQueryParams.add(new Pair(name, payIDUrl.queryParameter(name)))
      );

    List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    if (localVarAccept != null) {
      localVarHeaderParams.put("Accept", localVarAccept);
    }
    final String[] localVarContentTypes = {};
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    localVarHeaderParams.put("Content-Type", localVarContentType);
    String[] localVarAuthNames = new String[]{};

    try {
      com.squareup.okhttp.Call call = apiClient.buildCall(
        payIDUrl.encodedPath(),
        "GET",
        localVarQueryParams,
        localVarCollectionQueryParams,
        null,
        localVarHeaderParams,
        localVarFormParams,
        localVarAuthNames,
        null
      );
      Type localVarReturnType = new TypeToken<PaymentInformation>() {
      }.getType();
      ApiResponse<PaymentInformation> response = apiClient.execute(call, localVarReturnType);
      PaymentInformation result = response.getData();
      return result.getAddressDetails().getAddress();
    } catch (ApiException exception) {
      int code = exception.getCode();
      if (code == 404) {
        throw new PayIDException(
          PayIDExceptionType.MAPPING_NOT_FOUND,
          "Could not resolve " + payID + " on network " + this.network.getNetworkName()
        );
      } else {
        throw new PayIDException(PayIDExceptionType.UNEXPECTED_RESPONSE, code + ": " + exception.getMessage());
      }
    }
  }

  /**
   * Resolve the given PayID to an XRP Address.
   * <p>
   * Note: The returned value will always be in an X-Address format.
   * </p>
   *
   * @param payID The payID to resolve for an address.
   * @return An XRP address representing the given PayID.
   */
  public String xrpAddressForPayID(String payID) throws PayIDException {
    try {
      PayID typedPayID = PayID.of(payID);
      return this.xrpAddressForPayID(typedPayID);
    } catch (NullPointerException | IllegalArgumentException e) {
      throw PayIDException.invalidPayIDException;
    }
  }
}
