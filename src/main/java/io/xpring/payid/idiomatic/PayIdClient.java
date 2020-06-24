package io.xpring.payid.idiomatic;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.reflect.TypeToken;
import io.xpring.common.idiomatic.XrplNetwork;
import io.xpring.payid.generated.ApiClient;
import io.xpring.payid.generated.ApiException;
import io.xpring.payid.generated.ApiResponse;
import io.xpring.payid.generated.Pair;
import io.xpring.payid.generated.model.CryptoAddressDetails;
import io.xpring.payid.generated.model.PaymentInformation;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implements interaction with a PayID service.
 * Warning:  This class is experimental and should not be used in production applications.
 */
public class PayIdClient {
  /**
   * The version of PayID.
   */
  private static final String PAY_ID_VERSION = "1.0";
  
  /**
   * The network this PayID client resolves on.
   */
  private String network;

  /**
   * Whether to enable SSL Verification.
   */
  private boolean enableSslVerification;

  /**
   * Initialize a new PayID client.
   *  Note: Networks in this constructor take the form of an asset and an optional network (asset-network).
   *  For instance:
   *    - xrpl-testnet
   *    - xrpl-mainnet
   *    - eth-rinkeby
   *    - ach
   *  TODO: Link a canonical list at payid.org when available.
   *
   * @param network The network that addresses will be resolved on.
   */
  public PayIdClient(String network) {
    this.network = network;
    this.enableSslVerification = true;
  }

  /**
   * Retrieve the network that addresses will be resolved on.
   *
   * @return The {@link XrplNetwork} of this {@link PayIdClient}
   */
  public String getNetwork() {
    return this.network;
  }

  /**
   * Set whether to enable or disable SSL verification.
   * Exposed for testing purposes.
   *
   * @param enableSslVerification true if SSL should be enabled.
   */
  @VisibleForTesting
  public void setEnableSslVerification(boolean enableSslVerification) {
    this.enableSslVerification = enableSslVerification;
  }

  /**
   * Resolve the given PayID to an address.
   *
   * @param payId The PayID to resolve for an address.
   * @return A CryptoAddressDetails that contains an address representing the given PayID.
   */
  public CryptoAddressDetails addressForPayId(String payId) throws PayIdException {
    PayIdComponents paymentPointer = PayIdUtils.parsePayID(payId);
    if (paymentPointer == null) {
      throw PayIdException.invalidPaymentPointerException;
    }

    ApiClient apiClient = new ApiClient();
    apiClient.setBasePath("https://" + paymentPointer.host());
    apiClient.setVerifyingSsl(enableSslVerification);

    String path = paymentPointer.path().substring(1);
    final String[] localVarAccepts = {
        "application/" + this.network + "+json"
    };

    // NOTE: Swagger produces a higher level client that does not require this level of configuration,
    // however access to Accept headers is not available unless we access the underlying class.
    // TODO(keefertaylor): Factor this out to a helper function to make it clear which inputs matter.

    String localVarPath = "/" + apiClient.escapeString(path.toString());
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
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
    localVarHeaderParams.put("PayID-Version", PayIdClient.PAY_ID_VERSION);

    String[] localVarAuthNames = new String[]{};

    try {
      com.squareup.okhttp.Call call = apiClient.buildCall(
          localVarPath,
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
      if (result.getAddresses().size() == 1 ) {
        return result.getAddresses().get(0).getAddressDetails();
      } else {
        // With a specific network, exactly one address should be returned by a PayId lookup.
        throw new PayIdException(PayIdExceptionType.UNEXPECTED_RESPONSE,
                "Expected one address for " + payId + " on network " + this.network
                + " but got " + result.getAddresses().size());
      }
    } catch (ApiException exception) {
      int code = exception.getCode();
      if (code == 404) {
        throw new PayIdException(
            PayIdExceptionType.MAPPING_NOT_FOUND,
            "Could not resolve " + payId + " on network " + this.network
        );
      } else {
        throw new PayIdException(PayIdExceptionType.UNEXPECTED_RESPONSE, code + ": " + exception.getMessage());
      }
    }
  }
}
