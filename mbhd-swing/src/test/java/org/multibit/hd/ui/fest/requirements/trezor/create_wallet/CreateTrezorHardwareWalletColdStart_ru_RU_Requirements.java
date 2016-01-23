package org.multibit.hd.ui.fest.requirements.trezor.create_wallet;

import com.google.common.collect.Maps;
import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.testing.hardware_wallet_fixtures.HardwareWalletFixture;
import org.multibit.hd.ui.fest.use_cases.standard.welcome_select.AcceptLicenceUseCase;
import org.multibit.hd.ui.fest.use_cases.standard.welcome_select.WelcomeSelectLanguage_ru_RU_UseCase;

import java.util.Map;

/**
 * <p>FEST Swing UI test to provide:</p>
 * <ul>
 * <li>Exercise the responses to hardware wallet events before wallet unlock takes place</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class CreateTrezorHardwareWalletColdStart_ru_RU_Requirements extends BaseCreateTrezorHardwareWalletColdStartRequirements {

  public static void verifyUsing(FrameFixture window, HardwareWalletFixture hardwareWalletFixture) {

    Map<String, Object> parameters = Maps.newHashMap();

    // Work through the licence and language panels
    new AcceptLicenceUseCase(window).execute(parameters);

    // Assign the ro_RO language
    new WelcomeSelectLanguage_ru_RU_UseCase(window).execute(parameters);

    // Hand over to the standard Trezor process
    verifyCreateTrezorHardwareWalletAfterLanguage(window, hardwareWalletFixture, parameters);

  }

}
