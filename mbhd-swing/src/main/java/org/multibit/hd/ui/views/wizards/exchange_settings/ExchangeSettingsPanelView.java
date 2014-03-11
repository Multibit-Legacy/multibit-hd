package org.multibit.hd.ui.views.wizards.exchange_settings;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.xeiam.xchange.ExchangeSpecification;
import com.xeiam.xchange.currency.Currencies;
import com.xeiam.xchange.dto.marketdata.Ticker;
import net.miginfocom.swing.MigLayout;
import org.joda.money.CurrencyUnit;
import org.multibit.hd.core.config.BitcoinConfiguration;
import org.multibit.hd.core.config.Configuration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.config.LanguageConfiguration;
import org.multibit.hd.core.exceptions.ExceptionHandler;
import org.multibit.hd.core.exchanges.ExchangeKey;
import org.multibit.hd.ui.audio.Sounds;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
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
  private JButton exchangeRateProviderBrowserButton;

  private JLabel exchangeErrorStatus;
  private JComboBox<String> currencyCodeComboBox;

  private JLabel apiKeyLabel;
  private JTextField apiKey;
  private JLabel apiKeyErrorStatus;
  private DocumentListener apiKeyDocumentListener;

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
    ExchangeKey exchangeKey = ExchangeKey.valueOf(bitcoinConfiguration.getExchangeKey());

    Preconditions.checkNotNull(locale, "'locale' cannot be empty");

    exchangeRateProviderBrowserButton = Buttons.newLaunchBrowserButton(getExchangeRateProviderBrowserAction());

    exchangeRateProviderComboBox = ComboBoxes.newExchangeRateProviderComboBox(this, bitcoinConfiguration);
    currencyCodeComboBox = ComboBoxes.newCurrencyCodeComboBox(this, bitcoinConfiguration);

    exchangeErrorStatus = Labels.newErrorStatus(false);
    exchangeErrorStatus.setVisible(false);

    // API key
    apiKey = TextBoxes.newEnterApiKey(getApiKeyDocumentListener());
    apiKeyErrorStatus = Labels.newErrorStatus(false);
    apiKeyLabel = Labels.newApiKeyLabel();

    // API key visibility
    boolean isOERExchange = ExchangeKey.OPEN_EXCHANGE_RATES.equals(exchangeKey);
    apiKey.setVisible(isOERExchange);
    apiKeyErrorStatus.setVisible(isOERExchange);
    apiKeyLabel.setVisible(isOERExchange);

    // API key value
    if (bitcoinConfiguration.getExchangeApiKeys().isPresent()) {
      apiKey.setText(bitcoinConfiguration.getExchangeApiKeys().get());
    }

    // Hide the currency code combo if there is no API key for OER
    if (isOERExchange && Strings.isNullOrEmpty(apiKey.getText())) {
      currencyCodeComboBox.setVisible(false);
    }

    contentPanel.add(Labels.newExchangeSettingsNote(), "growx,push,span 3,wrap");

    contentPanel.add(Labels.newSelectExchangeRateProviderLabel(), "shrink");
    contentPanel.add(exchangeRateProviderComboBox, "growx,push");
    contentPanel.add(exchangeRateProviderBrowserButton, "shrink,wrap");

    contentPanel.add(apiKeyLabel, "shrink");
    contentPanel.add(apiKey, "growx,push");
    contentPanel.add(apiKeyErrorStatus, "grow,push,wrap");

    contentPanel.add(Labels.newLocalCurrencyLabel(), "shrink");
    contentPanel.add(currencyCodeComboBox, "growx,push");
    contentPanel.add(exchangeErrorStatus, "grow,push,wrap");

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

      // If the user has selected OER then update the API key
      if (apiKey.isVisible() && !Strings.isNullOrEmpty(apiKey.getText())) {
        getWizardModel().getConfiguration().getBitcoinConfiguration().setExchangeApiKeys(apiKey.getText());
      }

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
   * @return The "exchange rate provider browser" action
   */
  private Action getExchangeRateProviderBrowserAction() {

    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        int selectedIndex = exchangeRateProviderComboBox.getSelectedIndex();
        if (selectedIndex == -1) {
          return;
        }

        ExchangeKey exchangeKey = ExchangeKey.values()[selectedIndex];

        try {
          URI exchangeUri = URI.create("http://" + exchangeKey.getExchange().getExchangeSpecification().getHost());
          Desktop.getDesktop().browse(exchangeUri);
        } catch (IOException ex) {
          ExceptionHandler.handleThrowable(ex);
        }

      }
    };
  }

  /**
   * <p>The exchange rate provider selection has changed</p>
   *
   * @param e The action event
   */
  private void handleExchangeRateProviderSelection(ActionEvent e) {

    JComboBox source = (JComboBox) e.getSource();
    int exchangeIndex = source.getSelectedIndex();

    // Exchanges are presented in the same order as they are declared in the enum
    ExchangeKey exchangeKey = ExchangeKey.values()[exchangeIndex];

    // Test for Open Exchange Rates
    if (ExchangeKey.OPEN_EXCHANGE_RATES.equals(exchangeKey)) {
      apiKeyLabel.setVisible(true);
      apiKey.setVisible(true);

      // Hide the currency combo if there is no API key
      currencyCodeComboBox.setVisible(!Strings.isNullOrEmpty(apiKey.getText()));
    } else {
      apiKeyLabel.setVisible(false);
      apiKey.setVisible(false);

      // Show the currency combo
      currencyCodeComboBox.setVisible(true);
    }

    exchangeErrorStatus.setVisible(false);

    // Update the model (even if in error)
    getWizardModel().getConfiguration().getBitcoinConfiguration().setExchangeKey(exchangeKey.name());

    // Reset the available currencies
    String[] allCurrencies = exchangeKey.allCurrencies();
    currencyCodeComboBox.setModel(new DefaultComboBoxModel<>(allCurrencies));
    currencyCodeComboBox.setSelectedIndex(-1);

    // Prevent application until the currency is selected (to allow ticker check)
    ViewEvents.fireWizardButtonEnabledEvent(
      getPanelName(),
      WizardButton.APPLY,
      false
    );

  }

  /**
   * <p>The currency selection has changed</p>
   *
   * @param e The action event
   */
  private void handleCurrencySelection(ActionEvent e) {

    JComboBox source = (JComboBox) e.getSource();

    // Ignore cascading events from an exchange selection
    if (source.getSelectedIndex() == -1) {
      return;
    }

    String isoCounterCode = String.valueOf(source.getSelectedItem()).substring(0, 3);

    handleTestTicker(isoCounterCode);

  }

  /**
   * <p>Handles the process of testing the currency selection against the exchange</p>
   *
   * @param isoCounterCode The ISO counter code (e.g. "USD", "RUB" etc)
   */
  private void handleTestTicker(String isoCounterCode) {

    // Get the current exchange key
    ExchangeKey exchangeKey = ExchangeKey.valueOf(getWizardModel().getConfiguration().getBitcoinConfiguration().getExchangeKey());

    // Use the wizard model to provide the exchange specification details
    BitcoinConfiguration bitcoinConfiguration = getWizardModel().getConfiguration().getBitcoinConfiguration();
    ExchangeSpecification currentExchangeSpecification = exchangeKey.getExchange().getExchangeSpecification();

    ExchangeSpecification testExchangeSpecification = new ExchangeSpecification(currentExchangeSpecification.getExchangeClassName());
    testExchangeSpecification.setApiKey(bitcoinConfiguration.getExchangeApiKeys().orNull());

    // TODO Take this off the AWT thread
    Optional<Ticker> tickerOptional = ExchangeKey.latestTicker(Currencies.BTC, exchangeKey, Optional.of(testExchangeSpecification));

    if (!tickerOptional.isPresent()) {
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

    CurrencyUnit currencyUnit = CurrencyUnit.getInstance(isoCounterCode);

    // Update the model (even if in error)
    getWizardModel().getConfiguration().getBitcoinConfiguration().setLocalCurrencySymbol(isoCounterCode);
    getWizardModel().getConfiguration().getBitcoinConfiguration().setLocalCurrencyUnit(currencyUnit);
  }

  /**
   * @return A document listener for interacting with the API key field
   */
  public DocumentListener getApiKeyDocumentListener() {
    return new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
        verify();
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        verify();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        verify();
      }

      public void verify() {
        boolean failed = apiKey.getText().length() != 32;

        // Handle error status
        apiKeyErrorStatus.setVisible(failed);

        // Handle currency combo
        currencyCodeComboBox.setVisible(!failed);

        if (!failed) {

          // Update the wizard model so that the ticker can be tested
          getWizardModel().getConfiguration().getBitcoinConfiguration().setExchangeApiKeys(apiKey.getText());

        }

        // Handle currency code (always deselected after someone fiddles with the API key)
        currencyCodeComboBox.setSelectedIndex(-1);

        // Handle apply
        ViewEvents.fireWizardButtonEnabledEvent(
          getPanelName(),
          WizardButton.APPLY,
          false
        );

      }
    };
  }
}
