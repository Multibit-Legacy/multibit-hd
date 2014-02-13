package org.multibit.hd.ui.views;

import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.events.controller.ControllerEvents;
import org.multibit.hd.ui.events.controller.ShowScreenEvent;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.screens.AbstractScreenView;
import org.multibit.hd.ui.views.screens.Screen;
import org.multibit.hd.ui.views.screens.Screens;
import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * <p>View to provide the following to application:</p>
 * <ul>
 * <li>Provision of components and layout for the detail display</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class DetailView {

  private final JPanel contentPanel;

  private CardLayout cardLayout = new CardLayout();

  private JPanel screenHolder = Panels.newPanel(cardLayout);

  private Map<Screen, AbstractScreenView> screenViewMap = Maps.newHashMap();

  public DetailView() {

    CoreServices.uiEventBus.register(this);

    MigLayout layout = new MigLayout(
      "fill,insets 0", // Layout constraints
      "[]", // Column constraints
      "[]" // Row constraints
    );
    contentPanel = Panels.newPanel(layout);

    // Override the default theme
    contentPanel.setBackground(Themes.currentTheme.detailPanelBackground());

    for (Screen screen: Screen.values()) {

      AbstractScreenView view = Screens.newScreen(screen);

      // Keep track of the view instances in case of a locale change
      screenViewMap.put(screen, view);

      // Add their panels to the overall card layout
      screenHolder.add(view.newScreenViewPanel(), screen.name());

    }

    // Once all the views are initialised allow events to occur
    for (Map.Entry<Screen, AbstractScreenView> entry : screenViewMap.entrySet()) {

      // Ensure the screen is in the correct starting state
      entry.getValue().fireInitialStateViewEvents();

    }

    // Add the screen holder to the overall content panel
    contentPanel.add(screenHolder, "grow");

    ControllerEvents.fireShowDetailScreenEvent(Screen.WALLET);

  }

  /**
   * @return The content panel for this View
   */
  public JPanel getContentPanel() {
    return contentPanel;
  }

  @Subscribe
  public void onShowDetailScreen(ShowScreenEvent event) {

    cardLayout.show(screenHolder, event.getScreen().name());

  }

}
