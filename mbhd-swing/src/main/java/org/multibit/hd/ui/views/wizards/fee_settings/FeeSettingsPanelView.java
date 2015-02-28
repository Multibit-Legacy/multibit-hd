package org.multibit.hd.ui.views.wizards.fee_settings;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.bitcoinj.core.Coin;
import org.bitcoinj.uri.BitcoinURI;
import org.bitcoinj.uri.BitcoinURIParseException;
import org.multibit.hd.brit.services.FeeService;
import org.multibit.hd.core.config.Configuration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.config.WalletConfiguration;
import org.multibit.hd.ui.events.view.ViewEvents;
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
import org.multibit.hd.ui.views.wizards.Wizards;
import org.multibit.hd.ui.views.wizards.send_bitcoin.SendBitcoinParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Fee settings: select feePerKB</li>
 * </ul>
 *
 * @since 0.0.1
 */

public class FeeSettingsPanelView extends AbstractWizardPanelView<FeeSettingsWizardModel, FeeSettingsPanelModel> implements ChangeListener {

  private static final Logger log = LoggerFactory.getLogger(FeeSettingsPanelView.class);

  // Panel specific components
  private JSlider feePerKBSlider;

  private ModelAndView<DisplayAmountModel, DisplayAmountView> transactionFeeDisplayAmountMaV;

  private Configuration configuration;

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name
   */
  public FeeSettingsPanelView(AbstractWizard<FeeSettingsWizardModel> wizard, String panelName) {
    super(wizard, panelName, MessageKey.FEES_SETTINGS_TITLE, AwesomeIcon.TICKET);
  }

  @Override
  public void newPanelModel() {

    // Use a deep copy to avoid reference leaks
    configuration = Configurations.currentConfiguration.deepCopy();

    // Configure the panel model
    setPanelModel(
      new FeeSettingsPanelModel(
        getPanelName(),
        configuration
      ));
  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(
      new MigLayout(
        Panels.migXYLayout(),
        "[]20[]", // Column constraints
        "[]2[]8[]2[]6[]6[]2[]" // Row constraints
      ));

    WalletConfiguration walletConfiguration = Configurations.currentConfiguration.getWallet().deepCopy();
    feePerKBSlider = Sliders.newAdjustTransactionFeeSlider(this, walletConfiguration.getFeePerKB());

    transactionFeeDisplayAmountMaV = Components.newDisplayAmountMaV(
      DisplayAmountStyle.PLAIN,
      false,
      "transaction.fee.amount");
    JPanel transactionFeeAmountViewPanel = transactionFeeDisplayAmountMaV.getView().newComponentPanel();
    transactionFeeDisplayAmountMaV.getView().setVisible(true);

    contentPanel.add(Labels.newExplainTransactionFee1(), "span 2, wrap");
    contentPanel.add(Labels.newExplainTransactionFee2(), "span 2, wrap");
    contentPanel.add(Labels.newAdjustTransactionFee(), "shrink");
    contentPanel.add(feePerKBSlider, "growx,shrinky,width min:250:,wrap");

    contentPanel.add(Labels.newLabel(MessageKey.TRANSACTION_FEE_CHOSEN), "shrink");
    contentPanel.add(transactionFeeAmountViewPanel, "growx,shrinky,push,wrap");
    contentPanel.add(Labels.newBlankLabel(), "span 2, push, wrap"); // spacer

    contentPanel.add(Labels.newExplainClientFee1(FeeService.FEE_PER_SEND), "span 2, wrap");
    contentPanel.add(Labels.newExplainClientFee2(), "span 2, wrap");

    contentPanel.add(Labels.newBlankLabel(), "");
    contentPanel.add(Buttons.newDonateNowButton(createDonateNowAction()), "wrap");
    contentPanel.add(Labels.newBlankLabel(), "span 2, push, wrap"); // spacer
    setChosenFee();
  }

  /**
   * @return Action to process the 'donate now' button press
   */
  private Action createDonateNowAction() {
    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          setChosenFee();
          // Set the new feePerKB
          Configurations.currentConfiguration.getWallet().setFeePerKB(configuration.getWallet().getFeePerKB());

          Panels.hideLightBoxIfPresent();

          SendBitcoinParameter donateParameter = new SendBitcoinParameter(
            new BitcoinURI("bitcoin:" + FeeService.DONATION_ADDRESS + "?amount=" + FeeService.DEFAULT_DONATION_AMOUNT),
            null
          );
          Panels.showLightBox(Wizards.newSendBitcoinWizard(donateParameter).getWizardScreenHolder());
        } catch (BitcoinURIParseException pe) {
          // Should not happen
          log.error(pe.getMessage());
        }
      }
    };
  }

  @Override
  protected void initialiseButtons(AbstractWizard<FeeSettingsWizardModel> wizard) {
    PanelDecorator.addCancelApply(this, wizard);
  }

  @Override
  public void fireInitialStateViewEvents() {
    // Apply button starts off enabled
    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.APPLY, true);
  }

  @Override
  public void afterShow() {
    SwingUtilities.invokeLater(
      new Runnable() {
        @Override
        public void run() {
          feePerKBSlider.requestFocusInWindow();
        }
      });
  }

  @Override
  public boolean beforeHide(boolean isExitCancel) {
    if (!isExitCancel) {

      // Set the new feePerKB
      Configurations.currentConfiguration.getWallet().setFeePerKB(configuration.getWallet().getFeePerKB());
    }

    // Must be OK to proceed
    return true;
  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {
    // Do nothing
  }

  @Override
  public void stateChanged(ChangeEvent e) {
    setChosenFee();
    getPanelModel().get().getConfiguration().getWallet().setFeePerKB(feePerKBSlider.getValue() * Sliders.RESOLUTION);
  }

  private void setChosenFee() {
    transactionFeeDisplayAmountMaV.getModel().setCoinAmount(Coin.valueOf(feePerKBSlider.getValue() * Sliders.RESOLUTION));
    transactionFeeDisplayAmountMaV.getModel().setLocalAmountVisible(false);
    transactionFeeDisplayAmountMaV.getView().updateView(configuration);
  }
}
