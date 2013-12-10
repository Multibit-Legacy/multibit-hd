package org.multibit.hd.ui.views;

import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.events.controller.ShowDetailScreenEvent;

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

  private JPanel cardHolder = new JPanel(cardLayout);


  public DetailView() {

    CoreServices.uiEventBus.register(this);

    MigLayout layout = new MigLayout(
      "fill", // Layout constrains
      "[]", // Column constraints
      "[grow]10[shrink]" // Row constraints
    );
    contentPanel = new JPanel(layout);

    cardHolder.add(new WalletDetailView().getContentPanel());

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

    cardLayout.show(cardHolder, event.getScreen().name());

  }

}
