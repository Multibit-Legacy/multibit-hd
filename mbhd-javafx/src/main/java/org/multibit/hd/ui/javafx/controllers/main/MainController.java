package org.multibit.hd.ui.javafx.controllers.main;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.multibit.hd.ui.javafx.controllers.MultiBitController;
import org.multibit.hd.ui.javafx.fonts.AwesomeDecorator;
import org.multibit.hd.ui.javafx.fonts.AwesomeIcon;
import org.multibit.hd.ui.javafx.views.Screen;
import org.multibit.hd.ui.javafx.views.StageManager;

public class MainController extends MultiBitController  {

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

  @FXML
  public AnchorPane detailAnchorPane;

  @Override
  public void initClickEvents() {

    homeLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {

      @Override
      public void handle(MouseEvent mouseEvent) {
        if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
          StageManager.MAIN_STAGE.changeScreen(Screen.MAIN_HOME);
        }
      }
    });

    contactsLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {

      @Override
      public void handle(MouseEvent mouseEvent) {
        if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
          StageManager.MAIN_STAGE.changeScreen(Screen.MAIN_CONTACTS);
        }
      }
    });

  }

  @Override
  public void initAwesome() {

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
  }

  public AnchorPane getDetailAnchorPane() {
    return detailAnchorPane;
  }
}
