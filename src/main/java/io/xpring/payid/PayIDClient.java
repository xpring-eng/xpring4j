package io.xpring.payid;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.reflect.TypeToken;
import io.xpring.payid.generated.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.interledger.spsp.PaymentPointer;

import io.xpring.payid.generated.api.DefaultApi;
import io.xpring.payid.generated.model.PaymentInformation;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implements interaction with a PayID service.
 * Warning:  This class is experimental and should not be used in production applications.
 */
public class PayIDClient {
    /**
     * The network this PayID client resolves on.
     */
    private XRPLNetwork network;

    /**
     * Whether to disable SSL Verification.
     */
    private boolean disableSSLVerification;

    /**
     * Initialize a new PayIDClient.
     *
     * @param network The network that addresses will be resolved on.
     */
    public PayIDClient(XRPLNetwork network) {
        this.network = network;
        this.disableSSLVerification = false;
    }

    /**
     * Set whether to enable or disable SSL verification.
     *
     * Exposed for testing purposes.
     */
    public void setDisableSSLVerification(boolean disableSSLVerification) {
        this.disableSSLVerification = disableSSLVerification;
    }

    /**
     * Resolve the given PayID to an XRP Address.
     *
     * Note: The returned value will always be in an X-Address format.
     *
     * @param payID The payID to resolve for an address.
     * @return An XRP address representing the given PayID.
     */
    public String xrpAddressForPayID(String payID) throws PayIDException {
        PaymentPointer paymentPointer = PayIDUtils.parsePayID(payID);
        if (paymentPointer == null) {
            throw PayIDException.invalidPaymentPointerExpection;
        }

        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath("https://" + paymentPointer.host());
        apiClient.setVerifyingSsl(disableSSLVerification);

        String path = paymentPointer.path().substring(1);
        final String[] localVarAccepts = {
                "application/xrpl-" + this.network.getNetworkName() + "+json"
        };

        // NOTE: Swagger produces a higher level client that does not require this level of configuration,
        // however access to Accept headers is not available unless we access the underlying class.
        // TODO(keefertaylor): Factor this out to a helper function to make it clear which inputs matter.

        String localVarPath = "/{path}"
                .replaceAll("\\{" + "path" + "\\}", apiClient.escapeString(path.toString()));
        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        Map<String, String> localVarHeaderParams = new HashMap<String, String>();
        Map<String, Object> localVarFormParams = new HashMap<String, Object>();
        final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) localVarHeaderParams.put("Accept", localVarAccept);
        final String[] localVarContentTypes = {};
        final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
        localVarHeaderParams.put("Content-Type", localVarContentType);
        String[] localVarAuthNames = new String[] {};

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
            Type localVarReturnType = new TypeToken<PaymentInformation>(){}.getType();
            ApiResponse<PaymentInformation> response = apiClient.execute(call, localVarReturnType);
            PaymentInformation result = response.getData();
            return result.getAddressDetails().getAddress();
        } catch (ApiException exception) {
            int code = exception.getCode();
            if (code == 404) {
                throw new PayIDException(PayIDExceptionType.MAPPING_NOT_FOUND, "Could not resolve " + payID + " on network " + this.network.getNetworkName());
            } else {
                throw new PayIDException(PayIDExceptionType.UNEXPECTED_RESPONSE, code + ": " + exception.getMessage());
            }
        }
    }
}
