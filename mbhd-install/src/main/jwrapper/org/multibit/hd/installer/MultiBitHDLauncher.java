package org.multibit.hd.installer;

import com.google.common.base.Optional;
import jwrapper.jwutils.JWMacOS;
import jwrapper.jwutils.JWWindowsOS;
import org.multibit.hd.ui.MultiBitHD;
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

  public static void main(String[] args) throws Exception {

    // Check for any URLs when launching
    Optional<String> url = Optional.absent();
    if (OS.isMacOS()) {
      url = Optional.fromNullable(JWMacOS.getRequestedURL());
    }
    if (OS.isWindows()) {
      url = Optional.fromNullable(JWWindowsOS.getRequestedURL());
    }

    // Multiple instances will handover correctly
    if (url.isPresent()) {
      MultiBitHD.main(new String[]{url.get()});
    } else {
      MultiBitHD.main(new String[]{});
    }

  }

}
