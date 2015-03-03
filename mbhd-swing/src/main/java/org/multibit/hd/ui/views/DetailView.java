package org.multibit.hd.ui.views;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;
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
 */
public class DetailView extends AbstractView {
  private final JPanel contentPanel;

  private CardLayout cardLayout = new CardLayout();
  private JPanel screenPanel = Panels.newPanel(cardLayout);

  private Map<Screen, AbstractScreenView> screenViewMap = Maps.newHashMap();

  public DetailView() {
    super();

    contentPanel = Panels.newPanel();

    // Apply theme
    contentPanel.setBackground(Themes.currentTheme.detailPanelBackground());

    // Apply opacity
    contentPanel.setOpaque(true);

    // Add the screen holder to the overall content panel
    contentPanel.add(screenPanel, "grow");
  }

  @Override
  public void unregister() {
    super.unregister();

    // Unsubscribe the screens
    for (Map.Entry<Screen, AbstractScreenView> entry : screenViewMap.entrySet()) {
      entry.getValue().unsubscribe();
    }
  }

  /**
   * @return The content panel for this View
   */
  public JPanel getContentPanel() {
    return contentPanel;
  }

  @Subscribe
  public void onShowDetailScreen(final ShowScreenEvent event) {
    Preconditions.checkNotNull(event, "'event' must be present");

    Screen screen = event.getScreen();

    Preconditions.checkNotNull(screen, "'screen' must be present in ShowScreenEvent");

    // Initialise screen if it does not exist already
    if (!screenViewMap.containsKey(screen)) {
      AbstractScreenView view = Screens.newScreen(screen);

      // Ensure the screen is in the correct starting state
      view.fireInitialStateViewEvents();

      // Keep track of the view instances
      screenViewMap.put(screen, view);
    }

    AbstractScreenView view = screenViewMap.get(screen);

    if (!view.isInitialised()) {
      // Initialise the panel and add it to the card layout parent
      screenPanel.add(view.getScreenViewPanel(), screen.name());
    }

    cardLayout.show(screenPanel, event.getScreen().name());

    view.afterShow();
  }

  /**
   * Clear the screen cache so that they are recreated on demand
   */
  public void clearScreenCache() {
    screenViewMap.clear();
  }
}
