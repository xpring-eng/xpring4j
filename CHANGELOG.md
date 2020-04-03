# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
#### Changed
- `IlpClient` methods now throw `IlpException`s if something goes wrong during the call 
    (either client side or server side).  
    This is only breaking if users are handling special error cases, which were previously `StatusRuntimeException`s

#### Added
- A new `accountExists` method added to XRPClient which determines whether a given address exists on the XRP Ledger.

#### Removed

- All legacy services are removed from XpringKit. All RPC's go through [rippled's protocol buffer API](https://github.com/ripple/rippled/pull/3254).

#### Changed
- The `XRPClient` constructor requires a new parameter that identifies the network it is connected to.


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
