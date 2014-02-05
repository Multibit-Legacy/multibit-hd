package org.multibit.hd.ui.views.detail_views.wallet;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.views.components.Buttons;
import org.multibit.hd.ui.views.components.Components;
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

  private int count = 0;

  public WalletDetailView() {

    CoreServices.uiEventBus.register(this);

    MigLayout layout = new MigLayout(
      "fill", // Layout constraints
      "[]10[]", // Column constraints
      "[]50[]" // Row constraints
    );
    contentPanel = Panels.newPanel(layout);

    Action showSendBitcoinWizardAction = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        Panels.showLightBox(Wizards.newSendBitcoinWizard().getWizardPanel());
      }
    };

    Action showReceiveBitcoinWizardAction = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        Panels.showLightBox(Wizards.newReceiveBitcoinWizard().getWizardPanel());
      }
    };

    contentPanel.add(Buttons.newSendBitcoinWizardButton(showSendBitcoinWizardAction),"w 240,h 200,align center,push");
    contentPanel.add(Buttons.newReceiveBitcoinWizardButton(showReceiveBitcoinWizardAction),"w 240, h 200,align center,push,wrap");
    contentPanel.add(Components.newWalletDetailPanel(),"span 2,grow");

  }

  /**
   * @return The content panel for this View
   */
  public JPanel getContentPanel() {
    return contentPanel;
  }

}
