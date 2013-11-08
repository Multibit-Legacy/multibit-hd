package org.multibit.hd.ui.javafx.controllers.main;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import org.multibit.hd.ui.javafx.fonts.AwesomeDecorator;
import org.multibit.hd.ui.javafx.fonts.AwesomeIcon;
import org.multibit.hd.ui.javafx.screens.ScreenTransitionManager;
import org.multibit.hd.ui.javafx.screens.TransitionAware;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable, TransitionAware {

  @FXML
  public Label contactsLabel  ;

  @FXML
  public Label currentWalletLabel;

  @FXML
  public Label balanceLabel;

  @FXML
  public Label homeLabel;

  @FXML
  public Label helpLabel;

  ScreenTransitionManager transitionManager;

  @Override
  public void initialize(URL url, ResourceBundle rb) {

    // Title

    // Sidebar
    AwesomeDecorator.applyIcon(balanceLabel, AwesomeIcon.BITCOIN, ContentDisplay.LEFT, "32px");
    balanceLabel.setText("123,456.12345678");

    // Top icons
    AwesomeDecorator.applyIcon(homeLabel, AwesomeIcon.HOME, ContentDisplay.LEFT);
    AwesomeDecorator.applyIcon(contactsLabel, AwesomeIcon.USER, ContentDisplay.LEFT);
    AwesomeDecorator.applyIcon(helpLabel, AwesomeIcon.QUESTION_CIRCLE, ContentDisplay.LEFT);

    // Wallets
    AwesomeDecorator.applyIcon(currentWalletLabel, AwesomeIcon.MONEY, ContentDisplay.LEFT);

    // TODO Dynamically add a hardware wallet

  }

  public void setScreenTransitionManager(ScreenTransitionManager screenTransitionManager) {
    transitionManager = screenTransitionManager;
  }
}
