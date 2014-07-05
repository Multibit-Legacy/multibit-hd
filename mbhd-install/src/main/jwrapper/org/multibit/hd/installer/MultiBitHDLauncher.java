package org.multibit.hd.installer;

import com.google.bitcoin.uri.BitcoinURI;
import com.google.bitcoin.uri.BitcoinURIParseException;
import com.google.common.base.Optional;
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

    System.out.println("Launching MultiBit HD");

    // Check for any URLs when launching
    Optional<String> url = Optional.absent();
    if (OS.isMacOS()) {
      url = Optional.fromNullable(JWMacOS.getRequestedURL());
    }
    if (OS.isWindows()) {
      url = Optional.fromNullable(JWWindowsOS.getRequestedURL());
    }

    // Start the app if not done so already
    if (app == null) {
      MultiBitHD.main(null);
    }

    // Trigger an alert if a Bitcoin URI is present
    if (url.isPresent()) {

      System.out.println("Seen request url: " + url.orNull());

      // Validate the data
      final BitcoinURI bitcoinURI;
      try {
        bitcoinURI = new BitcoinURI(url.get());
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

}
