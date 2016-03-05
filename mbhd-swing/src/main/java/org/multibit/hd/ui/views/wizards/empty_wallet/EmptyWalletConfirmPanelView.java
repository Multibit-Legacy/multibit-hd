package org.multibit.hd.ui.views.wizards.empty_wallet;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Wallet;
import org.multibit.hd.core.config.Configuration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.Components;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.ModelAndView;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.display_amount.DisplayAmountModel;
import org.multibit.hd.ui.views.components.display_amount.DisplayAmountStyle;
import org.multibit.hd.ui.views.components.display_amount.DisplayAmountView;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

  private static final Logger log = LoggerFactory.getLogger(EmptyWalletConfirmPanelView.class);

  // View components
  private ModelAndView<DisplayAmountModel, DisplayAmountView> transactionDisplayAmountMaV;
  private ModelAndView<DisplayAmountModel, DisplayAmountView> transactionFeeDisplayAmountMaV;

  private JLabel recipientSummaryLabel;

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name for filtering component events
   */
  public EmptyWalletConfirmPanelView(AbstractWizard<EmptyWalletWizardModel> wizard, String panelName) {

    super(wizard, panelName, AwesomeIcon.FIRE, MessageKey.EMPTY_WALLET_CONFIRM_TITLE);

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

    // Ensure amounts are visible
    transactionDisplayAmountMaV.getView().setVisible(true);
    transactionFeeDisplayAmountMaV.getView().setVisible(true);

    // Blank labels populated from wizard model later
    recipientSummaryLabel = Labels.newRecipientSummary(getWizardModel().getRecipient());

    contentPanel.setLayout(new MigLayout(
      Panels.migXYLayout(),
      "[][][]", // Column constraints
      "[]10[]10[][][]" // Row constraints
    ));

    contentPanel.add(Labels.newConfirmSendAmount(), "span 3,push,wrap");

    contentPanel.add(Labels.newRecipient());
    contentPanel.add(recipientSummaryLabel, "align left, growx, push, wrap");

    contentPanel.add(Labels.newAmount(), "baseline");
    contentPanel.add(transactionDisplayAmountMaV.getView().newComponentPanel(), "align left, wrap");

    contentPanel.add(Labels.newTransactionFee(), "top");
    contentPanel.add(transactionFeeDisplayAmountMaV.getView().newComponentPanel(), "align left, wrap");

    contentPanel.add(Labels.newBlankLabel(), "span 3, top, growx, push,wrap");

    // Register components
    registerComponents(transactionDisplayAmountMaV, transactionFeeDisplayAmountMaV);

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
    getCancelButton().requestFocusInWindow();
    // Enable the Send button after showing since there is nothing to stop confirmation
    // It should start disabled to avoid double click skipping the confirmation
    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.NEXT, true);

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