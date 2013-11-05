package org.multibit.hd.ui.javafx.controllers.main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.multibit.hd.ui.javafx.screens.Screen;
import org.multibit.hd.ui.javafx.screens.TransitionAware;
import org.multibit.hd.ui.javafx.screens.ScreenTransitionManager;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable, TransitionAware {

  @FXML
  private Label messageBar;

  ScreenTransitionManager transitionManager;

  @Override
  public void initialize(URL url, ResourceBundle rb) {
  }

  public void setScreenTransitionManager(ScreenTransitionManager screenTransitionManager) {
    transitionManager = screenTransitionManager;
  }

  public void newIssueFired(ActionEvent actionEvent) {
    messageBar.setText("New issue fired");
    transitionManager.transitionTo(Screen.WELCOME_LOGIN);
  }

  public void saveIssueFired(ActionEvent actionEvent) {
    messageBar.setText("Save issue fired");
  }

  public void deleteIssueFired(ActionEvent actionEvent) {
    messageBar.setText("Delete issue fired");
  }
}
