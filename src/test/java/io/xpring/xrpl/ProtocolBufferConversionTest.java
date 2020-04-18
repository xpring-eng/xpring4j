package io.xpring.xrpl;

import io.xpring.xrpl.model.XRPCurrency;
import io.xpring.xrpl.model.XRPCurrencyAmount;
import io.xpring.xrpl.model.XRPIssuedCurrency;
import io.xpring.xrpl.model.XRPMemo;
import io.xpring.xrpl.model.XRPPath;
import io.xpring.xrpl.model.XRPPathElement;
import io.xpring.xrpl.model.XRPPayment;
import io.xpring.xrpl.model.XRPSigner;
import io.xpring.xrpl.model.XRPTransaction;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.xrpl.rpc.v1.Common.Flags;
import org.xrpl.rpc.v1.Common.LastLedgerSequence;
import org.xrpl.rpc.v1.Common.SourceTag;
import org.xrpl.rpc.v1.Currency;
import org.xrpl.rpc.v1.CurrencyAmount;
import org.xrpl.rpc.v1.Memo;
import org.xrpl.rpc.v1.Payment;
import org.xrpl.rpc.v1.Payment.PathElement;
import org.xrpl.rpc.v1.Signer;
import org.xrpl.rpc.v1.Transaction;

import java.math.BigInteger;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class ProtocolBufferConversionTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  // Currency

  @Test
  public void convertCurrencyTest() {
    // GIVEN a Currency protocol buffer with a code and a name.
    Currency currencyProto = FakeXRPProtobufs.currency;

    // WHEN the protocol buffer is converted to a native Java type.
    XRPCurrency xrpCurrency = XRPCurrency.from(currencyProto);

    // THEN the currency converted as expected.
    assertThat(xrpCurrency.name()).isEqualTo(currencyProto.getName());
    assertThat(xrpCurrency.code()).isEqualTo(currencyProto.getCode().toByteArray());
  }

  // PathElement

  @Test
  public void convertPathElementTest() {
    // GIVEN a PathElement protocol buffer with all fields set.
    PathElement testPathElementProto = FakeXRPProtobufs.pathElement;

    // WHEN the protocol buffer is converted to a native Java type.
    XRPPathElement xrpPathElement = XRPPathElement.from(testPathElementProto);

    // THEN the currency converted as expected.
    assertThat(xrpPathElement.account().get()).isEqualTo(testPathElementProto.getAccount().getAddress());
    assertThat(xrpPathElement.currency().get()).isEqualTo(XRPCurrency.from(testPathElementProto.getCurrency()));
    assertThat(xrpPathElement.issuer().get()).isEqualTo(testPathElementProto.getIssuer().getAddress());
  }

  @Test
  public void convertPathElementWithNoFieldsTest() {
    // GIVEN a PathElement protocol buffer with no fields set.
    PathElement emptyPathElementProto = PathElement.newBuilder().build();

    // WHEN the protocol buffer is converted to a native Java type.
    XRPPathElement xrpPathElement = XRPPathElement.from(emptyPathElementProto);

    // THEN the currency converted as expected.
    assertThat(xrpPathElement.account().isPresent()).isFalse();
    assertThat(xrpPathElement.currency().isPresent()).isFalse();
    assertThat(xrpPathElement.issuer().isPresent()).isFalse();
  }

  // Path

  @Test
  public void convertPathWithNoPathsTest() {
    // GIVEN a set of paths with zero path elements.
    // WHEN the protocol buffer is converted to a native Java type.
    XRPPath xrpPath = XRPPath.from(FakeXRPProtobufs.emptyPath);

    // Then there are zero paths in the output.
    assertThat(xrpPath.pathElements().size()).isZero();
  }

  @Test
  public void convertPathWithOnePathTest() {
    // GIVEN a set of paths with one path element.
    // WHEN the protocol buffer is converted to a native Java type.
    XRPPath xrpPath = XRPPath.from(FakeXRPProtobufs.pathWithOneElement);

    // THEN there is one path in the output.
    assertThat(xrpPath.pathElements().size()).isOne();
  }

  @Test
  public void convertPathWithManyPathsTest() {
    // GIVEN a set of paths with three path elements.
    // WHEN the protocol buffer is converted to a native Java type.
    XRPPath xrpPath = XRPPath.from(FakeXRPProtobufs.pathWithThreeElements);

    // THEN there are three paths in the output.
    assertThat(xrpPath.pathElements().size()).isEqualTo(3);
  }

  // IssuedCurrency

  @Test
  public void convertIssuedCurrencyTest() {
    // GIVEN an issued currency protocol buffer,
    // WHEN the protocol buffer is converted to a native Java type.
    XRPIssuedCurrency xrpIssuedCurrency = XRPIssuedCurrency.from(FakeXRPProtobufs.issuedCurrencyAmount);

    // THEN the issued currency converted as expected.
    assertThat(xrpIssuedCurrency.currency())
        .isEqualTo(XRPCurrency.from(FakeXRPProtobufs.issuedCurrencyAmount.getCurrency()));

    assertThat(xrpIssuedCurrency.issuer())
        .isEqualTo(FakeXRPProtobufs.issuedCurrencyAmount.getIssuer().getAddress());

    assertThat(xrpIssuedCurrency.value()).isEqualTo(new BigInteger(FakeXRPProtobufs.issuedCurrencyAmount.getValue()));
  }

  @Test
  public void convertIssuedCurrencyWithBadValueTest() {
    // GIVEN an issued currency protocol buffer with a non numeric value
    // WHEN the protocol buffer is converted to a native Java type.
    // THEN a NumberFormatException is thrown.
    expectedException.expect(NumberFormatException.class);
    XRPIssuedCurrency.from(FakeXRPProtobufs.invalidIssuedCurrencyAmount);
  }

  // CurrencyAmount

  @Test
  public void convertCurrencyAmountWithDropsTest() {
    // GIVEN a currency amount protocol buffer with an XRP amount.
    // WHEN the protocol buffer is converted to a native Java type.
    XRPCurrencyAmount xrpCurrencyAmount = XRPCurrencyAmount.from(FakeXRPProtobufs.dropsCurrencyAmount);

    // THEN the result has drops set and an empty issued currency.
    assertThat(xrpCurrencyAmount.drops().get())
        .isEqualTo(Long.toString(FakeXRPProtobufs.dropsCurrencyAmount.getXrpAmount().getDrops()));
    assertThat(xrpCurrencyAmount.issuedCurrency().isPresent()).isFalse();
  }

  @Test
  public void convertCurrencyAmountWithIssuedCurrencyTest() {
    // GIVEN a currency amount protocol buffer with an issued currency amount.
    // WHEN the protocol buffer is converted to a native Java type.
    XRPCurrencyAmount xrpCurrencyAmount = XRPCurrencyAmount.from(FakeXRPProtobufs.issuedCurrencyCurrencyAmount);

    // THEN the result has an issued currency set and no drops amount.
    assertThat(xrpCurrencyAmount.drops()).isNull();
    assertThat(xrpCurrencyAmount.issuedCurrency())
        .isEqualTo(XRPIssuedCurrency.from(FakeXRPProtobufs.issuedCurrencyCurrencyAmount.getIssuedCurrencyAmount()));
  }

  @Test
  public void convertCurrencyAmountWithBadInputsTest() {
    // GIVEN a currency amount protocol buffer with no amounts.
    CurrencyAmount emptyCurrencyAmount = CurrencyAmount.newBuilder().build();

    // WHEN the protocol buffer is converted to a native Java type.
    XRPCurrencyAmount xrpCurrencyAmount = XRPCurrencyAmount.from(emptyCurrencyAmount);

    // THEN the result is null.
    assertThat(xrpCurrencyAmount).isNull();
  }

  @Test
  public void convertCurrencyAmountWithInvalidIssuedCurrencyTest() {
    // GIVEN a currency amount protocol buffer with an invalid issued currency.
    // WHEN the protocol buffer is converted to a native Java type.
    // THEN a NumberFormatException is re-thrown.
    expectedException.expect(NumberFormatException.class);
    XRPCurrencyAmount.from(FakeXRPProtobufs.invalidCurrencyAmount);
  }

  // Payment

  @Test
  public void convertPaymentWithAllFieldsSetTest() {
    // GIVEN a payment protocol buffer with all fields set.
    Payment paymentProto = FakeXRPProtobufs.paymentWithAllFieldsSet;

    // WHEN the protocol buffer is converted to a native Java type.
    XRPPayment xrpPayment = XRPPayment.from(paymentProto);

    // THEN the result is as expected.
    assertThat(xrpPayment.amount()).isEqualTo(XRPCurrencyAmount.from(paymentProto.getAmount().getValue()));
    assertThat(xrpPayment.destination()).isEqualTo(paymentProto.getDestination().getValue().getAddress());
    assertThat(xrpPayment.destinationTag()).isEqualTo(paymentProto.getDestinationTag().getValue());
    assertThat(xrpPayment.deliverMin()).isEqualTo(XRPCurrencyAmount.from(paymentProto.getDeliverMin().getValue()));
    assertThat(xrpPayment.invoiceID()).isEqualTo(paymentProto.getInvoiceId().getValue().toByteArray());
    assertThat(xrpPayment.paths())
        .isEqualTo(paymentProto.getPathsList()
            .stream()
            .map(path -> XRPPath.from(path))
            .collect(Collectors.toList()));
    assertThat(xrpPayment.sendMax()).isEqualTo(XRPCurrencyAmount.from(paymentProto.getSendMax().getValue()));
  }

  @Test
  public void convertPaymentWithMandatoryFieldsSetTest() {
    // GIVEN a payment protocol buffer with only mandatory fields set.
    Payment paymentProto = FakeXRPProtobufs.paymentWithMandatoryFieldsSet;

    // WHEN the protocol buffer is converted to a native Java type.
    XRPPayment xrpPayment = XRPPayment.from(paymentProto);

    // THEN the result is as expected.
    assertThat(xrpPayment.amount()).isEqualTo(XRPCurrencyAmount.from(paymentProto.getAmount().getValue()));
    assertThat(xrpPayment.destination()).isEqualTo(paymentProto.getDestination().getValue().getAddress());
    assertThat(xrpPayment.destinationTag()).isNull();
    assertThat(xrpPayment.destinationTag()).isNull();
    assertThat(xrpPayment.deliverMin()).isNull();
    assertThat(xrpPayment.invoiceID()).isNull();
    assertThat(xrpPayment.paths()).isNull();
    assertThat(xrpPayment.sendMax()).isNull();
  }

  @Test
  public void convertPaymentWithBadAmountFieldTest() {
    // GIVEN a payment protocol buffer with an invalid amount field.
    // WHEN the protocol buffer is converted to a native Java type.
    // THEN a NumberFormatException is re-thrown.
    expectedException.expect(NumberFormatException.class);
    XRPPayment.from(FakeXRPProtobufs.invalidPaymentBadAmount);
  }

  @Test
  public void convertPaymentWithBadDeliverMinFieldTest() {
    // GIVEN a payment protocol buffer with an invalid deliverMin field.
    // WHEN the protocol buffer is converted to a native Java type.
    // THEN a NumberFormatException is re-thrown.
    expectedException.expect(NumberFormatException.class);
    XRPPayment.from(FakeXRPProtobufs.invalidPaymentBadDeliverMin);
  }

  @Test
  public void convertPaymentWithBadSendMaxFieldTest() {
    // GIVEN a payment protocol buffer with an invalid sendMax field.
    // WHEN the protocol buffer is converted to a native Java type.
    // THEN a NumberFormatException is re-thrown.
    expectedException.expect(NumberFormatException.class);
    XRPPayment.from(FakeXRPProtobufs.invalidPaymentBadSendMax);
  }

  // Memo

  @Test
  public void convertMemoWithAllFieldsSetTest() {
    // GIVEN a memo with all fields set.
    Memo memoProto = FakeXRPProtobufs.memoWithAllFieldsSet;

    // WHEN the protocol buffer is converted to a native Java type.
    XRPMemo xrpMemo = XRPMemo.from(memoProto);

    // THEN all fields are present and set correctly.
    assertThat(xrpMemo.data()).isEqualTo(memoProto.getMemoData().getValue().toByteArray());
    assertThat(xrpMemo.format()).isEqualTo(memoProto.getMemoFormat().getValue().toByteArray());
    assertThat(xrpMemo.type()).isEqualTo(memoProto.getMemoType().getValue().toByteArray());
  }

  @Test
  public void convertMemoWithNoFieldsSetTest() {
    // GIVEN a memo with no fields set.
    Memo memoProto = Memo.newBuilder().build();

    // WHEN the protocol buffer is converted to a native Java type.
    XRPMemo xrpMemo = XRPMemo.from(memoProto);

    // THEN all fields are null.
    assertThat(xrpMemo.data()).isNull();
    assertThat(xrpMemo.format()).isNull();
    assertThat(xrpMemo.type()).isNull();
  }

  // Signer

  @Test
  public void convertSignerWithAllFieldsSetTest() {
    // GIVEN a Signer protocol buffer with all fields set.
    Signer signerProto = FakeXRPProtobufs.signerWithAllFieldsSet;

    // WHEN the protocol buffer is converted to a native Java type.
    XRPSigner xrpSigner = XRPSigner.from(signerProto);

    // THEN all fields are present and converted correctly.
    assertThat(xrpSigner.account()).isEqualTo(signerProto.getAccount().getValue().getAddress());
    assertThat(xrpSigner.signingPublicKey())
        .isEqualTo(signerProto.getSigningPublicKey().getValue().toByteArray());
    assertThat(xrpSigner.transactionSignature())
        .isEqualTo(signerProto.getTransactionSignature().getValue().toByteArray());
  }

  // Transaction

  @Test
  public void convertPaymentTransactionWithAllCommonFieldsSetTest() {
    // GIVEN a Transaction protocol buffer with all common fields set.
    Transaction transactionProto = FakeXRPProtobufs.transactionWithAllFieldsSet;

    // WHEN the protocol buffer is converted to a native Java type.
    XRPTransaction xrpTransaction = XRPTransaction.from(transactionProto);

    // THEN all fields are present and converted correctly.
    assertThat(xrpTransaction.account()).isEqualTo(transactionProto.getAccount().getValue().getAddress());
    assertThat(xrpTransaction.accountTransactionID())
        .isEqualTo(transactionProto.getAccountTransactionId().getValue().toByteArray());
    assertThat(xrpTransaction.fee()).isEqualTo(transactionProto.getFee().getDrops());
    assertThat(xrpTransaction.flags()).isEqualTo(transactionProto.getFlags().getValue());
    assertThat(xrpTransaction.lastLedgerSequence()).isEqualTo(transactionProto.getLastLedgerSequence().getValue());
    assertThat(xrpTransaction.memos()).isEqualTo(transactionProto.getMemosList()
        .stream()
        .map(memo -> XRPMemo.from(memo))
        .collect(Collectors.toList()));
    assertThat(xrpTransaction.sequence()).isEqualTo(transactionProto.getSequence().getValue());
    assertThat(xrpTransaction.signers()).isEqualTo(transactionProto.getSignersList()
        .stream()
        .map(signer -> XRPSigner.from(signer))
        .collect(Collectors.toList()));
    assertThat(xrpTransaction.signingPublicKey())
        .isEqualTo(transactionProto.getSigningPublicKey().getValue().toByteArray());
    assertThat(xrpTransaction.sourceTag()).isEqualTo(transactionProto.getSourceTag().getValue());
    assertThat(xrpTransaction.transactionSignature())
        .isEqualTo(transactionProto.getTransactionSignature().getValue().toByteArray());
    assertThat(xrpTransaction.type()).isEqualTo(TransactionType.PAYMENT);
    assertThat(xrpTransaction.paymentFields()).isEqualTo(XRPPayment.from(transactionProto.getPayment()));
  }

  @Test
  public void convertPaymentTransactionWithOnlyMandatoryCommonFieldsSetTest() {
    // GIVEN a Transaction protocol buffer with only mandatory common fields set.
    Transaction transactionProto = FakeXRPProtobufs.transactionWithOnlyMandatoryCommonFieldsSet;

    // WHEN the protocol buffer is converted to a native Java type.
    XRPTransaction xrpTransaction = XRPTransaction.from(transactionProto);

    // THEN all fields are present and converted correctly.
    assertThat(xrpTransaction.account()).isEqualTo(transactionProto.getAccount().getValue().getAddress());
    assertThat(xrpTransaction.accountTransactionID()).isNull();
    assertThat(xrpTransaction.fee()).isEqualTo(transactionProto.getFee().getDrops());
    assertThat(xrpTransaction.flags()).isEqualTo(Flags.newBuilder().build().getValue());
    assertThat(xrpTransaction.lastLedgerSequence())
        .isEqualTo(LastLedgerSequence.newBuilder().build().getValue());
    assertThat(xrpTransaction.memos()).isNull();
    assertThat(xrpTransaction.sequence()).isEqualTo(transactionProto.getSequence().getValue());
    assertThat(xrpTransaction.signers()).isNull();
    assertThat(xrpTransaction.signingPublicKey())
        .isEqualTo(transactionProto.getSigningPublicKey().getValue().toByteArray());
    assertThat(xrpTransaction.sourceTag()).isEqualTo(SourceTag.newBuilder().build().getValue());
    assertThat(xrpTransaction.transactionSignature())
        .isEqualTo(transactionProto.getTransactionSignature().getValue().toByteArray());
    assertThat(xrpTransaction.type()).isEqualTo(TransactionType.PAYMENT);
    assertThat(xrpTransaction.paymentFields()).isEqualTo(XRPPayment.from(transactionProto.getPayment()));
  }

  @Test
  public void convertPaymentTransactionWithBadPaymentFieldsTest() {
    // GIVEN a Transaction protocol buffer with payment fields which are incorrect.
    Transaction transactionProto = FakeXRPProtobufs.invalidTransactionWithEmptyPaymentFields;

    // WHEN the protocol buffer is converted to a native Java type.
    XRPTransaction xrpTransaction = XRPTransaction.from(transactionProto);

    // THEN the result is null.
    assertThat(xrpTransaction).isNull();
  }

  @Test
  public void convertTransactionWithUnsupportedTypeTest() {
    // GIVEN a Transaction protocol buffer with an unsupported transaction type.
    Transaction transactionProto = FakeXRPProtobufs.invalidTransactionUnsupportedType;

    // WHEN the protocol buffer is converted to a native Java type.
    XRPTransaction xrpTransaction = XRPTransaction.from(transactionProto);

    // THEN the result is null.
    assertThat(xrpTransaction).isNull();
  }
}
