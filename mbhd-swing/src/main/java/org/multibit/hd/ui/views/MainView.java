package org.multibit.hd.ui.views;

import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.events.ShutdownEvent;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.events.view.LocaleChangedEvent;
import org.multibit.hd.ui.i18n.Languages;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.themes.Themes;
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

  private final JPanel headerPanel;
  private final JPanel sidebarPanel;
  private final JPanel detailPanel;
  private final JPanel footerPanel;

  public MainView(
    JPanel headerPanel,
    JPanel sidebarPanel,
    JPanel detailPanel,
    JPanel footerPanel
  ) {

    this.headerPanel = headerPanel;
    this.sidebarPanel = sidebarPanel;
    this.detailPanel = detailPanel;
    this.footerPanel = footerPanel;

    CoreServices.uiEventBus.register(this);

    // Provide all panels with a reference to the main frame
    Panels.frame = this;

    setDefaultLookAndFeelDecorated(true);

    // TODO i18n
    setTitle("MultiBit HD");

    setBackground(Themes.currentTheme.headerPanelBackground());

    // TODO Configuration
    setPreferredSize(new Dimension(1000, 700));

    // Hard coded
    setMinimumSize(new Dimension(1000, 700));

    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

  }

  @Subscribe
  public void onShutdownEvent(ShutdownEvent shutdownEvent) {

    Panels.frame.dispose();

  }

  @Subscribe
  public void onLocaleChangedEvent(LocaleChangedEvent event) {

    log.debug("Received 'locale changed' event");

    getContentPane().removeAll();
    getContentPane().add(createMainContent());

    pack();

    setVisible(true);

  }

  /**
   * @return The contents of the main panel (header, body and footer)
   */
  private JPanel createMainContent() {

    // Create the main panel and place it in this frame
    MigLayout layout = new MigLayout(
      "fill,insets 0,novisualpadding", // Layout
      "[]", // Columns
      "[][][]"  // Rows
    );
    JPanel mainPanel = Panels.newPanel(layout);

    // Set the overall tone
    mainPanel.setBackground(Themes.currentTheme.headerPanelBackground());

    // Create a splitter pane
    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

    if (Languages.isLeftToRight()) {
    splitPane.setLeftComponent(sidebarPanel);
    splitPane.setRightComponent(detailPanel);
    } else {
      splitPane.setLeftComponent(detailPanel);
      splitPane.setRightComponent(sidebarPanel);
    }

    splitPane.setDividerSize(3);

    // Sets the colouring for divider and borders
    splitPane.setBackground(Themes.currentTheme.text());
    splitPane.setBorder(BorderFactory
      .createMatteBorder(1, 0, 1, 0, Themes.currentTheme.text()));

    // Add the supporting panels
    mainPanel.add(headerPanel, "grow,wrap");
    mainPanel.add(splitPane, "grow,wrap");
    mainPanel.add(footerPanel, "grow,wrap");

    return mainPanel;
  }
}