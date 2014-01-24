package org.multibit.hd.ui.views.wizards.send_bitcoin;

import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.api.MessageKey;
import org.multibit.hd.ui.events.view.WizardModelChangedEvent;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.display_amount.DisplayAmountModel;
import org.multibit.hd.ui.views.components.display_amount.DisplayAmountStyle;
import org.multibit.hd.ui.views.components.display_amount.DisplayAmountView;
import org.multibit.hd.ui.views.components.enter_password.EnterPasswordModel;
import org.multibit.hd.ui.views.components.enter_password.EnterPasswordView;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardView;

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

  private ModelAndView<DisplayAmountModel, DisplayAmountView> transactionDisplayAmountMaV;
  private ModelAndView<DisplayAmountModel, DisplayAmountView> transactionFeeDisplayAmountMaV;
  private ModelAndView<DisplayAmountModel, DisplayAmountView> developerFeeDisplayAmountMaV;
  private ModelAndView<EnterPasswordModel, EnterPasswordView> enterPasswordMaV;

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
  public JPanel newWizardViewPanel() {

    transactionDisplayAmountMaV = Components.newDisplayAmountMaV(DisplayAmountStyle.TRANSACTION_DETAIL_AMOUNT);
    transactionFeeDisplayAmountMaV = Components.newDisplayAmountMaV(DisplayAmountStyle.FEE_AMOUNT);
    developerFeeDisplayAmountMaV = Components.newDisplayAmountMaV(DisplayAmountStyle.FEE_AMOUNT);
    enterPasswordMaV = Components.newEnterPasswordMaV(getWizardViewPanelName());

    // Configure the panel model
    setPanelModel(new SendBitcoinConfirmPanelModel(
      getWizardViewPanelName(),
      enterPasswordMaV.getModel()
    ));

    // Blank labels populated from wizard model later
    recipientSummaryLabel = new JLabel("");

    // User entered text
    notesTextArea = TextBoxes.newEnterNotes();

    JPanel panel = Panels.newPanel(new MigLayout(
      "fillx,insets 0", // Layout constraints
      "[][]", // Column constraints
      "[]10[]10[][][]10[][]" // Row constraints
    ));

    panel.add(Labels.newConfirmSendAmount(), "span 2,push,wrap");
    panel.add(Labels.newRecipient());
    panel.add(recipientSummaryLabel, "wrap");
    panel.add(Labels.newAmount(),"baseline");
    panel.add(transactionDisplayAmountMaV.getView().newPanel(), "wrap");
    panel.add(Labels.newTransactionFee(getWizardModel().getRawTransactionFee()),"top");
    panel.add(transactionFeeDisplayAmountMaV.getView().newPanel(), "wrap");
    panel.add(Labels.newDeveloperFee(getWizardModel().getRawDeveloperFee()),"top");
    panel.add(developerFeeDisplayAmountMaV.getView().newPanel(), "wrap");
    panel.add(Labels.newNotes());
    panel.add(notesTextArea, "growx,push,wrap");
    panel.add(Labels.newEnterPassword());
    panel.add(enterPasswordMaV.getView().newPanel(),"wrap");

    return panel;
  }

  @Override
  public boolean updateFromComponentModels() {
    // Do nothing - panel model is updated via an action and wizard model is not applicable
    return true;
  }

  @Subscribe
  public void onWizardModelChangedEvent(WizardModelChangedEvent event) {

    // Update the model and view for the amount
    transactionDisplayAmountMaV.getModel().setRawBitcoinAmount(getWizardModel().getRawBitcoinAmount());
    transactionDisplayAmountMaV.getModel().setLocalAmount(getWizardModel().getLocalAmount());
    transactionDisplayAmountMaV.getView().updateView();

    // Update the model and view for the transaction fee
    transactionFeeDisplayAmountMaV.getModel().setRawBitcoinAmount(getWizardModel().getRawTransactionFee());
    transactionFeeDisplayAmountMaV.getModel().setLocalAmountVisible(false);
    transactionFeeDisplayAmountMaV.getView().updateView();

    // Update the model and view for the developer fee
    developerFeeDisplayAmountMaV.getModel().setRawBitcoinAmount(getWizardModel().getRawDeveloperFee());
    developerFeeDisplayAmountMaV.getModel().setLocalAmountVisible(false);
    developerFeeDisplayAmountMaV.getView().updateView();

    // Update the model and view for the recipient
    recipientSummaryLabel.setText(getWizardModel().getRecipient().getSummary());

  }
}
