package org.multibit.hd.ui.javafx.controllers.main;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.multibit.hd.ui.javafx.config.Configuration;
import org.multibit.hd.ui.javafx.config.Configurations;
import org.multibit.hd.ui.javafx.controllers.MultiBitController;
import org.multibit.hd.ui.javafx.fonts.AwesomeDecorator;
import org.multibit.hd.ui.javafx.fonts.AwesomeIcon;
import org.multibit.hd.ui.javafx.i18n.Languages;
import org.multibit.hd.ui.javafx.views.Screen;
import org.multibit.hd.ui.javafx.views.StageManager;
import org.multibit.hd.ui.javafx.views.Stages;

import java.util.Map;

public class SettingsController extends MultiBitController {

  private Configuration tempConfiguration;

  @FXML
  public CheckBox showCurrencyTickerCheckBox;

  @FXML
  public CheckBox showFiatConversionCheckBox;

  @FXML
  public CheckBox exchangeColumnCheckBox;

  @FXML
  public CheckBox currencyColumnCheckBox;

  @FXML
  public CheckBox lastColumnCheckBox;

  @FXML
  public CheckBox bidColumnCheckBox;

  @FXML
  public CheckBox askColumnCheckBox;

  @FXML
  public ChoiceBox exchange1ChoiceBox;

  @FXML
  public ChoiceBox currency1ChoiceBox;

  @FXML
  public ChoiceBox bitcoinSymbolChoiceBox;

  @FXML
  public CheckBox beforeAmountCheckBox;

  @FXML
  public CheckBox restoreLayoutCheckBox;

  @FXML
  public RadioButton languageRadio1;

  @FXML
  public RadioButton languageRadio2;

  @FXML
  public ChoiceBox languageChoiceBox;

  @FXML
  public ChoiceBox fontChoiceBox;

  @FXML
  public Button applySettingsButton;

  @FXML
  public Button undoSettingsButton;

  @FXML
  public TabPane settingsTabPane;

  @FXML
  public Tab exchangeRatesTab;

  @FXML
  public Tab bitcoinTab;

  @FXML
  public Button resetBlockChainButton;

  @FXML
  public Tab applicationTab;

  @FXML
  public Tab languageTab;

  @Override
  public void initClickEvents() {

    Map<String, Tab> tabMap = Maps.newHashMap();
    tabMap.put(bitcoinTab.getId(), bitcoinTab);
    tabMap.put(applicationTab.getId(), applicationTab);
    tabMap.put(languageTab.getId(), languageTab);
    tabMap.put(exchangeRatesTab.getId(), exchangeRatesTab);

    // Create a temporary configuration as a backing model
    tempConfiguration = Configurations.currentConfiguration.deepCopy();

    initExchangeRatesTab();
    initBitcoinTab();
    initApplicationTab();
    initLanguageTab();

    Optional<String> currentTab = Configurations
      .currentConfiguration
      .getApplicationConfiguration()
      .getCurrentTab();

    if (currentTab.isPresent()) {

      SingleSelectionModel<Tab> selectionModel = settingsTabPane.getSelectionModel();
      selectionModel.select(tabMap.get(currentTab.get()));

    }

  }

  private void initBitcoinTab() {

  }

  private void initExchangeRatesTab() {

  }

  private void initApplicationTab() {

  }

  private void initLanguageTab() {

    // Fill in the language names and standard codes
    languageChoiceBox.setItems(
      FXCollections.observableList(
        Languages.getLanguageNames(resourceBundle, true)));
    languageChoiceBox.getSelectionModel().select(
      Languages.getIndexFromLocale(resourceBundle.getLocale()));

    // Register a change listener for language transition (after setting the initial value)
    languageChoiceBox.getSelectionModel().selectedIndexProperty().addListener(
      new ChangeListener<Number>() {
        @Override
        public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {

          // Update the temp configuration with the new language (wait for Apply to allow Undo)
          tempConfiguration.getI18NConfiguration().setLocale(Languages.newLocaleFromIndex((Integer) newValue));

        }
      });

  }

  @Override
  public void initAwesome() {

    // Apply/undo buttons
    AwesomeDecorator.applyIcon(applySettingsButton, AwesomeIcon.CHECK, ContentDisplay.LEFT);
    AwesomeDecorator.applyIcon(undoSettingsButton, AwesomeIcon.UNDO, ContentDisplay.LEFT);

    AwesomeDecorator.applyIcon(exchangeRatesTab, AwesomeIcon.MOON_ALT);
    AwesomeDecorator.applyIcon(bitcoinTab, AwesomeIcon.BITCOIN);
    AwesomeDecorator.applyIcon(applicationTab, AwesomeIcon.DESKTOP);
    AwesomeDecorator.applyIcon(languageTab, AwesomeIcon.GLOBE);

  }

  public void onApplyFired(ActionEvent actionEvent) {

    // Track the current active tab
    SingleSelectionModel<Tab> selectionModel = settingsTabPane.getSelectionModel();
    tempConfiguration.getApplicationConfiguration().setCurrentTab(selectionModel.getSelectedItem().getId());

    // Keep track of the previous configuration
    Configurations.previousConfiguration = Configurations.currentConfiguration;
    Configurations.currentConfiguration = tempConfiguration;

    // Persist the configuration
    Configurations.writeCurrentConfiguration();

    // Update all the stages to the new locale
    Stages.build();

    // Show this screen with the new settings in place
    StageManager.MAIN_STAGE.changeScreen(Screen.MAIN_SETTINGS);

  }

  public void onUndoFired(ActionEvent actionEvent) {

    // Track the current active tab
    SingleSelectionModel<Tab> selectionModel = settingsTabPane.getSelectionModel();
    tempConfiguration.getApplicationConfiguration().setCurrentTab(selectionModel.getSelectedItem().getId());

    // Replace the previous configuration
    Configurations.currentConfiguration = Configurations.previousConfiguration;

    // Persist the configuration
    Configurations.writeCurrentConfiguration();

    // Update all the stages to the new locale
    Stages.build();

    // Show this screen with the new settings in place
    StageManager.MAIN_STAGE.changeScreen(Screen.MAIN_SETTINGS);

  }

}
