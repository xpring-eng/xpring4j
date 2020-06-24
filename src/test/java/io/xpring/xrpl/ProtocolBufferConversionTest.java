package io.xpring.xrpl;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import io.xpring.common.XrplNetwork;
import io.xpring.xrpl.model.XrpCurrency;
import io.xpring.xrpl.model.XrpCurrencyAmount;
import io.xpring.xrpl.model.XrpIssuedCurrency;
import io.xpring.xrpl.model.XrpMemo;
import io.xpring.xrpl.model.XrpPath;
import io.xpring.xrpl.model.XrpPathElement;
import io.xpring.xrpl.model.XrpPayment;
import io.xpring.xrpl.model.XrpSigner;
import io.xpring.xrpl.model.XrpTransaction;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.xrpl.rpc.v1.Currency;
import org.xrpl.rpc.v1.CurrencyAmount;
import org.xrpl.rpc.v1.GetTransactionResponse;
import org.xrpl.rpc.v1.Memo;
import org.xrpl.rpc.v1.Payment;
import org.xrpl.rpc.v1.Payment.PathElement;
import org.xrpl.rpc.v1.Signer;
import org.xrpl.rpc.v1.Transaction;

import java.math.BigInteger;
import java.util.stream.Collectors;

public class ProtocolBufferConversionTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  // Currency

  @Test
  public void convertCurrencyTest() {
    // GIVEN a Currency protocol buffer with a code and a name.
    Currency currencyProto = FakeXrpProtobufs.currency;

    // WHEN the protocol buffer is converted to a native Java type.
    XrpCurrency xrpCurrency = XrpCurrency.from(currencyProto);

    // THEN the currency converted as expected.
    assertThat(xrpCurrency.name()).isEqualTo(currencyProto.getName());
    assertThat(xrpCurrency.code()).isEqualTo(currencyProto.getCode().toByteArray());
  }

  // PathElement

  @Test
  public void convertPathElementTest() {
    // GIVEN a PathElement protocol buffer with all fields set.
    PathElement testPathElementProto = FakeXrpProtobufs.pathElement;

    // WHEN the protocol buffer is converted to a native Java type.
    XrpPathElement xrpPathElement = XrpPathElement.from(testPathElementProto);

    // THEN all fields converted correctly.
    assertThat(xrpPathElement.account().get()).isEqualTo(testPathElementProto.getAccount().getAddress());
    assertThat(xrpPathElement.currency().get()).isEqualTo(XrpCurrency.from(testPathElementProto.getCurrency()));
    assertThat(xrpPathElement.issuer().get()).isEqualTo(testPathElementProto.getIssuer().getAddress());
  }

  @Test
  public void convertPathElementWithNoFieldsTest() {
    // GIVEN a PathElement protocol buffer with no fields set.
    PathElement emptyPathElementProto = PathElement.newBuilder().build();

    // WHEN the protocol buffer is converted to a native Java type.
    XrpPathElement xrpPathElement = XrpPathElement.from(emptyPathElementProto);

    // THEN all fields are empty Optionals.
    assertThat(xrpPathElement.account()).isEmpty();
    assertThat(xrpPathElement.currency()).isEmpty();
    assertThat(xrpPathElement.issuer()).isEmpty();
  }

  // Path

  @Test
  public void convertPathWithNoPathsTest() {
    // GIVEN a set of paths with zero path elements.
    // WHEN the protocol buffer is converted to a native Java type.
    XrpPath xrpPath = XrpPath.from(FakeXrpProtobufs.emptyPath);

    // Then there are zero paths in the output.
    assertThat(xrpPath.pathElements().size()).isZero();
  }

  @Test
  public void convertPathWithOnePathTest() {
    // GIVEN a set of paths with one path element.
    // WHEN the protocol buffer is converted to a native Java type.
    XrpPath xrpPath = XrpPath.from(FakeXrpProtobufs.pathWithOneElement);

    // THEN there is one path in the output.
    assertThat(xrpPath.pathElements().size()).isOne();
  }

  @Test
  public void convertPathWithManyPathsTest() {
    // GIVEN a set of paths with three path elements.
    // WHEN the protocol buffer is converted to a native Java type.
    XrpPath xrpPath = XrpPath.from(FakeXrpProtobufs.pathWithThreeElements);

    // THEN there are three paths in the output.
    assertThat(xrpPath.pathElements().size()).isEqualTo(3);
  }

  // IssuedCurrency

  @Test
  public void convertIssuedCurrencyTest() {
    // GIVEN an issued currency protocol buffer,
    // WHEN the protocol buffer is converted to a native Java type.
    XrpIssuedCurrency xrpIssuedCurrency = XrpIssuedCurrency.from(FakeXrpProtobufs.issuedCurrencyAmount);

    // THEN the issued currency converted as expected.
    assertThat(xrpIssuedCurrency.currency())
        .isEqualTo(XrpCurrency.from(FakeXrpProtobufs.issuedCurrencyAmount.getCurrency()));

    assertThat(xrpIssuedCurrency.issuer())
        .isEqualTo(FakeXrpProtobufs.issuedCurrencyAmount.getIssuer().getAddress());

    assertThat(xrpIssuedCurrency.value()).isEqualTo(new BigInteger(FakeXrpProtobufs.issuedCurrencyAmount.getValue()));
  }

  @Test
  public void convertIssuedCurrencyWithBadValueTest() {
    // GIVEN an issued currency protocol buffer with a non numeric value
    // WHEN the protocol buffer is converted to a native Java type.
    // THEN a NumberFormatException is thrown.
    expectedException.expect(NumberFormatException.class);
    XrpIssuedCurrency.from(FakeXrpProtobufs.invalidIssuedCurrencyAmount);
  }

  // CurrencyAmount

  @Test
  public void convertCurrencyAmountWithDropsTest() {
    // GIVEN a currency amount protocol buffer with an XRP amount.
    // WHEN the protocol buffer is converted to a native Java type.
    XrpCurrencyAmount xrpCurrencyAmount = XrpCurrencyAmount.from(FakeXrpProtobufs.dropsCurrencyAmount);

    // THEN the result has drops set and an empty issued currency.
    assertThat(xrpCurrencyAmount.drops().get())
        .isEqualTo(Long.toString(FakeXrpProtobufs.dropsCurrencyAmount.getXrpAmount().getDrops()));
    assertThat(xrpCurrencyAmount.issuedCurrency()).isEmpty();
  }

  @Test
  public void convertCurrencyAmountWithIssuedCurrencyTest() {
    // GIVEN a currency amount protocol buffer with an issued currency amount.
    // WHEN the protocol buffer is converted to a native Java type.
    XrpCurrencyAmount xrpCurrencyAmount = XrpCurrencyAmount.from(FakeXrpProtobufs.issuedCurrencyCurrencyAmount);

    // THEN the result has an issued currency set and no drops amount.
    assertThat(xrpCurrencyAmount.drops()).isEmpty();
    assertThat(xrpCurrencyAmount.issuedCurrency().get())
        .isEqualTo(XrpIssuedCurrency.from(FakeXrpProtobufs.issuedCurrencyCurrencyAmount.getIssuedCurrencyAmount()));
  }

  @Test
  public void convertCurrencyAmountWithBadInputsTest() {
    // GIVEN a currency amount protocol buffer with no amounts.
    CurrencyAmount emptyCurrencyAmount = CurrencyAmount.newBuilder().build();

    // WHEN the protocol buffer is converted to a native Java type.
    XrpCurrencyAmount xrpCurrencyAmount = XrpCurrencyAmount.from(emptyCurrencyAmount);

    // THEN the result is null.
    assertThat(xrpCurrencyAmount).isNull();
  }

  @Test
  public void convertCurrencyAmountWithInvalidIssuedCurrencyTest() {
    // GIVEN a currency amount protocol buffer with an invalid issued currency.
    // WHEN the protocol buffer is converted to a native Java type.
    // THEN a NumberFormatException is re-thrown.
    expectedException.expect(NumberFormatException.class);
    XrpCurrencyAmount.from(FakeXrpProtobufs.invalidCurrencyAmount);
  }

  // Payment

  @Test
  public void convertPaymentWithAllFieldsSetTest() {
    // GIVEN a payment protocol buffer with all fields set.
    Payment paymentProto = FakeXrpProtobufs.paymentWithAllFieldsSet;

    // WHEN the protocol buffer is converted to a native Java type.
    XrpPayment xrpPayment = XrpPayment.from(paymentProto, XrplNetwork.TEST);

    // THEN the result is as expected.
    assertThat(xrpPayment.amount()).isEqualTo(XrpCurrencyAmount.from(paymentProto.getAmount().getValue()));
    assertThat(xrpPayment.destination()).isEqualTo(paymentProto.getDestination().getValue().getAddress());
    assertThat(xrpPayment.destinationTag().get()).isEqualTo(paymentProto.getDestinationTag().getValue());
    assertThat(xrpPayment.destinationXAddress()).isEqualTo(Utils.encodeXAddress(
            ImmutableClassicAddress.builder()
                    .address(xrpPayment.destination())
                    .tag(xrpPayment.destinationTag())
                    .isTest(true)
                    .build()
            )
    );
    assertThat(xrpPayment.deliverMin().get())
            .isEqualTo(XrpCurrencyAmount.from(paymentProto.getDeliverMin().getValue()));
    assertThat(xrpPayment.invoiceID()).isEqualTo(paymentProto.getInvoiceId().getValue().toByteArray());
    assertThat(xrpPayment.paths()).isEqualTo(paymentProto.getPathsList()
            .stream()
            .map(path -> XrpPath.from(path))
            .collect(Collectors.toList()));
    assertThat(xrpPayment.sendMax().get()).isEqualTo(XrpCurrencyAmount.from(paymentProto.getSendMax().getValue()));
  }

  @Test
  public void convertPaymentWithMandatoryFieldsSetTest() {
    // GIVEN a payment protocol buffer with only mandatory fields set.
    Payment paymentProto = FakeXrpProtobufs.paymentWithMandatoryFieldsSet;

    // WHEN the protocol buffer is converted to a native Java type.
    XrpPayment xrpPayment = XrpPayment.from(paymentProto, XrplNetwork.TEST);

    // THEN the result is as expected.
    assertThat(xrpPayment.amount()).isEqualTo(XrpCurrencyAmount.from(paymentProto.getAmount().getValue()));
    assertThat(xrpPayment.destination()).isEqualTo(paymentProto.getDestination().getValue().getAddress());
    assertThat(xrpPayment.destinationTag()).isEmpty();
    assertThat(xrpPayment.destinationXAddress()).isEqualTo(Utils.encodeXAddress(
            ImmutableClassicAddress.builder()
                    .address(xrpPayment.destination())
                    .tag(xrpPayment.destinationTag())
                    .isTest(true)
                    .build()
            )
    );
    assertThat(xrpPayment.deliverMin()).isEmpty();
    assertThat(xrpPayment.invoiceID()).isEmpty();
    assertThat(xrpPayment.paths()).isEmpty();
    assertThat(xrpPayment.sendMax()).isEmpty();
  }

  @Test
  public void convertPaymentWithBadAmountFieldTest() {
    // GIVEN a payment protocol buffer with an invalid amount field.
    // WHEN the protocol buffer is converted to a native Java type.
    // THEN a NumberFormatException is re-thrown.
    expectedException.expect(NumberFormatException.class);
    XrpPayment.from(FakeXrpProtobufs.invalidPaymentBadAmount, XrplNetwork.TEST);
  }

  @Test
  public void convertPaymentWithBadDeliverMinFieldTest() {
    // GIVEN a payment protocol buffer with an invalid deliverMin field.
    // WHEN the protocol buffer is converted to a native Java type.
    // THEN a NumberFormatException is re-thrown.
    expectedException.expect(NumberFormatException.class);
    XrpPayment.from(FakeXrpProtobufs.invalidPaymentBadDeliverMin, XrplNetwork.TEST);
  }

  @Test
  public void convertPaymentWithBadSendMaxFieldTest() {
    // GIVEN a payment protocol buffer with an invalid sendMax field.
    // WHEN the protocol buffer is converted to a native Java type.
    // THEN a NumberFormatException is re-thrown.
    expectedException.expect(NumberFormatException.class);
    XrpPayment.from(FakeXrpProtobufs.invalidPaymentBadSendMax, XrplNetwork.TEST);
  }

  // Memo

  @Test
  public void convertMemoWithAllFieldsSetTest() {
    // GIVEN a memo with all fields set.
    Memo memoProto = FakeXrpProtobufs.memoWithAllFieldsSet;

    // WHEN the protocol buffer is converted to a native Java type.
    XrpMemo xrpMemo = XrpMemo.from(memoProto);

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
    XrpMemo xrpMemo = XrpMemo.from(memoProto);

    // THEN all fields are empty Optionals.
    assertThat(xrpMemo.data()).isEmpty();
    assertThat(xrpMemo.format()).isEmpty();
    assertThat(xrpMemo.type()).isEmpty();
  }

  // Signer

  @Test
  public void convertSignerWithAllFieldsSetTest() {
    // GIVEN a Signer protocol buffer with all fields set.
    Signer signerProto = FakeXrpProtobufs.signerWithAllFieldsSet;

    // WHEN the protocol buffer is converted to a native Java type.
    XrpSigner xrpSigner = XrpSigner.from(signerProto);

    // THEN all fields are present and converted correctly.
    assertThat(xrpSigner.account()).isEqualTo(signerProto.getAccount().getValue().getAddress());
    assertThat(xrpSigner.signingPublicKey()).isEqualTo(signerProto.getSigningPublicKey().getValue().toByteArray());
    assertThat(xrpSigner.transactionSignature())
            .isEqualTo(signerProto.getTransactionSignature().getValue().toByteArray());
  }

  // Transaction

  @Test
  public void convertPaymentTransactionWithAllCommonFieldsSetTest() {
    // GIVEN a GetTransactionResponse protocol buffer with all common fields set.
    GetTransactionResponse getTransactionResponseProto = FakeXrpProtobufs.getTransactionResponsePaymentAllFields;
    Transaction transactionProto = getTransactionResponseProto.getTransaction();

    // WHEN the protocol buffer is converted to a native Java type.
    XrpTransaction xrpTransaction = XrpTransaction.from(getTransactionResponseProto, XrplNetwork.TEST);

    // THEN all fields are present and converted correctly.
    assertThat(xrpTransaction.hash())
            .isEqualTo(Utils.byteArrayToHex(FakeXrpProtobufs.testTransactionHash.toByteArray()));
    assertThat(xrpTransaction.account()).isEqualTo(transactionProto.getAccount().getValue().getAddress());
    assertThat(xrpTransaction.accountTransactionID())
        .isEqualTo(transactionProto.getAccountTransactionId().getValue().toByteArray());
    assertThat(xrpTransaction.fee()).isEqualTo(transactionProto.getFee().getDrops());
    assertThat(xrpTransaction.flags().get()).isEqualTo(transactionProto.getFlags().getValue());
    assertThat(xrpTransaction.lastLedgerSequence().get())
            .isEqualTo(transactionProto.getLastLedgerSequence().getValue());
    assertThat(xrpTransaction.memos()).isEqualTo(transactionProto.getMemosList()
        .stream()
        .map(memo -> XrpMemo.from(memo))
        .collect(Collectors.toList()));
    assertThat(xrpTransaction.sequence()).isEqualTo(transactionProto.getSequence().getValue());
    assertThat(xrpTransaction.signers()).isEqualTo(transactionProto.getSignersList()
        .stream()
        .map(signer -> XrpSigner.from(signer))
        .collect(Collectors.toList()));
    assertThat(xrpTransaction.signingPublicKey())
        .isEqualTo(transactionProto.getSigningPublicKey().getValue().toByteArray());
    assertThat(xrpTransaction.sourceTag().get()).isEqualTo(transactionProto.getSourceTag().getValue());
    assertThat(xrpTransaction.transactionSignature())
        .isEqualTo(transactionProto.getTransactionSignature().getValue().toByteArray());
    assertThat(xrpTransaction.type()).isEqualTo(TransactionType.PAYMENT);
    assertThat(xrpTransaction.paymentFields())
            .isEqualTo(XrpPayment.from(transactionProto.getPayment(), XrplNetwork.TEST));
    assertThat(xrpTransaction.timestamp().get()).isEqualTo(FakeXrpProtobufs.expectedTimestamp);
    assertThat(xrpTransaction.deliveredAmount().get()).isEqualTo(Long.toString(FakeXrpProtobufs.testDeliveredDrops));
    assertThat(xrpTransaction.validated()).isEqualTo(FakeXrpProtobufs.testIsValidated);
    assertThat(xrpTransaction.ledgerIndex()).isEqualTo(FakeXrpProtobufs.testLedgerIndex);
  }

  @Test
  public void convertPaymentTransactionWithXrpDeliveredAmountTest() {
    // GIVEN a GetTransactionResponse protocol buffer containing a transaction of XRP.
    GetTransactionResponse getTransactionResponseProto = FakeXrpProtobufs.getTransactionResponsePaymentXRP;

    // WHEN the protocol buffer is converted to a native Java type.
    XrpTransaction xrpTransaction = XrpTransaction.from(getTransactionResponseProto, XrplNetwork.TEST);

    // THEN the deliveredAmount field converted correctly to a drops amount.
    assertThat(xrpTransaction.deliveredAmount().get()).isEqualTo(Long.toString(FakeXrpProtobufs.testDeliveredDrops));
  }

  @Test
  public void convertPaymentTransactionWithIssuedCurrencyDeliveredAmountTest() {
    // GIVEN a GetTransactionResponse protocol buffer containing a transaction of issued currency.
    GetTransactionResponse getTransactionResponseProto = FakeXrpProtobufs.getTransactionResponsePaymentIssued;

    // WHEN the protocol buffer is converted to a native Java type.
    XrpTransaction xrpTransaction = XrpTransaction.from(getTransactionResponseProto, XrplNetwork.TEST);

    // THEN the deliveredAmount field converted correctly to an issued currency value.
    assertThat(xrpTransaction.deliveredAmount().get()).isEqualTo(FakeXrpProtobufs.testIssuedCurrencyValue);
  }

  @Test
  public void convertPaymentTransactionWithOnlyMandatoryCommonFieldsSetTest() {
    // GIVEN a GetTransactionResponse protocol buffer containing a Transaction with only mandatory common fields set.
    GetTransactionResponse getTransactionResponseProto = FakeXrpProtobufs.getTransactionResponsePaymentMandatoryFields;
    Transaction transactionProto = getTransactionResponseProto.getTransaction();

    // WHEN the protocol buffer is converted to a native Java type.
    XrpTransaction xrpTransaction = XrpTransaction.from(getTransactionResponseProto, XrplNetwork.TEST);

    // THEN all fields are present and converted correctly.
    assertThat(xrpTransaction.hash())
            .isEqualTo(Utils.byteArrayToHex(FakeXrpProtobufs.testTransactionHash.toByteArray()));
    assertThat(xrpTransaction.account()).isEqualTo(transactionProto.getAccount().getValue().getAddress());
    assertThat(xrpTransaction.accountTransactionID()).isEmpty();
    assertThat(xrpTransaction.fee()).isEqualTo(transactionProto.getFee().getDrops());
    assertThat(xrpTransaction.flags()).isEmpty();
    assertThat(xrpTransaction.lastLedgerSequence()).isEmpty();
    assertThat(xrpTransaction.memos()).isEmpty();
    assertThat(xrpTransaction.sequence()).isEqualTo(transactionProto.getSequence().getValue());
    assertThat(xrpTransaction.signers()).isEmpty();
    assertThat(xrpTransaction.signingPublicKey())
        .isEqualTo(transactionProto.getSigningPublicKey().getValue().toByteArray());
    assertThat(xrpTransaction.sourceTag()).isEmpty();
    assertThat(xrpTransaction.transactionSignature())
        .isEqualTo(transactionProto.getTransactionSignature().getValue().toByteArray());
    assertThat(xrpTransaction.type()).isEqualTo(TransactionType.PAYMENT);
    assertThat(xrpTransaction.paymentFields())
            .isEqualTo(XrpPayment.from(transactionProto.getPayment(), XrplNetwork.TEST));
    assertThat(xrpTransaction.timestamp().isPresent()).isFalse();
    assertThat(xrpTransaction.deliveredAmount().isPresent()).isFalse();
  }

  @Test
  public void convertPaymentTransactionWithBadPaymentFieldsTest() {
    // GIVEN a GetTransactionResponse protocol buffer containing a Transaction with incorrect payment fields.
    GetTransactionResponse getTransactionResponseProto =
                                                      FakeXrpProtobufs.invalidGetTransactionResponseEmptyPaymentFields;

    // WHEN the protocol buffer is converted to a native Java type.
    XrpTransaction xrpTransaction = XrpTransaction.from(getTransactionResponseProto, XrplNetwork.TEST);

    // THEN the result is null.
    assertThat(xrpTransaction).isNull();
  }

  @Test
  public void convertTransactionWithUnsupportedTypeTest() {
    // GIVEN a GetTransactionResponse protocol buffer containing a Transaction of an unsupported transaction type.
    GetTransactionResponse getTransactionResponseProto =
                                              FakeXrpProtobufs.invalidGetTransactionResponseUnsupportedTransactionType;

    // WHEN the protocol buffer is converted to a native Java type.
    XrpTransaction xrpTransaction = XrpTransaction.from(getTransactionResponseProto, XrplNetwork.TEST);

    // THEN the result is null.
    assertThat(xrpTransaction).isNull();
  }
}
