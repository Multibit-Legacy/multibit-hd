package org.multibit.hd.ui.javafx.controllers.welcome;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import org.multibit.hd.ui.javafx.screens.TransitionAware;
import org.multibit.hd.ui.javafx.screens.Screen;
import org.multibit.hd.ui.javafx.screens.ScreenTransitionManager;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * <p>Controller to provide the following to UI:</p>
 * <ul>
 * <li>Handles events from the login view</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class ProvideInitialSeedController implements Initializable, TransitionAware {

  ScreenTransitionManager transitionManager;

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    // TODO
  }

  public void setScreenTransitionManager(ScreenTransitionManager screenTransitionManager) {
    this.transitionManager = screenTransitionManager;
  }


  public void onRecoverFired(ActionEvent actionEvent) {
    transitionManager.transitionTo(Screen.MAIN_HOME);
  }
}
