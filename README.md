Build status: [![Build Status](https://travis-ci.org/bitcoin-solutions/multibit-hd.png?branch=master)](https://travis-ci.org/bitcoin-solutions/multibit-hd)

Project status: Alpha. Expect bugs and API changes. Not suitable for production, but early adopter developers should get on board.

### MultiBit HD

A Bitcoin wallet based on the Simplified Payment Verification (SPV) mode to provide very fast blockchain synchronization.
Public and private keys are held in a hierarchical deterministic (HD) wallet to provide much greater security than traditional
desktop clients. Support for external hardware wallets (such as the Trezor) is available.

### Technologies

* [Bitcoinj](https://code.google.com/p/bitcoinj/) - Providing various Bitcoin protocol utilities
* [Java HID API](https://code.google.com/p/javahidapi/) - Java library providing USB Human Interface Device (HID) native interface
* [Google Protocol Buffers](https://code.google.com/p/protobuf/) (protobuf) - For use with communicating with the Trezor device
* Java 7 and Swing

### Frequently asked questions (FAQ)

Here are some common questions that developers ask when they first encounter MBHD.

#### Why not Java 8 ?

At the time MBHD was being written (Q4 2013) Java 8 was not in production release and the sheer size of the packaged download
was coming in at 150Mb (18x MultiBit Classic and 3x the Java 7 packaged footprints). That footprint alone would be sufficient
to dramatically increase the cost of serving the application.

#### Why not JavaFX ?

JavaFX was only available as version 2.2 on Java 7 and the move to Java 8 was not going to happen. There were many significant
features missing in JavaFX which would only be fixed in Java 8 (e.g. right to left languages, integration with native platform
for Bitcoin URI protocol handling, reporting uncaught exceptions).

This technology was not suitable for the very wide range of people using MultiBit in all corners of the globe.

#### Why Swing ?

Although Swing is not pretty, there is a vast amount of support for it. The code is near bullet-proof for most use cases and it
fully supports internationalization which is a key requirement for MultiBit. Also, many of the supporting libraries for Swing
pre-date 2009 making it much harder for [dependency chain attacks](http://gary-rowe.com/agilestack/2013/07/03/preventing-dependency-chain-attacks-in-maven/) to take place.

Swing also allows us to smoothly integrate with the native platform which puts it ahead of JavaFX until at least Q4 2014.

#### Why the Nimbus look and feel ?

In Java 7 the Nimbus look and feel became integrated with the JDK. It provides a modern 2D rendered UI that is the same across
all platforms. It is highly customisable through simple themes and provides consistent painting behaviour across platforms. For
example to paint a button red in Swing using the Mac-only Aqua theme requires complex custom ButtonUI code.

Using Nimbus ensures that we don't have this or similar problems.

[Technical details on the default colours](http://docs.oracle.com/javase/tutorial/uiswing/lookandfeel/_nimbusDefaults.html#primary)

#### Is there a developer wiki ?

Yes. [The wiki pages](https://github.com/bitcoin-solutions/multibit-hd/wiki/_pages) provide comprehensive instructions for
developers that cover a variety of environments.

### Getting started

MBHD is a standard Maven build, but relies on some snapshot builds of libraries which won't be available in Maven Central.

In general you should check out the following from their respective source control repos and install them locally:

 * [MultiBit HD Hardware](https://github.com/bitcoin-solutions/mbhd-hardware) - use "master" branch
 * [Bitcoinj](https://code.google.com/p/bitcoinj/) - use "master" branch

Use the standard Maven build and install process:

```
$ mvn clean install
```

If you want to run the application within an IDE, you will need to run `MultiBitHD.main()` in the `mbhd-swing` module.

### Use cases documentation

The use cases are described here:
https://docs.google.com/document/d/18qtE5lmRzB32Sc9Ii37GySJGLKx3VNypBkjnHbNjdik/edit?usp=drive_web
