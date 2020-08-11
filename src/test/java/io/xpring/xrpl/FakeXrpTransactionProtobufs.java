package io.xpring.xrpl;

import com.google.protobuf.ByteString;
import org.xrpl.rpc.v1.AccountAddress;
import org.xrpl.rpc.v1.AccountDelete;
import org.xrpl.rpc.v1.AccountSet;
import org.xrpl.rpc.v1.Common;

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
}
