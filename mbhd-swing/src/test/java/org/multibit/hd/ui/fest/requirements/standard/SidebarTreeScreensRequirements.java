package org.multibit.hd.ui.fest.requirements.standard;

import com.google.common.collect.Maps;
import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.standard.sidebar.SidebarAccessibilityUseCase;
import org.multibit.hd.ui.fest.use_cases.standard.sidebar.buy_sell.ShowThenCancelBuySellUseCase;
import org.multibit.hd.ui.fest.use_cases.standard.sidebar.contacts.ShowContactsScreenUseCase;
import org.multibit.hd.ui.fest.use_cases.standard.sidebar.exit.ShowExitScreenUseCase;
import org.multibit.hd.ui.fest.use_cases.standard.sidebar.help.ShowHelpScreenUseCase;
import org.multibit.hd.ui.fest.use_cases.standard.sidebar.payments.ShowPaymentsScreenUseCase;
import org.multibit.hd.ui.fest.use_cases.standard.sidebar.send_request.ShowSendRequestScreenUseCase;
import org.multibit.hd.ui.fest.use_cases.standard.sidebar.settings.ShowSettingsScreenUseCase;
import org.multibit.hd.ui.fest.use_cases.standard.sidebar.tools.ShowToolsScreenUseCase;

import java.util.Map;

/**
 * <p>FEST Swing UI test to provide:</p>
 * <ul>
 * <li>Exercise the sidebar tree to verify the screens show correctly</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class SidebarTreeScreensRequirements {

  public static void verifyUsing(FrameFixture window) {

    Map<String, Object> parameters = Maps.newHashMap();

    new SidebarAccessibilityUseCase(window).execute(parameters);

    // Verify each screen shows
    new ShowSendRequestScreenUseCase(window).execute(parameters);
    // Do the buy/sell after send/request to ensure sidebar transitions work correctly
    new ShowThenCancelBuySellUseCase(window).execute(parameters);
    new ShowPaymentsScreenUseCase(window).execute(parameters);
    new ShowContactsScreenUseCase(window).execute(parameters);
    new ShowHelpScreenUseCase(window).execute(parameters);
    new ShowSettingsScreenUseCase(window).execute(parameters);

    new ShowToolsScreenUseCase(window).execute(parameters);
    new ShowExitScreenUseCase(window).execute(parameters);
    // Not working
    //new ShowManageWalletScreenUseCase(window).execute(parameters);
  }
}
