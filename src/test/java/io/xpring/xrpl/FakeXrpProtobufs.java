package io.xpring.xrpl;

import com.google.protobuf.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xrpl.rpc.v1.AccountAddress;
import org.xrpl.rpc.v1.AccountSet;
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
import org.xrpl.rpc.v1.GetTransaction;
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
public class FakeXrpProtobufs {
  private static final Logger logger = LoggerFactory.getLogger(FakeXrpProtobufs.class);

  // primitive test values
  public static String testCurrencyName = "currencyName";
  public static ByteString testCurrencyCode;

  public static String fakeAddress1 = "rsKouRxYLWGseFwXSAo57qXjcGiNqR55wr";
  public static String fakeAddress2 = "rPuNV4oA6f3SrKA4pLEpdVZW6QLvn3UJxK";

  static {
    try {
      testCurrencyCode = ByteString.copyFrom("123", "Utf8");
    } catch (UnsupportedEncodingException exception) {
      logger.error("Can't create testCurrencyCode", exception);
    }
  }

  public static String testIssuedCurrencyValue = "100";
  public static String testInvalidIssuedCurrencyValue = "xrp"; // non-numeric
  public static long testDrops = 10;
  public static long testDeliveredDrops = 20;
  public static int testDestinationTag = 2;

  public static ByteString memoDataBytes;
  public static ByteString memoFormatBytes;
  public static ByteString memoTypeBytes;

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

  public static ByteString testSigningPublicKey;

  static {
    try {
      testSigningPublicKey = ByteString.copyFrom("123", "Utf8");
    } catch (UnsupportedEncodingException exception) {
      logger.error("Can't create testSigningPublicKey", exception);
    }
  }

  public static ByteString testTransactionSignature;

  static {
    try {
      testTransactionSignature = ByteString.copyFrom("456", "Utf8");
    } catch (UnsupportedEncodingException exception) {
      logger.error("Can't create testTransactionSignature", exception);
    }
  }

  public static ByteString testAccountTransactionID;

  static {
    try {
      testAccountTransactionID = ByteString.copyFrom("789", "Utf8");
    } catch (UnsupportedEncodingException exception) {
      exception.printStackTrace();
    }
  }

  public static Integer testSequence = 1;
  public static Integer testFlags = 4;
  public static Integer testSourceTag = 6;
  public static Integer testLastLedgerSequence = 5;
  public static Integer testTimestamp = 0; // expected to convert to a Unix time of 946684800, beginning of Ripple epoch
  public static Long expectedTimestamp = 946684800L;

  public static ByteString testTransactionHash;

  static {
    try {
      testTransactionHash = ByteString.copyFrom("faketransactionhash", "Utf8");
    } catch (UnsupportedEncodingException exception) {
      exception.printStackTrace();
    }
  }

  static boolean testIsValidated = true;
  static int testLedgerIndex = 1000;

  // VALID OBJECTS ===============================================================

  // Currency proto
  public static Currency currency = Currency.newBuilder()
      .setName(testCurrencyName)
      .setCode(testCurrencyCode)
      .build();

  // AccountAddress protos
  public static AccountAddress accountAddress = AccountAddress.newBuilder()
      .setAddress(fakeAddress1)
      .build();

  public static AccountAddress accountAddress_issuer = AccountAddress.newBuilder()
      .setAddress(fakeAddress2)
      .build();

  // PathElement proto
  public static Payment.PathElement pathElement = Payment.PathElement.newBuilder()
      .setAccount(accountAddress)
      .setCurrency(currency)
      .setIssuer(accountAddress_issuer)
      .build();

  // Path protos
  public static Payment.Path emptyPath = Payment.Path.newBuilder().build();

  public static Payment.Path pathWithOneElement = Payment.Path.newBuilder()
      .addElements(pathElement)
      .build();

  public static Payment.Path pathWithThreeElements = Payment.Path.newBuilder()
      .addElements(pathElement)
      .addElements(pathElement)
      .addElements(pathElement)
      .build();

