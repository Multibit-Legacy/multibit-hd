package org.multibit.hd.ui.javafx.controllers.main;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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
  public Button cancelSettingsButton;

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

    initExchangeRatesTab();
    initBitcoinTab();
    initApplicationTab();
    initLanguagesTab();

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

  private void initLanguagesTab() {

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

          Configurations.currentConfiguration.getI18NConfiguration().setLocale(Languages.newLocaleFromIndex((Integer) newValue));
          Configurations.currentConfiguration.getApplicationConfiguration().setCurrentTab(languageTab.getId());

          // Update all the stages to the new locale
          Stages.build();


          StageManager.MAIN_STAGE.changeScreen(Screen.MAIN_SETTINGS);

        }
      });

  }

  @Override
  public void initAwesome() {

    // Apply buttons
    AwesomeDecorator.applyIcon(cancelSettingsButton, AwesomeIcon.TIMES, ContentDisplay.LEFT);
    AwesomeDecorator.applyIcon(applySettingsButton, AwesomeIcon.CHECK, ContentDisplay.LEFT);
    AwesomeDecorator.applyIcon(undoSettingsButton, AwesomeIcon.UNDO, ContentDisplay.LEFT);

  }

}
