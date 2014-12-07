package org.multibit.hd.ui.views.wizards.send_bitcoin;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import net.miginfocom.swing.MigLayout;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.uri.BitcoinURI;
import org.multibit.hd.brit.dto.FeeState;
import org.multibit.hd.core.config.Configuration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.WalletType;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.Languages;
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
 */
public class SendBitcoinConfirmPanelView extends AbstractWizardPanelView<SendBitcoinWizardModel, SendBitcoinConfirmPanelModel> {

  // View components
  private JTextArea notesTextArea;

  private ModelAndView<DisplayAmountModel, DisplayAmountView> transactionDisplayAmountMaV;
  private ModelAndView<DisplayAmountModel, DisplayAmountView> transactionFeeDisplayAmountMaV;
  private ModelAndView<DisplayAmountModel, DisplayAmountView> clientFeeDisplayAmountMaV;
  private ModelAndView<EnterPasswordModel, EnterPasswordView> enterPasswordMaV;

  private JLabel recipientSummaryLabel;

  private JLabel clientFeeInfoLabel;

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

    // Require a reference for the model
    enterPasswordMaV = Components.newEnterPasswordMaV(getPanelName());

    // Configure the panel model
    panelModel = new SendBitcoinConfirmPanelModel(
      getPanelName(),
      enterPasswordMaV.getModel()
    );
    setPanelModel(panelModel);

    // Bind it to the wizard model
    getWizardModel().setConfirmPanelModel(panelModel);

    // Register components
    registerComponents(enterPasswordMaV);

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    // Transaction information
    transactionDisplayAmountMaV = Components.newDisplayAmountMaV(
      DisplayAmountStyle.TRANSACTION_DETAIL_AMOUNT,
      true,
      SendBitcoinState.SEND_CONFIRM_AMOUNT.name() + ".transaction"
    );
    transactionFeeDisplayAmountMaV = Components.newDisplayAmountMaV(
      DisplayAmountStyle.FEE_AMOUNT,
      true,
      SendBitcoinState.SEND_CONFIRM_AMOUNT.name() + ".transaction_fee"
    );
    clientFeeDisplayAmountMaV = Components.newDisplayAmountMaV(
      DisplayAmountStyle.FEE_AMOUNT,
      true,
      SendBitcoinState.SEND_CONFIRM_AMOUNT.name() + ".client_fee"
    );

    // Ensure visibility
    transactionDisplayAmountMaV.getView().setVisible(true);
    transactionFeeDisplayAmountMaV.getView().setVisible(true);
    clientFeeDisplayAmountMaV.getView().setVisible(true);

    // Blank labels populated from wizard model later
    recipientSummaryLabel = Labels.newRecipientSummary(getWizardModel().getRecipient());

    // User entered text
    notesTextArea = TextBoxes.newEnterPrivateNotes(getWizardModel());

    // Apply any Bitcoin URI parameters
    if (getWizardModel().getBitcoinURI().isPresent()) {

      BitcoinURI uri = getWizardModel().getBitcoinURI().get();
      String notes = "";
      if (!Strings.isNullOrEmpty(uri.getLabel())) {
        // We have a label
        notes += uri.getLabel();
      }
      if (!Strings.isNullOrEmpty(uri.getMessage())) {
        // We have a message
        if (!Strings.isNullOrEmpty(notes)) {
          notes += "\n";
        }
        notes += uri.getMessage();
      }
      notesTextArea.setText(notes);

    }

    contentPanel.setLayout(
      new MigLayout(
        Panels.migXYLayout(),
        "[][]", // Column constraints
        "[]10[]10[][][]10[][]" // Row constraints
      ));

    clientFeeInfoLabel = Labels.newBlankLabel();
    AccessibilityDecorator.apply(clientFeeInfoLabel, MessageKey.CLIENT_FEE);

    contentPanel.add(Labels.newConfirmSendAmount(), "span 4,push,wrap");

    contentPanel.add(Labels.newRecipient());
    contentPanel.add(recipientSummaryLabel, "span 3,wrap");

    contentPanel.add(Labels.newAmount(), "baseline");
    contentPanel.add(transactionDisplayAmountMaV.getView().newComponentPanel(), "span 3,wrap");

    contentPanel.add(Labels.newTransactionFee(), "top");
    contentPanel.add(transactionFeeDisplayAmountMaV.getView().newComponentPanel(), "span 3,wrap");

