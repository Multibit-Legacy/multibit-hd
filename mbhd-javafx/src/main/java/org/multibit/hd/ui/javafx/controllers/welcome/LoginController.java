package org.multibit.hd.ui.javafx.controllers.welcome;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContentDisplay;
import org.multibit.hd.ui.javafx.fonts.AwesomeDecorator;
import org.multibit.hd.ui.javafx.fonts.AwesomeIcon;
import org.multibit.hd.ui.javafx.i18n.Languages;
import org.multibit.hd.ui.javafx.screens.Screen;
import org.multibit.hd.ui.javafx.screens.ScreenTransitionManager;
import org.multibit.hd.ui.javafx.screens.TransitionAware;

import java.net.URL;
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
public class LoginController implements Initializable, TransitionAware {

  @FXML
  private Button loginButton;

  @FXML
  private ChoiceBox<String> languageChoice;

  private ScreenTransitionManager transitionManager;

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {

    AwesomeDecorator.applyIcon(loginButton, AwesomeIcon.ARROW_RIGHT, ContentDisplay.RIGHT);

    // Fill in the language names and standard codes
    languageChoice.setItems(FXCollections.observableList(Languages.getLanguageNames(resourceBundle, true)));
    languageChoice.getSelectionModel().select(Languages.getIndexFromLocale(resourceBundle.getLocale()));

    // Register a change listener for language transition (after setting the initial value)
    languageChoice.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
      @Override
      public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
        transitionManager.onLocaleChanged(Languages.newLocaleFromIndex((Integer) newValue));
      }
    });

  }

  public void setScreenTransitionManager(ScreenTransitionManager screenTransitionManager) {
    transitionManager = screenTransitionManager;
  }


  public void onLoginFired(ActionEvent actionEvent) {
    transitionManager.transitionTo(Screen.MAIN_HOME);
  }

  public void onForgottenClicked(ActionEvent actionEvent) {
    transitionManager.transitionTo(Screen.WELCOME_PROVIDE_INITIAL_SEED);
  }

}
