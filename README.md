
[![CircleCI](https://img.shields.io/circleci/build/github/xpring-eng/xpring4j?style=flat-square)](https://circleci.com/gh/xpring-eng/Xpring4j)
[![CodeCov](https://img.shields.io/codecov/c/github/xpring-eng/xpring4j?style=flat-square)](https://codecov.io/gh/xpring-eng/Xpring4J)
[![Dependabot Status](https://img.shields.io/static/v1?label=Dependabot&message=enabled&color=success&style=flat-square&logo=dependabot)](https://dependabot.com)

# Xpring4j

Xpring4j is the Java client-side library of Xpring SDK.

## Features

Xpring4j provides the following features:
- XRP:
    - Wallet generation and derivation (Seed-based or HD Wallet-based)
    - Address validation
    - Account balance retrieval
    - Sending XRP payments
- Interledger (ILP):
    - Account balance retrieval
    - Send ILP Payments

## Installation

### Client-Side Library

Xpring4j is available as a Java library from Maven Central. Simply add the following to your `pom.xml`:

```xml
<dependency>
  <groupId>io.xpring</groupId>
  <artifactId>xpring4j</artifactId>
  <version>5.2.3</version>
</dependency>
```

### rippled Node

Xpring SDK needs to communicate with a rippled node which has gRPC enabled. Consult the [rippled documentation](https://github.com/ripple/rippled#build-from-source) for details on how to build your own node.

To get developers started right away, Xpring currently provides nodes:

```
# Testnet
test.xrp.xpring.io:50051

# Mainnet
main.xrp.xpring.io:50051
```

### Hermes Node
Xpring SDK's `IlpClient` needs to communicate with Xpring's ILP infrastructure through an instance of [Hermes](https://github.com/xpring-eng/hermes-ilp).   

In order to connect to the Hermes instance that Xpring currently operates, you will need to create an ILP wallet [here](https://xpring.io/portal/ilp-wallet)

Once your wallet has been created, you can use the gRPC URL specified in your wallet, as well as your **access token** to check your balance
and send payments over ILP.

## Usage: XRP

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

A wallet can also sign and verify arbitrary hex messages. Generally, users should use the functions on `XrpClient` to perform cryptographic functions rather than using these low level APIs.

```java
import io.xpring.xrpl.Wallet;

String mnemonic = "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about";
String message = "deadbeef";

Wallet wallet = new Wallet(mnemonic, null);

String signature = wallet.sign(message);
wallet.verify(message, signature); // true
```

### XrpClient

`XrpClient` is a gateway into the XRP Ledger. `XrpClient` is initialized with a two parameters:
- The URL of the gRPC API on the remote rippled node
- An enum representing the XRPL Network the remote rippled node is attached to

```java
import io.xpring.common.idiomatic.XrplNetwork;
import io.xpring.xrpl.idiomatic.XrpClient;

String grpcURL = "test.xrp.xpring.io:50051"; // Testnet URL, use main.xrp.xpring.io:50051 for Mainnet
XrpClient xrpClient = new XrpClient(grpcURL, XrplNetwork.TEST);
```

#### Retrieving a Balance

An `XrpClient` can check the balance of an account on the XRP Ledger.

```java
import io.xpring.common.idiomatic.XrplLNetwork;
import io.xpring.xrpl.idiomatic.XrpClient;
import java.math.BigInteger;

String grpcURL = "test.xrp.xpring.io:50051"; // Testnet URL, use main.xrp.xpring.io:50051 for Mainnet
XrpClient xrpClient = new XrpClient(grpcURL, XrplNetwork.TEST);

String address = "X7u4MQVhU2YxS4P9fWzQjnNuDRUkP3GM6kiVjTjcQgUU3Jr";
BigInteger balance = xrpClient.getBalance(address);
System.out.println(balance); // Logs a balance in drops of XRP
```

### Checking Transaction Status

A `XrpClient` can check the status of an payment on the XRP Ledger.

This method can only determine the status of [payment transactions](https://xrpl.org/payment.html) which do not have the partial payment flag ([tfPartialPayment](https://xrpl.org/payment.html#payment-flags)) set.

Xpring4J returns the following transaction states:
- `SUCCEEDED`: The transaction was successfully validated and applied to the XRP Ledger.
- `FAILED:` The transaction was successfully validated but not applied to the XRP Ledger. Or the operation will never be validated.
- `PENDING`: The transaction has not yet been validated, but may be validated in the future.
- `UNKNOWN`: The transaction status could not be determined, the hash represented a non-payment type transaction, or the hash represented a transaction with the [tfPartialPayment](https://xrpl.org/payment.html#payment-flags) flag set.

**Note:** For more information, see [Reliable Transaction Submission](https://xrpl.org/reliable-transaction-submission.html) and [Transaction Results](https://xrpl.org/transaction-results.html).

These states are determined by the `TransactionStatus` enum.

```java
import io.xpring.common.idiomatic.XrplNetwork;
import io.xpring.xrpl.idiomatic.XrpClient;
import io.xpring.xrpl.TransactionStatus;

String grpcURL = "test.xrp.xpring.io:50051"; // Testnet URL, use main.xrp.xpring.io:50051 for Mainnet
XrpClient xrpClient = new XrpClient(grpcURL, XrplNetwork.TEST);

String transactionHash = "9FC7D277C1C8ED9CE133CC17AEA9978E71FC644CE6F5F0C8E26F1C635D97AF4A";
TransactionStatus transactionStatus = xrpClient.getPaymentStatus(transactionHash); // TransactionStatus.SUCCEEDED
```

**Note:** The example `transactionHash` may lead to a "Transaction not found." error because the Testnet is regularly reset, or the accessed node may only maintain one month of history.  Recent transaction hashes can be found in the [XRP Ledger Explorer](https://testnet.xrpl.org)

#### Retrieve specific payment

An `XRPClient` can return a specific payment transaction identified by hash.

```java
import io.xpring.xrpl.XRPClient;
import io.xpring.common.XRPLNetwork;
import io.xpring.xrpl.model.XRPTransaction;

String remoteURL = "test.xrp.xpring.io:50051"; // Testnet URL, use main.xrp.xpring.io:50051 for Mainnet
XRPClient xrpClient = new XRPClient(remoteURL, XRPLNetwork.TEST);
XRPTransaction payment = xrpClient.getPayment(transactionHash);
```

**Note:** The example `transactionHash` may lead to a "Transaction not found." error because the Testnet is regularly reset, or the accessed node may only maintain one month of history.  Recent transaction hashes can be found in the [XRP Ledger Explorer](https://testnet.xrpl.org)

#### Payment history

An `XrpClient` can return a list of payments to and from an account.

```java
import io.xpring.xrpl.idiomatic.XrpClient;
import io.xpring.common.idiomatic.XrplNetwork;
import io.xpring.xrpl.model.idiomatic.XrpTransaction;
import java.util.List;

String remoteURL = "test.xrp.xpring.io:50051"; // Testnet URL, use main.xrp.xpring.io:50051 for Mainnet
XrpClient xrpClient = new XrpClient(remoteURL, XrplNetwork.TEST);
String address = "XVMFQQBMhdouRqhPMuawgBMN1AVFTofPAdRsXG5RkPtUPNQ";
List<XrpTransaction> paymentHistory = xrpClient.paymentHistory(address);
```

#### Sending XRP

An `XrpClient` can send XRP to other [accounts](https://xrpl.org/accounts.html) on the XRP Ledger.

**Note:** The payment operation will block the calling thread until the operation reaches a definitive and irreversible success or failure state.

```java
import io.xpring.xrpl.Wallet;
import io.xpring.common.idiomatic.XrplNetwork;
import io.xpring.xrpl.idiomatic.XrpClient;
import java.math.BigInteger;

String grpcURL = "test.xrp.xpring.io:50051"; // Testnet URL, use main.xrp.xpring.io:50051 for Mainnet
XrpClient xrpClient = new XrpClient(grpcURL, XrplNetwork.TEST);

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

## Usage: PayID

Two classes are used to work with PayID: `PayIDClient` and `XRPPayIDClient`.

`PayIDClient` can resolve addresses on arbitrary cryptocurrency networks.

```java
// Resolve on Bitcoin Mainnet.
String btcNetwork = "btc-testnet";

PayIDClient btcPayIdClient = new PayIDClient(btcNetwork);
String payID = "georgewashington$xpring.money";

CryptoAddressDetails btcAddressComponents = btcPayIdClient.addressForPayID(payId);
System.out.println("Resolved to " + btcAddressComponents.getAddress());
```

### XRPPayIDClient

`XRPPayIDClient` can resolve addresses on the XRP Ledger network. The class always coerces returned addresses into an X-Address. (See https://xrpaddress.info/)

```java
XRPLNetwork xrpNetwork = XRPLNetwork.MAIN;

XRPPayIDClient xrpPayIdClient = new XRPPayIDClient(xrpNetwork);
String xrpAddress = xrpPayIdClient.xrpAddressForPayID(payId);
```

## Usage: ILP
### IlpClient
`IlpClient` is the main interface into the ILP network.  `IlpClient` must be initialized with the URL of a Hermes instance.
This can be found in your [wallet](https://xpring.io/portal/ilp-wallet).

All calls to `IlpClient` must pass an access token, which can be generated in your [wallet](https://xpring.io/portal/ilp-wallet). 

```java
import io.xpring.ilp.IlpClient;

String grpcUrl = "prod.grpcng.wallet.xpring.io"; // Testnet ILP Wallet URL
IlpClient ilpClient = new IlpClient(grpcUrl);
```

#### Retreiving a Balance
An `IlpClient` can check the balance of an account on a connector.

```java
import io.xpring.ilp.IlpClient;
import io.xpring.ilp.model.AccountBalance;

String grpcUrl = "prod.grpcng.wallet.xpring.io"; // Testnet ILP Wallet URL
IlpClient ilpClient = new IlpClient(grpcUrl);

AccountBalance balance = ilpClient.getBalance("demo_user", "2S1PZh3fEKnKg"); // Just a demo user on Testnet
System.out.println("Net balance was " + balance.netBalance() + " with asset scale " + balance.assetScale());
```

#### Sending a Payment
An `IlpClient` can send an ILP payment to another ILP address by supplying a [Payment Pointer](https://github.com/interledger/rfcs/blob/master/0026-payment-pointers/0026-payment-pointers.md)
and a sender's account ID

```java
import io.xpring.ilp.IlpClient;
import io.xpring.ilp.model.PaymentRequest;
import io.xpring.ilp.model.PaymentResponse;

String grpcUrl = "prod.grpcng.wallet.xpring.io"; // Testnet ILP Wallet URL
IlpClient ilpClient = new IlpClient(grpcUrl);

PaymentRequest paymentRequest = PaymentRequest.builder()
  .amount(amountToSend)
  .destinationPaymentPointer("$xpring.money/demo_receiver")
  .senderAccountId("demo_user")
  .build();

PaymentResponse payment = ilpClient.sendPayment(paymentRequest, "2S1PZh3fEKnKg");
```

## Usage: Xpring

Xpring components compose PayID and XRP components to make complex interactions easy.

```java
// The expected address of the gRPC server.
String grpcURL = "test.xrp.xpring.io:50051";

// A wallet with funds on testnet.
Wallet wallet = new Wallet("snYP7oArxKepd3GPDcrjMsJYiJeJB", true);

// The number of drops to send.
BigInteger dropsToSend = BigInteger.valueOf(10);

// The Pay ID to resolve.
String payID = "georgewashington$xpring.money";

// The network to resolve on.
XRPLNetwork network = XRPLNetwork.TEST;

XRPClient xrpClient = new XRPClient(grpcURL, network);
XRPPayIDClient payIdClient = new XRPPayIDClient(network);
XpringClient xpringClient = new XpringClient(payIdClient, xrpClient);

String hash = xpringClient.send(dropsToSend, payID, wallet);
```

# Contributing

Pull requests are welcome! To get started with building this library and opening pull requests, please see [contributing.md](CONTRIBUTING.md).

Thank you to all the users who have contributed to this library!

<a href="https://github.com/xpring-eng/xpring4j/graphs/contributors">
  <img src="https://contributors-img.firebaseapp.com/image?repo=xpring-eng/xpring4j" />
</a>

# License

Xpring SDK is available under the MIT license. See the [LICENSE](LICENSE) file for more info.
