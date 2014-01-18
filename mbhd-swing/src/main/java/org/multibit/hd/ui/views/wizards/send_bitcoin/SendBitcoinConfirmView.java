package org.multibit.hd.ui.views.wizards.send_bitcoin;

import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.api.MessageKey;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.events.view.WizardModelChangedEvent;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.display_amount.DisplayAmountModel;
import org.multibit.hd.ui.views.components.display_amount.DisplayAmountView;
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

  // View components
  private JTextArea notesTextArea;
  private JPasswordField passwordField;
  private ModelAndView<DisplayAmountModel, DisplayAmountView> displayAmountMaV;
  private JLabel recipientSummaryLabel;

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

    displayAmountMaV = Components.newDisplayAmountMaV();

    recipientSummaryLabel = new JLabel("");
    notesTextArea = TextBoxes.newEnterNotes();
    passwordField = TextBoxes.newPassword();

    setPanelModel(new SendBitcoinConfirmPanelModel(getPanelName()));

    JPanel panel = Panels.newPanel(new MigLayout(
      "fillx,insets 0", // Layout constraints
      "[][]", // Column constraints
      "[]10[]10[]10[]10[]" // Row constraints
    ));

    panel.add(Labels.newConfirmSendAmount(), "span 2,push,wrap");
    panel.add(Labels.newRecipient());
    panel.add(recipientSummaryLabel, "span 2,wrap");
    panel.add(Labels.newAmount());
    panel.add(displayAmountMaV.getView().newPanel(), "span 2,wrap");
    panel.add(Labels.newNotes());
    panel.add(notesTextArea, "growx,push,wrap");
    panel.add(Labels.newEnterPassword());
    panel.add(passwordField, "growx,push,wrap");

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

  @Subscribe
  public void onWizardModelChangedEvent(WizardModelChangedEvent event) {

    // Update the model and view for the amount
    displayAmountMaV.getModel().setBitcoinAmount(getWizardModel().getBitcoinAmount());
    displayAmountMaV.getModel().setLocalAmount(getWizardModel().getLocalAmount());
    displayAmountMaV.getView().updateView();

    // Update the model and view for the recipient
    recipientSummaryLabel.setText(getWizardModel().getRecipient().getSummary());


  }
}
