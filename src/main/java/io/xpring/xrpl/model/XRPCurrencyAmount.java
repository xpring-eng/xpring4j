package io.xpring.xrpl.model;


import org.xrpl.rpc.v1.CurrencyAmount;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * An amount of currency on the XRP Ledger.
 *
 * @see "https://xrpl.org/basic-data-types.html#specifying-currency-amounts"
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public class XRPCurrencyAmount {
  @Nullable
  private final String drops;

  @Nullable
  private final XRPIssuedCurrency issuedCurrency;

  /**
   * (Optional) An amount of XRP, specified in drops.
   * Note: Mutually exclusive fields - only drops XOR issuedCurrency should be set.
   *
   * @return The {@link String} representation of an amount of XRP, specified in drops.
   */
  public Optional<String> drops() {
    return Optional.ofNullable(drops);
  }

  /**
   * (Optional) An amount of an issued currency.
   * Note: Mutually exclusive fields - only drops XOR issuedCurrency should be set.
   *
   * @return The {@link XRPIssuedCurrency} of this {@link XRPCurrencyAmount}.
   */
  public Optional<XRPIssuedCurrency> issuedCurrency() {
    return Optional.ofNullable(issuedCurrency);
  }

  /**
   * Constructs an {@link XRPCurrencyAmount} from a {@link CurrencyAmount}.
   *
   * @param currencyAmount a {@link CurrencyAmount} (protobuf object) whose field values will be used
   *                       to construct an {@link XRPCurrencyAmount}
   * @return an {@link XRPCurrencyAmount} with its fields set via the analogous protobuf fields.
   * @see <a href="https://github.com/ripple/rippled/blob/develop/src/ripple/proto/org/xrpl/rpc/v1/amount.proto#L10">
   * CurrencyAmount protocol buffer</a>
   */
  public static XRPCurrencyAmount from(CurrencyAmount currencyAmount) throws NumberFormatException {
    switch (currencyAmount.getAmountCase()) {
      // Mutually exclusive: either drops or issuedCurrency is set in an XRPCurrencyAmount
      case ISSUED_CURRENCY_AMOUNT: {
        if (currencyAmount.hasIssuedCurrencyAmount()) {
          XRPIssuedCurrency xrpIssuedCurrency = XRPIssuedCurrency.from(currencyAmount.getIssuedCurrencyAmount());
          if (xrpIssuedCurrency != null) {
            return new XRPCurrencyAmountBuilder().issuedCurrency(xrpIssuedCurrency).build();
          }
        }
        // if AmountCase is ISSUED_CURRENCY_AMOUNT, we MUST be able to convert this to an XRPIssuedCurrency
        return null;
      }
      case XRP_AMOUNT: {
        if (currencyAmount.hasXrpAmount()) {
          long numericDrops = currencyAmount.getXrpAmount().getDrops();
          String drops = Long.toString(numericDrops);
          return new XRPCurrencyAmountBuilder().drops(drops).build();
        }
        // if AmountCase is XRP_AMOUNT, the protobuf MUST have an xrpAmount
        return null;
      }
      // if no AmountCase is set (e.g. empty CurrencyAmount protobuf was passed to constructor)
      default:
        return null;
    }
  }

  private XRPCurrencyAmount(XRPCurrencyAmount.XRPCurrencyAmountBuilder builder) {
    this.drops = builder.drops;
    this.issuedCurrency = builder.issuedCurrency;
  }

  //Builder class
  public static class XRPCurrencyAmountBuilder {
    //optional fields
    private String drops;
    private XRPIssuedCurrency issuedCurrency;

    public XRPCurrencyAmountBuilder() {}

    public XRPCurrencyAmount.XRPCurrencyAmountBuilder drops(String drops) {
      this.drops = drops;
      return this;
    }

    public XRPCurrencyAmount.XRPCurrencyAmountBuilder issuedCurrency(XRPIssuedCurrency issuedCurrency) {
      this.issuedCurrency = issuedCurrency;
      return this;
    }

    public XRPCurrencyAmount build() {
      return new XRPCurrencyAmount(this);
    }
  }
}
