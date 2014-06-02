Project status: Late-alpha. Expect bugs and API changes. Not suitable for production, but early adopter developers and early
testers should get on board.

### MultiBit HD (MBHD)

A desktop Hierarchical Deterministic Wallet (HDW) for Bitcoin using the Simplified Payment Verification (SPV) mode to provide very fast
blockchain synchronization.

The target audience is "international mainstream" which compels the user interface to remain as simple as possible while still
retaining advanced capabilities under the covers. In general this means

Support for external hardware wallets (such as the Trezor) is [available through the MultiBit Hardware project](https://github.com/bitcoin-solutions/mbhd-hardware).

### Technologies

* Java 7 and Swing
* [Bitcoinj](https://github.com/bitcoinj/bitcoinj) - Providing various Bitcoin protocol utilities (GitHub is the reference)
* [Java HID API](https://code.google.com/p/javahidapi/) - Java library providing USB Human Interface Device (HID) native interface
* [Google Protocol Buffers](https://code.google.com/p/protobuf/) (protobuf) - For use with serialization and hardware communications
* [Font Awesome](http://fortawesome.github.io/Font-Awesome/) - for iconography
* [JWrapper](http://www.jwrapper.com/) - for a smooth installation and update process

### Getting started

MBHD is a standard Maven build, but currently relies on some snapshot builds of libraries which aren't available in Maven Central.

#### Verify you have Maven 3+

Most IDEs (such as [Intellij Community Edition](http://www.jetbrains.com/idea/download/)) come with support for Maven built in,
but if not then you may need to [install it manually](http://maven.apache.org/download.cgi).

IDEs such as Eclipse may require the [m2eclipse plugin](http://www.sonatype.org/m2eclipse) to be configured.

To quickly check that you have Maven 3+ installed check on the command line:
```
$ mvn --version
```

#### Manually build and install Bitcoinj (optional)

The [MultiBit Staging repository](https://github.com/bitcoin-solutions/mbhd-maven) contains a `Bitcoinj-0.12-SNAPSHOT` that is aligned with the MultiBit HD `develop` branch. This can
be used for development builds but is certainly not suitable for production. The Bitcoinj occasionally gets updated but should not
be relied upon.

Ideally, developers should clone [Bitcoinj](https://code.google.com/p/bitcoinj/) and build it manually. You should start with the
HEAD of the `master` branch:
```
$ mvn clean install
```

#### Start the application (from an IDE)

To run the application within an IDE, simply execute `MultiBitHD.main()` in the `mbhd-swing` module. No command line parameters
are needed, although a Bitcoin URI is accepted.

#### Start the application (from the command line)

To run the application from the command line, first build with:
```
$ mvn clean package
```
then start the application using the shaded JAR:
```
$ java -jar mbhd-install/target/multibit-hd.jar
```
No command line parameters are needed, although a Bitcoin URI is accepted (the quotes are required to avoid URL decoding):
```
$ java -jar mbhd-install/target/multibit-hd.jar "bitcoin:1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty?amount=0.01&label=Please%20donate%20to%20multibit.org"
```
#### Multiple instances

MultiBit HD will avoid multiple instances by using port 8330 as a method of detecting another running instance. If port 8330 cannot
be bound MultiBit HD will assume that another instance is running and hand over any Bitcoin URI arguments present when it started.
It will then perform a hard shutdown terminating its own JVM. The other instance will react to receiving a Bitcoin URI message on
port 8330 by displaying an alert bar requesting the user to act upon the Bitcoin URI payment request.

### Frequently asked questions (FAQ)

Here are some common questions that developers ask when they first encounter MBHD.

#### Why not Java 8 ?

At the time MBHD was being written (Q4 2013 - Q2 2014) Java 8 was not in production release and the sheer size of the packaged download
was coming in at 150Mb (18x MultiBit Classic and 3x the standard Java 7 packaged footprints). That footprint alone would be sufficient
to dramatically increase the cost of serving the application and deter people from downloading in countries where bandwidth is less
available.

We will revisit this once we have suitable compressed JWrapper JREs available.

#### Why not JavaFX ?

JavaFX was only available as version 2.2 on Java 7 and the move to Java 8 was not going to happen. There were many significant
features missing in JavaFX 2.2 which would only be fixed in Java 8 (e.g. right to left languages, integration with native platform
for Bitcoin URI protocol handling, reporting uncaught exceptions).

This technology was not suitable for the very wide range of people using MultiBit in all corners of the globe.

#### Why Swing ?

There is a vast amount of support for Swing. The code is near bullet-proof for most use cases and it fully supports internationalization
which is a key requirement for MultiBit HD. Also, many of the supporting libraries for Swing
pre-date 2009 making it much harder for [dependency chain attacks](http://gary-rowe.com/agilestack/2013/07/03/preventing-dependency-chain-attacks-in-maven/)
to take place.

With some effort Swing can be made to look quite modern.

Swing also allows us to smoothly integrate with the native platform which puts it ahead of JavaFX until at least Q4 2014.

#### Why not SwingX ?

SwingX is a large support library that introduces a lot of additional functionality to Swing applications. Much of this additional
functionality is not required by MultiBit or can be relatively easily worked around. Consequently including it would increase the
available attack surface.

#### Why the Nimbus look and feel ?

In Java 7 the Nimbus look and feel became integrated with the JDK. It provides a modern 2D rendered UI that is the same across
all platforms. It is highly customisable through simple themes and provides consistent painting behaviour across platforms. For
example to paint a button red in Swing using the Mac-only Aqua theme requires complex custom ButtonUI code.

Using Nimbus ensures that we don't have this or similar problems.

[Technical details on the default colours](http://docs.oracle.com/javase/tutorial/uiswing/lookandfeel/_nimbusDefaults.html#primary)

#### Where's the Trezor support ?

Due to the complexity of getting MultiBit HD working with a "soft" wallet we've moved the Trezor support into a
different branch (`mbhd-trezor`) which is periodically updated from `develop`. Once the main MBHD code has finalised
then we will be integrating Trezor into the main branch.

#### I want an installer not this IDE

The code is changing too rapidly and is too unstable to justify a long-lived installer for download. If you want to create one
for demo purposes you need to [first read the installer README](mbhd-install/README.md) to do the necessary manual steps to configure JWrapper
and then run the following Maven command:
```
mvn -Dinstaller=true clean install
```
The installers will be found in the `target` directory ready for installer signing.

Alternatively apply to join the [private beta mailing list](https://groups.google.com/forum/?hl=en#!forum/multibit-hd-private-beta) where new signed installers will be shared.

#### Is there a developer wiki ?

Yes. [The wiki pages](https://github.com/bitcoin-solutions/multibit-hd/wiki/_pages) provide comprehensive instructions for
developers that cover a variety of environments.

#### What is your development roadmap ?

We are currently working to the following timetable:

1. Add BRIT support
2. BIP32/39 hierarchical deterministic wallet (HDW) support following Bitcoinj + Trezor approach
3. BIP70-73 payment protocol support
4. Hardware wallet (Trezor) support
5. Hierarchical deterministic multi-signature (HDM) support

### Use cases documentation

The use cases are described here:
https://docs.google.com/document/d/18qtE5lmRzB32Sc9Ii37GySJGLKx3VNypBkjnHbNjdik/edit?usp=drive_web

### Automated requirements testing with Swing FEST

We use [Swing FEST](http://docs.codehaus.org/display/FEST/Swing+Module) to perform automated requirements testing of the user interface. It gives super fast overview of the application and runs like a standard unit test. `MultiBitHDFestTest` provides the entry point.

This provides an ever-improving set of regression tests to ensure that new code does not break the existing work.

The code is arranged as a single test case with multiple individual tests that are independent of each other. Each create their own temporary application directory and may or may not require an initial randomly created empty wallet.

Developers are strongly encouraged to create a FEST test for any UI work they are about to undertake and use it to actually test the work in progress. It is far faster to run FEST than to manually run up the application and do it manually.

FEST is not intended to run as part of a Maven build since not all build environments support a display.

### Upgrading Font Awesome

Use the `FontAwesomeTools` to create the necessary enum entries for `AwesomeIcon` as required.
