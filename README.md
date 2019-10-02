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

Xpring4j is available as a Java Library. Simply add the following to your `pom.xml`:

```xml
<repository>
  <id>github</id>
  <name>GitHub xpring-eng Apache Maven Packages</name>
  <url>https://maven.pkg.github.com/xpring-eng</url>
</repository>
```

### Server-Side Component

The server-side component sends client-side requests to an XRP Node.

To get developers started right away, Xpring provides the server-side component as a hosted service, which proxies requests from client-side libraries to a hosted XRP Node. Developers can reach the endpoint at:

```
grpc.xpring.tech:80
```

Xpring is working on building a zero-config way for XRP node users to deploy and use the adapter as an open-source component of [rippled](https://github.com/ripple/rippled). Watch this space!

## Usage

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
import xpring.io.Wallet;

String mnemonic = "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about";

Wallet hdWallet1 = new Wallet(mnemonic, null); // Has default derivation path
Wallet hdWallet2 = new Wallet(mnemonic, Wallet.getDefaultDerivationPath()); // Same as hdWallet1

Wallet hdWallet = new Wallet(mnemonic, "m/44'/144'/0'/0/1"); // Wallet with custom derivation path.
```

##### Seed-Based Wallets

You can construct a seed based wallet by passing a base58check encoded seed string.

```java
import xpring.io.Wallet;

Wallet seedWallet = new Wallet("snRiAJGeKCkPVddbjB3zRwwoiYDBm1M");
```

#### Wallet Generation

Xpring4j can generate a new and random HD Wallet. The result of a wallet generation call is a tuple which contains the following:

- A randomly generated mnemonic
- The derivation path used, which is the default path
- A reference to the new wallet

```java
import xpring.io.Wallet;

// Generate a random wallet.
WalletGenerationResult generationResult = Wallet.generateRandomWallet();
Wallet newWallet = generationResult.getWallet();

// Wallet can be recreated with the artifacts of the initial generation.
Wallet copyOfNewWallet = new Wallet(generationResult.getMnemonic(), generationResult.getDerivationPath());
```

#### Wallet Properties

A generated wallet can provide its public key, private key, and address on the XRP ledger.

```java
import xpring.io.Wallet;

String mnemonic = "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about";

Wallet wallet = new Wallet(mnemonic, null);

System.out.println(wallet.getAddress()); // rHsMGQEkVNJmpGWs8XUBoTBiAAbwxZN5v3
System.out.println(wallet.getPublicKey()); // 031D68BC1A142E6766B2BDFB006CCFE135EF2E0E2E94ABB5CF5C9AB6104776FBAE
System.out.println(wallet.getPrivateKey()); // 0090802A50AA84EFB6CDB225F17C27616EA94048C179142FECF03F4712A07EA7A4
```

#### Signing / Verifying

A wallet can also sign and verify arbitrary hex messages. Generally, users should use the functions on `XpringClient` to perform cryptographic functions rather than using these low level APIs.

```java
import xpring.io.Wallet;

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

XpringClient xpringClient = new XpringClient();
```

#### Retrieving a Balance

A `XpringClient` can check the balance of an account on the ledger.

```java
import io.xpring.xrpl.XpringClient;
import java.math.BigInteger;

XpringClient xpringClient = new XpringClient();

String address = "rHsMGQEkVNJmpGWs8XUBoTBiAAbwxZN5v3";
BigInteger balance = xpringClient.getBalance(XRPL_ADDRESS);
System.out.println(balance); // Logs a balance in drops of XRP
```

#### Sending XRP

A `XpringClient` can send XRP to other [accounts](https://xrpl.org/accounts.html) on the ledger.

```java
import java.math.BigInteger;
import io.xpring.SubmitSignedTransactionResponseOuterClass.SubmitSignedTransactionResponse;
import io.xpring.Wallet;
import io.xpring.xrpl.XpringClient;

BigInteger amount = new BigInteger("1");
Wallet wallet = new Wallet("snYP7oArxKepd3GPDcrjMsJYiJeJB");

SubmitSignedTransactionResponse response = xpringClient.send(amount, "rsegqrgSP8XmhCYwL9enkZ9BNDNawfPZnn", wallet);

System.out.println(response.getEngineResultMessage());
```

### Utilities

#### Address validation

The `Utils` object provides an easy way to validate addresses.

```java
import io.xpring.Utils;

String rippleAddress = "rnysDDrRXxz9z66DmCmfWpq4Z5s4TyUP3G";
String bitcoinAddress = "1DiqLtKZZviDxccRpowkhVowsbLSNQWBE8";

Utils.isValidAddress(rippleAddress); // returns true
Utils.isValidAddress(bitcoinAddress); // returns false
```

## Development

To get set up for development on XpringJ, run the following commands:

```shell
# Clone repository
$ git clone https://github.com/xpring-eng/xpring4j.git
$ cd xpring4j

# Pull submodules
$ git submodule init
$ git submodule update --remote --recursive --init
```
