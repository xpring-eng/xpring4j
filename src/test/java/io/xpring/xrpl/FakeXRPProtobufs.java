package io.xpring.xrpl;
import com.google.protobuf.ByteString;
import org.xrpl.rpc.v1.*;

import java.io.UnsupportedEncodingException;

/** Common set of fake objects - protobuf and native Java conversions - for testing */
public class FakeXRPProtobufs {
    // primitive test values
    static String testCurrencyName = "currencyName";
    static ByteString testCurrencyCode;

    static {
        try {
            testCurrencyCode = ByteString.copyFrom("123", "Utf8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    /** will use in future fake objects */
    /*
    static String testIssuedCurrencyValue = "100";
    static String testInvalidIssuedCurrencyValue = "xrp" // non-numeric;
    static String testAddress = "XVfC9CTCJh6GN2x8bnrw3LtdbqiVCUFyQVMzRrMGUZpokKH";
    static String testDestination = "XV5sbjUmgPpvXv4ixFWZ5ptAYZ6PD28Sq49uo34VyjnmK5H";

    static ByteString testPublicKey;
    static {
        try {
            testPublicKey = ByteString.copyFrom("123", "Utf8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    static ByteString testTransactionSignature;
    static {
        try {
            testTransactionSignature = ByteString.copyFrom("456", "Utf8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    static int testSequence = 1;
    static String testFee = "3";
    */

    // VALID OBJECTS ===============================================

    // Currency proto
    static Currency currency = Currency.newBuilder()
                                            .setName(testCurrencyName)
                                            .setCode(testCurrencyCode)
                                            .build();
}