  // IssuedCurrencyAmount protos
  public static IssuedCurrencyAmount issuedCurrencyAmount = IssuedCurrencyAmount.newBuilder()
      .setCurrency(currency)
      .setIssuer(accountAddress)
      .setValue(testIssuedCurrencyValue)
      .build();

  // CurrencyAmount protos
  // XRPDropsAmount proto
  public static XRPDropsAmount xrpDropsAmount = XRPDropsAmount.newBuilder().setDrops(testDrops).build();
  public static CurrencyAmount dropsCurrencyAmount = CurrencyAmount.newBuilder().setXrpAmount(xrpDropsAmount).build();

  public static CurrencyAmount issuedCurrencyCurrencyAmount = CurrencyAmount.newBuilder()
      .setIssuedCurrencyAmount(issuedCurrencyAmount)
      .build();

  // Payment protos
  // Amount
  public static Amount amount = Amount.newBuilder().setValue(issuedCurrencyCurrencyAmount).build();

  // Destination
  public static Destination destination = Destination.newBuilder().setValue(accountAddress).build();

  // DestinationTag
  public static DestinationTag destinationTag = DestinationTag.newBuilder()
      .setValue(testDestinationTag).build();

  // DeliverMin
  public static DeliverMin deliverMin = DeliverMin.newBuilder().setValue(dropsCurrencyAmount).build();

  // InvoiceID
  public static InvoiceID invoiceID = InvoiceID.newBuilder().setValue(testCurrencyCode).build();

  // Paths
  public static AccountAddress accountAddress_456 = AccountAddress.newBuilder().setAddress("r456").build();
  public static AccountAddress accountAddress_789 = AccountAddress.newBuilder().setAddress("r789").build();
  public static AccountAddress accountAddress_abc = AccountAddress.newBuilder().setAddress("rabc").build();

  public static Payment.PathElement pathElement_456 = Payment.PathElement.newBuilder()
      .setAccount(accountAddress_456).build();
  public static Payment.PathElement pathElement_789 = Payment.PathElement.newBuilder()
      .setAccount(accountAddress_789).build();
  public static Payment.PathElement pathElement_abc = Payment.PathElement.newBuilder()
      .setAccount(accountAddress_abc).build();


  public static Payment.Path pathProtoOneElement = Payment.Path.newBuilder().addElements(pathElement_456).build();
  public static Payment.Path pathProtoTwoElements = Payment.Path.newBuilder()
      .addElements(pathElement_789)
      .addElements(pathElement_abc)
      .build();

  // SendMax
  public static SendMax sendMax = SendMax.newBuilder().setValue(dropsCurrencyAmount).build();

  // finally, populate Payment
  public static Payment paymentWithAllFieldsSet = Payment.newBuilder()
      .setAmount(amount)
      .setDestination(destination)
      .setDestinationTag(destinationTag)
      .setDeliverMin(deliverMin)
      .setInvoiceId(invoiceID)
      .addPaths(pathProtoOneElement)
      .addPaths(pathProtoTwoElements)
      .setSendMax(sendMax)
      .build();

  public static Payment paymentWithMandatoryFieldsSet = Payment.newBuilder()
      .setAmount(amount)
      .setDestination(destination)
      .build();

  // Memo protos
  public static MemoData memoData = MemoData.newBuilder()
      .setValue(memoDataBytes)
      .build();
  public static MemoFormat memoFormat = MemoFormat.newBuilder()
      .setValue(memoFormatBytes)
      .build();
  public static MemoType memoType = MemoType.newBuilder()
      .setValue(memoTypeBytes)
      .build();

  public static Memo memoWithAllFieldsSet = Memo.newBuilder()
      .setMemoData(memoData)
      .setMemoFormat(memoFormat)
      .setMemoType(memoType)
      .build();

  // Signer protos
  // Account
  public static Account account = Account.newBuilder().setValue(accountAddress).build();

  // SigningPublicKey
  public static SigningPublicKey signingPublicKey = SigningPublicKey.newBuilder()
      .setValue(testSigningPublicKey)
      .build();

