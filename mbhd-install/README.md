## How to build the installer

JWrapper requires the use of a large JAR and a supporting package of JREs. Clearly these should not be held under
version control and so it is necessary to manually copy the following when making an installer:

* `mbhd-install/JRE-1.7` should contain the supported JREs by unzipping the provided JRE download from JWrapper
* `jwrapper-000version.jar` should be copied into `mbhd-install`

All the above, and the derivative files that are produced, are git ignored.

Later these JREs will be automatically downloaded over HTTPS from the main site.

Once the JREs are in place, the build command is

```
mvn -q -Dinstaller=true clean install
```

The first time this process is run it will take ages (typically 20mins) as `pack200` compresses large artifacts.
Subsequent builds are a lot faster.

The final installers are available in `mbhd-install/target` for subsequent upload to the main site.

### Notes on the protocol handler code 

In order to respond to Bitcoin URIs being clicked in external applications such as browsers a protocol handler
has to be registered. The code to support this is within JWrapper in the `jwrapper_utils.jar` which is not under
version control due to its size (1.5+Mb). Consequently the Maven dependency references a JAR within the project
itself which gives rise to a Maven warning that is suppressed with the `-q` option.
 
## Notes on various installers

The choice of a robust installer has been a difficult one. Here are the notes that lead to the current situation.

### JWrapper (the winner)

#### Pros

* Seamless installation targeting all platforms natively
* Free and unrestricted use
* Huge compression of JVMs leading to 16Mb delivery
* Potential for continuous update on startup
* MultiBit can control the JVM that is downloaded
* Potential for multi-OS support libraries allowing better integration (e.g. registry, icon decoration etc)

#### Cons

* Splash screen is JWrapper branded possibly requiring a fee to remove
* OSX builds are troublesome (expert help may fix this)
* Difficult to get build environment working smoothly (no Maven integration)
* Lots of quirks in the XML configuration file to locate files (might be due to manual build)

## Graveyard

### IzPack with JSmooth

This is the installer solution in place for MultiBit Classic. It works, but has a few problems:

#### Pros

* Proven solution across Windows, OSX and Linux
* Allows one-off registration script
* Works well with ProGuard and code signing

#### Cons

* Very complex scripts
* Doesn't appear to be actively maintained
* Requires JSmooth to build the executable
* Uninstall is not guaranteed
* Missing JRE causes the horrible Oracle JRE installation process to trigger

With a heavy heart, we have to say goodbye to IzPack/JSmooth for MultiBit HD. It served us well
and we thank the original developers for their efforts.

The following installers never even made it to a code spike.

### JavaFX Maven plugin

The ZenJava people have created a Maven plugin for wrapping the JavaFX packager.

#### Pros

* Very easy configuration within Maven

#### Cons

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
JRE with it leads to a huge download size and there are no facilities for OS-specific scripts.


