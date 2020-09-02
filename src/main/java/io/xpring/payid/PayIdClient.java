package io.xpring.payid;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.reflect.TypeToken;
import io.xpring.payid.generated.ApiClient;
import io.xpring.payid.generated.ApiException;
import io.xpring.payid.generated.ApiResponse;
import io.xpring.payid.generated.Pair;
import io.xpring.payid.generated.model.Address;
import io.xpring.payid.generated.model.CryptoAddressDetails;
import io.xpring.payid.generated.model.PaymentInformation;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implements interaction with PayID.
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public class PayIdClient {
  /**
   * The version of PayID.
   */
  private static final String PAY_ID_VERSION = "1.0";

  /**
   * Whether to enable SSL Verification.
   */
  private boolean enableSSLVerification;

  /**
   * Initialize a new PayID client.
   *  Note: Networks in this constructor take the form of an asset and an optional network (asset-network).
   *  For instance:
   *    - xrpl-testnet
   *    - xrpl-mainnet
   *    - eth-rinkeby
   *    - ach
   *  TODO: Link a canonical list at payid.org when available.
   */
  public PayIdClient() {
    this.enableSSLVerification = true;
  }

  /**
   * Set whether to enable or disable SSL verification.
   * Exposed for testing purposes.
   *
   * @param enableSSLVerification true if SSL should be enabled.
   */
  @VisibleForTesting
  public void setEnableSSLVerification(boolean enableSSLVerification) {
    this.enableSSLVerification = enableSSLVerification;
  }

  /**
   * Retrieve the crypto address associated with a PayID.
   *
   * @param payId The PayID to resolve.
   * @param network The network to resolve on.
   * @return A {@link CryptoAddressDetails} containing address information for the given PayID.
   * @throws PayIdException if the PayID is invalid or more than one address is returned.
   */
  public CryptoAddressDetails cryptoAddressForPayId(String payId, String network) throws PayIdException {
    List<Address> addresses = this.addressesForPayIdAndNetwork(payId, network);

    // With a specific network, exactly one address should be returned by a PayID lookup.
    if (addresses.size() == 1) {
      return addresses.get(0).getAddressDetails();
    } else {
      // With a specific network, exactly one address should be returned by a PayID lookup.
      throw new PayIdException(PayIdExceptionType.UNEXPECTED_RESPONSE,
              "Expected one address for " + payId + " on network " + network
                      + " but got " + addresses.size());
    }
  }

  /**
   * Retrieve all addresses associated with a PayID.
   *
   * @param payId The PayID to resolve.
   * @return a list of all {@link Address}es associated with the given PayID.
   * @throws PayIdException if the given PayID is invalid.
   */
  public List<Address> allAddressesForPayId(String payId) throws PayIdException {
    return this.addressesForPayIdAndNetwork(payId, "payid");
  }

  /**
   * Return a list of {@link Address}es for the given payId on the given network.
   *
   * @param payId The PayID to resolve.
   * @param network The network to resolve on.
   * @return a list of {@link Address}es for the given payId on the given network.
   */
  private List<Address> addressesForPayIdAndNetwork(String payId, String network) throws PayIdException {
    PayIdComponents paymentPointer = PayIdUtils.parsePayID(payId);
    if (paymentPointer == null) {
      throw PayIdException.invalidPaymentPointerException;
    }

    ApiClient apiClient = new ApiClient();
    apiClient.setBasePath("https://" + paymentPointer.host());
    apiClient.setVerifyingSsl(enableSSLVerification);

    String path = paymentPointer.path().substring(1);
    final String[] localVarAccepts = {
        "application/" + network + "+json"
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
      return result.getAddresses();
    } catch (ApiException exception) {
      int code = exception.getCode();
      if (code == 404) {
        throw new PayIdException(
                PayIdExceptionType.MAPPING_NOT_FOUND,
                "Could not resolve " + payId + " on network " + network
        );
      } else {
        throw new PayIdException(PayIdExceptionType.UNEXPECTED_RESPONSE, code + ": " + exception.getMessage());
      }
    }
  }
}
