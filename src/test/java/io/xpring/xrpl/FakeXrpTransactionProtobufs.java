package io.xpring.xrpl;

import com.google.protobuf.ByteString;
import org.xrpl.rpc.v1.AccountAddress;
import org.xrpl.rpc.v1.AccountDelete;
import org.xrpl.rpc.v1.AccountSet;
import org.xrpl.rpc.v1.CheckCancel;
import org.xrpl.rpc.v1.CheckCash;
import org.xrpl.rpc.v1.CheckCreate;
import org.xrpl.rpc.v1.Common;
import org.xrpl.rpc.v1.CurrencyAmount;
import org.xrpl.rpc.v1.DepositPreauth;
import org.xrpl.rpc.v1.EscrowCancel;
import org.xrpl.rpc.v1.EscrowCreate;
import org.xrpl.rpc.v1.EscrowFinish;

import java.io.UnsupportedEncodingException;

/**
 * Common set of fake objects - protobuf and native Java conversions - for testing.
 */
public class FakeXrpTransactionProtobufs {
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

  // AccountDelete fake primitive test values
  public static String testDestination = "rPEPPER7kfTD9w2To4CQk6UCfuHM9c6GDY";
  public static Integer testDestinationTag = 13;

  // CheckCancel fake primitive test values
  public static ByteString testCheckId = ByteString.copyFromUtf8(
      "49647F0D748DC3FE26BDACBC57F251AADEFFF391403EC9BF87C97F67E9977FB0"
  );

  // CheckCreate fake primitive test values
  public static Integer testExpiration = 570113521;
  public static ByteString testInvoiceId = ByteString.copyFromUtf8(
      "6F1DFD1D0FE8A32E40E1F2C05CF1C15545BAB56B617F9C6C2D63A6B704BEF59B"
  );

  // EscrowCancel fake primitive test values
  public static Integer testOfferSequence = 23;

  // EscrowCreate fake primitive test values
  public static ByteString testCondition;

  static {
    try {
      testCondition = ByteString.copyFrom("condition", "Utf8");
    } catch (UnsupportedEncodingException exception) {
      exception.printStackTrace();
    }
  }

  // EscrowFinish fake primitive test values
  public static ByteString testFulfillment;

  static {
    try {
      testFulfillment = ByteString.copyFrom("fulfillment", "Utf8");
    } catch (UnsupportedEncodingException exception) {
      exception.printStackTrace();
    }
  }

  // AccountSet protos
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

  // Common.TickSize proto
  public static Common.TickSize tickSizeProto = Common.TickSize.newBuilder()
      .setValue(testTickSize)
      .build();

  // Common.TransferRate proto
  public static Common.TransferRate transferRateProto = Common.TransferRate.newBuilder()
      .setValue(testTransferRate)
      .build();

  public static AccountSet allFieldsAccountSet = AccountSet.newBuilder()
      .setClearFlag(clearFlagProto)
      .setDomain(domainProto)
      .setEmailHash(emailHashProto)
      .setMessageKey(messageKeyProto)
      .setSetFlag(setFlagProto)
      .setTickSize(tickSizeProto)
      .setTransferRate(transferRateProto)
      .build();

  public static AccountSet oneFieldAccountSet = AccountSet.newBuilder()
      .setClearFlag(clearFlagProto)
      .build();

  // AccountDelete protos
  public static AccountAddress accountAddressProto = AccountAddress.newBuilder()
      .setAddress(testDestination)
      .build();

  // Common.Destination proto
  public static Common.Destination destinationProto = Common.Destination.newBuilder()
      .setValue(accountAddressProto)
      .build();

  // Common.DestinationTag proto
  public static Common.DestinationTag destinationTagProto = Common.DestinationTag.newBuilder()
      .setValue(testDestinationTag)
      .build();

  public static AccountDelete allFieldsAccountDelete = AccountDelete.newBuilder()
      .setDestination(destinationProto)
      .setDestinationTag(destinationTagProto)
      .build();

  public static AccountDelete noDestinationTagAccountDelete = AccountDelete.newBuilder()
      .setDestination(destinationProto)
      .build();

  // CheckCancel protos
  public static Common.CheckID checkIdProto = Common.CheckID.newBuilder()
      .setValue(testCheckId)
      .build();

  public static CheckCancel checkCancelProto = CheckCancel.newBuilder()
      .setCheckId(checkIdProto)
      .build();

  public static CheckCancel invalidCheckCancelProto = CheckCancel.newBuilder().build();

  // CheckCash protos
  public static CheckCash checkCashProtoWithAmount = CheckCash.newBuilder()
      .setAmount(FakeXrpProtobufs.amount)
      .setCheckId(checkIdProto)
      .build();

  public static CheckCash checkCashProtoWithDeliverMin = CheckCash.newBuilder()
      .setCheckId(checkIdProto)
      .setDeliverMin(FakeXrpProtobufs.deliverMin)
      .build();

  // Invalid CheckCash proto (missing checkId)
  public static CheckCash invalidCheckCashMissingCheckIdProto = CheckCash.newBuilder()
      .setAmount(FakeXrpProtobufs.amount)
      .build();

  public static CurrencyAmount invalidCurrencyAmountWithNoFields = CurrencyAmount.newBuilder()
      .build();

  public static Common.Amount invalidEmptyAmountWithNoFields = Common.Amount.newBuilder()
      .setValue(invalidCurrencyAmountWithNoFields)
      .build();

