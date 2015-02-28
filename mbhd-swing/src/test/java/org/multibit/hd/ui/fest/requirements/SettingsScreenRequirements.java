package org.multibit.hd.ui.fest.requirements;

import com.google.common.collect.Maps;
import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.sidebar.settings.ShowSettingsScreenUseCase;
import org.multibit.hd.ui.fest.use_cases.sidebar.settings.appearance.ShowThenCancelAppearanceUseCase;
import org.multibit.hd.ui.fest.use_cases.sidebar.settings.appearance.VerifyAppearanceShowBalanceUseCase;
import org.multibit.hd.ui.fest.use_cases.sidebar.settings.appearance.VerifyAppearanceThemeUseCase;
import org.multibit.hd.ui.fest.use_cases.sidebar.settings.exchange.ShowThenCancelExchangeUseCase;
import org.multibit.hd.ui.fest.use_cases.sidebar.settings.exchange.VerifyExchangeNoneUseCase;
import org.multibit.hd.ui.fest.use_cases.sidebar.settings.exchange.VerifyExchangeOERUseCase;
import org.multibit.hd.ui.fest.use_cases.sidebar.settings.fees.ShowThenCancelFeesUseCase;
import org.multibit.hd.ui.fest.use_cases.sidebar.settings.fees.VerifyFeesDonateNowUseCase;
import org.multibit.hd.ui.fest.use_cases.sidebar.settings.fees.VerifyFeesSliderUseCase;
import org.multibit.hd.ui.fest.use_cases.sidebar.settings.labs.ShowThenCancelLabsUseCase;
import org.multibit.hd.ui.fest.use_cases.sidebar.settings.labs.VerifyLabsToggleTrezorUseCase;
import org.multibit.hd.ui.fest.use_cases.sidebar.settings.language.ShowThenCancelLanguageUseCase;
import org.multibit.hd.ui.fest.use_cases.sidebar.settings.sounds.ShowThenCancelSoundsUseCase;
import org.multibit.hd.ui.fest.use_cases.sidebar.settings.units.ShowThenCancelUnitsUseCase;

import java.util.Map;

/**
 * <p>FEST Swing UI test to provide:</p>
 * <ul>
 * <li>Exercise the "settings" screen to verify its wizards show correctly</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class SettingsScreenRequirements {

  public static void verifyUsing(FrameFixture window) {

    Map<String, Object> parameters = Maps.newHashMap();

    // Show the settings screen
    new ShowSettingsScreenUseCase(window).execute(parameters);

    // Exercise the basic settings by showing and cancelling
    new ShowThenCancelLanguageUseCase(window).execute(parameters);
    new ShowThenCancelUnitsUseCase(window).execute(parameters);
    new ShowThenCancelExchangeUseCase(window).execute(parameters);
    new ShowThenCancelAppearanceUseCase(window).execute(parameters);
    new ShowThenCancelFeesUseCase(window).execute(parameters);
    new ShowThenCancelSoundsUseCase(window).execute(parameters);
    new ShowThenCancelLabsUseCase(window).execute(parameters);

    // TODO Language

    // TODO Units

    // Exchange
    new VerifyExchangeOERUseCase(window).execute(parameters);
    new VerifyExchangeNoneUseCase(window).execute(parameters);

    // Appearance
    new VerifyAppearanceThemeUseCase(window).execute(parameters);
    new VerifyAppearanceShowBalanceUseCase(window).execute(parameters);

    // Fees
    new VerifyFeesSliderUseCase(window).execute(parameters);
    new VerifyFeesDonateNowUseCase(window).execute(parameters);

    // TODO Sounds

    // Labs
    new VerifyLabsToggleTrezorUseCase(window).execute(parameters);

  }
}
