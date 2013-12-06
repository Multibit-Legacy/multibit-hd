package org.multibit.hd.ui.views;

import com.google.common.eventbus.Subscribe;
import com.xeiam.xchange.currency.MoneyUtils;
import org.multibit.hd.core.events.BitcoinNetworkChangeEvent;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.events.LocaleChangeEvent;
import org.multibit.hd.ui.events.ViewEvents;
import org.multibit.hd.ui.views.components.Panels;

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

    // TODO i18n
    setTitle("MultiBit HD");

    // TODO Configuration
    setBackground(Color.gray);

    // TODO Configuration
    setPreferredSize(new Dimension(1280, 1024));

    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

  }

  @Subscribe
  public void onLocaleChangeEvent(LocaleChangeEvent event) {

    setVisible(false);

    // TODO Check if the Swing way can be made to work here
    setLocale(event.getLocale());

    getContentPane().removeAll();
    getContentPane().add(createMainContent());

    pack();
    setVisible(true);

  }

  @Subscribe
  public void onBitcoinNetworkChangeEvent(BitcoinNetworkChangeEvent event) {

    // TODO Do something!

  }

  /**
   * @return The contents of the main panel (header, body and footer)
   */
  private JPanel createMainContent() {

    // Create the main panel and place it in this frame
    JPanel mainPanel = new JPanel(new BorderLayout());

    // Create a splitter pane
    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

    splitPane.setLeftComponent(sidebarPanel);
    splitPane.setRightComponent(detailPanel);

    splitPane.setDividerSize(3);

    // TODO Add this to themes
    splitPane.setBackground(new Color(128, 128, 128));

    // Add the supporting panels
    mainPanel.add(headerPanel, BorderLayout.PAGE_START);
    mainPanel.add(splitPane, BorderLayout.CENTER);
    mainPanel.add(footerPanel, BorderLayout.PAGE_END);

    return mainPanel;
  }
}