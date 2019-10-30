package io.xpring.javascript;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

/** Provides JavaScript based Utils functionality. */
public class JavaScriptUtils {
    /**
     * An reference to the underlying JavaScript Utils object.
     */
    private Value javaScriptUtils;

    /**
     * @throws JavaScriptLoaderException If the underlying JavaScript was missing or malformed.
     */
    public JavaScriptUtils() throws JavaScriptLoaderException {
        Context context = JavaScriptLoader.getContext();
        Value javaScriptUtils = JavaScriptLoader.loadResource("Utils", context);

        this.javaScriptUtils = javaScriptUtils;
    }

    /**
     * Check if the given string is a valid address on the XRP Ledger.
     *
     * @param address: A string to validate
     * @return A boolean indicating whether this was a valid address.
     */
    public boolean isValidAddress(String address) {
        Value isValidAddressFunction = javaScriptUtils.getMember("isValidAddress");
        return isValidAddressFunction.execute(address).asBoolean();
    }

    /**
     * Encode the given classic address and tag into an x-address.
     *
     * @param classicAddress A classic address to encode.
     * @param tag            An optional tag to encode.
     * @return A new x-address if inputs were valid, otherwise undefined.
     * @see https://xrpaddress.info/
     */
    public String encodeXAddress(String classicAddress, Long tag) {
        Value encodeXAddressFunction = javaScriptUtils.getMember("encodeXAddress");
        Value result = tag != null ? encodeXAddressFunction.execute(classicAddress, tag) : encodeXAddressFunction.execute(classicAddress);
        return result.asString();
    }
}
