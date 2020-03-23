package io.xpring.payid;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.interledger.spsp.PaymentPointer;

import io.xpring.payid.generated.ApiClient;
import io.xpring.payid.generated.api.DefaultApi;
import io.xpring.payid.generated.model.PaymentInformation;
import io.xpring.payid.generated.ApiException;

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
        String path = paymentPointer.path().substring(1);

        // TODO(keefertaylor): Headers?

        try {
            PaymentInformation result = apiInstance.resolvePayID(path);
            return result.getAddressDetails().getAddress();
        } catch (ApiException e) {
            System.err.println("Exception when calling DefaultApi#getUserAndHost");
            e.printStackTrace();
            return null;
        }
    }
}