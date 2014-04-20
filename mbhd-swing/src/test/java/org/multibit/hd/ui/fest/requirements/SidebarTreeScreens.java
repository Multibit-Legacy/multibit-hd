package org.multibit.hd.ui.fest.requirements;

import com.google.common.collect.Maps;
import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.contacts.ShowContactsScreenUseCase;
import org.multibit.hd.ui.fest.use_cases.exit.ShowExitScreenUseCase;
import org.multibit.hd.ui.fest.use_cases.help.ShowHelpScreenUseCase;
import org.multibit.hd.ui.fest.use_cases.history.ShowHistoryScreenUseCase;
import org.multibit.hd.ui.fest.use_cases.payments.ShowPaymentsScreenUseCase;
import org.multibit.hd.ui.fest.use_cases.send_request.ShowSendRequestScreenUseCase;
import org.multibit.hd.ui.fest.use_cases.settings.ShowSettingsScreenUseCase;
import org.multibit.hd.ui.fest.use_cases.tools.ShowToolsScreenUseCase;

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
public class SidebarTreeScreens {

  public static void verifyUsing(FrameFixture window) {

    Map<String,Object> parameters = Maps.newHashMap();

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
