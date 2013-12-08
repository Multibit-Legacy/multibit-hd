package org.multibit.hd.ui.views.wizards;

import javax.swing.*;
import java.awt.*;

/**
 * <p>Abstract base class to provide the following to UI:</p>
 * <ul>
 * <li>Provision of common methods to wizards</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public abstract class AbstractWizard {

  private CardLayout cardLayout = new CardLayout();
  protected final JPanel contentPanel = new JPanel(cardLayout);

  /**
   * <p>Close the wizard</p>
   */
  public void close() {

  }

  public JPanel getContentPanel() {

    return contentPanel;

  }

  public CardLayout getCardLayout() {

    return cardLayout;

  }

  /**
   * Show the previous panel
   */
  public void previous() {

    // TODO Limit to first page
    cardLayout.previous(contentPanel);
  }

  /**
   * Show the next panel
   */
  public void next() {

    // TODO Limit to last page
    cardLayout.next(contentPanel);
  }

}
