package org.multibit.hd.ui.views.wizards.send_bitcoin;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.config.Configuration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.display_amount.DisplayAmountModel;
import org.multibit.hd.ui.views.components.display_amount.DisplayAmountStyle;
import org.multibit.hd.ui.views.components.display_amount.DisplayAmountView;
import org.multibit.hd.ui.views.components.enter_password.EnterPasswordModel;
import org.multibit.hd.ui.views.components.enter_password.EnterPasswordView;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
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
public class SendBitcoinConfirmPanelView extends AbstractWizardPanelView<SendBitcoinWizardModel, SendBitcoinConfirmPanelModel> {

  // View components
  private JTextArea notesTextArea;

  private ModelAndView<DisplayAmountModel, DisplayAmountView> transactionDisplayAmountMaV;
  private ModelAndView<DisplayAmountModel, DisplayAmountView> transactionFeeDisplayAmountMaV;
  private ModelAndView<DisplayAmountModel, DisplayAmountView> developerFeeDisplayAmountMaV;
  private ModelAndView<EnterPasswordModel, EnterPasswordView> enterPasswordMaV;

  private JLabel recipientSummaryLabel;

  private SendBitcoinConfirmPanelModel panelModel;

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name for filtering component events
   */
  public SendBitcoinConfirmPanelView(AbstractWizard<SendBitcoinWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.CONFIRM_SEND_TITLE, AwesomeIcon.CLOUD_UPLOAD);

  }

  @Override
  public void newPanelModel() {

    transactionDisplayAmountMaV = Components.newDisplayAmountMaV(DisplayAmountStyle.TRANSACTION_DETAIL_AMOUNT);
    transactionFeeDisplayAmountMaV = Components.newDisplayAmountMaV(DisplayAmountStyle.FEE_AMOUNT);
    developerFeeDisplayAmountMaV = Components.newDisplayAmountMaV(DisplayAmountStyle.FEE_AMOUNT);
    enterPasswordMaV = Components.newEnterPasswordMaV(getPanelName());

    // Configure the panel model
    panelModel = new SendBitcoinConfirmPanelModel(
      getPanelName(),
      enterPasswordMaV.getModel()
    );
    setPanelModel(panelModel);

    // Bind it to the wizard model
    getWizardModel().setConfirmPanelModel(panelModel);

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    // Blank labels populated from wizard model later
    recipientSummaryLabel = Labels.newBlankLabel();

    // User entered text
    notesTextArea = TextBoxes.newEnterNotes(getWizardModel());

    contentPanel.setLayout(new MigLayout(
      Panels.migXYLayout(),
      "[][]", // Column constraints
      "[]10[]10[][][]10[][]" // Row constraints
    ));

    contentPanel.add(Labels.newConfirmSendAmount(), "span 2,push,wrap");
    contentPanel.add(Labels.newRecipient());
    contentPanel.add(recipientSummaryLabel, "wrap");
    contentPanel.add(Labels.newAmount(), "baseline");
    contentPanel.add(transactionDisplayAmountMaV.getView().newComponentPanel(), "wrap");
    contentPanel.add(Labels.newTransactionFee(getWizardModel().getTransactionFee()), "top");
    contentPanel.add(transactionFeeDisplayAmountMaV.getView().newComponentPanel(), "wrap");
    contentPanel.add(Labels.newDeveloperFee(getWizardModel().getDeveloperFee()), "top");
    contentPanel.add(developerFeeDisplayAmountMaV.getView().newComponentPanel(), "wrap");
    contentPanel.add(Labels.newNotes());
    contentPanel.add(notesTextArea, "growx,push,wrap");
    contentPanel.add(Labels.newEnterPassword());
    contentPanel.add(enterPasswordMaV.getView().newComponentPanel(), "wrap");

  }

  @Override
  protected void initialiseButtons(AbstractWizard<SendBitcoinWizardModel> wizard) {

    PanelDecorator.addCancelPreviousSend(this, wizard);

  }

  @Override
  public boolean beforeShow() {

    Configuration configuration = Configurations.currentConfiguration;

    // Update the model and view for the amount
    transactionDisplayAmountMaV.getModel().setSatoshis(getWizardModel().getSatoshis());
    transactionDisplayAmountMaV.getModel().setLocalAmount(getWizardModel().getLocalAmount());
    transactionDisplayAmountMaV.getView().updateView(configuration);

    // Update the model and view for the transaction fee
    transactionFeeDisplayAmountMaV.getModel().setSatoshis(getWizardModel().getTransactionFee());
    transactionFeeDisplayAmountMaV.getModel().setLocalAmountVisible(false);
    transactionFeeDisplayAmountMaV.getView().updateView(configuration);

    // Update the model and view for the developer fee
    developerFeeDisplayAmountMaV.getModel().setSatoshis(getWizardModel().getDeveloperFee());
    developerFeeDisplayAmountMaV.getModel().setLocalAmountVisible(false);
    developerFeeDisplayAmountMaV.getView().updateView(configuration);

    // Update the model and view for the recipient
    recipientSummaryLabel.setText(getWizardModel().getRecipient().getSummary());

    return true;
  }

  @Override
  public void afterShow() {

    registerDefaultButton(getNextButton());

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        notesTextArea.requestFocusInWindow();
      }
    });

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {

    panelModel.setNotes(notesTextArea.getText());

    // Determine any events
    ViewEvents.fireWizardButtonEnabledEvent(
            getPanelName(),
            WizardButton.NEXT,
            isNextEnabled()
    );

  }

  /**
   * @return True if the "next" button should be enabled
   */
  private boolean isNextEnabled() {

    return !Strings.isNullOrEmpty(getPanelModel().get().getPasswordModel().getValue());
  }
}
