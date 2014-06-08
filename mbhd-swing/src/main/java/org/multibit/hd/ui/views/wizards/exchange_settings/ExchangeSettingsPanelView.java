package org.multibit.hd.ui.views.wizards.exchange_settings;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.xeiam.xchange.dto.marketdata.Ticker;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.config.BitcoinConfiguration;
import org.multibit.hd.core.config.Configuration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.config.LanguageConfiguration;
import org.multibit.hd.core.dto.CoreMessageKey;
import org.multibit.hd.core.exceptions.ExceptionHandler;
import org.multibit.hd.core.exchanges.ExchangeKey;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.core.services.ExchangeTickerService;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.audio.Sounds;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.Themes;
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
import java.util.Map;

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

  private JLabel apiKeyLabel;
  private JTextField apiKeyTextField;

  private JLabel currencyCodeLabel;
  private JComboBox<String> currencyCodeComboBox;

  private JLabel tickerVerifiedStatus;
  private JLabel tickerSpinner;

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name
   */
  public ExchangeSettingsPanelView(AbstractWizard<ExchangeSettingsWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.EXCHANGE_SETTINGS_TITLE, AwesomeIcon.DOLLAR);

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

    LanguageConfiguration languageConfiguration = Configurations.currentConfiguration.getLanguage().deepCopy();
    BitcoinConfiguration bitcoinConfiguration = Configurations.currentConfiguration.getBitcoin().deepCopy();
    Locale locale = languageConfiguration.getLocale();
    ExchangeKey exchangeKey = ExchangeKey.valueOf(bitcoinConfiguration.getCurrentExchange());

    Preconditions.checkNotNull(locale, "'locale' cannot be empty");

    exchangeRateProviderBrowserButton = Buttons.newLaunchBrowserButton(getExchangeRateProviderBrowserAction());
    exchangeRateProviderBrowserButton.setEnabled(false);

    exchangeRateProviderComboBox = ComboBoxes.newExchangeRateProviderComboBox(this, bitcoinConfiguration);
    currencyCodeComboBox = ComboBoxes.newCurrencyCodeComboBox(this);

    // API key
    apiKeyTextField = TextBoxes.newEnterApiKey(getApiKeyDocumentListener());
    apiKeyLabel = Labels.newApiKey();

    // API key visibility
    boolean isOERExchange = ExchangeKey.OPEN_EXCHANGE_RATES.equals(exchangeKey);

    // API key value
    Map<String, String> exchangeApiKeys = bitcoinConfiguration.getExchangeApiKeys();
    if (exchangeApiKeys.containsKey(exchangeKey.name())) {
      apiKeyTextField.setText(exchangeApiKeys.get(exchangeKey.name()));
    }

    // Ticker verification status
    tickerVerifiedStatus = Labels.newVerificationStatus(true);
    tickerVerifiedStatus.setVisible(false);

    // Ticker spinner
    tickerSpinner = Labels.newSpinner(Themes.currentTheme.fadedText(), MultiBitUI.NORMAL_PLUS_ICON_SIZE);
    tickerSpinner.setVisible(false);

    // Local currency
    currencyCodeLabel = Labels.newLocalCurrency();

    // All components are initialised
    setHasComponents(true);

    ///////////////////////////// Components ready ////////////////////////////////

    // Show the API key if OER
    setApiKeyVisibility(isOERExchange);

    // Show the currency code by default
    setCurrencyCodeVisibility(true);

    // Hide the currency code if OER and no API key is filled in
    if (isOERExchange && Strings.isNullOrEmpty(apiKeyTextField.getText())) {
      // No API key so hide currency code
      setCurrencyCodeVisibility(false);
    }

    contentPanel.add(Labels.newExchangeSettingsNote(), "growx,push,span 3,wrap");

    contentPanel.add(Labels.newSelectExchangeRateProvider(), "shrink");
    contentPanel.add(exchangeRateProviderComboBox, "growx,push");
    contentPanel.add(exchangeRateProviderBrowserButton, "shrink,wrap");

    contentPanel.add(apiKeyLabel, "shrink");
    contentPanel.add(apiKeyTextField, "growx,push,wrap");

    contentPanel.add(currencyCodeLabel, "shrink");
    contentPanel.add(currencyCodeComboBox, "growx,push");
    contentPanel.add(tickerSpinner, "grow," + MultiBitUI.NORMAL_PLUS_ICON_SIZE_MIG + ",wrap");
    contentPanel.add(tickerVerifiedStatus, "grow,cell 1 4,push,wrap");

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

    // Required to finish the addition of a listener

    final BitcoinConfiguration bitcoinConfiguration = Configurations.currentConfiguration.getBitcoin().deepCopy();

    final ExchangeKey exchangeKey = ExchangeKey.valueOf(bitcoinConfiguration.getCurrentExchange());

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {

        if (!ExchangeKey.NONE.equals(exchangeKey)) {

          exchangeRateProviderComboBox.requestFocusInWindow();

          // Exchange is not NONE so we can browse
          exchangeRateProviderBrowserButton.setEnabled(true);

          // Get all the currencies available at the exchange
          ExchangeTickerService exchangeTickerService = CoreServices.newExchangeService(bitcoinConfiguration);

          ListenableFuture<String[]> futureAllCurrencies = exchangeTickerService.allCurrencies();
          Futures.addCallback(futureAllCurrencies, new FutureCallback<String[]>() {

            @Override
            public void onSuccess(final String[] allCurrencies) {

              SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {

                  DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(allCurrencies);
                  currencyCodeComboBox.setModel(model);
                  currencyCodeComboBox.setMaximumRowCount(MultiBitUI.COMBOBOX_MAX_ROW_COUNT);

                  ComboBoxes.selectFirstMatch(currencyCodeComboBox, allCurrencies, bitcoinConfiguration.getLocalCurrencyCode());

                }
              });

            }

            @Override
            public void onFailure(Throwable t) {

              handleFailure(t);
            }
          });

        } else {

          setCurrencyCodeVisibility(false);
          setApiKeyVisibility(false);

          // Exchange is NONE so no browsing
          exchangeRateProviderBrowserButton.setEnabled(false);

        }
      }
    });

  }

  @Override
  public boolean beforeHide(boolean isExitCancel, ModelAndView... mavs) {

    // Always call super() before hiding
    super.beforeHide(isExitCancel);

    if (!isExitCancel) {

      // If the user has selected OER then update the API key
      if (apiKeyTextField.isVisible() && !Strings.isNullOrEmpty(apiKeyTextField.getText())) {

        BitcoinConfiguration bitcoinConfiguration = getWizardModel().getConfiguration().getBitcoin();
        bitcoinConfiguration.getExchangeApiKeys().put(ExchangeKey.OPEN_EXCHANGE_RATES.name(), apiKeyTextField.getText());

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

        // Break out early
        if (ExchangeKey.NONE.equals(exchangeKey)) {
          return;
        }

        try {
          final URI exchangeUri;
          if (ExchangeKey.OPEN_EXCHANGE_RATES.equals(exchangeKey)) {
            // Ensure MultiBit customers go straight to the free API key page with referral
            exchangeUri = URI.create("https://openexchangerates.org/signup/free?r=multibit");
            Desktop.getDesktop().browse(exchangeUri);
          } else {
            // All other exchanges go to the main host (not API root)
            exchangeUri = URI.create("http://" + exchangeKey.getExchange().get().getExchangeSpecification().getHost());
            Desktop.getDesktop().browse(exchangeUri);
          }
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
    final ExchangeKey exchangeKey = ExchangeKey.values()[exchangeIndex];

    // Test for None
    if (ExchangeKey.NONE.equals(exchangeKey)) {

      // Hide everything

      // Prevent exchange browsing
      exchangeRateProviderBrowserButton.setEnabled(false);

      setApiKeyVisibility(false);
      setCurrencyCodeVisibility(false);

      // Hide the ticker verification
      tickerVerifiedStatus.setVisible(false);

      // Hide the spinner since its related component is not visible
      tickerSpinner.setVisible(false);

    } else if (ExchangeKey.OPEN_EXCHANGE_RATES.equals(exchangeKey)) {

      // Allow exchange browsing
      exchangeRateProviderBrowserButton.setEnabled(true);

      // Show the API key
      setApiKeyVisibility(true);

      // Hide the currency code if the API key is not present
      setCurrencyCodeVisibility(!Strings.isNullOrEmpty(apiKeyTextField.getText()));

      // Hide the ticker verification
      tickerVerifiedStatus.setVisible(false);

      // Show the spinner so the user knows something is happening
      tickerSpinner.setVisible(true);

    } else {

      // Allow exchange browsing
      exchangeRateProviderBrowserButton.setEnabled(true);

      // Hide the API key
      setApiKeyVisibility(false);

      // Show the currency code
      setCurrencyCodeVisibility(true);

      // Disable until populated
      currencyCodeComboBox.setEnabled(false);

      // Hide the ticker verification
      tickerVerifiedStatus.setVisible(false);

      // Show the spinner so the user knows something is happening
      tickerSpinner.setVisible(true);
    }

    // Update the model
    getWizardModel().getConfiguration().getBitcoin().setCurrentExchange(exchangeKey.name());

    // Reset the available currencies
    ExchangeTickerService exchangeTickerService = CoreServices.newExchangeService(getWizardModel().getConfiguration().getBitcoin());
    ListenableFuture<String[]> futureAllCurrencies = exchangeTickerService.allCurrencies();
    Futures.addCallback(futureAllCurrencies, new FutureCallback<String[]>() {
      @Override
      public void onSuccess(final String[] allCurrencies) {

        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {

            if (ExchangeKey.NONE.equals(exchangeKey)) {

              // Permit application since there are no currencies to select
              ViewEvents.fireWizardButtonEnabledEvent(
                getPanelName(),
                WizardButton.APPLY,
                true
              );

            } else {
              currencyCodeComboBox.setEnabled(true);
              currencyCodeComboBox.setModel(new DefaultComboBoxModel<>(allCurrencies));
              currencyCodeComboBox.setSelectedIndex(-1);

              // Prevent application until the currency is selected (to allow ticker check)
              ViewEvents.fireWizardButtonEnabledEvent(
                getPanelName(),
                WizardButton.APPLY,
                false
              );

            }

            // Hide the spinner
            tickerSpinner.setVisible(false);

          }
        });


      }

      @Override
      public void onFailure(Throwable t) {

        // Clear the currency combo
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        currencyCodeComboBox.setModel(model);

        handleFailure(t);

      }
    });


  }

  /**
   * <p>The currency selection has changed</p>
   *
   * @param e The action event
   */
  private void handleCurrencySelection(ActionEvent e) {

    // Always disable the Apply after a currency change
    ViewEvents.fireWizardButtonEnabledEvent(
      getPanelName(),
      WizardButton.APPLY,
      false
    );

    JComboBox source = (JComboBox) e.getSource();

    // Ignore cascading events from an exchange selection
    if (source.getSelectedIndex() == -1) {
      return;
    }

    String isoCounterCode = String.valueOf(source.getSelectedItem()).substring(0, 3);

    // Update the model so that both symbol and code are the same
    getWizardModel().getConfiguration().getBitcoin().setLocalCurrencySymbol(isoCounterCode);
    getWizardModel().getConfiguration().getBitcoin().setLocalCurrencyCode(isoCounterCode);

    // Test the new settings
    handleTestTicker();

  }

  /**
   * <p>Handles the process of testing the currency selection against the exchange</p>
   */
  private void handleTestTicker() {

    // Hide the ticker verification
    tickerVerifiedStatus.setVisible(false);

    tickerSpinner.setVisible(true);

    BitcoinConfiguration bitcoinConfiguration = getWizardModel().getConfiguration().getBitcoin();

    // Build a custom exchange ticker service from the wizard model
    ExchangeTickerService exchangeTickerService = CoreServices.newExchangeService(bitcoinConfiguration);
    ListenableFuture<Ticker> futureTicker = exchangeTickerService.latestTicker();

    // Avoid freezing the UI
    Futures.addCallback(futureTicker, new FutureCallback<Ticker>() {
      @Override
      public void onSuccess(final Ticker ticker) {

        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {

            // Network or exchange might be down
            if (ticker == null) {
              // Stop the spinner but do not allow the Apply
              tickerSpinner.setVisible(false);
              return;
            }

            // Show the ticker verification
            tickerVerifiedStatus.setText(Languages.safeText(MessageKey.VERIFICATION_STATUS));
            AwesomeDecorator.bindIcon(
              AwesomeIcon.CHECK,
              tickerVerifiedStatus,
              true,
              MultiBitUI.NORMAL_ICON_SIZE
            );
            tickerVerifiedStatus.setVisible(true);

            ViewEvents.fireWizardButtonEnabledEvent(
              getPanelName(),
              WizardButton.APPLY,
              true
            );

            tickerSpinner.setVisible(false);

          }
        });


      }

      @Override
      public void onFailure(Throwable t) {

        handleFailure(t);

      }
    });

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

        if (!isHasComponents()) {
          return;
        }

        // Some exchanges will offer a variety of API key lengths so cannot verify beyond a presence
        boolean notEmpty = apiKeyTextField.getText().length() > 0;

        // Update the wizard model so that the ticker can be tested if a currency combo selection is made
        getWizardModel().getConfiguration().getBitcoin().getExchangeApiKeys().put(ExchangeKey.OPEN_EXCHANGE_RATES.name(), apiKeyTextField.getText());

        // Handle currency combo
        setCurrencyCodeVisibility(notEmpty);

        // Handle currency code (always deselected after someone fiddles with the API key)
        currencyCodeComboBox.setSelectedIndex(-1);

        // Hide the ticker verification
        tickerVerifiedStatus.setVisible(false);

        // Apply is always disabled when the currency code combo is not selected
        ViewEvents.fireWizardButtonEnabledEvent(
          getPanelName(),
          WizardButton.APPLY,
          false
        );

      }
    };
  }

  /**
   * @param t The throwable that caused the failure
   */
  private void handleFailure(final Throwable t) {

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {

        Sounds.playBeep();

        tickerVerifiedStatus.setText(Languages.safeText(CoreMessageKey.THE_ERROR_WAS, t.getMessage()));
        AwesomeDecorator.bindIcon(
          AwesomeIcon.TIMES,
          tickerVerifiedStatus,
          true,
          MultiBitUI.NORMAL_ICON_SIZE
        );
        tickerVerifiedStatus.setVisible(true);

        ViewEvents.fireWizardButtonEnabledEvent(
          getPanelName(),
          WizardButton.APPLY,
          false
        );

        tickerSpinner.setVisible(false);

      }

    });

  }

  /**
   * @param visible True to make the label and text field visible
   */
  private void setApiKeyVisibility(boolean visible) {

    if (!isHasComponents()) {
      return;
    }
    if (apiKeyLabel == null || apiKeyTextField == null) {
      return;
    }
    apiKeyLabel.setVisible(visible);
    apiKeyTextField.setVisible(visible);

    if (visible) {
      // Ensure we use the current API key setting (currently only OER supported)
      BitcoinConfiguration bitcoinConfiguration = getWizardModel().getConfiguration().getBitcoin();
      apiKeyTextField.setText(bitcoinConfiguration.getExchangeApiKeys().get(ExchangeKey.OPEN_EXCHANGE_RATES.name()));
    }
  }

  /**
   * @param visible True to make the label and combo box visible
   */
  private void setCurrencyCodeVisibility(boolean visible) {

    if (!isHasComponents()) {
      return;
    }
    currencyCodeLabel.setVisible(visible);
    currencyCodeComboBox.setVisible(visible);

  }


}
