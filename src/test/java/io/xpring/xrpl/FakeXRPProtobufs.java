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

    static String testIssuedCurrencyValue = "100";
    static String testInvalidIssuedCurrencyValue = "xrp"; // non-numeric

    /**
     * will use in future fake objects
     */
    /*
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

    // VALID OBJECTS ===============================================================

    // Currency proto
    static Currency currency = Currency.newBuilder()
                                        .setName(testCurrencyName)
                                        .setCode(testCurrencyCode)
                                        .build();

    // AccountAddress protos
    static AccountAddress accountAddress = AccountAddress.newBuilder()
                                                        .setAddress("r123")
                                                        .build();

    static AccountAddress accountAddress_issuer = AccountAddress.newBuilder()
                                                                .setAddress("r456")
                                                                .build();

    // PathElement proto
    static Payment.PathElement pathElement = Payment.PathElement.newBuilder()
                                                                .setAccount(accountAddress)
                                                                .setCurrency(currency)
                                                                .setIssuer(accountAddress_issuer)
                                                                .build();

    // Path protos
    static Payment.Path emptyPath = Payment.Path.newBuilder().build();

    static Payment.Path pathWithOneElement = Payment.Path.newBuilder()
                                                        .addElements(pathElement)
                                                        .build();

    static Payment.Path pathWithThreeElements = Payment.Path.newBuilder()
                                                            .addElements(pathElement)
                                                            .addElements(pathElement)
                                                            .addElements(pathElement)
                                                            .build();

    // IssuedCurrencyAmount protos
    static IssuedCurrencyAmount issuedCurrencyAmount = IssuedCurrencyAmount.newBuilder()
                                                                            .setCurrency(currency)
                                                                            .setIssuer(accountAddress)
                                                                            .setValue(testIssuedCurrencyValue)
                                                                            .build();


    // INVALID OBJECTS ===============================================================

    // Invalid IssuedCurrencyAmount proto
    static IssuedCurrencyAmount invalidIssuedCurrencyAmount = IssuedCurrencyAmount.newBuilder()
                                                                            .setCurrency(currency)
                                                                            .setIssuer(accountAddress)
                                                                            .setValue(testInvalidIssuedCurrencyValue)
                                                                            .build();

}