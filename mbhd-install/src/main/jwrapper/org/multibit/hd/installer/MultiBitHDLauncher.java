package org.multibit.hd.installer;

import org.bitcoinj.uri.BitcoinURI;
import org.bitcoinj.uri.BitcoinURIParseException;
import com.google.common.base.Optional;
import jwrapper.hidden.events.JWOSXEventListener;
import jwrapper.jwutils.JWMacOS;
import jwrapper.jwutils.JWWindowsOS;
import org.multibit.hd.ui.MultiBitHD;
import org.multibit.hd.ui.events.controller.ControllerEvents;
import org.multibit.hd.ui.models.AlertModel;
import org.multibit.hd.ui.models.Models;
import utils.ostools.OS;

/**
 * <p>JWrapper launcher to provide the following to application:</p>
 * <ul>
 * <li>Respond to application start either by click or protocol handler</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class MultiBitHDLauncher {

  private static MultiBitHD app = null;

  public static void main(String[] args) throws Exception {

    // Post installer will have handled the registration process for protocol handlers
    // so that this launcher will be called if the application is not already running

    // Check for any URLs when launching
    Optional<String> url;

    // Check for any URLs on the command line (could have launched from a browser link request)
    if (OS.isMacOS()) {

      //JWMacOS.registerURLSchemeForVirtualApp("bitcoin", JWSystem.getMyAppName());
      url = Optional.fromNullable(JWMacOS.getRequestedURL());

    } else if (OS.isWindows()) {

      //JWWindowsOS.registerURLSchemeForVirtualApp("bitcoin",JWSystem.getMyAppName());
      url = Optional.fromNullable(JWWindowsOS.getRequestedURL());
    } else {

      // Linux may put the URL on the arguments
      url = Optional.fromNullable(args == null || args.length == 0 ? null : args[0]);
    }

    // Start the app if not done so already
    if (app == null) {

      if (url.isPresent()) {

        // Launch with the Bitcoin URI
        // If we are in a different JVM to an already running instance then this instance will
        // quit after performing a handover of the URI
        MultiBitHD.main(new String[]{url.get()});

      } else {

        // Launch without a Bitcoin URI (normal)
        MultiBitHD.main(null);
      }

      // This must be a one-off at startup
      if (OS.isMacOS()) {
        // Add an OS X event listener, which is called every time a getURL request is received by JWrapper
        JWMacOS.getMacOSInstance().setOSXEventListener(new JWOSXEventListener() {

          public void openURL(final String url) {

            showBitcoinUriAlert(url);

          }

        });
      }

    } else {

      // App is already running within this JVM

      if (url.isPresent()) {

        // A second launch has been requested with a URL, so just display the alert directly
        showBitcoinUriAlert(url.get());
      }

    }
  }

  private static void showBitcoinUriAlert(String url) {

    // Validate the data
    final BitcoinURI bitcoinURI;
    try {
      bitcoinURI = new BitcoinURI(url);
    } catch (BitcoinURIParseException e) {
      // Quietly ignore
      return;
    }

    Optional<AlertModel> alertModel = Models.newBitcoinURIAlertModel(bitcoinURI);

    // If there is sufficient information in the Bitcoin URI display it to the user as an alert
    if (alertModel.isPresent()) {

      // Add the alert
      ControllerEvents.fireAddAlertEvent(alertModel.get());
    }
  }

}
