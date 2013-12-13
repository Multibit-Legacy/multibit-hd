package org.multibit.hd.ui.views.detail_views;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.api.RAGStatus;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.audio.Sounds;
import org.multibit.hd.ui.events.controller.ControllerEvents;
import org.multibit.hd.ui.models.AlertModel;
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

  private int count = 0;

  public WalletDetailView() {

    CoreServices.uiEventBus.register(this);

    MigLayout layout = new MigLayout(
      "fill", // Layout constrains
      "[]10[]", // Column constraints
      "[]50[]" // Row constraints
    );
    contentPanel = Panels.newPanel(layout);

    Action showSendBitcoinWizardAction = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Panels.showLightBox(Wizards.newSendBitcoinWizard().getContentPanel());
      }
    };

    Action showReceiveBitcoinWizardAction = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        // Select the event
        final AlertModel alertModel;
        switch (count % 3) {
          case 0:
            alertModel = new AlertModel("Bad thing "+count, RAGStatus.RED);
            break;
          case 1:
            alertModel = new AlertModel("Warning thing "+count, RAGStatus.AMBER);
            break;
          case 2:
            alertModel = new AlertModel("Good thing "+count, RAGStatus.GREEN);
            Sounds.playReceiveBitcoin();
            break;
          default:
            throw new IllegalStateException("Bang");
        }

        ControllerEvents.fireAddAlertEvent(alertModel);

        count++;
      }
    };

    contentPanel.add(Buttons.newSendBitcoinWizardButton(showSendBitcoinWizardAction),"w 240,h 200,align center,push");
    contentPanel.add(Buttons.newReceiveBitcoinWizardButton(showReceiveBitcoinWizardAction),"w 240, h 200,align center,push,wrap");
    contentPanel.add(Panels.newWalletDetailPanel(),"span 2,grow");

  }

  /**
   * @return The content panel for this View
   */
  public JPanel getContentPanel() {
    return contentPanel;
  }

}
