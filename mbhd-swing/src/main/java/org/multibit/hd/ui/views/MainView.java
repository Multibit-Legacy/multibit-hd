package org.multibit.hd.ui.views;

import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.events.ShutdownEvent;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.events.view.LocaleChangedEvent;
import org.multibit.hd.ui.events.view.WizardHideEvent;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.themes.Themes;
import org.multibit.hd.ui.views.wizards.Wizards;
import org.multibit.hd.ui.views.wizards.welcome.WelcomeWizardState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

/**
 * <p>View to provide the following to application:</p>
 * <ul>
 * <li>Provision of components and layout for the main frame</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class MainView extends JFrame {

  private static final Logger log = LoggerFactory.getLogger(MainView.class);

  private HeaderView headerView;
  private SidebarView sidebarView;
  private DetailView detailView;
  private FooterView footerView;

  // Need to track if a wizard was showing before a refresh occurred
  private boolean showExitingWelcomeWizard = false;
  private boolean showExitingPasswordWizard = false;

  public MainView() {

    CoreServices.uiEventBus.register(this);

    // Provide all panels with a reference to the main frame
    Panels.frame = this;

    setTitle(Languages.safeText(MessageKey.APPLICATION_TITLE));

    // Hard coded
    setMinimumSize(new Dimension(MultiBitUI.UI_MIN_WIDTH, MultiBitUI.UI_MIN_HEIGHT));

    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

  }

  @Subscribe
  public void onShutdownEvent(ShutdownEvent shutdownEvent) {

    Panels.frame.dispose();

  }

  @Subscribe
  public void onLocaleChangedEvent(LocaleChangedEvent event) {

    log.debug("Received 'locale changed' event");

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        refresh();
      }
    });

  }

  /**
   * <p>Rebuild the contents of the main view based on the current configuration and theme</p>
   * <p>This should be in the Swing AWT event thread</p>
   */
  public void refresh() {

    // Clear out all the old content and rebuild it from scratch
    getContentPane().removeAll();
    getContentPane().add(createMainContent());

    // Catch up on recent events
    CoreServices.getApplicationEventService().repeatLatestEvents();

    Panels.hideLightBox();

    // Check for any wizards that were showing before the refresh occurred
    if (showExitingWelcomeWizard) {
      Panels.showLightBox(Wizards.newExitingWelcomeWizard(WelcomeWizardState.WELCOME_SELECT_LANGUAGE).getWizardScreenHolder());
    }
    if (showExitingPasswordWizard) {
      // Force an exit if the user can't get through
      Panels.showLightBox(Wizards.newExitingPasswordWizard().getWizardScreenHolder());
    }

    // Tidy up and show
    pack();
    setVisible(true);

  }

  /**
   * @return The contents of the main panel (header, body and footer)
   */
  private JPanel createMainContent() {

    // Create the main panel and place it in this frame
    MigLayout layout = new MigLayout(
      Panels.migXYLayout(),
      "[]", // Columns
      "[][][]"  // Rows
    );
    JPanel mainPanel = Panels.newPanel(layout);

    // Set the overall tone
    mainPanel.setBackground(Themes.currentTheme.headerPanelBackground());

    // Require opaque to ensure the color is shown
    mainPanel.setOpaque(true);

    // Deregister any previous references
    if (headerView != null) {
      CoreServices.uiEventBus.unregister(headerView);
      CoreServices.uiEventBus.unregister(sidebarView);
      CoreServices.uiEventBus.unregister(detailView);
      CoreServices.uiEventBus.unregister(footerView);
    }

    // Create supporting views (rebuild every time for language support)
    headerView = new HeaderView();
    sidebarView = new SidebarView();
    detailView = new DetailView();
    footerView = new FooterView();

    // Create a splitter pane
    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

    // Set the divider width (3 is about right for a clean look)
    splitPane.setDividerSize(3);

    if (Languages.isLeftToRight()) {
      splitPane.setLeftComponent(sidebarView.getContentPanel());
      splitPane.setRightComponent(detailView.getContentPanel());
      // TODO Use the configuration to provide the basis
      splitPane.setDividerLocation(MultiBitUI.SIDEBAR_LHS_PREF_WIDTH);
    } else {
      splitPane.setLeftComponent(detailView.getContentPanel());
      splitPane.setRightComponent(sidebarView.getContentPanel());
      // TODO Use the configuration to provide the basis
      splitPane.setDividerLocation(Panels.frame.getWidth() - MultiBitUI.SIDEBAR_LHS_PREF_WIDTH);
    }

    // Sets the colouring for divider and borders
    splitPane.setBackground(Themes.currentTheme.text());
    splitPane.setBorder(BorderFactory.createMatteBorder(
      1, 0, 1, 0,
      Themes.currentTheme.text()
    ));

    splitPane.applyComponentOrientation(Languages.currentComponentOrientation());

    // Add the supporting panels
    mainPanel.add(headerView.getContentPanel(), "growx,shrink,wrap"); // Ensure header size remains fixed
    mainPanel.add(splitPane, "grow,push,wrap");
    mainPanel.add(footerView.getContentPanel(), "growx,shrink"); // Ensure footer size remains fixed

    return mainPanel;
  }

  /**
   * @param show True if the exiting welcome wizard should be shown during the next refresh
   */
  public void setShowExitingWelcomeWizard(boolean show) {

    showExitingWelcomeWizard = show;

  }

  /**
   * @param show True if the exiting password wizard should be shown during the next refresh
   */
  public void setShowExitingPasswordWizard(boolean show) {

    showExitingPasswordWizard = show;

  }

  @Subscribe
  public void onWizardHideEvent(WizardHideEvent event) {

    showExitingWelcomeWizard = false;
    showExitingPasswordWizard = false;

  }

}