package io.xpring.xrpl;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.io.BaseEncoding;
import org.junit.Test;
import org.xrpl.rpc.v1.AccountAddress;
import org.xrpl.rpc.v1.Common.Account;
import org.xrpl.rpc.v1.Common.Amount;
import org.xrpl.rpc.v1.Common.Destination;
import org.xrpl.rpc.v1.Common.Sequence;
import org.xrpl.rpc.v1.CurrencyAmount;
import org.xrpl.rpc.v1.Payment;
import org.xrpl.rpc.v1.Transaction;
import org.xrpl.rpc.v1.XRPDropsAmount;

public class SignerTest {
  @Test
  public void testSign() throws Exception {
    // GIVEN a wallet, a transaction and an expected serialized and signed output.
    Wallet wallet = new Wallet("snYP7oArxKepd3GPDcrjMsJYiJeJB");

    int sequenceInt = 1;
    XRPDropsAmount feeAmount = XRPDropsAmount.newBuilder().setDrops(10).build();
    XRPDropsAmount sendAmount = XRPDropsAmount.newBuilder().setDrops(1000).build();
    AccountAddress senderAddress =
        AccountAddress.newBuilder().setAddress("X7vjQVCddnQ7GCESYnYR3EdpzbcoAMbPw7s2xv8YQs94tv4").build();
    AccountAddress destinationAddress =
        AccountAddress.newBuilder().setAddress("XVPcpSm47b1CZkf5AkKM9a84dQHe3m4sBhsrA4XtnBECTAc").build();
    CurrencyAmount paymentAmount = CurrencyAmount.newBuilder().setXrpAmount(sendAmount).build();
    Destination destination = Destination.newBuilder().setValue(destinationAddress).build();
    Amount amount = Amount.newBuilder().setValue(paymentAmount).build();
    Account account = Account.newBuilder().setValue(senderAddress).build();
    Sequence sequence = Sequence.newBuilder().setValue(sequenceInt).build();

    Payment payment = Payment.newBuilder().setDestination(destination).setAmount(amount).build();
    Transaction transaction = Transaction.newBuilder()
        .setAccount(account)
        .setFee(feeAmount)
        .setSequence(sequence)
        .setPayment(payment)
        .build();

    String expected = "12000022800000002400000001201B000000006140000000000003E868400000000000000A732102FD0E8479CE8"
        + "182ABD35157BB0FA17A469AF27DCB12B5DDED697C61809116A33B7447304502210087BEEA9CB1F29862E4C4F4DEA44772144298FA"
        + "14962777CBF39172DA3CE0E92E02202E1DB58CAE3E8905F1B86B81FCA66AE1537EF8289FAE46C6ECBEA9B6108C27E581145B812C9"
        + "D57731E27A2DA8B1830195F88EF32A3B68314B5F762798A53D543A014CAF8B297CFF8F2F937E8";

    // WHEN the transaction is signed.
    byte[] signedTransaction = Signer.signTransaction(transaction, wallet);

    // THEN the result is the same as expected.
    assertThat(BaseEncoding.base16().encode(signedTransaction)).isEqualTo(expected);
  }
}
