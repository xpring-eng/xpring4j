package io.xpring.payid;

/**
 * Represents a response from the Pay ID Service.
 */
// TODO(keefertaylor): Generalize this class when response format is decided.
public class PayIDResponse {
    /** The address represented by this response. */
    protected String address;

    /**
     * Create a new PayIDResponse.
     *
     * @param address The address.
     */
    public PayIDResponse(String address) {
        this.address = address;
    }
}