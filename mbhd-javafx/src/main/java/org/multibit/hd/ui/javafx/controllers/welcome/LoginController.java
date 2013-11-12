package org.multibit.hd.ui.javafx.controllers.welcome;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContentDisplay;
import org.multibit.hd.ui.javafx.controllers.MultiBitController;
import org.multibit.hd.ui.javafx.fonts.AwesomeDecorator;
import org.multibit.hd.ui.javafx.fonts.AwesomeIcon;
import org.multibit.hd.ui.javafx.i18n.Languages;
import org.multibit.hd.ui.javafx.views.Screen;
import org.multibit.hd.ui.javafx.views.StageManager;
import org.multibit.hd.ui.javafx.views.Stages;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * <p>Controller to provide the following to UI:</p>
 * <ul>
 * <li>Handles events from the login view</li>
 * <li>Decorates controls with iconography</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class LoginController extends MultiBitController {

  @FXML
  private Button loginButton;

  @FXML
  private ChoiceBox<String> languageChoice;

  @Override
  public void initAwesome() {

    AwesomeDecorator.applyIcon(loginButton, AwesomeIcon.ARROW_RIGHT, ContentDisplay.RIGHT);

  }

  @Override
  public void initClickEvents() {

    // Fill in the language names and standard codes
    languageChoice.setItems(
      FXCollections.observableList(
        Languages.getLanguageNames(resourceBundle, true)));
    languageChoice.getSelectionModel().select(
      Languages.getIndexFromLocale(resourceBundle.getLocale()));

    // Register a change listener for language transition (after setting the initial value)
    languageChoice.getSelectionModel().selectedIndexProperty().addListener(
      new ChangeListener<Number>() {
      @Override
      public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {

        Locale locale = Languages.newLocaleFromIndex((Integer) newValue);
        ResourceBundle resourceBundle = Languages.newResourceBundle(locale);

        // Update all the stages to the new locale
        Stages.buildWelcomeStage(locale, resourceBundle);
        Stages.buildMainStage(locale, resourceBundle);

      }
    });

  }

  public void onLoginFired(ActionEvent actionEvent) {
    StageManager.WELCOME_STAGE.handOver(StageManager.MAIN_STAGE, Screen.MAIN_HOME);
  }

  public void onForgottenClicked(ActionEvent actionEvent) {
    StageManager.WELCOME_STAGE.changeScreen(Screen.WELCOME_PROVIDE_INITIAL_SEED);
  }

}
