package io.xpring.xrpl;

import com.google.protobuf.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xrpl.rpc.v1.AccountAddress;
import org.xrpl.rpc.v1.CheckCash;
import org.xrpl.rpc.v1.Common;
import org.xrpl.rpc.v1.Common.Account;
import org.xrpl.rpc.v1.Common.AccountTransactionID;
import org.xrpl.rpc.v1.Common.Amount;
import org.xrpl.rpc.v1.Common.Date;
import org.xrpl.rpc.v1.Common.DeliverMin;
import org.xrpl.rpc.v1.Common.Destination;
import org.xrpl.rpc.v1.Common.DestinationTag;
import org.xrpl.rpc.v1.Common.Flags;
import org.xrpl.rpc.v1.Common.InvoiceID;
import org.xrpl.rpc.v1.Common.LastLedgerSequence;
import org.xrpl.rpc.v1.Common.MemoData;
import org.xrpl.rpc.v1.Common.MemoFormat;
import org.xrpl.rpc.v1.Common.MemoType;
import org.xrpl.rpc.v1.Common.SendMax;
import org.xrpl.rpc.v1.Common.Sequence;
import org.xrpl.rpc.v1.Common.SigningPublicKey;
import org.xrpl.rpc.v1.Common.SourceTag;
import org.xrpl.rpc.v1.Common.TransactionSignature;
import org.xrpl.rpc.v1.Currency;
import org.xrpl.rpc.v1.CurrencyAmount;
import org.xrpl.rpc.v1.GetAccountTransactionHistoryResponse;
import org.xrpl.rpc.v1.GetTransactionResponse;
import org.xrpl.rpc.v1.IssuedCurrencyAmount;
import org.xrpl.rpc.v1.Memo;
import org.xrpl.rpc.v1.Meta;
import org.xrpl.rpc.v1.Payment;
import org.xrpl.rpc.v1.Signer;
import org.xrpl.rpc.v1.Transaction;
import org.xrpl.rpc.v1.XRPDropsAmount;

import java.io.UnsupportedEncodingException;

