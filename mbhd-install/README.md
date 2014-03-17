# Notes on various installers

The choice of a robust installer has been a difficult one. Here are the notes that lead to the current situation.

## IzPack with JSmooth

This is the installer solution in place for MultiBit Classic. It works, but has a few problems:

### Pros

* Proven solution across Windows, OSX and Linux
* Allows one-off registration script
* Works well with ProGuard and code signing

### Cons

* Very complex scripts
* Doesn't appear to be actively maintained
* Requires JSmooth to build the executable
* Uninstall is not guaranteed
* Missing JRE causes the horrible Oracle JRE installation process to trigger

## Notes on JWrapper

### Pros

* Seamless installation targeting all platforms natively
* Free and unrestricted use
* Huge compression of JVMs leading to 16Mb delivery
* Potential for continuous update on startup
* MultiBit can control the JVM that is downloaded
* Potential for multi-OS support libraries allowing better integration (e.g. registry, icon decoration etc)

### Cons

* Splash screen is JWrapper branded possibly requiring a fee to remove
* OSX builds are troublesome (expert help may fix this)
* Difficult to get build environment working smoothly (no Maven integration)
* Lots of quirks in the XML configuration file to locate files (might be due to manual build)

### How to build

There is a bit of one-off set up to do first:

1. Ensure you include `mbhd-install/JRE-1.7` manually (it is git ignored)
2. Ensure that the jwrapper JAR is the latest available one

The usual native build process then becomes

```
java -Xmx512m -jar jwrapper-000something.jar ../multibit-hd/mbhd-jwrapper.xml
```

5. Wait while JREs are updated and compressed
6. Final artifacts are placed in `jwrapper/build` directory for all major platforms

## Graveyard

These installers never even made it to a code spike.

## JavaFX Maven plugin

The ZenJava people have created a Maven plugin for wrapping the JavaFX packager.

### Pros

* Very easy configuration within Maven
*

### Cons

* Targets JavaFX applications rather than Swing
* No support for auto-update
* No support for cross-platform builds (e.g. MSI files on MacOSX)
* Bloated JREs
* No customisation of install process (e.g. license, register protocol handlers)

### Java Web Start (JNLP)

Many reports from developers of their clients having a terrible first installation experience in
the absence of a JRE. This will become the norm as OSX, Linux and then Windows are likely to drop
a standard JRE leaving it to developers to package their own.

### Launch4j

### JavaFX packager

The JavaFX packager from Oracle has been hailed as the correct way to deploy a JavaFX application,
but our experience has shown that it is very tricky to configure within a Maven build. Packaging a
JRE with it leads to a huge download size

