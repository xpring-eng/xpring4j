# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## 6.1.1 - 2020-09-01

This release contains updated dependencies for stability and security.

## 6.1.0 - 2020-08-17

### Added
- A new method, `sendWithDetails`, is exposed on the `XrpClient` and `XpringClient`,
allowing memo data to be attached to the transaction when sending XRP.
- A new method `enableDepositAuth` is added to `XrpClient` which enables Deposit Authorization for the specified XRPL account.
(See https://xrpl.org/depositauth.html)

#### Removed
- `Utils.byteArrayToHex` is deprecated.  Please use `CommonUtils.byteArrayToHex`.
- `Utils.hexStringToByteArray` is deprecated.  Please use `CommonUtils.stringToByteArray`.

## 6.0.2 - 2020-08-01

This release contains updated dependencies for stability and security.

## 6.0.1 - 2020-07-15

This release contains updated dependencies for stability and security.

## 6.0.0 - 2020-06-25

This new release contains production ready classes for [PayID](https://payid.org).

This release also provides idiomatic capitalization. Previously, classes that were 'PayID' are now named as 'PayId' and classes which were named as 'XRP' are now named 'Xrp'.

### Added
- A new method, `cryptoAddressForPayId`, replaces the `addressForPayId` method in `PayIdClient`.
- A new method, `allAddressesForPayId`, is added to `PayIdClient`.

#### Removed
- `PayIDClient` was deprecated for two releases and has been removed. Use `PayIdClient` instead.
- `PayIDUtils` was deprecated for two releases and has been removed. Use `PayIdUtils` instead.
- `XRPPayIDClient` was deprecated for two releases and has been removed. Use `XrpPayIdClient` instead.
- `XRPPayIDClientInterface` was deprecated for two releases and has been removed. Use `XrpPayIdClientInterface` instead.
- `JavaScriptPayIDUtils` was deprecated for two releases and has been removed. Use `JavaScriptPayIdUtils` instead.
- `AbstractPayID` was deprecated for two releases and has been removed. Use `AbstractPayId` instead.
- `PayId` is deprecated. Please use the idiomatically cased `PayID` interface instead.
- `PayIdComponents` was deprecated for two releases and has been removed. Use `PayIDComponents` interface instead.
- `PayIDException` was deprecated for two releases and has been removed. Use `PayIdException` class instead.
- `PayIDExceptionType` was deprecated for two releases and has been removed. Use `PayIdExceptionType` enum instead.

- `XRPLNetwork` was deprecated for two releases and has been removed. Use `XRPLNetwork` instead.
- `DefaultXRPClient` was deprecated for two releases and has been removed. Use `DefaultXrpClient` instead.
- `XRPClient` was deprecated for two releases and has been removed. Use `XrpClient` instead.
- `XRPClientDecorator` was deprecated for two releases and has been removed. Use `XrpClientDecorator` instead.
- `XRPClientInterface` was deprecated for two releases and has been removed. Use `XrpClientInterface` instead.
- `XRPException` was deprecated for two releases and has been removed. Use `XrpException` instead.
- `XRPExceptionType` was deprecated for two releases and has been removed. Use `XrpExceptionType` instead.
- `XRPClient` was deprecated for two releases and has been removed. Use `XrpClient` instead.
- `XRPCurrencyAmount` was deprecated for two releases and has been removed. Use `XrpCurrencyAmount` instead.
- `XRPIssuedCurrency` was deprecated for two releases and has been removed. Use `XrpIssuedCurrency` instead.
- `XRPMemo` was deprecated for two releases and has been removed. Use `XrpMemo` instead.
- `XRPPath` was deprecated for two releases and has been removed. Use `XrpPath` instead.
- `XRPPathElement` was deprecated for two releases and has been removed. Use `XrpPathElement` instead.
- `XRPPayment` was deprecated for two releases and has been removed. Use `XrpPayment` instead.
- `XRPSigner` was deprecated for two releases and has been removed. Use `XrpSigner` instead.
- `XRPTransaction` was deprecated for two releases and has been removed. Use `XrpTransaction` instead.
- `XRPCurrency` was deprecated for two releases and has been removed. Use `XrpCurrency` instead.

- The `network` parameter passed to the constructor of `PayIdClient` has been removed.  Clients should
favor calling the new `cryptoAddressForPayId` method which allows them to specify the network at request time.
- `addressForPayId` method has been removed from `PayIdClient` and replaced with `cryptoAddressForPayId`

## 5.2.3 - 2020-06-23

### Fixed

- The fix for destination tags in 5.2.2 was incorrectly applied only to the deprecated class `XRPClient`. This release applies it to `XrpClient` as well.

## 5.2.2 - 2020-06-18

### Fixed
- Destination tags were being dropped from payments. This release fixes the issue.

## 5.2.1 - 2020-06-16

### Added
- `XrpPayment` and `XrpTransaction` now contain X-address representations of their address and tag fields.
  (See https://xrpaddress.info/)
	
#### Deprecated
- `XrpTransaction.account` and `XrpTransaction.sourceTag` are deprecated.
   Please use the X-address encoded field `sourceXAddress` instead.
- `XrpPayment.destination` and `XrpPayment.destinationTag` are deprecated.  
   Please use the X-address encoded field `destinationXAddress` instead.

### Added
- `PayIdClient` provides the functionality of `PayIDClient` under an idiomatically cased name.
- `PayIdUtils` provides the functionality of `PayIDUtils` under an idiomatically cased name.
- `XrpPayIdClient` provides the functionality of `XRPPayIDClient` under an idiomatically cased name.
- `XrpPayIdClientInterface` provides the functionality of `XRPPayIDClientInterface` under an idiomatically cased name.
- `JavaScriptPayIdUtils` provides the functionality of `JavaScriptPayIDUtils` under an idiomatically cased name.

### Deprecated
- `PayIDClient` is deprecated, use the idiomatically named `PayIdClient` instead.
- `PayIDUtils` is deprecated, use the idiomatically named `PayIdUtils` instead.
- `XRPPayIDClient` is deprecated, use the idiomatically named `XrpPayIdClient` instead.
- `XRPPayIDClientInterface` is deprecated, use the idiomatically named `XrpPayIdClientInterface` instead.
- `JavaScriptPayIDUtils` is deprecated, use the idiomatically named `JavaScriptPayIdUtils` instead.

## 5.2.0 - 2020-06-01

### Added
- A new method, `getPayment`, added to `XRPClient` for retrieving payment transactions by hash.
- `XrplNetwork` provides the functionality of `XRPLNetwork` under an idiomatic cased name.
- `DefaultXrpClient` provides the functionality of `DefaultXRPClient` under an idiomatic cased name.
- `XrpClient` provides the functionality of `XRPClient` under an idiomatic cased name.
- `XrpClientDecorator` provides the functionality of `XRPClientDecorator` under an idiomatic cased name.
- `XrpClientInterface` provides the functionality of `XRPClientInterface` under an idiomatic cased name.
- `XrpException` provides the functionality of `XRPException` under an idiomatic cased name.
- `XrpExceptionType` provides the functionality of `XRPExceptionType` under an idiomatic cased name.
- `XrpClient` provides the functionality of `XRPClient` under an idiomatic cased name.
- `XrpCurrencyAmount` provides the functionality of `XRPCurrencyAmount` under an idiomatic cased name.
- `XrpIssuedCurrency` provides the functionality of `XRPIssuedCurrency` under an idiomatic cased name.
- `XrpMemo` provides the functionality of `XRPMemo` under an idiomatic cased name.
- `XrpPath` provides the functionality of `XRPPath` under an idiomatic cased name.
- `XrpPathElement` provides the functionality of `XRPPathElement` under an idiomatic cased name.
- `XrpPayment` provides the functionality of `XRPPayment` under an idiomatic cased name.
- `XrpSigner` provides the functionality of `XRPSigner` under an idiomatic cased name.
- `XrpTransaction` provides the functionality of `XRPTransaction` under an idiomatic cased name.
- `XrpCurrency` provides the functionality of `XRPCurrency` under an idiomatic cased name.

### Deprecated
- `XRPLNetwork` is deprecated, please use the idiomatically named `XRPLNetwork` instead.
- `DefaultXRPClient` is deprecated, please use the idiomatically named `DefaultXrpClient` instead.
- `XRPClient` is deprecated, please use the idiomatically named `XrpClient` instead.
- `XRPClientDecorator` is deprecated, please use the idiomatically named `XrpClientDecorator` instead.
- `XRPClientInterface` is deprecated, please use the idiomatically named `XrpClientInterface` instead.
- `XRPException` is deprecated, please use the idiomatically named `XrpException` instead.
- `XRPExceptionType` is deprecated, please use the idiomatically named `XrpExceptionType` instead.
- `XRPClient` is deprecated, please use the idiomatically named `XrpClient` instead.
- `XRPCurrencyAmount` is deprecated, please use the idiomatically named `XrpCurrencyAmount` instead.
- `XRPIssuedCurrency` is deprecated, please use the idiomatically named `XrpIssuedCurrency` instead.
- `XRPMemo` is deprecated, please use the idiomatically named `XrpMemo` instead.
- `XRPPath` is deprecated, please use the idiomatically named `XrpPath` instead.
- `XRPPathElement` is deprecated, please use the idiomatically named `XrpPathElement` instead.
- `XRPPayment` is deprecated, please use the idiomatically named `XrpPayment` instead.
- `XRPSigner` is deprecated, please use the idiomatically named `XrpSigner` instead.
- `XRPTransaction` is deprecated, please use the idiomatically named `XrpTransaction` instead.
- `XRPCurrency` is deprecated, please use the idiomatically named `XrpCurrency` instead.

### Added
- A new class, `AbstractPayId`, replaces the functionality in `AbstractPayID` with an idiomatically cased name.
- A new interface, `PayId`, replaces the functionality in `PayID` with an idiomatically cased name.
- A new interface, `PayIdComponents`, replaces the functionality in `PayIDComponents` with an idiomatically cased name.

### Deprecated
- `AbstractPayID` is deprecated. Please use the idiomatically cased `AbstractPayId` class instead.
- `PayId` is deprecated. Please use the idiomatically cased `PayID` interface instead.
- `PayIdComponents` is deprecated. Please use the idiomatically cased `PayIDComponents` interface instead.

## 5.1.1 - 2020-05-15

This release contains minor deprecations of names of methods and classes to make this library more idiomatic with the wider Java ecosystem.

### Added
- A new class, `PayIdException`, replaces the functionality in `PayIDException` with an idiomatically cased name.
- A new enum, `PayIdExceptionType`, replaces the functionality in `PayIDExceptionType` with an idiomatically cased name.

### Deprecated
- `PayIDException` is deprecated. Please use the idiomatically cased `PayIdException` class instead.
- `PayIDExceptionType` is deprecated. Please use the idiomatically cased `PayIdExceptionType` enum instead.

## 5.1.0 - May 6, 2020

### Added
- `xrpToDrops` and `dropsToXrp` conversion utilities added to `io.xpring.xrpl.Utils`

## 5.0.0

### Added
- `XRPTransaction` contains additional synthetic fields to represent the timestamp, hash, and deliveredAmount of the transaction.

### Changed
- `XRPClient` requires a new parameter in it's constructor that identifies the network it is attached to.

## 4.0.0

This major release contains new features in XRP to check for existence of an account and to retrieve payment history for an account.

We make several breaking API changes in order to accomodate some larger refactors across the codebase and standardize interfaces. In particular, exception naming is refactored.

Additionally, this release turns down support for the legacy protocol buffers. This functionality has been defaulted to off and slated for removal for several releases.

### Added
- Add a new `paymentHistory` method to `XRPClient`. This method allows clients to retrieve payment history for an address.
- A new `accountExists` method added to XRPClient which determines whether a given address exists on the XRP Ledger.

### Changed
- The `XRPClient` constructor requires a new parameter that identifies the network it is connected to.
- Classes in `io.xpring.ilp` now throw an `IlpException` rather than a `XpringException`.
- Classes in `io.xpring.xrp` now throw an `XRPException` rather than a `XpringException`.
- `IlpClient` methods now throw `IlpException`s if something goes wrong during the call (either client side or server side). This is only breaking if users are handling special error cases, which were previously `StatusRuntimeException`s

### Removed
- The `XpringException` class is removed and no longer exists.
- All legacy services are removed from XpringKit. All RPCs go through [rippled's protocol buffer API](https://github.com/ripple/rippled/pull/3254).

## 3.0.0 - March 24, 2020
#### Added
- A new `getPaymentStatus` is added which retrieves the status of payment transactions.

#### Removed

- `XpringClient` is removed from XpringKit. This class has been deprecated since 1.5.0. Clients should use `XRPClient` instead.
- `getTransactionStatus` is removed. Please use `getPaymentStatus` instead.

#### Changed
- `XRPClient` now uses [rippled's protocol buffer API](https://github.com/ripple/rippled/pull/3254) rather than the legacy API. Users who wish to use the legacy API should pass `false` for `useNewProtocolBuffers` in the constructor.
- Introduces a breaking change to `IlpClient` API.
	- `IlpClient.getBalance` now returns an `AccountBalance` instead of a protobuf generated `GetBalanceResponse`.
	- `IlpClient.sendPayment` now consumes a `PaymentRequest` instead of individual parameters, and now returns a `PaymentResult` instead of a protobuf generated `SendPaymentResponse`

## 2.2.0 - March 6, 2020

This release contains new functionality for InterLedger Protocol (ILP) in the `ILPClient` class. 

This release also deprecates `XpringClient` and creates a new class called `XRPClient` with the same API. Clients should move to using `XRPClient` at their convenience. 

### Added
- `XRPClient` is a renamed version of XpringClient to better clarify the class's role in the SDK
- `ILPClient` is a new class for interacting with the ILP Network.

### Deprecated
- The `XpringClient` class is deprecated. Please use `XRPClient` instead.

## 2.1.0 - Feb 28, 2020

This version uses new protocol buffers from rippled which have breaking changes in them. Specifically, the breaking changes include:
- Re-ordering and repurposing of fields in order to add additional layers of abstraction
- Change package from `rpc.v1` to `org.xrpl.rpc.v1`

This change is transparent to public API users. However, clients will need to connect to a rippled node which is built at any commit after [#3254](https://github.com/ripple/rippled/pull/3254).

## Version 2.0.0 - Feb 24, 2020

### rippled Support

This release adds support for the new rippled protocol buffers and adds browser compatibility support.

New Protocol Buffers from rippled
rippled implemented protocol buffer support natively, which uses a new and incompatible set of protocol buffers. The implementation in rippled was completed in: ripple/rippled#3159. Xpring4j is now capable of using these protocol buffers. This change is opt-in and non-breaking.

To switch to these protocol buffers, pass true to the useNewProtocolBuffer parameter in XpringClient's constructor. The default for this field is false. The remote rippled node must have gRPC enabled.

### Additional Fixes

This release contains some breaking changes that fix and productionize this library. In particular, users will now have to provide a URL for a remote gRPC node when using `XpringClient`. Clients can use `grpc.xpring.tech:80` if they wish to use Xpring's hosted TestNet service. 

Additionally, exceptions were mistakenly named `XpringKitException`, which was a reference to the Swift variant of this library. This is a simple rename, no functionality is added, changed or removed.

#### Breaking Changes
- Renamed `XpringKitException` to `XpringException`. XpringKit is the name of the Swift flavor of the Xpring SDK.
- Require a gRPC parameter in XpringClient's initializer. A default value is no longer provided. 
