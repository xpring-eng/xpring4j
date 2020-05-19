package io.xpring.payid.idiomatic;

import static io.xpring.payid.idiomatic.AbstractPayId.upperCasePercentEncoded;
import static java.lang.String.format;

import com.google.common.base.Preconditions;

import java.util.Locale;
import java.util.Objects;
import java.util.StringTokenizer;

/**
 * A standardized identifier for payment accounts.
 *
 * @see "https://github.com/xpring-eng/rfcs/blob/master/TBD.md"
 */
public interface PayId {

  String PAY_ID_SCHEME = "payid:";

  static ImmutablePayId.Builder builder() {
    return ImmutablePayId.builder();
  }

  /**
   * <p>Parses a PayId URI string into a @{code PayId}, applying normalization rules defined in the Pay ID RFC.</p>
   *
   * <p>Normalization includes the following:
   *
   * <ul>
   *   <li>Lower-case the scheme, if present.</li>
   *   <li>Lower-case the accountpart</li>
   *   <li>Lower-case the host</li>
   *   <li>For any hex-encoded String, upper-case the Hexadecimal letters (e.g., 'f' to 'F')</li>
   * </ul>
   *
   * @param value text of a complete Pay ID.
   *
   * @return A valid {@link PayId}.
   *
   * @throws NullPointerException     if {@code value} is null.
   * @throws IllegalArgumentException if {@code value} cannot be properly parsed or has invalid characters per the Pay
   *                                  ID RFC.
   */
  static PayId of(String value) {
    Objects.requireNonNull(value, "Pay ID must not be null");
    if (value.toLowerCase(Locale.ENGLISH).startsWith(PAY_ID_SCHEME)) {
      value = value.substring(6);
    } else {
      throw new IllegalArgumentException(format("Pay ID `%s` must start with the 'payid:' scheme", value));
    }

    if (!value.contains("$")) {
      throw new IllegalArgumentException(format("Pay ID `%s` must contain a $", value));
    } else {
      Preconditions
        .checkArgument(value.length() > 6, format("Pay ID `%s` must specify a valid account and host", value));
    }

    // Ensure no more than a single dollar-sign ($) without using a library.
    // See https://stackoverflow.com/a/35242882
    int numDollarSigns = new StringTokenizer(" " + value + " ", "$").countTokens() - 1;
    Preconditions.checkArgument(numDollarSigns == 1,
        format("Pay ID `%s` may only contain a single dollar-sign. All other dollar-signs must be percent-encoded.",
          value));
    Preconditions.checkArgument(!value.startsWith("%"),
        format("Pay ID `%s` MUST start with either an 'unreserved' or 'sub-selims' character rules. "
          + "A Pay ID may not start with a percent-encoded value.", value));

    final String[] parts = value.split("\\$");

    String account = parts[0];
    String host = parts[1];

    // NORMALIZATION: Capitalization
    account = account.toLowerCase(Locale.ENGLISH);
    host = host.toLowerCase(Locale.ENGLISH);

    // NORMALIZATION: Percent-encoding
    account = upperCasePercentEncoded(account);
    host = upperCasePercentEncoded(host);

    final ImmutablePayId.Builder builder = builder();
    if (account.length() > 0) {
      builder.account(account);
    }

    builder.host(host);

    return builder.build();
  }

  /**
   * A payment account identifier defined in the `payid-uri` RFC.
   *
   * @return A {@link String} containing the 'path' portion of this PayId.
   */
  String account();

  /**
   * A host as defined defined in the `payid-uri` RFC.
   *
   * @return A {@link String} containing the 'host' portion of this PayId.
   */
  String host();
}
