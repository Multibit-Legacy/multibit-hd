package org.multibit.hd.ui.views.wizards.empty_wallet;

import com.google.bitcoin.core.Coin;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.Wallet;
import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.brit.dto.FeeState;
import org.multibit.hd.core.config.Configuration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.display_amount.DisplayAmountModel;
import org.multibit.hd.ui.views.components.display_amount.DisplayAmountStyle;
import org.multibit.hd.ui.views.components.display_amount.DisplayAmountView;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;

import javax.swing.*;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Empty wallet: Confirm</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class EmptyWalletConfirmPanelView extends AbstractWizardPanelView<EmptyWalletWizardModel, String> {

  // View components
  private ModelAndView<DisplayAmountModel, DisplayAmountView> transactionDisplayAmountMaV;
  private ModelAndView<DisplayAmountModel, DisplayAmountView> transactionFeeDisplayAmountMaV;
  private ModelAndView<DisplayAmountModel, DisplayAmountView> clientFeeDisplayAmountMaV;

  private JLabel recipientSummaryLabel;

  private JLabel clientFeeInfoLabel;

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name for filtering component events
   */
  public EmptyWalletConfirmPanelView(AbstractWizard<EmptyWalletWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.EMPTY_WALLET_CONFIRM_TITLE, AwesomeIcon.FIRE);

  }

  @Override
  public void newPanelModel() {

    // Configure the panel model (no user data)

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    // Transaction information
    transactionDisplayAmountMaV = Components.newDisplayAmountMaV(
      DisplayAmountStyle.TRANSACTION_DETAIL_AMOUNT,
      true,
      EmptyWalletState.EMPTY_WALLET_CONFIRM.name() + ".transaction"
    );
    transactionFeeDisplayAmountMaV = Components.newDisplayAmountMaV(
      DisplayAmountStyle.FEE_AMOUNT,
      true,
      EmptyWalletState.EMPTY_WALLET_CONFIRM.name() + ".transaction_fee"
    );
    clientFeeDisplayAmountMaV = Components.newDisplayAmountMaV(
      DisplayAmountStyle.FEE_AMOUNT,
      true,
      EmptyWalletState.EMPTY_WALLET_CONFIRM.name() + ".client_fee"
    );

    // Ensure amounts are visible
    transactionDisplayAmountMaV.getView().setVisible(true);
    transactionFeeDisplayAmountMaV.getView().setVisible(true);
    clientFeeDisplayAmountMaV.getView().setVisible(true);

    // Blank labels populated from wizard model later
    recipientSummaryLabel = Labels.newRecipientSummary(getWizardModel().getRecipient());

    contentPanel.setLayout(new MigLayout(
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

    // Register components
    registerComponents(transactionDisplayAmountMaV, transactionFeeDisplayAmountMaV, clientFeeDisplayAmountMaV);

  }

  @Override
  protected void initialiseButtons(AbstractWizard<EmptyWalletWizardModel> wizard) {

    PanelDecorator.addCancelPreviousSend(this, wizard);

  }

  @Override
  public boolean beforeShow() {

    Configuration configuration = Configurations.currentConfiguration;

    // Update the model and view for the amount
    transactionDisplayAmountMaV.getModel().setCoinAmount(getWizardModel().getCoinAmount().or(Coin.ZERO));
    transactionDisplayAmountMaV.getModel().setLocalAmountVisible(false);
    transactionDisplayAmountMaV.getView().updateView(configuration);

    // Update the model and view for the transaction fee - by this point the prepareTransaction will have been called by the EmptyWalletWizardModel#showNext
    Optional<Wallet.SendRequest> sendRequest = getWizardModel().getSendRequestSummary().getSendRequest();
    if (sendRequest.isPresent()) {
      transactionFeeDisplayAmountMaV.getModel().setCoinAmount(sendRequest.get().fee);
    }
    transactionFeeDisplayAmountMaV.getModel().setLocalAmountVisible(false);
    transactionFeeDisplayAmountMaV.getView().updateView(configuration);

    // Update the model and view for the client fee
    Optional<FeeState> feeStateOptional = WalletManager.INSTANCE.calculateBRITFeeState();
    log.debug("Fee state at beforeShow {}", feeStateOptional);
    String feeText;
    if (feeStateOptional.isPresent()) {
      FeeState feeState = feeStateOptional.get();

      // With an empty wallet you always pay the client fee now (if above the dust level)
      if (feeState.getFeeOwed().compareTo(Coin.ZERO) < 0) {
        // The user has overpaid
        feeText = Languages.safeText(MessageKey.CLIENT_FEE_OVERPAID);
      }  else {
        if (feeState.getFeeOwed().compareTo(Transaction.MIN_NONDUST_OUTPUT) <= 0) {
          // Below dust limit
          feeText = "";
        } else {
          // The fee is due now
          feeText = Languages.safeText(MessageKey.CLIENT_FEE_NOW);
          clientFeeDisplayAmountMaV.getModel().setCoinAmount(feeState.getFeeOwed());
          clientFeeDisplayAmountMaV.getModel().setLocalAmountVisible(false);
          clientFeeDisplayAmountMaV.getView().updateView(configuration);
        }
      }
    } else {
      // Possibly no wallet loaded
      feeText = "";
    }

    clientFeeInfoLabel.setText(feeText);

    // Update the model and view for the recipient
    recipientSummaryLabel.setText(getWizardModel()
        .getRecipient()
        .getSummary()
    );

    return true;
  }

  @Override
  public void afterShow() {

    // Start with Cancel having focus to avoid accidental confirmation
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        getCancelButton().requestFocusInWindow();
        // Enable the Send button after showing since there is nothing to stop confirmation
        // It should start disabled to avoid double click skipping the confirmation
        ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.NEXT, true);

      }
    });

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {

    // Determine any events
    ViewEvents.fireWizardButtonEnabledEvent(
      getPanelName(),
      WizardButton.NEXT,
      true
    );
  }
}