Status: [![Build Status](https://travis-ci.org/bitcoin-solutions/multibit-hd.png?branch=master)](https://travis-ci.org/bitcoin-solutions/multibit-hd)

### MultiBit HD

A Bitcoin wallet based on the Simplified Payment Verification (SPV) mode to provide very fast blockchain synchronization.
Public and private keys are held in a hierarchical deterministic (HD) wallet to provide much greater security than traditional
desktop clients. Support for external hardware wallets (such as the Trezor) is available.

### Technologies

* [Bitcoinj](https://code.google.com/p/bitcoinj/) - Providing various Bitcoin protocol utilities
* [Java HID API](https://code.google.com/p/javahidapi/) - Java library providing USB Human Interface Device (HID) native interface
* [Google Protocol Buffers](https://code.google.com/p/protobuf/) (protobuf) - For use with communicating with the Trezor device
* Java 7 and Swing

#### Why not Java 8?

At the time MBHD was being written (Q4 2013) Java 8 was not in production release and the sheer size of the packaged download
was coming in at 150Mb (18x MultiBit Classic and 3x the Java 7 packaged footprints). That footprint alone would be sufficient
to dramatically increase the cost of serving the application.

#### Why not JavaFX?

JavaFX was only available as version 2.2 on Java 7 and the move to Java 8 was not going to happen. There were many significant
features missing in JavaFX which would only be fixed in Java 8 (e.g. right to left languages, integration with native platform
for Bitcoin URI protocol handling, reporting uncaught exceptions).

This technology was not suitable for the very wide range of people using MultiBit.

#### Why Swing?

Although Swing is not pretty, there is a vast amount of support for it. The code is near bullet-proof for most use cases and it
fully supports internationalization which is a key requirement for MultiBit. Also, many of the supporting libraries for Swing
pre-date 2009 making it much harder for dependency chain attacks to take place.

Swing also allows us to smoothly integrate with the native platform which puts it ahead of JavaFX until at least Q4 2013.

### Project status

Alpha: Expect bugs and API changes. Not suitable for production, but early adopter developers should get on board.

### Getting started

Have a read of [the wiki pages](https://github.com/bitcoin-solutions/multibit-hd/wiki/_pages) which gives comprehensive instructions
for a variety of environments.

### IDE configuration notes

You can set the application name for MacOS using a VM argument of

```
-Xdock:name="MultiBit HD"
```

### Use cases documentation

The use cases are described here:
https://docs.google.com/document/d/18qtE5lmRzB32Sc9Ii37GySJGLKx3VNypBkjnHbNjdik/edit?usp=drive_web
