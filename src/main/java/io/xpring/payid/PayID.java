package io.xpring.payid;

import static io.xpring.payid.AbstractPayID.upperCasePercentEncoded;
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
public interface PayID {

  String PAYID_SCHEME = "payid:";

  static ImmutablePayID.Builder builder() {
    return ImmutablePayID.builder();
  }

  /**
   * <p>Parses a PayId URI string into a @{code PayId}, applying normalization rules defined in the PayID RFC.</p>
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
   * @param value text of a complete PayID.
   *
   * @return A valid {@link PayID}.
   *
   * @throws NullPointerException     if {@code value} is null.
   * @throws IllegalArgumentException if {@code value} cannot be properly parsed or has invalid characters per the PayID
   *                                  RFC.
   */
  static PayID of(String value) {
    Objects.requireNonNull(value, "PayID must not be null");
    if (value.toLowerCase(Locale.ENGLISH).startsWith(PAYID_SCHEME)) {
      value = value.substring(6);
    } else {
      throw new IllegalArgumentException(format("PayID `%s` must start with the 'payid:' scheme", value));
    }
    if (!value.contains("$")) {
      throw new IllegalArgumentException(format("PayID `%s` must contain a $", value));
    } else {
      Preconditions
        .checkArgument(value.length() > 6, format("PayID `%s` must specify a valid account and host", value));
    }
    Preconditions.checkArgument(!value.startsWith("%"),
      format("PayID `%s` MUST start with either an 'unreserved' or 'sub-selims' character rules. "
        + "A PayID may not start with a percent-encoded value.", value));
    int lastDollar = value.lastIndexOf("$");
    String account = value.substring(0, lastDollar);
    String host = value.substring(lastDollar + 1);
    // NORMALIZATION: Capitalization
    account = account.toLowerCase(Locale.ENGLISH);
    host = host.toLowerCase(Locale.ENGLISH);
    // NORMALIZATION: Percent-encoding
    account = upperCasePercentEncoded(account);
    host = upperCasePercentEncoded(host);
    final ImmutablePayID.Builder builder = builder();
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