  // Invalid CheckCash proto (invalid amount)
  public static CheckCash invalidCheckCashInvalidAmountProto = CheckCash.newBuilder()
      .setAmount(invalidEmptyAmountWithNoFields)
      .setCheckId(checkIdProto)
      .build();

  public static Common.DeliverMin invalidEmptyDeliverMinWithNoFields = Common.DeliverMin.newBuilder()
      .setValue(invalidCurrencyAmountWithNoFields)
      .build();

  // Invalid CheckCash proto (invalid deliverMin)
  public static CheckCash invalidCheckCashInvalidDeliverMinProto = CheckCash.newBuilder()
      .setCheckId(checkIdProto)
      .setDeliverMin(invalidEmptyDeliverMinWithNoFields)
      .build();

  // CheckCreate protos
  public static Common.Expiration expirationProto = Common.Expiration.newBuilder()
      .setValue(testExpiration)
      .build();

  public static CheckCreate allFieldsCheckCreateProto = CheckCreate.newBuilder()
      .setDestination(destinationProto)
      .setDestinationTag(destinationTagProto)
      .setExpiration(expirationProto)
      .setInvoiceId(FakeXrpProtobufs.invoiceID)
      .setSendMax(FakeXrpProtobufs.sendMax)
      .build();

  public static CheckCreate checkCreateProtoWithMandatoryFields = CheckCreate.newBuilder()
      .setDestination(destinationProto)
      .setSendMax(FakeXrpProtobufs.sendMax)
      .build();

  public static CheckCreate invalidCheckCreateProto = CheckCreate.newBuilder()
      .setSendMax(FakeXrpProtobufs.sendMax)
      .build();

  // DepositPreauth protos
  public static Common.Authorize authorizeProto = Common.Authorize.newBuilder()
      .setValue(FakeXrpProtobufs.accountAddress)
      .build();

  public static Common.Unauthorize unauthorizeProto = Common.Unauthorize.newBuilder()
      .setValue(FakeXrpProtobufs.accountAddress)
      .build();

  public static DepositPreauth depositPreauthWithAuthorize = DepositPreauth.newBuilder()
      .setAuthorize(authorizeProto)
      .build();

  public static DepositPreauth depositPreauthWithUnauthorize = DepositPreauth.newBuilder()
      .setUnauthorize(unauthorizeProto)
      .build();

  // EscrowCancel protos
  public static Common.Owner ownerProto = Common.Owner.newBuilder()
      .setValue(FakeXrpProtobufs.accountAddress)
      .build();

  public static Common.OfferSequence offerSequenceProto =  Common.OfferSequence.newBuilder()
      .setValue(testOfferSequence)
      .build();

  public static EscrowCancel escrowCancelProto = EscrowCancel.newBuilder()
      .setOfferSequence(offerSequenceProto)
      .setOwner(ownerProto)
      .build();

  public static EscrowCancel invalidEscrowCancelProtoMissingOwner = EscrowCancel.newBuilder()
      .setOfferSequence(offerSequenceProto)
      .build();

  public static EscrowCancel invalidEscrowCancelProtoMissingOfferSequence = EscrowCancel.newBuilder()
      .setOwner(ownerProto)
      .build();

  // EscrowCreate protos
  public static Common.CancelAfter cancelAfterProto = Common.CancelAfter.newBuilder()
      .setValue(testExpiration)
      .build();

  public static Common.Condition conditionProto = Common.Condition.newBuilder()
      .setValue(testCondition)
      .build();

  public static Common.FinishAfter finishAfterProto = Common.FinishAfter.newBuilder()
      .setValue(testExpiration)
      .build();

  public static EscrowCreate escrowCreateProtoWithRequiredFields = EscrowCreate.newBuilder()
      .setAmount(FakeXrpProtobufs.amount)
      .setDestination(destinationProto)
      .setDestinationTag(destinationTagProto)
      .build();

  public static EscrowCreate escrowCreateProtoWithAllFields = EscrowCreate
      .newBuilder(escrowCreateProtoWithRequiredFields)
      .setCancelAfter(cancelAfterProto)
      .setCondition(conditionProto)
      .setFinishAfter(finishAfterProto)
      .build();

  public static EscrowCreate invalidEscrowCreateMissingAmount = EscrowCreate.newBuilder()
      .setDestination(destinationProto)
      .setDestinationTag(destinationTagProto)
      .build();

  public static EscrowCreate invalidEscrowCreateMissingDestination = EscrowCreate.newBuilder()
      .setAmount(FakeXrpProtobufs.amount)
      .build();

  public static EscrowCreate invalidEscrowCreateInvalidAmount = EscrowCreate.newBuilder()
      .setAmount(invalidEmptyAmountWithNoFields)
      .setDestination(destinationProto)
      .setDestinationTag(destinationTagProto)
      .build();

  // EscrowFinish protos
  public static EscrowFinish escrowFinishProtoWithRequiredFields = EscrowFinish.newBuilder()
      .setOfferSequence(offerSequenceProto)
      .setOwner(ownerProto)
      .build();

  public static Common.Fulfillment fulfillmentProto = Common.Fulfillment.newBuilder()
      .setValue(testFulfillment)
      .build();

  public static EscrowFinish escrowFinishProtoWithAllFields = EscrowFinish.newBuilder(escrowFinishProtoWithRequiredFields)
      .setCondition(conditionProto)
      .setFulfillment(fulfillmentProto)
      .build();
}
