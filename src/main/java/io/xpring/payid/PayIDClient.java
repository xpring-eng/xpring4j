package io.xpring.payid;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.interledger.spsp.PaymentPointer;

/**
 * Implements interaction with a PayID service.
 * @warning This class is experimental and should not be used in production applications.
 */
public class PayIDClient {
    /** The internal HTTP client which makes requests. */
    private OkHttpClient httpClient;

    /** A JSON Decoder. */
    private Gson jsonDecoder;

    /**
     * Initialize a new PayIDClient.
     */
    public PayIDClient() {
        this.httpClient = new OkHttpClient();
        this.jsonDecoder = new Gson();
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

        String url = "https://" + paymentPointer.host() + paymentPointer.path();

        // TODO(keefertaylor): Generalize to add different headers.
        // TODO(keefertaylor): Add network field when spec is finalized.
        Request request = new Request.Builder()
                .addHeader("Accept", "application/json+xrp")
                .url(url)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            PayIDResponse responseJSON = this.jsonDecoder.fromJson(response.body().string(), PayIDResponse.class);
            return responseJSON.address;
        } catch (Exception e) {
            throw new PayIDException("Unable to connect to server.");
        }
    }
}