/**
 * Common set of fake objects - protobuf and native Java conversions - for testing.
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public class FakeXRPProtobufs {
  private static final Logger logger = LoggerFactory.getLogger(FakeXRPProtobufs.class);

  // primitive test values
  static String testCurrencyName = "currencyName";
  static ByteString testCurrencyCode;

  static String fakeAddress1 = "r123";
  static String fakeAddress2 = "r456";

  static {
    try {
      testCurrencyCode = ByteString.copyFrom("123", "Utf8");
    } catch (UnsupportedEncodingException exception) {
      logger.error("Can't create testCurrencyCode", exception);
    }
  }

  static String testIssuedCurrencyValue = "100";
  static String testInvalidIssuedCurrencyValue = "xrp"; // non-numeric
  static long testDrops = 10;
  static long testDeliveredDrops = 20;
  static int testDestinationTag = 2;

  static ByteString memoDataBytes;
  static ByteString memoFormatBytes;
  static ByteString memoTypeBytes;

  static {
    try {
      memoDataBytes = ByteString.copyFrom("123", "Utf8");
    } catch (UnsupportedEncodingException exception) {
      logger.error("Can't create memoDataBytes", exception);
    }
  }

  static {
    try {
      memoFormatBytes = ByteString.copyFrom("456", "Utf8");
    } catch (UnsupportedEncodingException exception) {
      logger.error("Can't create memoFormatBytes", exception);
    }
  }

  static {
    try {
      memoTypeBytes = ByteString.copyFrom("789", "Utf8");
    } catch (UnsupportedEncodingException exception) {
      logger.error("Can't create memoTypeBytes", exception);
    }
  }

  static ByteString testSigningPublicKey;

  static {
    try {
      testSigningPublicKey = ByteString.copyFrom("123", "Utf8");
    } catch (UnsupportedEncodingException exception) {
      logger.error("Can't create testSigningPublicKey", exception);
    }
  }

  static ByteString testTransactionSignature;

  static {
    try {
      testTransactionSignature = ByteString.copyFrom("456", "Utf8");
    } catch (UnsupportedEncodingException exception) {
      logger.error("Can't create testTransactionSignature", exception);
    }
  }

  static ByteString testAccountTransactionID;

  static {
    try {
      testAccountTransactionID = ByteString.copyFrom("789", "Utf8");
    } catch (UnsupportedEncodingException exception) {
      exception.printStackTrace();
    }
  }

  static Integer testSequence = 1;
  static Integer testFlags = 4;
  static Integer testSourceTag = 6;
  static Integer testLastLedgerSequence = 5;
  static Integer testTimestamp = 0; // expected to convert to a Unix time of 946684800, the beginning of Ripple epoch
  static Integer expectedTimestamp = 946684800;

  static ByteString testTransactionHash;

  static {
    try {
      testTransactionHash = ByteString.copyFrom("faketransactionhash", "Utf8");
    } catch (UnsupportedEncodingException exception) {
      exception.printStackTrace();
    }
  }

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
  // XRPDropsAmount proto
  static XRPDropsAmount xrpDropsAmount = XRPDropsAmount.newBuilder().setDrops(testDrops).build();
  static CurrencyAmount dropsCurrencyAmount = CurrencyAmount.newBuilder().setXrpAmount(xrpDropsAmount).build();

  static CurrencyAmount issuedCurrencyCurrencyAmount = CurrencyAmount.newBuilder()
      .setIssuedCurrencyAmount(issuedCurrencyAmount)
      .build();

  // Payment protos
  // Amount
  static Amount amount = Amount.newBuilder().setValue(issuedCurrencyCurrencyAmount).build();

  // Destination
  static Destination destination = Destination.newBuilder().setValue(accountAddress).build();

  // DestinationTag
  static DestinationTag destinationTag = DestinationTag.newBuilder()
      .setValue(testDestinationTag).build();

  // DeliverMin
  static DeliverMin deliverMin = DeliverMin.newBuilder().setValue(dropsCurrencyAmount).build();

  // InvoiceID
  static InvoiceID invoiceID = InvoiceID.newBuilder().setValue(testCurrencyCode).build();

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
  static SendMax sendMax = SendMax.newBuilder().setValue(dropsCurrencyAmount).build();

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
  static MemoData memoData = MemoData.newBuilder()
      .setValue(memoDataBytes)
      .build();
  static MemoFormat memoFormat = MemoFormat.newBuilder()
      .setValue(memoFormatBytes)
      .build();
  static MemoType memoType = MemoType.newBuilder()
      .setValue(memoTypeBytes)
      .build();

  static Memo memoWithAllFieldsSet = Memo.newBuilder()
      .setMemoData(memoData)
      .setMemoFormat(memoFormat)
      .setMemoType(memoType)
      .build();

  // Signer protos
  // Account
  static Account account = Account.newBuilder().setValue(accountAddress).build();

  // SigningPublicKey
  static SigningPublicKey signingPublicKey = SigningPublicKey.newBuilder()
      .setValue(testSigningPublicKey)
      .build();

  // TransactionSignature
  static TransactionSignature transactionSignature = TransactionSignature.newBuilder()
      .setValue(testTransactionSignature)
      .build();

  static Signer signerWithAllFieldsSet = Signer.newBuilder()
      .setAccount(account)
      .setSigningPublicKey(signingPublicKey)
      .setTransactionSignature(transactionSignature)
      .build();

  // CheckCash proto
  static CheckCash checkCash = CheckCash.newBuilder().build();

  // Transaction protos
  // Common.Sequence proto
  static Sequence sequence = Sequence.newBuilder().setValue(testSequence).build();

  // Common.AccountTransactionID proto
  static AccountTransactionID accountTransactionID = AccountTransactionID.newBuilder()
      .setValue(testAccountTransactionID)
      .build();
  // Common.Flags proto
  static Flags flags = Flags.newBuilder().setValue(testFlags).build();

  // Common.LastLedgerSequence proto
  static LastLedgerSequence lastLedgerSequence = LastLedgerSequence.newBuilder()
      .setValue(testLastLedgerSequence)
      .build();

  // Common.SourceTag proto
  static SourceTag sourceTag = SourceTag.newBuilder().setValue(testSourceTag).build();

  static Transaction transactionWithAllFieldsSet = Transaction.newBuilder()
      .setAccount(account)
      .setAccountTransactionId(accountTransactionID)
      .setFee(xrpDropsAmount)
      .setFlags(flags)
      .setLastLedgerSequence(lastLedgerSequence)
      .addMemos(memoWithAllFieldsSet)
      .setSequence(sequence)
      .addSigners(signerWithAllFieldsSet)
      .setSigningPublicKey(signingPublicKey)
      .setSourceTag(sourceTag)
      .setTransactionSignature(transactionSignature)
      .setPayment(paymentWithAllFieldsSet)
      .build();

  static Transaction transactionWithOnlyMandatoryCommonFieldsSet = Transaction.newBuilder()
      .setAccount(account)
      .setFee(xrpDropsAmount)
      .setSequence(sequence)
      .setSigningPublicKey(signingPublicKey)
      .setTransactionSignature(transactionSignature)
      .setPayment(paymentWithAllFieldsSet)
      .build();

  static Transaction checkCashTransactionWithCommonFieldsSet = Transaction.newBuilder()
      .setAccount(account)
      .setFee(xrpDropsAmount)
      .setSequence(sequence)
      .setSigningPublicKey(signingPublicKey)
      .setTransactionSignature(transactionSignature)
      .setCheckCash(checkCash)
      .build();

  // Additional Transaction metadata protos for GetTransactionResponse protos
  // Date
  static Date dateProto = Date.newBuilder().setValue(testTimestamp).build();

  // Meta with DeliveredAmount
  static XRPDropsAmount deliveredXRPDropsAmount = XRPDropsAmount.newBuilder().setDrops(testDeliveredDrops).build();
  static CurrencyAmount deliveredAmountCurrencyAmount = CurrencyAmount.newBuilder()
                                                                      .setXrpAmount(deliveredXRPDropsAmount)
                                                                      .build();
  static Common.DeliveredAmount deliveredAmountProto = Common.DeliveredAmount.newBuilder()
                                                                              .setValue(deliveredAmountCurrencyAmount)
                                                                              .build();

  static Meta metaProto = Meta.newBuilder().setDeliveredAmount(deliveredAmountProto).build();

  // GetTransactionResponse protos
  static GetTransactionResponse getTransactionResponsePaymentAllFields = GetTransactionResponse.newBuilder()
                                                                  .setTransaction(transactionWithAllFieldsSet)
                                                                  .setDate(dateProto)
                                                                  .setMeta(metaProto)
                                                                  .setHash(testTransactionHash)
                                                                  .build();

  static GetTransactionResponse getTransactionResponsePaymentMandatoryFields =
                                                            GetTransactionResponse.newBuilder()
                                                            .setTransaction(transactionWithOnlyMandatoryCommonFieldsSet)
                                                            .setHash(testTransactionHash)
                                                            .build();

  static GetTransactionResponse getTransactionResponseCheckCash = GetTransactionResponse.newBuilder()
                                                                .setTransaction(checkCashTransactionWithCommonFieldsSet)
                                                                .setDate(dateProto)
                                                                .setHash(testTransactionHash)
                                                                .build();

  // GetAccountTransactionHistoryResponse protos
  static GetAccountTransactionHistoryResponse paymentOnlyGetAccountTransactionHistoryResponse =
      GetAccountTransactionHistoryResponse.newBuilder()
          .addTransactions(getTransactionResponsePaymentAllFields)
          .addTransactions(getTransactionResponsePaymentMandatoryFields)
          .build();

  static GetAccountTransactionHistoryResponse mixedGetAccountTransactionHistoryResponse =
      GetAccountTransactionHistoryResponse.newBuilder()
          .addTransactions(getTransactionResponsePaymentAllFields)
          .addTransactions(getTransactionResponsePaymentMandatoryFields)
          .addTransactions(getTransactionResponseCheckCash)
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
  static Amount invalidAmount = Amount.newBuilder().setValue(invalidCurrencyAmount).build();

  // Invalid DeliverMin proto
  static DeliverMin invalidDeliverMin = DeliverMin.newBuilder().setValue(invalidCurrencyAmount).build();

  // Invalid SendMax proto
  static SendMax invalidSendMax = SendMax.newBuilder().setValue(invalidCurrencyAmount).build();

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

  // Invalid Transaction with empty Payment
  static Transaction invalidTransactionWithEmptyPaymentFields = Transaction.newBuilder()
      .setAccount(account)
      .setFee(xrpDropsAmount)
      .setSequence(sequence)
      .setSigningPublicKey(signingPublicKey)
      .setTransactionSignature(transactionSignature)
      .setPayment(Payment.newBuilder().build()) // empty Payment
      .build();

  // Invalid Transaction due to unsupported transaction type
  static Transaction invalidTransactionUnsupportedType = Transaction.newBuilder()
      .setAccount(account)
      .setFee(xrpDropsAmount)
      .setSequence(sequence)
      .setSigningPublicKey(signingPublicKey)
      .setTransactionSignature(transactionSignature)
      .setCheckCash(checkCash) // unsupported
      .build();

  // Invalid GetTransactionResponse protos
  static GetTransactionResponse invalidGetTransactionResponseEmptyPaymentFields = GetTransactionResponse.newBuilder()
      .setTransaction(invalidTransactionWithEmptyPaymentFields)
      .build();

  static GetTransactionResponse invalidGetTransactionResponseUnsupportedTransactionType =
                                                              GetTransactionResponse.newBuilder()
                                                                      .setTransaction(invalidTransactionUnsupportedType)
                                                                      .build();
  // Invalid GetAccountTransactionHistoryResponse protos
  static GetAccountTransactionHistoryResponse invalidPaymentGetAccountTransactionHistoryResponse =
      GetAccountTransactionHistoryResponse.newBuilder()
          .addTransactions(invalidGetTransactionResponseEmptyPaymentFields)
          .addTransactions(getTransactionResponsePaymentAllFields)
          .build();
}
