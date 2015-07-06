package org.multibit.hd.ui.views.screens;

import org.multibit.hd.core.services.ContactService;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.screens.contacts.ContactsScreenModel;
import org.multibit.hd.ui.views.screens.contacts.ContactsScreenView;
import org.multibit.hd.ui.views.screens.exit.ExitScreenModel;
import org.multibit.hd.ui.views.screens.exit.ExitScreenView;
import org.multibit.hd.ui.views.screens.help.HelpScreenModel;
import org.multibit.hd.ui.views.screens.help.HelpScreenView;
import org.multibit.hd.ui.views.screens.manage_wallet.ManageWalletScreenModel;
import org.multibit.hd.ui.views.screens.manage_wallet.ManageWalletScreenView;
import org.multibit.hd.ui.views.screens.payments.PaymentsScreenModel;
import org.multibit.hd.ui.views.screens.payments.PaymentsScreenView;
import org.multibit.hd.ui.views.screens.settings.SettingsScreenModel;
import org.multibit.hd.ui.views.screens.settings.SettingsScreenView;
import org.multibit.hd.ui.views.screens.tools.ToolsScreenModel;
import org.multibit.hd.ui.views.screens.tools.ToolsScreenView;
import org.multibit.hd.ui.views.screens.wallet.SendRequestScreenModel;
import org.multibit.hd.ui.views.screens.wallet.SendRequestScreenView;

/**
 * <p>Factory to provide the following to UI:</p>
 * <ul>
 * <li>Provision of different screens targeting various use cases</li>
 * </ul>
 *
 * <h3>Overview of the Screen architecture</h3>
 *
 * <p>A screen presents a single panels accessed through the sidebar. This is in line with the standard
 * user experience of working with "master-detail" structures.</p>
 *
 * <p>From a data perspective the DetailView maintains a reference to each screen, in a manner reminiscent
 * of a wizard, so that the user can flip between screens easily. Components are reused across screens and
 * so do not maintain a back reference to a parent but instead use a <code>ScreenComponentModelChangedEvent</code> to
 * inform all interested screens that their data has changed. Events are filtered by the screen name to prevent
 * collisions.</p>
 *
 * <p>A "screen view" has a consistent layout: a title and description (top) and some components (center).
 * The top is handled mainly by boilerplate code in the DetailView leaving just the presentation and management of
 * the center section to the developer.</p>
 *
 * <h3>Quickly assembling a screen</h3>
 *
 * <p>The quickest way to get a screen up and running is to take an existing one and modify it accordingly. If
 * your requirement is straightforward (no MaV components or reliance on previous panels) then the boilerplate
 * will handle all the work for you.</p>
 *
 * @since 0.0.1
 *
 */
public class Screens {

  /**
   * @return A new screen panel view
   */
  public static AbstractScreenView newScreen(Screen screen) {

    final AbstractScreenView view;

    switch (screen) {

      // Deprecated screens
      case HISTORY:
        // History no longer supported so show a SendRequest screen to get the user started

      // Sidebar screens
      case SEND_REQUEST:
        view = new SendRequestScreenView(new SendRequestScreenModel(screen), screen, MessageKey.SEND_OR_REQUEST);
        break;
      case CONTACTS:
        // Expect a current contact service
        ContactService contactService = CoreServices.getCurrentContactService();
        view = new ContactsScreenView(new ContactsScreenModel(contactService, screen), screen, MessageKey.CONTACTS);
        break;
      case TRANSACTIONS:
        view = new PaymentsScreenView(new PaymentsScreenModel(screen), screen, MessageKey.PAYMENTS);
        break;
      case HELP:
        view = new HelpScreenView(new HelpScreenModel(screen), screen, MessageKey.HELP);
        break;
      case SETTINGS:
        view = new SettingsScreenView(new SettingsScreenModel(screen), screen, MessageKey.SETTINGS);
        break;
      case MANAGE_WALLET:
        view = new ManageWalletScreenView(new ManageWalletScreenModel(screen), screen, MessageKey.MANAGE_WALLET);
        break;
      case TOOLS:
        view = new ToolsScreenView(new ToolsScreenModel(screen), screen, MessageKey.TOOLS);
        break;
      case EXIT:
        view = new ExitScreenView(new ExitScreenModel(screen), screen, MessageKey.EXIT);
        break;

      default:
        throw new IllegalStateException("Unknown screen:" + screen.name());
    }

    return view;

  }


}
