package org.multibit.hd.ui.fest.requirements;

import com.google.common.collect.Maps;
import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.sidebar.settings.ShowSettingsScreenUseCase;
import org.multibit.hd.ui.fest.use_cases.sidebar.settings.labs.VerifyLabsToggleTrezorUseCase;

import java.util.Map;

/**
 * <p>FEST Swing UI test to provide:</p>
 * <ul>
 * <li>Exercise the "settings" screen to verify its wizards show correctly</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class SettingsScreenRequirements {

  public static void verifyUsing(FrameFixture window) {

    Map<String, Object> parameters = Maps.newHashMap();

    // Show the settings screen
    new ShowSettingsScreenUseCase(window).execute(parameters);

    // Exercise the basic settings by showing and cancelling
//    new ShowThenCancelLanguageUseCase(window).execute(parameters);
//    new ShowThenCancelUnitsUseCase(window).execute(parameters);
//    new ShowThenCancelExchangeUseCase(window).execute(parameters);
//    new ShowThenCancelAppearanceUseCase(window).execute(parameters);
//    new ShowThenCancelSoundsUseCase(window).execute(parameters);
//    new ShowThenCancelLabsUseCase(window).execute(parameters);

    // TODO Language

    // TODO Units

    // Exchange
//    new VerifyExchangeOERUseCase(window).execute(parameters);
//    new VerifyExchangeNoneUseCase(window).execute(parameters);

    // Application
//    new VerifyAppearanceThemeUseCase(window).execute(parameters);
//    new VerifyAppearanceShowBalanceUseCase(window).execute(parameters);

    // TODO Sounds

    // Labs
    new VerifyLabsToggleTrezorUseCase(window).execute(parameters);

  }
}
