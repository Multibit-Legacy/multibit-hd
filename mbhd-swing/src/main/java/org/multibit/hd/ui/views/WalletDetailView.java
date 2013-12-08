package org.multibit.hd.ui.views;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.events.ViewEvents;
import org.multibit.hd.ui.views.components.Buttons;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.wizards.Wizards;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * <p>View to provide the following to application:</p>
 * <ul>
 * <li>Provision of components and layout for the wallet detail display</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class WalletDetailView {

  private final JPanel contentPanel;

  public WalletDetailView() {

    CoreServices.uiEventBus.register(this);

    MigLayout layout = new MigLayout(
      "fillx", // Layout constrains
      "[]10[]", // Column constraints
      "[grow]" // Row constraints
    );
    contentPanel = new JPanel(layout);

    Action showSendBitcoinWizardAction = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Panels.applyLightBoxPanel(Wizards.newSendBitcoinWizard().getContentPanel());
      }
    };

    Action showReceiveBitcoinWizardAction = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        ViewEvents.fireShowAlertEvent();
      }
    };

    contentPanel.add(Buttons.newSendBitcoinWizardButton(showSendBitcoinWizardAction),"grow");
    contentPanel.add(Buttons.newReceiveBitcoinWizardButton(showReceiveBitcoinWizardAction),"grow");

  }

  /**
   * @return The content panel for this View
   */
  public JPanel getContentPanel() {
    return contentPanel;
  }

}
