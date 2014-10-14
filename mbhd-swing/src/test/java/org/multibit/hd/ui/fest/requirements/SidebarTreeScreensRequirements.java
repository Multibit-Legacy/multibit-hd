package org.multibit.hd.ui.fest.requirements;

import com.google.common.collect.Maps;
import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.sidebar.SidebarAccessibilityUseCase;
import org.multibit.hd.ui.fest.use_cases.sidebar.contacts.ShowContactsScreenUseCase;
import org.multibit.hd.ui.fest.use_cases.sidebar.exit.ShowExitScreenUseCase;
import org.multibit.hd.ui.fest.use_cases.sidebar.help.ShowHelpScreenUseCase;
import org.multibit.hd.ui.fest.use_cases.sidebar.history.ShowHistoryScreenUseCase;
import org.multibit.hd.ui.fest.use_cases.sidebar.payments.ShowPaymentsScreenUseCase;
import org.multibit.hd.ui.fest.use_cases.sidebar.send_request.ShowSendRequestScreenUseCase;
import org.multibit.hd.ui.fest.use_cases.sidebar.settings.ShowSettingsScreenUseCase;
import org.multibit.hd.ui.fest.use_cases.sidebar.tools.ShowToolsScreenUseCase;

import java.util.Map;

/**
 * <p>FEST Swing UI test to provide:</p>
 * <ul>
 * <li>Exercise the sidebar tree to verify the screens show correctly</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class SidebarTreeScreensRequirements {

  public static void verifyUsing(FrameFixture window) {

    Map<String, Object> parameters = Maps.newHashMap();

    new SidebarAccessibilityUseCase(window).execute(parameters);

    // Verify each screen shows
    new ShowSendRequestScreenUseCase(window).execute(parameters);
    new ShowPaymentsScreenUseCase(window).execute(parameters);
    new ShowContactsScreenUseCase(window).execute(parameters);
    new ShowHelpScreenUseCase(window).execute(parameters);
    new ShowHistoryScreenUseCase(window).execute(parameters);
    new ShowSettingsScreenUseCase(window).execute(parameters);
    new ShowToolsScreenUseCase(window).execute(parameters);
    new ShowExitScreenUseCase(window).execute(parameters);
  }
}