    contentPanel.add(Labels.newDeveloperFee(), "top");
    contentPanel.add(clientFeeDisplayAmountMaV.getView().newComponentPanel(), "top");
    contentPanel.add(clientFeeInfoLabel, "top");

    contentPanel.add(Labels.newBlankLabel(), "top, growx, push,wrap");

    contentPanel.add(Labels.newNotes());
    contentPanel.add(notesTextArea, "span 3,growx,push,wrap");

    if (!isTrezorWallet()) {
      contentPanel.add(enterPasswordMaV.getView().newComponentPanel(), "span 4,align right,wrap");
    }

    // Register components
    registerComponents(transactionDisplayAmountMaV, transactionFeeDisplayAmountMaV, clientFeeDisplayAmountMaV);

  }

  @Override
  protected void initialiseButtons(AbstractWizard<SendBitcoinWizardModel> wizard) {

    PanelDecorator.addCancelPreviousSend(this, wizard);

  }

  @Override
  public void fireInitialStateViewEvents() {

    // Send button starts off disabled for regular wallets, enabled for Trezor hard wallets
    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.NEXT, isTrezorWallet());

  }

  private boolean isTrezorWallet() {
    return WalletManager.INSTANCE.getCurrentWalletSummary().isPresent() &&
                WalletType.TREZOR_HARD_WALLET.equals(WalletManager.INSTANCE.getCurrentWalletSummary().get().getWalletType());
  }

  @Override
  public boolean beforeShow() {

    Configuration configuration = Configurations.currentConfiguration;

    // Update the model and view for the amount
    transactionDisplayAmountMaV.getModel().setCoinAmount(getWizardModel().getCoinAmount());
    if (getWizardModel().getLocalAmount().isPresent()) {
      transactionDisplayAmountMaV.getModel().setLocalAmount(getWizardModel().getLocalAmount().get());
    } else {
      transactionDisplayAmountMaV.getModel().setLocalAmount(null);
    }
    transactionDisplayAmountMaV.getView().updateView(configuration);

    // Update the model and view for the transaction fee - by this point the prepareTransaction will have been called by the SendBitcoinWizardModel#showNext
    Optional<Wallet.SendRequest> sendRequest = getWizardModel().getSendRequestSummary().getSendRequest();
    if (sendRequest.isPresent()) {
      transactionFeeDisplayAmountMaV.getModel().setCoinAmount(sendRequest.get().fee);
    }
    transactionFeeDisplayAmountMaV.getModel().setLocalAmountVisible(false);
    transactionFeeDisplayAmountMaV.getView().updateView(configuration);

    // Update the model and view for the client fee
    Optional<FeeState> feeStateOptional = WalletManager.INSTANCE.calculateBRITFeeState(true);
    String feeText;
    if (feeStateOptional.isPresent()) {
      FeeState feeState = feeStateOptional.get();

      if (feeState.getCurrentNumberOfSends() == feeState.getNextFeeSendCount()) {
        // The fee is due at the next send e.g. current number of sends = 20, nextFeeSendCount = 20 (the 21st send i.e. the coming one)
        feeText = Languages.safeText(MessageKey.CLIENT_FEE_NOW);
      } else if (feeState.getFeeOwed().compareTo(Coin.ZERO) < 0) {
        // The user has overpaid
        feeText = Languages.safeText(MessageKey.CLIENT_FEE_OVERPAID);
      } else {
        // It is due later
        int dueLater = feeState.getNextFeeSendCount() - feeState.getCurrentNumberOfSends();
        if (dueLater == 1) {
          feeText = Languages.safeText(MessageKey.CLIENT_FEE_LATER_SINGULAR, dueLater);
        } else {
          feeText = Languages.safeText(MessageKey.CLIENT_FEE_LATER_PLURAL, dueLater);
        }
      }

      clientFeeDisplayAmountMaV.getModel().setCoinAmount(feeState.getFeeOwed());
      clientFeeDisplayAmountMaV.getModel().setLocalAmountVisible(false);
      clientFeeDisplayAmountMaV.getView().updateView(configuration);

    } else {
      // Possibly no wallet loaded
      feeText = "";
    }

    clientFeeInfoLabel.setText(feeText);

    // Update the model and view for the recipient
    recipientSummaryLabel.setText(
      getWizardModel()
        .getRecipient()
        .getSummary()
    );

    return true;
  }

  @Override
  public void afterShow() {

    SwingUtilities.invokeLater(
      new Runnable() {
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