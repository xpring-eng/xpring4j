
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

### Client-Side Library

Xpring4j is available as a Java library from Maven Central. Simply add the following to your `pom.xml`:

```xml
<dependency>
  <groupId>io.xpring</groupId>
  <artifactId>xpring4j</artifactId>
  <version>1.2.0</version>
</dependency>
```

### rippled Node

Xpring SDK needs to communicate with a rippled node which has gRPC enabled. Consult the [rippled documentation](https://github.com/ripple/rippled#build-from-source) for details on how to build your own node.

To get developers started right away, Xpring currently provides nodes:

```
# TestNet
test.xrp.xpring.io:50051

# MainNet
main.xrp.xpring.io:50051
```

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
import io.xpring.xrpl.Wallet;


String mnemonic = "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about";

Wallet hdWallet1 = new Wallet(mnemonic, null); // Has default derivation path
Wallet hdWallet2 = new Wallet(mnemonic, Wallet.getDefaultDerivationPath()); // Same as hdWallet1

Wallet hdWallet = new Wallet(mnemonic, "m/44'/144'/0'/0/1"); // Wallet with custom derivation path.
```

##### Seed-Based Wallets

You can construct a seed based wallet by passing a base58check encoded seed string.

```java
import io.xpring.xrpl.Wallet;

Wallet seedWallet = new Wallet("snRiAJGeKCkPVddbjB3zRwwoiYDBm1M");
```

#### Wallet Generation

Xpring4j can generate a new and random HD Wallet. The result of a wallet generation call is a tuple which contains the following:

- A randomly generated mnemonic
- The derivation path used, which is the default path
- A reference to the new wallet

```java
import io.xpring.xrpl.Wallet;

// Generate a random wallet.
WalletGenerationResult generationResult = Wallet.generateRandomWallet();
Wallet newWallet = generationResult.getWallet();

// Wallet can be recreated with the artifacts of the initial generation.
Wallet copyOfNewWallet = new Wallet(generationResult.getMnemonic(), generationResult.getDerivationPath());
```

#### Wallet Properties

A generated wallet can provide its public key, private key, and address on the XRP ledger.

```java
import io.xpring.xrpl.Wallet;

String mnemonic = "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about";

Wallet wallet = new Wallet(mnemonic, null);

System.out.println(wallet.getAddress()); // X7u4MQVhU2YxS4P9fWzQjnNuDRUkP3GM6kiVjTjcQgUU3Jr
System.out.println(wallet.getPublicKey()); // 031D68BC1A142E6766B2BDFB006CCFE135EF2E0E2E94ABB5CF5C9AB6104776FBAE
System.out.println(wallet.getPrivateKey()); // 0090802A50AA84EFB6CDB225F17C27616EA94048C179142FECF03F4712A07EA7A4
```

#### Signing / Verifying

A wallet can also sign and verify arbitrary hex messages. Generally, users should use the functions on `XRPClient` to perform cryptographic functions rather than using these low level APIs.

```java
import io.xpring.xrpl.Wallet;

String mnemonic = "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about";
String message = "deadbeef";

Wallet wallet = new Wallet(mnemonic, null);

String signature = wallet.sign(message);
wallet.verify(message, signature); // true
```

### XRPClient

`XRPClient` is a gateway into the XRP Ledger. `XRPClient` is initialized with a single parameter, which is the URL of the remote adapter (see [Server-Side Component][#server-side-component]).

```java
import io.xpring.xrpl.XRPClient;

String grpcURL = "test.xrp.xpring.io:50051"; // TestNet URL, use main.xrp.xpring.io:50051 for MainNet
XRPClient xrpClient = new XRPClient(grpcURL, true);
```

#### Retrieving a Balance

An `XRPClient` can check the balance of an account on the XRP Ledger.

```java
import io.xpring.xrpl.XRPClient;
import java.math.BigInteger;

String grpcURL = "test.xrp.xpring.io:50051"; // TestNet URL, use main.xrp.xpring.io:50051 for MainNet
XRPClient xrpClient = new XRPClient(grpcURL, true);

String address = "X7u4MQVhU2YxS4P9fWzQjnNuDRUkP3GM6kiVjTjcQgUU3Jr";
BigInteger balance = xrpClient.getBalance(address);
System.out.println(balance); // Logs a balance in drops of XRP
```

### Checking Transaction Status

An `XRPClient` can check the status of an transaction on the XRP Ledger.

Xpring4J returns the following transaction states:
- `SUCCEEDED`: The transaction was successfully validated and applied to the XRP Ledger.
- `FAILED:` The transaction was successfully validated but not applied to the XRP Ledger. Or the operation will never be validated.
- `PENDING`: The transaction has not yet been validated, but may be validated in the future.
- `UNKNOWN`: The transaction status could not be determined.

**Note:** For more information, see [Reliable Transaction Submission](https://xrpl.org/reliable-transaction-submission.html) and [Transaction Results](https://xrpl.org/transaction-results.html).

These states are determined by the `TransactionStatus` enum.

```java
import io.xpring.xrpl.XRPClient;
import io.xpring.xrpl.TransactionStatus;

String grpcURL = "test.xrp.xpring.io:50051"; // TestNet URL, use main.xrp.xpring.io:50051 for MainNet
XRPClient xrpClient = new XRPClient(grpcURL, true);

String transactionHash = "9FC7D277C1C8ED9CE133CC17AEA9978E71FC644CE6F5F0C8E26F1C635D97AF4A";
TransactionStatus transactionStatus = xrpClient.xrpClient.getTransactionStatus(transactionHash); // TransactionStatus.SUCCEEDED
```

**Note:** The example transactionHash may lead to a "Transaction not found." error because the TestNet is regularly reset, or the accessed node may only maintain one month of history.  Recent transaction hashes can be found in the [XRP Ledger Explorer](https://livenet.xrpl.org)

#### Sending XRP

An `XRPClient` can send XRP to other [accounts](https://xrpl.org/accounts.html) on the XRP Ledger.

**Note:** The payment operation will block the calling thread until the operation reaches a definitive and irreversible success or failure state.

```java
import java.math.BigInteger;
import io.xpring.xrpl.Wallet;
import io.xpring.xrpl.XRPClient;

String grpcURL = "test.xrp.xpring.io:50051"; // TestNet URL, use main.xrp.xpring.io:50051 for MainNet
XRPClient xrpClient = new XRPClient(grpcURL, true);

// Amount of XRP to send.
BigInteger amount = new BigInteger("1");

// Wallet to send from.
WalletGenerationResult walletGenerationResult = Wallet.generateRandomWallet();
Wallet wallet = walletGenerationResult.getWallet();

// Destination address.
String destinationAddress = "X7u4MQVhU2YxS4P9fWzQjnNuDRUkP3GM6kiVjTjcQgUU3Jr";

String transactionHash = xrpClient.send(amount, destinationAddress, wallet);
```

**Note:** The above example will yield an "Account not found." error because the randomly generated wallet contains no XRP.

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
