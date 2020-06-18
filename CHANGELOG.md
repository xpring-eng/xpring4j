# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## Unreleased

## Fixed
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
