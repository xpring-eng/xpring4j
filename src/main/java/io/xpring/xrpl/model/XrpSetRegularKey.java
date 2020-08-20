package io.xpring.xrpl.model;

/**
 * Represents a SetRegularKey transaction on the XRP Ledger.
 * <p>
 * A SetRegularKey transaction assigns, changes, or removes the regular key pair associated with an account.
 * You can protect your account by assigning a regular key pair to it and using it instead of the master key
 * pair to sign transactions whenever possible. If your regular key pair is compromised, but your master key
 * pair is not, you can use a SetRegularKey transaction to regain control of your account.
 * </p>
 *
 * @see "https://xrpl.org/setregularkey.html"
 */
public interface XrpSetRegularKey {
}
