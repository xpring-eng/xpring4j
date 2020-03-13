package io.xpring.payid;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.interledger.spsp.PaymentPointer;

import io.xpring.payid.api.DefaultApi;
import io.xpring.payid.model.InlineResponse200;

/**
 * Implements interaction with a PayID service.
 * @warning This class is experimental and should not be used in production applications.
 */
public class PayIDClient {
    /** The internal HTTP client which makes requests. */
    private OkHttpClient httpClient;

    /** A JSON Decoder. */
    private ObjectMapper jsonDecoder;

    /**
     * Initialize a new PayIDClient.
     */
    public PayIDClient() {
        this.httpClient = new OkHttpClient();
        this.jsonDecoder = new ObjectMapper();
    }

    /**
     * Resolve the given PayID to an XRP Address.
     *
     * @param payID The PayID to resolve.
     * @return
     */
    public String resolveToXRPAddress(String payID) throws PayIDException {
        PaymentPointer paymentPointer = PayIDUtils.parsePayID(payID);
        if (paymentPointer == null) {
            throw PayIDException.invalidPaymentPointerExpection;
        }

        ApiClient client = new ApiClient();
        client.setBasePath("https://" + paymentPointer.host());
        DefaultApi apiInstance = new DefaultApi(client);
        String userAndHost = paymentPointer.path().substring(1);
        try {
            InlineResponse200 result = apiInstance.getUserAndHost(userAndHost);
            return result.getAddress();
        } catch (ApiException e) {
            System.err.println("Exception when calling DefaultApi#getUserAndHost");
            e.printStackTrace();
            return null;
        }



//
//        String url = "https://" + paymentPointer.host() + paymentPointer.path();
//
//        // TODO(keefertaylor): Generalize to add different headers.
//        // TODO(keefertaylor): Add network field when spec is finalized.
//        Request request = new Request.Builder()
//                .addHeader("Accept", "application/json+xrp")
//                .url(url)
//                .build();
//
//        try (Response response = httpClient.newCall(request).execute()) {
//            PayIDResponse responseJSON = this.jsonDecoder.readValue(response.body().string(), PayIDResponse.class);
//            return responseJSON.address();
//        } catch (Exception e) {
//            throw new PayIDException("Unknown error " + e.getMessage());
//        }
    }
}
