# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## Version 2.0.0 - Feb 24, 2020

### rippled support

This release adds support for the new rippled protocol buffers and adds browser compatibility support.

New Protocol Buffers from rippled
rippled implemented protocol buffer support natively, which uses a new and incompatible set of protocol buffers. The implementation in rippled was completed in: ripple/rippled#3159. Xpring4j is now capable of using these protocol buffers. This change is opt-in and non-breaking.

To switch to these protocol buffers, pass true to the useNewProtocolBuffer parameter in XpringClient's constructor. The default for this field is false. The remote rippled node must have gRPC enabled.

### Additional fixes

This release contains some breaking changes that fix and productionize this library. In particular, users will now have to provide a URL for a remote gRPC node when using `XpringClient`. Clients can use `grpc.xpring.tech:80` if they weish to use Xpring's hosted TestNet service. 

Additionally, exceptions were mistakenly named `XpringKitException`, which was a reference to the Swift variant of this library. This is a simple rename, no functionality is added, changed or removed.

### Breaking Changes
- Renamed `XpringKitException` to `XpringException`. XpringKit is the name of the Swift flavor of the Xpring SDK.
- Require a gRPC parameter in XpringClient's initializer. A default value is no longer provided. 

