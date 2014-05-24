package org.multibit.hd.ui.fest.requirements;

import com.google.common.collect.Maps;
import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.settings.ShowSettingsScreenUseCase;
import org.multibit.hd.ui.fest.use_cases.settings.exchange.ShowThenCancelExchangeUseCase;

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
public class SettingsScreen {

  public static void verifyUsing(FrameFixture window) {

    Map<String, Object> parameters = Maps.newHashMap();

    // Show the settings screen
    new ShowSettingsScreenUseCase(window).execute(parameters);

    // TODO (GR) Add language and units

    // Show then cancel the "exchange" wizard
    new ShowThenCancelExchangeUseCase(window).execute(parameters);

  }
}
