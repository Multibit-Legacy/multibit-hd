package org.multibit.hd.installer;

import jwrapper.JWConstants;
import jwrapper.hidden.events.JWOSXEventListener;
import jwrapper.jwutils.JWMacOS;
import jwrapper.jwutils.JWSystem;
import jwrapper.jwutils.JWWindowsOS;
import jwrapper.updater.JWApp;
import jwrapper.updater.JWLaunchProperties;
import org.multibit.hd.core.concurrent.SafeExecutors;
import org.multibit.hd.ui.MultiBitHD;
import utils.ostools.OS;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

/**
 * <p>JWrapper launcher to provide the following to application:</p>
 * <ul>
 * <li>Native OS integration for protocol handling</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class MultiBitHDLauncher {

  /**
   * Ensure we only deal with one Bitcoin URI at a time
   */
  private static final ExecutorService bitcoinUriExecutorService = SafeExecutors.newSingleThreadExecutor("bitcoin-uri");

  public static void main(String[] args) throws Exception {

    // TODO Need some configuration to prevent overwriting user's choice
    registerProtocolHandler();

    // Check for any URLs when launching
    String url = JWLaunchProperties.getPropertyPossiblyNull(JWLaunchProperties.PROP_URL_HANDLER_OPEN);

    // Multiple instances will handover correctly
    if (url != null) {
      MultiBitHD.main(new String[]{url});
    } else {
      MultiBitHD.main(new String[]{});
    }

  }

  /**
   * Handles the process of registering this application as a protocol handler
   */
  private static void registerProtocolHandler() throws IOException, InterruptedException {

    if (OS.isMacOS()) {

      String appBundle = JWSystem.getAppBundleName();
      String bundleID = JWConstants.buildOsxDomainFromBundle(appBundle);

      JWMacOS.registerAppAsURLHandler("bitcoin", bundleID);

      JWMacOS.setOSXEventListener(new JWOSXEventListener() {
        public void openURL(final String url) {

          bitcoinUriExecutorService.submit(new Runnable() {
            @Override
            public void run() {
              try {
                // Multiple instances will handover correctly
                MultiBitHD.main(new String[]{url});
              } catch (Exception e) {
                // Nothing to be done if this fails
                e.printStackTrace();
              }

            }
          });
        }
      });
    } else if (OS.isWindows()) {

      String vappName = JWApp.getMyVirtualApp().getUserVisibleName();

      JWWindowsOS.registerURLSchemeForVirtualApp("bitcoin", vappName);
    }

    // TODO Find a suitable solution for Linux
  }

}
