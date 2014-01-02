package org.multibit.hd.ui.views;

import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.events.controller.ShowDetailScreenEvent;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.detail_views.DetailScreen;
import org.multibit.hd.ui.views.detail_views.ToolsDetailView;
import org.multibit.hd.ui.views.detail_views.WalletDetailView;
import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;
import java.awt.*;

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

  private JPanel cardHolder = Panels.newPanel(cardLayout);


  public DetailView() {

    CoreServices.uiEventBus.register(this);

    MigLayout layout = new MigLayout(
      "fill", // Layout constrains
      "[]", // Column constraints
      "[grow]10[shrink]" // Row constraints
    );
    contentPanel = Panels.newPanel(layout);

    // Override the default theme
    contentPanel.setBackground(Themes.currentTheme.detailPanelBackground());

    cardHolder.add(new WalletDetailView().getContentPanel(), DetailScreen.WALLET.name());
    cardHolder.add(new ToolsDetailView().getContentPanel(), DetailScreen.TOOLS.name());

    contentPanel.add(cardHolder, "grow,wrap");

  }

  /**
   * @return The content panel for this View
   */
  public JPanel getContentPanel() {
    return contentPanel;
  }

  @Subscribe
  public void onShowDetailScreen(ShowDetailScreenEvent event) {

    cardLayout.show(cardHolder, event.getDetailScreen().name());

  }

}