  // TransactionSignature
  public static TransactionSignature transactionSignature = TransactionSignature.newBuilder()
      .setValue(testTransactionSignature)
      .build();

  public static Signer signerWithAllFieldsSet = Signer.newBuilder()
      .setAccount(account)
      .setSigningPublicKey(signingPublicKey)
      .setTransactionSignature(transactionSignature)
      .build();

  // CheckCash proto
  public static CheckCash checkCash = CheckCash.newBuilder().build();

  // Transaction protos
  // Common.Sequence proto
  public static Sequence sequence = Sequence.newBuilder().setValue(testSequence).build();

  // Common.AccountTransactionID proto
  public static AccountTransactionID accountTransactionID = AccountTransactionID.newBuilder()
      .setValue(testAccountTransactionID)
      .build();
  // Common.Flags proto
  public static Flags flags = Flags.newBuilder().setValue(testFlags).build();

  // Common.LastLedgerSequence proto
  public static LastLedgerSequence lastLedgerSequence = LastLedgerSequence.newBuilder()
      .setValue(testLastLedgerSequence)
      .build();

  // Common.SourceTag proto
  public static SourceTag sourceTag = SourceTag.newBuilder().setValue(testSourceTag).build();

  public static Transaction transactionWithAllFieldsSet = Transaction.newBuilder()
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

  public static Transaction transactionWithOnlyMandatoryCommonFieldsSet = Transaction.newBuilder()
      .setAccount(account)
      .setFee(xrpDropsAmount)
      .setSequence(sequence)
      .setSigningPublicKey(signingPublicKey)
      .setTransactionSignature(transactionSignature)
      .setPayment(paymentWithAllFieldsSet)
      .build();

  public static Transaction checkCashTransactionWithCommonFieldsSet = Transaction.newBuilder()
      .setAccount(account)
      .setFee(xrpDropsAmount)
      .setSequence(sequence)
      .setSigningPublicKey(signingPublicKey)
      .setTransactionSignature(transactionSignature)
      .setCheckCash(checkCash)
      .build();

  // Additional Transaction metadata protos for GetTransactionResponse protos
  // Date
  public static Date dateProto = Date.newBuilder().setValue(testTimestamp).build();

  // Meta with DeliveredAmount
  public static XRPDropsAmount deliveredXRPDropsAmount = XRPDropsAmount.newBuilder()
                                                                        .setDrops(testDeliveredDrops)
                                                                        .build();

  public static CurrencyAmount deliveredAmountXRPCurrencyAmount = CurrencyAmount.newBuilder()
                                                                      .setXrpAmount(deliveredXRPDropsAmount)
                                                                      .build();

  public static CurrencyAmount deliveredAmountIssuedCurrencyAmount = CurrencyAmount.newBuilder()
                                                                          .setIssuedCurrencyAmount(issuedCurrencyAmount)
                                                                          .build();

  public static Common.DeliveredAmount deliveredAmountXRPProto = Common.DeliveredAmount.newBuilder()
                                                                            .setValue(deliveredAmountXRPCurrencyAmount)
                                                                            .build();

  public static Common.DeliveredAmount deliveredAmountIssuedProto = Common.DeliveredAmount.newBuilder()
                                                                          .setValue(deliveredAmountIssuedCurrencyAmount)
                                                                          .build();

  public static Meta metaProtoXRP = Meta.newBuilder().setDeliveredAmount(deliveredAmountXRPProto).build();

  public static Meta metaProtoIssued = Meta.newBuilder().setDeliveredAmount(deliveredAmountIssuedProto).build();

  // GetTransactionResponse protos
  public static GetTransactionResponse getTransactionResponsePaymentAllFields = GetTransactionResponse.newBuilder()
                                                                  .setTransaction(transactionWithAllFieldsSet)
                                                                  .setDate(dateProto)
                                                                  .setMeta(metaProtoXRP)
                                                                  .setHash(testTransactionHash)
                                                                  .setValidated(testIsValidated)
                                                                  .setLedgerIndex(testLedgerIndex)
                                                                  .build();

