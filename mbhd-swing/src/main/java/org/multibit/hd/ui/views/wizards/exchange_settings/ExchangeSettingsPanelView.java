package org.multibit.hd.ui.views.wizards.exchange_settings;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import net.miginfocom.swing.MigLayout;
import org.joda.money.CurrencyUnit;
import org.multibit.hd.core.config.BitcoinConfiguration;
import org.multibit.hd.core.config.Configuration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.config.LanguageConfiguration;
import org.multibit.hd.core.exchanges.ExchangeKey;
import org.multibit.hd.ui.audio.Sounds;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.ComboBoxes;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.TextBoxes;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Locale;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Settings: Exchange rate provider display</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class ExchangeSettingsPanelView extends AbstractWizardPanelView<ExchangeSettingsWizardModel, ExchangeSettingsPanelModel> implements ActionListener {

  private JComboBox<String> exchangeRateProviderComboBox;
  private JLabel exchangeErrorStatus;

  private JComboBox<String> currencyCodeComboBox;

  private JLabel accessCodeLabel;
  private JTextField accessCode;
  private JLabel accessCodeErrorStatus;

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name
   */
  public ExchangeSettingsPanelView(AbstractWizard<ExchangeSettingsWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.SHOW_EXCHANGE_WIZARD, AwesomeIcon.DOLLAR);

  }

  @Override
  public void newPanelModel() {

    // Use a deep copy to avoid reference leaks
    Configuration configuration = Configurations.currentConfiguration.deepCopy();

    // Configure the panel model
    setPanelModel(new ExchangeSettingsPanelModel(
      getPanelName(),
      configuration
    ));

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(new MigLayout(
      Panels.migXYLayout(),
      "[][][]", // Column constraints
      "[][][]" // Row constraints
    ));

    LanguageConfiguration languageConfiguration = Configurations.currentConfiguration.getLanguageConfiguration().deepCopy();
    BitcoinConfiguration bitcoinConfiguration = Configurations.currentConfiguration.getBitcoinConfiguration().deepCopy();
    Locale locale = languageConfiguration.getLocale();

    Preconditions.checkNotNull(locale, "'locale' cannot be empty");

    exchangeRateProviderComboBox = ComboBoxes.newExchangeRateProviderComboBox(this, bitcoinConfiguration);

    // TODO Hook this into the exchange currencies
    List<String> currencies = Lists.newArrayList("(No currency)", "USD", "RUB", "GBP", "EUR");
    currencyCodeComboBox = ComboBoxes.newCurrencyCodeComboBox(this, currencies, bitcoinConfiguration);

    accessCode = TextBoxes.newEnterAccessCode();
    accessCode.setVisible(false);

    exchangeErrorStatus = Labels.newErrorStatus(false);
    exchangeErrorStatus.setVisible(false);

    // Access code is initially hidden
    accessCodeErrorStatus = Labels.newErrorStatus(false);
    accessCodeErrorStatus.setVisible(false);
    accessCodeLabel = Labels.newAccessCodeLabel();
    accessCodeLabel.setVisible(false);

    contentPanel.add(Labels.newExchangeSettingsNote(), "growx,push,span 3,wrap");

    contentPanel.add(Labels.newSelectExchangeRateProviderLabel(), "shrink");
    contentPanel.add(exchangeRateProviderComboBox, "growx,push,wrap");

    contentPanel.add(Labels.newLocalCurrencyLabel(), "shrink");
    contentPanel.add(currencyCodeComboBox, "growx,push");
    contentPanel.add(exchangeErrorStatus, "grow,push,wrap");

    contentPanel.add(accessCodeLabel, "shrink");
    contentPanel.add(accessCode, "growx,push");
    contentPanel.add(accessCodeErrorStatus, "grow,push,wrap");

  }

  @Override
  protected void initialiseButtons(AbstractWizard<ExchangeSettingsWizardModel> wizard) {

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

        exchangeRateProviderComboBox.requestFocusInWindow();

      }
    });

  }

  @Override
  public boolean beforeHide(boolean isExitCancel) {

    if (!isExitCancel) {

      // Switch the main configuration over to the new one
      Configurations.switchConfiguration(getWizardModel().getConfiguration());

    }

    // Must be OK to proceed
    return true;

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {


  }


  /**
   * <p>Handle one of the combo boxes changing</p>
   *
   * @param e The action event
   */
  @Override
  public void actionPerformed(ActionEvent e) {

    if (ComboBoxes.EXCHANGE_RATE_PROVIDER_COMMAND.equals(e.getActionCommand())) {
      handleExchangeRateProviderSelection(e);
    }
    if (ComboBoxes.CURRENCY_COMMAND.equals(e.getActionCommand())) {
      handleCurrencySelection(e);
    }

  }

  /**
   * <p>The exchange rate provider selection has changed</p>
   *
   * @param e The action event
   */
  private void handleExchangeRateProviderSelection(ActionEvent e) {

    JComboBox source = (JComboBox) e.getSource();
    int exchangeIndex = source.getSelectedIndex();

    // Test for Open Exchange Rates
    if (exchangeIndex == 2) {
      accessCodeLabel.setVisible(true);
      accessCode.setVisible(true);
    } else {
      accessCodeLabel.setVisible(false);
      accessCode.setVisible(false);
    }

    // Exchanges are presented in the same order as they are declared in the enum
    ExchangeKey exchangeKey = ExchangeKey.values()[exchangeIndex];

    // Update the model (even if in error)
    getWizardModel().getConfiguration().getBitcoinConfiguration().setExchangeKey(exchangeKey.name());

  }

  /**
   * <p>The currency selection has changed</p>
   *
   * @param e The action event
   */
  private void handleCurrencySelection(ActionEvent e) {

    JComboBox source = (JComboBox) e.getSource();
    String currencyCode = String.valueOf(source.getSelectedItem());

    // TODO Validate the currency with a ticker test
    boolean isTickerValid = "USD".equals(currencyCode);

    if (!isTickerValid) {
      Sounds.playBeep();
      exchangeErrorStatus.setVisible(true);
      ViewEvents.fireWizardButtonEnabledEvent(
        getPanelName(),
        WizardButton.APPLY,
        false
      );
    } else {
      exchangeErrorStatus.setVisible(false);
      ViewEvents.fireWizardButtonEnabledEvent(
        getPanelName(),
        WizardButton.APPLY,
        true
      );
    }


    CurrencyUnit currencyUnit = CurrencyUnit.getInstance(currencyCode);

    // Update the model (even if in error)
    getWizardModel().getConfiguration().getBitcoinConfiguration().setLocalCurrencySymbol(currencyCode);
    getWizardModel().getConfiguration().getBitcoinConfiguration().setLocalCurrencyUnit(currencyUnit);

  }

}
