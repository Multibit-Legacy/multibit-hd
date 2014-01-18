package org.multibit.hd.ui.views.wizards.send_bitcoin;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.api.MessageKey;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.PanelDecorator;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.TextBoxes;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardView;
import org.multibit.hd.ui.views.wizards.WizardButton;

import javax.swing.*;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Send bitcoin: Confirm</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class SendBitcoinConfirmView extends AbstractWizardView<SendBitcoinWizardModel, SendBitcoinConfirmPanelModel> {

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name for filtering component events
   */
  public SendBitcoinConfirmView(AbstractWizard<SendBitcoinWizardModel> wizard, String panelName) {

    super(wizard.getWizardModel(), panelName, MessageKey.CONFIRM_SEND_TITLE);

    PanelDecorator.addCancelPreviousSend(this, wizard);

  }

  @Override
  public JPanel newDataPanel() {

    setPanelModel(new SendBitcoinConfirmPanelModel(getPanelName()));

    JPanel panel = Panels.newPanel(new MigLayout(
      "debug,fillx,insets 0", // Layout constrains
      "[][]", // Column constraints
      "[]10[]10[]" // Row constraints
    ));

    panel.add(Labels.newConfirmSendAmount(), "span 2,push,wrap");
    //panel.add(Labels.newConfirmSendNote(getWizardModel().getTransactionModel()),"span 2,wrap");
    panel.add(Labels.newNotes());
    panel.add(TextBoxes.newEnterNotes(), "growx,push,wrap");
    panel.add(Labels.newEnterPassword());
    panel.add(TextBoxes.newPassword(), "growx,push,wrap");

    return panel;
  }

  @Override
  public void fireViewEvents() {
    // Disable the next (send) button
    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.NEXT, false);
  }

  @Override
  public boolean updatePanelModel() {
    // Do nothing - panel model is updated via an action and wizard model is not applicable
    return true;
  }

}
