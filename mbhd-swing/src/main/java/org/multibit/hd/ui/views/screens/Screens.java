package org.multibit.hd.ui.views.screens;

import org.multibit.hd.ui.i18n.MessageKey;
import org.multibit.hd.ui.views.screens.contacts.ContactsPanelModel;
import org.multibit.hd.ui.views.screens.contacts.ContactsPanelView;

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
      case WALLET:
        view = new ContactsPanelView(new ContactsPanelModel(screen), screen, MessageKey.CONTACTS);
        break;
      case CONTACTS:
        view = new ContactsPanelView(new ContactsPanelModel(screen), screen, MessageKey.CONTACTS);
        break;
      case TRANSACTIONS:
        view = new ContactsPanelView(new ContactsPanelModel(screen), screen, MessageKey.CONTACTS);
        break;
      case HELP:
        view = new ContactsPanelView(new ContactsPanelModel(screen), screen, MessageKey.CONTACTS);
        break;
      case HISTORY:
        view = new ContactsPanelView(new ContactsPanelModel(screen), screen, MessageKey.CONTACTS);
        break;
      case SETTINGS:
        view = new ContactsPanelView(new ContactsPanelModel(screen), screen, MessageKey.CONTACTS);
        break;
      case TOOLS:
        view = new ContactsPanelView(new ContactsPanelModel(screen), screen, MessageKey.CONTACTS);
        break;
      case EXIT:
        view = new ContactsPanelView(new ContactsPanelModel(screen), screen, MessageKey.CONTACTS);
        break;
      case MAIN_ERROR:
        view = new ContactsPanelView(new ContactsPanelModel(screen), screen, MessageKey.CONTACTS);
        break;
      default:
        throw new IllegalStateException("Unknown screen:" + screen.name());
    }

    return view;

  }


}
