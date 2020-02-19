
[![CircleCI](https://img.shields.io/circleci/build/github/xpring-eng/xpring4j?style=flat-square)](https://circleci.com/gh/xpring-eng/Xpring4j)
[![CodeCov](https://img.shields.io/codecov/c/github/xpring-eng/xpring4j?style=flat-square)](https://codecov.io/gh/xpring-eng/Xpring4J)
[![Dependabot Status](https://img.shields.io/static/v1?label=Dependabot&message=enabled&color=success&style=flat-square&logo=dependabot)](https://dependabot.com)

# Xpring4j

Xpring4j is the Java client-side library of Xpring SDK.

## Features

Xpring4j provides the following features:

- Wallet generation and derivation (Seed-based or HD Wallet-based)
- Address validation
- Account balance retrieval
- Sending XRP payments

## Installation

Xpring4j utilizes two components to access Xpring:

1. The Xpring4j client-side library (This library)
2. A server-side component that handles requests from this library and proxies them to an XRP node

### Client-Side Library

Xpring4j is available as a Java library from Maven Central. Simply add the following to your `pom.xml`:

```xml
<dependency>
  <groupId>io.xpring</groupId>
  <artifactId>xpring4j</artifactId>
  <version>1.2.0</version>
</dependency>
```

### Server-Side Component

The server-side component sends client-side requests to an XRP Node.

To get developers started right away, Xpring provides the server-side component as a hosted service, which proxies requests from client-side libraries to a hosted XRP Node. Developers can reach the endpoint at:

```
grpc.xpring.tech:80
```

Xpring is working on building a zero-config way for XRP node users to deploy and use the adapter as an open-source component of [rippled](https://github.com/ripple/rippled). Watch this space!

## Usage

**Note:** Xpring SDK only works with the X-Address format. For more information about this format, see the [Utilities section](#utilities) and <http://xrpaddress.info>.

### Wallets

A wallet is a fundamental model object in Xpring4j. A wallet provides:

- key management
- address derivation
- signing functionality

Wallets can be derived from either a seed or a mnemonic and derivation path. You can also generate a new random HD wallet.

#### Wallet Derivation

Xpring4j can derive a wallet from a seed or it can derive a hierarchical deterministic wallet (HD Wallet) from a mnemonic and derivation path.

##### Hierarchical Deterministic Wallets

A hierarchical deterministic wallet is created using a mnemonic and a derivation path. Simply pass the mnemonic and derivation path to the wallet generation function. Note that you can pass `null` for the derivation path and have a default path be used instead.

```java
import xpring.io.xrpl.Wallet;

String mnemonic = "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about";

Wallet hdWallet1 = new Wallet(mnemonic, null); // Has default derivation path
Wallet hdWallet2 = new Wallet(mnemonic, Wallet.getDefaultDerivationPath()); // Same as hdWallet1

Wallet hdWallet = new Wallet(mnemonic, "m/44'/144'/0'/0/1"); // Wallet with custom derivation path.
```

##### Seed-Based Wallets

You can construct a seed based wallet by passing a base58check encoded seed string.

```java
import xpring.io.xrpl.Wallet;

Wallet seedWallet = new Wallet("snRiAJGeKCkPVddbjB3zRwwoiYDBm1M");
```

#### Wallet Generation

Xpring4j can generate a new and random HD Wallet. The result of a wallet generation call is a tuple which contains the following:

- A randomly generated mnemonic
- The derivation path used, which is the default path
- A reference to the new wallet

```java
import xpring.io.xrpl.Wallet;

// Generate a random wallet.
WalletGenerationResult generationResult = Wallet.generateRandomWallet();
Wallet newWallet = generationResult.getWallet();

// Wallet can be recreated with the artifacts of the initial generation.
Wallet copyOfNewWallet = new Wallet(generationResult.getMnemonic(), generationResult.getDerivationPath());
```

#### Wallet Properties

A generated wallet can provide its public key, private key, and address on the XRP ledger.

```java
import xpring.io.xrpl.Wallet;

String mnemonic = "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about";

Wallet wallet = new Wallet(mnemonic, null);

System.out.println(wallet.getAddress()); // X7u4MQVhU2YxS4P9fWzQjnNuDRUkP3GM6kiVjTjcQgUU3Jr
System.out.println(wallet.getPublicKey()); // 031D68BC1A142E6766B2BDFB006CCFE135EF2E0E2E94ABB5CF5C9AB6104776FBAE
System.out.println(wallet.getPrivateKey()); // 0090802A50AA84EFB6CDB225F17C27616EA94048C179142FECF03F4712A07EA7A4
```

#### Signing / Verifying

A wallet can also sign and verify arbitrary hex messages. Generally, users should use the functions on `XpringClient` to perform cryptographic functions rather than using these low level APIs.

```java
import xpring.io.xrpl.Wallet;

String mnemonic = "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about";
String message = "deadbeef";

Wallet wallet = new Wallet(mnemonic, null);

String signature = wallet.sign(message);
wallet.verify(message, signature); // true
```

### XpringClient

`XpringClient` is a gateway into the XRP Ledger. `XpringClient` is initialized with a single parameter, which is the URL of the remote adapter (see [Server-Side Component][#server-side-component]).

```java
import io.xpring.xrpl.XpringClient;

String grpcURL = "grpc.xpring.tech";
XpringClient xpringClient = new XpringClient(grpcURL);
```

#### Retrieving a Balance

A `XpringClient` can check the balance of an account on the XRP Ledger.

```java
import io.xpring.xrpl.XpringClient;
import java.math.BigInteger;

String grpcURL = "grpc.xpring.tech";
XpringClient xpringClient = new XpringClient(grpcURL);

String address = "X7u4MQVhU2YxS4P9fWzQjnNuDRUkP3GM6kiVjTjcQgUU3Jr";
BigInteger balance = xpringClient.getBalance(address);
System.out.println(balance); // Logs a balance in drops of XRP
```

### Checking Transaction Status

A `XpringClient` can check the status of an transaction on the XRP Ledger.

Xpring4J returns the following transaction states:
- `SUCCEEDED`: The transaction was successfully validated and applied to the XRP Ledger.
- `FAILED:` The transaction was successfully validated but not applied to the XRP Ledger. Or the operation will never be validated.
- `PENDING`: The transaction has not yet been validated, but may be validated in the future.
- `UNKNOWN`: The transaction status could not be determined.

**Note:** For more information, see [Reliable Transaction Submission](https://xrpl.org/reliable-transaction-submission.html) and [Transaction Results](https://xrpl.org/transaction-results.html).

These states are determined by the `TransactionStatus` enum.

```java
import io.xpring.xrpl.XpringClient;
import io.xpring.xrpl.TransactionStatus;

String grpcURL = "grpc.xpring.tech";
XpringClient xpringClient = new XpringClient(grpcURL);

String transactionHash = "2CBBD2523478848DA256F8EBFCBD490DD6048A4A5094BF8E3034F57EA6AA0522";
TransactionStatus transactionStatus = xpringClient.xpringClient.getTransactionStatus(transactionHash); // TransactionStatus.SUCCEEDED

```

#### Sending XRP

A `XpringClient` can send XRP to other [accounts](https://xrpl.org/accounts.html) on the XRP Ledger.

**Note:** The payment operation will block the calling thread until the operation reaches a definitive and irreversible success or failure state.

```java
import java.math.BigInteger;
import io.xpring.xrpl.Wallet;
import io.xpring.xrpl.XpringClient;

// Amount of XRP to send.
BigInteger amount = new BigInteger("1");

// Wallet to send from.
Wallet wallet = new Wallet("snYP7oArxKepd3GPDcrjMsJYiJeJB");

// Destination address.
String destinationAddress = "X7u4MQVhU2YxS4P9fWzQjnNuDRUkP3GM6kiVjTjcQgUU3Jr";

String transactionHash = xpringClient.send(amount, destinationAddress, wallet);
```

### Utilities

#### Address validation

The `Utils` object provides an easy way to validate addresses.

```java
import io.xpring.xrpl.Utils;

String rippleClassicAddress = "rnysDDrRXxz9z66DmCmfWpq4Z5s4TyUP3G";
String rippleXAddress = "X7jjQ4d6bz1qmjwxYUsw6gtxSyjYv5iWPqPEjGqqhn9Woti";
String bitcoinAddress = "1DiqLtKZZviDxccRpowkhVowsbLSNQWBE8";

Utils.isValidAddress(rippleClassicAddress); // returns true
Utils.isValidAddress(rippleXAddress); // returns true
Utils.isValidAddress(bitcoinAddress); // returns false
```

You can also validate if an address is an X-Address or a classic address.

```java
import io.xpring.xrpl.Utils;

String rippleClassicAddress = "rnysDDrRXxz9z66DmCmfWpq4Z5s4TyUP3G";
String rippleXAddress = "X7jjQ4d6bz1qmjwxYUsw6gtxSyjYv5iWPqPEjGqqhn9Woti";
String bitcoinAddress = "1DiqLtKZZviDxccRpowkhVowsbLSNQWBE8";

Utils.isValidXAddress(rippleClassicAddress); // returns false
Utils.isValidXAddress(rippleXAddress); // returns true
Utils.isValidXAddress(bitcoinAddress); // returns false

Utils.isValidClassicAddress(rippleClassicAddress); // returns true
Utils.isValidClassicAddress(rippleXAddress); // returns false
Utils.isValidClassicAddress(bitcoinAddress); // returns false
```

### X-Address Encoding

You can encode and decode X-Addresses with the SDK.

```java
import io.xpring.xrpl.Utils;

String rippleClassicAddress = "rnysDDrRXxz9z66DmCmfWpq4Z5s4TyUP3G"
ClassicAddress classicAddress = ImmutableClassicAddress.builder()
  .address("rnysDDrRXxz9z66DmCmfWpq4Z5s4TyUP3G")
  .tag(12345)
  .build();

// Encode an X-Address.
String xAddress = Utils.encodeXAddress(rippleClassicAddress); // X7jjQ4d6bz1qmjwxYUsw6gtxSyjYv5xRB7JM3ht8XC4P45P

// Decode an X-Address.
ClassicAddressdecodedClassicAddress = Utils.decodeXAddress(xAddress);
System.out.println(decodedClassicAddress.address()); // rnysDDrRXxz9z66DmCmfWpq4Z5s4TyUP3G
System.out.println(decodedClassicAddress.tag()); // 12345
```

# Contributing

Pull requests are welcome! To get started with building this library and opening pull requests, please see [contributing.md](CONTRIBUTING.md).

Thank you to all the users who have contributed to this library!

<a href="https://github.com/xpring-eng/xpring4j/graphs/contributors">
  <img src="https://contributors-img.firebaseapp.com/image?repo=xpring-eng/xpring4j" />
</a>

# License

Xpring SDK is available under the MIT license. See the [LICENSE](LICENSE) file for more info.
