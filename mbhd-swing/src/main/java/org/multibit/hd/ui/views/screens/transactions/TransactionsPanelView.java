package org.multibit.hd.ui.views.screens.transactions;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.views.components.Panels;

import javax.swing.*;

/**
 * <p>View to provide the following to application:</p>
 * <ul>
 * <li>Provision of components and layout for the exit confirmation display</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class TransactionsPanelView {

  private final JPanel contentPanel;

  public TransactionsPanelView() {

    CoreServices.uiEventBus.register(this);

    MigLayout layout = new MigLayout(
      "fill", // Layout constraints
      "[]10[]", // Column constraints
      "[]50[]" // Row constraints
    );

    contentPanel = Panels.newPanel(layout);

  }

  /**
   * @return The content panel for this View
   */
  public JPanel getContentPanel() {
    return contentPanel;
  }

}
