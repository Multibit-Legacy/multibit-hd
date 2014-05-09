package org.multibit.hd.ui.views.wizards.empty_wallet;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.brit.dto.FeeState;
import org.multibit.hd.core.config.Configuration;
import org.multibit.hd.core.config.Configurations;
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
import java.math.BigInteger;

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
    transactionDisplayAmountMaV = Components.newDisplayAmountMaV(DisplayAmountStyle.TRANSACTION_DETAIL_AMOUNT, true, "transaction");
    transactionFeeDisplayAmountMaV = Components.newDisplayAmountMaV(DisplayAmountStyle.FEE_AMOUNT, true, "transaction_fee");
    clientFeeDisplayAmountMaV = Components.newDisplayAmountMaV(DisplayAmountStyle.FEE_AMOUNT, true, "client_fee");

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

    contentPanel.add(Labels.newTransactionFee(getWizardModel().getTransactionFee()), "top");
    contentPanel.add(transactionFeeDisplayAmountMaV.getView().newComponentPanel(), "span 3,wrap");

    contentPanel.add(Labels.newDeveloperFee(), "top");
    contentPanel.add(clientFeeDisplayAmountMaV.getView().newComponentPanel(), "top");
    contentPanel.add(clientFeeInfoLabel, "top");

    contentPanel.add(Labels.newBlankLabel(), "top, growx, push,wrap");

  }

  @Override
  protected void initialiseButtons(AbstractWizard<EmptyWalletWizardModel> wizard) {

    PanelDecorator.addCancelPreviousSend(this, wizard);

  }

  @Override
  public void fireInitialStateViewEvents() {

    // Send button starts off enabled (nothing to confirm besides values)
    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.NEXT, true);

  }

  @Override
  public boolean beforeShow() {

    Configuration configuration = Configurations.currentConfiguration;

    // Update the model and view for the amount
    transactionDisplayAmountMaV.getModel().setSatoshis(getWizardModel().getSatoshis());
    transactionDisplayAmountMaV.getModel().setLocalAmountVisible(false);
    transactionDisplayAmountMaV.getView().updateView(configuration);

    // Update the model and view for the transaction fee
    transactionFeeDisplayAmountMaV.getModel().setSatoshis(getWizardModel().getTransactionFee());
    transactionFeeDisplayAmountMaV.getModel().setLocalAmountVisible(false);
    transactionFeeDisplayAmountMaV.getView().updateView(configuration);

    // Update the model and view for the developer fee
    Optional<FeeState> feeStateOptional = getWizardModel().calculateBRITFeeState();
    String feeText;
    if (feeStateOptional.isPresent()) {
      FeeState feeState = feeStateOptional.get();

      if (feeState.getCurrentNumberOfSends() == feeState.getNextFeeSendCount()) {

        // The fee is due now - so check for zero owing due to dust/force etc
        if (feeState.getFeeOwed().compareTo(BigInteger.ZERO) == 0) {
          feeText = "";
        } else {
          feeText = Languages.safeText(MessageKey.CLIENT_FEE_NOW);
        }

      } else if (feeState.getFeeOwed().compareTo(BigInteger.ZERO) < 0) {
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

      clientFeeDisplayAmountMaV.getModel().setSatoshis(feeState.getFeeOwed());
      clientFeeDisplayAmountMaV.getModel().setLocalAmountVisible(false);
      clientFeeDisplayAmountMaV.getView().updateView(configuration);

    } else

    {
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