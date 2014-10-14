package org.multibit.hd.installer;

import jwrapper.jwutils.JWInstallApp;
import jwrapper.jwutils.JWMacOS;
import jwrapper.jwutils.JWSystem;
import jwrapper.jwutils.JWWindowsOS;
import utils.ostools.OS;

/**
 * <p>JWrapper virtual app to provide the following to application:</p>
 * <ul>
 * <li>Native OS integration for protocol handling as one-off during install</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class MultiBitHDPostInstall {

  public static void main(String[] args) throws Exception {

    // Initialise as the Bitcoin protocol handler
    if (OS.isMacOS()) {
      JWMacOS.registerURLSchemeForVirtualApp("bitcoin", JWSystem.getMyAppName());
    }
    if (OS.isWindows()) {
      JWWindowsOS.registerURLSchemeForVirtualApp("bitcoin",JWSystem.getMyAppName());
    }

    JWInstallApp.addAppShortcut(
      "MultiBit HD",
      "MultiBit HD"
    );
  }

}
