package org.multibit.hd.ui.fest.requirements.trezor.create_wallet;

import com.google.common.collect.Maps;
import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.testing.hardware_wallet_fixtures.HardwareWalletFixture;
import org.multibit.hd.ui.fest.use_cases.standard.welcome_select.WelcomeSelectLanguage_ro_RO_UseCase;

import java.util.Map;

/**
 * <p>FEST Swing UI test to provide:</p>
 * <ul>
 * <li>Exercise the responses to hardware wallet events before wallet unlock takes place</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class CreateTrezorHardwareWalletColdStart_ro_RO_Requirements extends BaseCreateTrezorHardwareWalletColdStartRequirements {

  public static void verifyUsing(FrameFixture window, HardwareWalletFixture hardwareWalletFixture) {

    Map<String, Object> parameters = Maps.newHashMap();

    // Work through the language panel

    // Assign the ro_RO language
    new WelcomeSelectLanguage_ro_RO_UseCase(window).execute(parameters);

    // Hand over to the standard Trezor process
    verifyCreateTrezorHardwareWalletAfterLanguage(window, hardwareWalletFixture, parameters);

  }
}
