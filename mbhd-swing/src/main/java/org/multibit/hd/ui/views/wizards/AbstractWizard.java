package org.multibit.hd.ui.views.wizards;

import org.multibit.hd.ui.views.components.Panels;

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

  private static final int WIZARD_MIN_WIDTH = 600;
  private static final int WIZARD_MIN_HEIGHT = 400;

  private CardLayout cardLayout = new CardLayout();
  protected final JPanel contentPanel = Panels.newPanel(cardLayout);

  protected AbstractWizard() {

    contentPanel.setMinimumSize(new Dimension(WIZARD_MIN_WIDTH, WIZARD_MIN_HEIGHT));
    contentPanel.setPreferredSize(new Dimension(WIZARD_MIN_WIDTH, WIZARD_MIN_HEIGHT));

    contentPanel.setSize(WIZARD_MIN_WIDTH, WIZARD_MIN_HEIGHT);

  }

  /**
   * <p>Close the wizard</p>
   */
  public void close() {

    Panels.hideLightBox();

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
