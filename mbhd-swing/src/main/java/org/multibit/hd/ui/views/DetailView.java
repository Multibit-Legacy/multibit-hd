package org.multibit.hd.ui.views;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.events.controller.ShowScreenEvent;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.screens.AbstractScreenView;
import org.multibit.hd.ui.views.screens.Screen;
import org.multibit.hd.ui.views.screens.Screens;
import org.multibit.hd.ui.views.themes.Themes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

  private static final Logger log = LoggerFactory.getLogger(DetailView.class);

  private final JPanel contentPanel;

  private CardLayout cardLayout = new CardLayout();
  private JPanel screenPanel = Panels.newPanel(cardLayout);

  private Map<Screen, AbstractScreenView> screenViewMap = Maps.newHashMap();

  public DetailView() {

    CoreServices.uiEventBus.register(this);

    contentPanel = Panels.newPanel();

    // Apply theme
    contentPanel.setBackground(Themes.currentTheme.detailPanelBackground());

    // Apply opacity
    contentPanel.setOpaque(true);

    // Add the screen holder to the overall content panel
    contentPanel.add(screenPanel, "grow");

  }

  /**
   * @return The content panel for this View
   */
  public JPanel getContentPanel() {
    return contentPanel;
  }

  /**
   * Handles the screen initialisation operations that take place after the
   * wallet has been opened
   */
  public void afterWalletOpened() {

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {

        if (screenViewMap.isEmpty()) {

          // Populate based on the current locale
          populateScreenViewMap();

          // Once all the views are created allow events to occur
          for (Map.Entry<Screen, AbstractScreenView> entry : screenViewMap.entrySet()) {

            // Ensure the screen is in the correct starting state
            entry.getValue().fireInitialStateViewEvents();

          }
        }

      }
    });

  }

  @Subscribe
  public void onShowDetailScreen(final ShowScreenEvent event) {

    Preconditions.checkNotNull(event, "'event' must be present");

    Preconditions.checkState(!screenViewMap.isEmpty(),"'screenViewMap' has not been initialised. DetailView is not ready.");

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {

        Screen screen = event.getScreen();
        AbstractScreenView view = screenViewMap.get(screen);

        if (!view.isInitialised()) {

          // Initialise the panel and add it to the card layout parent
          screenPanel.add(view.getScreenViewPanel(), screen.name());

        }

        cardLayout.show(screenPanel, event.getScreen().name());

        view.afterShow();

      }
    });


  }

  /**
   * Populate all the available screens but do not initialise them
   */
  private void populateScreenViewMap() {

    log.debug("Populating the screens");

    for (Screen screen : Screen.values()) {

      AbstractScreenView view = Screens.newScreen(screen);

      // Keep track of the view instances but don't initialise them
      screenViewMap.put(screen, view);

    }

  }

}
