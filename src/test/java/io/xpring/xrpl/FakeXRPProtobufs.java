package io.xpring.xrpl;
import com.google.protobuf.ByteString;
import org.xrpl.rpc.v1.*;

import java.io.UnsupportedEncodingException;

/** Common set of fake objects - protobuf and native Java conversions - for testing */
public class FakeXRPProtobufs {
    // primitive test values
    static String testCurrencyName = "currencyName";
    static ByteString testCurrencyCode;

    static String fakeAddress1 = "r123";
    static String fakeAddress2 = "r456";

    static {
        try {
            testCurrencyCode = ByteString.copyFrom("123", "Utf8");
        } catch (UnsupportedEncodingException error) {
            error.printStackTrace();
        }
    }

    static String testIssuedCurrencyValue = "100";
    static String testInvalidIssuedCurrencyValue = "xrp"; // non-numeric
    static long testDrops = 10;

    static int testDestinationTag = 2;

    static ByteString memoDataBytes;
    static ByteString memoFormatBytes;
    static ByteString memoTypeBytes;

    static {
        try {
            memoDataBytes = ByteString.copyFrom("123", "Utf8");
        } catch (UnsupportedEncodingException error) {
            error.printStackTrace();
        }
    }
    static {
        try {
            memoFormatBytes = ByteString.copyFrom("456", "Utf8");
        } catch (UnsupportedEncodingException error) {
            error.printStackTrace();
        }
    }
    static {
        try {
            memoTypeBytes = ByteString.copyFrom("789", "Utf8");
        } catch (UnsupportedEncodingException error) {
            error.printStackTrace();
        }
    }
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
                                                        .setAddress(fakeAddress1)
                                                        .build();

    static AccountAddress accountAddress_issuer = AccountAddress.newBuilder()
                                                                .setAddress(fakeAddress2)
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

    // CurrencyAmount protos
    static XRPDropsAmount xrpDropsAmount = XRPDropsAmount.newBuilder().setDrops(testDrops).build();
    static CurrencyAmount dropsCurrencyAmount = CurrencyAmount.newBuilder().setXrpAmount(xrpDropsAmount).build();

    static CurrencyAmount issuedCurrencyCurrencyAmount = CurrencyAmount.newBuilder()
                                                                        .setIssuedCurrencyAmount(issuedCurrencyAmount)
                                                                        .build();

    // Payment protos
        // Amount
    static Common.Amount amount = Common.Amount.newBuilder().setValue(issuedCurrencyCurrencyAmount).build();

        // Destination
    static Common.Destination destination = Common.Destination.newBuilder().setValue(accountAddress).build();

        // DestinationTag
    static Common.DestinationTag destinationTag = Common.DestinationTag.newBuilder()
                                                                        .setValue(testDestinationTag).build();

        // DeliverMin
    static Common.DeliverMin deliverMin = Common.DeliverMin.newBuilder().setValue(dropsCurrencyAmount).build();

        // InvoiceID
    static Common.InvoiceID invoiceID = Common.InvoiceID.newBuilder().setValue(testCurrencyCode).build();

        // Paths
    static AccountAddress accountAddress_456 = AccountAddress.newBuilder().setAddress("r456").build();
    static AccountAddress accountAddress_789 = AccountAddress.newBuilder().setAddress("r789").build();
    static AccountAddress accountAddress_abc = AccountAddress.newBuilder().setAddress("rabc").build();

    static Payment.PathElement pathElement_456 = Payment.PathElement.newBuilder()
                                                                    .setAccount(accountAddress_456).build();
    static Payment.PathElement pathElement_789 = Payment.PathElement.newBuilder()
                                                                    .setAccount(accountAddress_789).build();
    static Payment.PathElement pathElement_abc = Payment.PathElement.newBuilder()
                                                                    .setAccount(accountAddress_abc).build();


    static Payment.Path pathProtoOneElement = Payment.Path.newBuilder().addElements(pathElement_456).build();
    static Payment.Path pathProtoTwoElements = Payment.Path.newBuilder()
                                                            .addElements(pathElement_789)
                                                            .addElements(pathElement_abc)
                                                            .build();

        // SendMax
    static Common.SendMax sendMax = Common.SendMax.newBuilder().setValue(dropsCurrencyAmount).build();

        // finally, populate Payment
    static Payment paymentWithAllFieldsSet = Payment.newBuilder()
                                                    .setAmount(amount)
                                                    .setDestination(destination)
                                                    .setDestinationTag(destinationTag)
                                                    .setDeliverMin(deliverMin)
                                                    .setInvoiceId(invoiceID)
                                                    .addPaths(pathProtoOneElement)
                                                    .addPaths(pathProtoTwoElements)
                                                    .setSendMax(sendMax)
                                                    .build();

    static Payment paymentWithMandatoryFieldsSet = Payment.newBuilder()
                                                        .setAmount(amount)
                                                        .setDestination(destination)
                                                        .build();


    // Memo protos
    static Common.MemoData memoData = Common.MemoData.newBuilder()
                                                    .setValue(memoDataBytes)
                                                    .build();
    static Common.MemoFormat memoFormat = Common.MemoFormat.newBuilder()
                                                        .setValue(memoFormatBytes)
                                                        .build();
    static Common.MemoType memoType = Common.MemoType.newBuilder()
                                                    .setValue(memoTypeBytes)
                                                    .build();

    static Memo memoWithAllFieldsSet = Memo.newBuilder()
                                            .setMemoData(memoData)
                                            .setMemoFormat(memoFormat)
                                            .setMemoType(memoType)
                                            .build();

    // INVALID OBJECTS ===============================================================

    // Invalid IssuedCurrencyAmount proto
    static IssuedCurrencyAmount invalidIssuedCurrencyAmount = IssuedCurrencyAmount.newBuilder()
                                                                            .setCurrency(currency)
                                                                            .setIssuer(accountAddress)
                                                                            .setValue(testInvalidIssuedCurrencyValue)
                                                                            .build();

    // Invalid CurrencyAmount proto
    static CurrencyAmount invalidCurrencyAmount = CurrencyAmount.newBuilder()
                                                                .setIssuedCurrencyAmount(invalidIssuedCurrencyAmount)
                                                                .build();

    // Invalid Amount proto
    static Common.Amount invalidAmount = Common.Amount.newBuilder().setValue(invalidCurrencyAmount).build();

    // Invalid DeliverMin proto
    static Common.DeliverMin invalidDeliverMin = Common.DeliverMin.newBuilder().setValue(invalidCurrencyAmount).build();

    // Invalid SendMax proto
    static Common.SendMax invalidSendMax = Common.SendMax.newBuilder().setValue(invalidCurrencyAmount).build();

    // Invalid Payment protos
        // invalid amount field
    static Payment invalidPaymentBadAmount = Payment.newBuilder()
                                                    .setAmount(invalidAmount)
                                                    .setDestination(destination)
                                                    .build();

        // invalid deliverMin field
    static Payment invalidPaymentBadDeliverMin = Payment.newBuilder()
                                                        .setAmount(amount)
                                                        .setDestination(destination)
                                                        .setDeliverMin(invalidDeliverMin)
                                                        .build();

        // invalid sendMax field
    static Payment invalidPaymentBadSendMax = Payment.newBuilder()
                                                    .setAmount(amount)
                                                    .setDestination(destination)
                                                    .setSendMax(invalidSendMax)
                                                    .build();
}
