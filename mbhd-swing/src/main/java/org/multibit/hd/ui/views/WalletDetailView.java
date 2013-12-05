package org.multibit.hd.ui.views;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.services.CoreServices;
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
      "fill", // Layout constrains
      "[]10[]", // Column constraints
      "[grow]" // Row constraints
    );
    contentPanel = new JPanel(layout);

    Action[] actions = {
      new ShowSendBitcoinWizardAction(),
      new ShowReceiveBitcoinWizardAction()
    };

    for (Action action : actions) {
      contentPanel.add(new JButton(action),"grow");
    }

  }

  /**
   * @return The content panel for this View
   */
  public JPanel getContentPanel() {
    return contentPanel;
  }

  private class ShowSendBitcoinWizardAction extends AbstractAction {
    public ShowSendBitcoinWizardAction() {
      super("Send");
    }

    @Override
    public void actionPerformed(ActionEvent e) {

      // TODO Add ViewEvent instead of direct creation
      // And this is develop
      Panels.applyLightBoxPanel(Wizards.newSendBitcoinWizard().getContentPanel());

    }
  }

  private class ShowReceiveBitcoinWizardAction extends AbstractAction {
    public ShowReceiveBitcoinWizardAction() {
      super("Receive");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      Panels.applyLightBoxPanel(Wizards.newSendBitcoinWizard().getContentPanel());
    }
  }

}