  public static GetTransactionResponse getTransactionResponsePaymentMandatoryFields =
                                                            GetTransactionResponse.newBuilder()
                                                            .setTransaction(transactionWithOnlyMandatoryCommonFieldsSet)
                                                            .setHash(testTransactionHash)
                                                            .build();

  public static GetTransactionResponse getTransactionResponsePaymentXRP = GetTransactionResponse.newBuilder()
                                                                            .setTransaction(transactionWithAllFieldsSet)
                                                                            .setHash(testTransactionHash)
                                                                            .setMeta(metaProtoXRP)
                                                                            .build();

  public static GetTransactionResponse getTransactionResponsePaymentIssued = GetTransactionResponse.newBuilder()
                                                                            .setTransaction(transactionWithAllFieldsSet)
                                                                            .setHash(testTransactionHash)
                                                                            .setMeta(metaProtoIssued)
                                                                            .build();

  public static GetTransactionResponse getTransactionResponseCheckCash = GetTransactionResponse.newBuilder()
                                                                .setTransaction(checkCashTransactionWithCommonFieldsSet)
                                                                .setDate(dateProto)
                                                                .setHash(testTransactionHash)
                                                                .build();

  // GetAccountTransactionHistoryResponse protos
  public static GetAccountTransactionHistoryResponse paymentOnlyGetAccountTransactionHistoryResponse =
      GetAccountTransactionHistoryResponse.newBuilder()
          .addTransactions(getTransactionResponsePaymentAllFields)
          .addTransactions(getTransactionResponsePaymentMandatoryFields)
          .build();

  public static GetAccountTransactionHistoryResponse mixedGetAccountTransactionHistoryResponse =
      GetAccountTransactionHistoryResponse.newBuilder()
          .addTransactions(getTransactionResponsePaymentAllFields)
          .addTransactions(getTransactionResponsePaymentMandatoryFields)
          .addTransactions(getTransactionResponseCheckCash)
          .build();

  // AccountSet protos
  // AccountSet fake primitive test values
  public static Integer testClearFlag = 5;
  public static String testDomain = "testdomain";

  public static ByteString testEmailHash;
  static {
    try {
      testEmailHash = ByteString.copyFrom("emailhash", "Utf8");
    } catch (UnsupportedEncodingException exception) {
      exception.printStackTrace();
    }
  }

  public static ByteString testMessageKey;
  static {
    try {
      testMessageKey = ByteString.copyFrom("messagekey", "Utf8");
    } catch (UnsupportedEncodingException exception) {
      exception.printStackTrace();
    }
  }

  public static Integer testSetFlag = 4;
  public static Integer testTransferRate = 1234567890;
  public static Integer testTickSize = 7;

  // Common.ClearFlag proto
  public static Common.ClearFlag clearFlagProto = Common.ClearFlag.newBuilder()
    .setValue(testClearFlag)
    .build();

  // Common.Domain proto
  public static Common.Domain domainProto = Common.Domain.newBuilder()
    .setValue(testDomain)
    .build();

  // Common.EmailHash proto
  public static Common.EmailHash emailHashProto = Common.EmailHash.newBuilder()
    .setValue(testEmailHash)
    .build();

  // Common.MessageKey proto
  public static Common.MessageKey messageKeyProto = Common.MessageKey.newBuilder()
    .setValue(testMessageKey)
    .build();

  // Common.SetFlag proto
  public static Common.SetFlag setFlagProto = Common.SetFlag.newBuilder()
    .setValue(testSetFlag)
    .build();

  // Common.TransferRate proto
  public static Common.TransferRate transferRateProto = Common.TransferRate.newBuilder()
    .setValue(testTransferRate)
    .build();

  // Common.TickSize proto
  public static Common.TickSize tickSizeProto = Common.TickSize.newBuilder()
    .setValue(testTickSize)
    .build();

