package org.multibit.hd.ui.views.wizards.fee_settings;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.bitcoinj.core.Coin;
import org.multibit.hd.brit.services.FeeService;
import org.multibit.hd.core.config.Configuration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.config.WalletConfiguration;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.Languages;
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.Hashtable;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Fee settings: select feePerKB</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */

public class FeeSettingsPanelView extends AbstractWizardPanelView<FeeSettingsWizardModel, FeeSettingsPanelModel> implements ChangeListener {

  private static final Logger log = LoggerFactory.getLogger(FeeSettingsPanelView.class);

  // Panel specific components
  private JSlider feePerKBSlider;
  private static final int RESOLUTION = 100;

  private JLabel resultLabel;

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
    setPanelModel(new FeeSettingsPanelModel(
      getPanelName(),
      configuration
    ));
  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(new MigLayout(
            Panels.migXYLayout(),
            "[][]", // Column constraints
            "[]10[]" // Row constraints
    ));

    WalletConfiguration walletConfiguration = Configurations.currentConfiguration.getWallet().deepCopy();

    // Resolution is RESOLUTION satoshis per tick
    int minimumPosition = (int)FeeService.MINIMUM_FEE_PER_KB.longValue()/RESOLUTION;
    int defaultPosition = (int)FeeService.DEFAULT_FEE_PER_KB.longValue()/RESOLUTION;
    int maximumPosition = (int)FeeService.MAXIMUM_FEE_PER_KB.longValue()/RESOLUTION;

    // Make sure feePerKB is normalised first so that it will be in range of the slider
    int currentPosition = (int)FeeService.normaliseRawFeePerKB(walletConfiguration.getFeePerKB()).longValue()/RESOLUTION;
    feePerKBSlider = new JSlider(minimumPosition, maximumPosition,
            currentPosition);
    feePerKBSlider.setMajorTickSpacing(10);
    feePerKBSlider.setMinorTickSpacing(2);
    feePerKBSlider.setPaintTicks(true);

    // Create the label table
    Hashtable<Integer, JComponent> labelTable = new Hashtable<>();
    labelTable.put( minimumPosition, new JLabel(Languages.safeText(MessageKey.LOWER)));
    labelTable.put( defaultPosition, new JLabel(Languages.toCapitalCase(Languages.safeText(MessageKey.DEFAULT))));
    labelTable.put( maximumPosition, new JLabel(Languages.safeText(MessageKey.HIGHER)));
    feePerKBSlider.setLabelTable(labelTable);
    feePerKBSlider.setPaintLabels(true);

    feePerKBSlider.addChangeListener(this);

    transactionFeeDisplayAmountMaV = Components.newDisplayAmountMaV(
                DisplayAmountStyle.PLAIN,
                false,
                "transaction.fee.amount");
    JPanel transactionFeeAmountViewPanel = transactionFeeDisplayAmountMaV.getView().newComponentPanel();
    transactionFeeDisplayAmountMaV.getView().setVisible(true);

    contentPanel.add(Labels.newAdjustTransactionFee(), "shrink");
    contentPanel.add(feePerKBSlider, "growx,shrinky,width min:250:,push,wrap");

    resultLabel = Labels.newBlankLabel();
    resultLabel.setText(Languages.safeText(MessageKey.TRANSACTION_FEE_CHOSEN));
    contentPanel.add(resultLabel, "growx,shrinky");
    contentPanel.add(transactionFeeAmountViewPanel, "growx,shrinky,push,wrap");

    setChosenFee();

    contentPanel.add(Labels.newBlankLabel(), "grow,span 2,push,wrap"); // Fill out the remainder
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
    SwingUtilities.invokeLater(new Runnable() {
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
    getPanelModel().get().getConfiguration().getWallet().setFeePerKB(feePerKBSlider.getValue() * RESOLUTION);
  }

  private void setChosenFee() {
    transactionFeeDisplayAmountMaV.getModel().setCoinAmount(Coin.valueOf(feePerKBSlider.getValue() * RESOLUTION));
    transactionFeeDisplayAmountMaV.getModel().setLocalAmountVisible(false);
    transactionFeeDisplayAmountMaV.getView().updateView(configuration);
  }
}