  public static AccountSet allFieldsAccountSet = AccountSet.newBuilder()
    .setClearFlag(clearFlagProto)
    .setDomain(domainProto)
    .setEmailHash(emailHashProto)
    .setMessageKey(messageKeyProto)
    .setSetFlag(setFlagProto)
    .setTransferRate(transferRateProto)
    .setTickSize(tickSizeProto)
    .build();

  public static AccountSet oneFieldAccountSet = AccountSet.newBuilder()
    .setClearFlag(clearFlagProto)
    .build();

  // INVALID OBJECTS ===============================================================

  // Invalid IssuedCurrencyAmount proto
  public static IssuedCurrencyAmount invalidIssuedCurrencyAmount = IssuedCurrencyAmount.newBuilder()
      .setCurrency(currency)
      .setIssuer(accountAddress)
      .setValue(testInvalidIssuedCurrencyValue)
      .build();

  // Invalid CurrencyAmount proto
  public static CurrencyAmount invalidCurrencyAmount = CurrencyAmount.newBuilder()
      .setIssuedCurrencyAmount(invalidIssuedCurrencyAmount)
      .build();

  // Invalid Amount proto
  public static Amount invalidAmount = Amount.newBuilder().setValue(invalidCurrencyAmount).build();

  // Invalid DeliverMin proto
  public static DeliverMin invalidDeliverMin = DeliverMin.newBuilder().setValue(invalidCurrencyAmount).build();

  // Invalid SendMax proto
  public static SendMax invalidSendMax = SendMax.newBuilder().setValue(invalidCurrencyAmount).build();

  // Invalid Payment protos
  // invalid amount field
  public static Payment invalidPaymentBadAmount = Payment.newBuilder()
      .setAmount(invalidAmount)
      .setDestination(destination)
      .build();

  // invalid deliverMin field
  public static Payment invalidPaymentBadDeliverMin = Payment.newBuilder()
      .setAmount(amount)
      .setDestination(destination)
      .setDeliverMin(invalidDeliverMin)
      .build();

  // invalid sendMax field
  public static Payment invalidPaymentBadSendMax = Payment.newBuilder()
      .setAmount(amount)
      .setDestination(destination)
      .setSendMax(invalidSendMax)
      .build();

  // Invalid Transaction with empty Payment
  public static Transaction invalidTransactionWithEmptyPaymentFields = Transaction.newBuilder()
      .setAccount(account)
      .setFee(xrpDropsAmount)
      .setSequence(sequence)
      .setSigningPublicKey(signingPublicKey)
      .setTransactionSignature(transactionSignature)
      .setPayment(Payment.newBuilder().build()) // empty Payment
      .build();

  // Invalid Transaction due to unsupported transaction type
  public static Transaction invalidTransactionUnsupportedType = Transaction.newBuilder()
      .setAccount(account)
      .setFee(xrpDropsAmount)
      .setSequence(sequence)
      .setSigningPublicKey(signingPublicKey)
      .setTransactionSignature(transactionSignature)
      .setCheckCash(checkCash) // unsupported
      .build();

  // Invalid GetTransactionResponse protos
  public static GetTransactionResponse invalidGetTransactionResponseEmptyPaymentFields = GetTransactionResponse
                                                              .newBuilder()
                                                              .setTransaction(invalidTransactionWithEmptyPaymentFields)
                                                              .build();

  public static GetTransactionResponse invalidGetTransactionResponseUnsupportedTransactionType =
                                                              GetTransactionResponse.newBuilder()
                                                                      .setTransaction(invalidTransactionUnsupportedType)
                                                                      .build();
  // Invalid GetAccountTransactionHistoryResponse protos
  public static GetAccountTransactionHistoryResponse invalidPaymentGetAccountTransactionHistoryResponse =
      GetAccountTransactionHistoryResponse.newBuilder()
          .addTransactions(invalidGetTransactionResponseEmptyPaymentFields)
          .addTransactions(getTransactionResponsePaymentAllFields)
          .build();
}
