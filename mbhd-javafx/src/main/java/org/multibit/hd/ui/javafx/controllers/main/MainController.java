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
import org.multibit.hd.ui.javafx.i18n.BitcoinSymbol;
import org.multibit.hd.ui.javafx.i18n.Formats;
import org.multibit.hd.ui.javafx.views.Screen;
import org.multibit.hd.ui.javafx.views.StageManager;
import org.multibit.hd.ui.javafx.views.Stages;

import java.util.Locale;

public class MainController extends MultiBitController {

  @FXML
  public Label balanceLHSLabel;

  @FXML
  public Label balanceRHSLabel;

  @FXML
  public Label homeLabel;

  @FXML
  public Label contactsLabel;

  @FXML
  public Label settingsLabel;

  @FXML
  public Label helpLabel;

  @FXML
  public Label transactionsLabel;

  @FXML
  public AnchorPane detailAnchorPane;


  @Override
  public void initClickEvents() {

    homeLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {

      @Override
      public void handle(MouseEvent mouseEvent) {
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
          StageManager.MAIN_STAGE.changeScreen(Screen.MAIN_HOME);
        }
      }
    });

    contactsLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {

      @Override
      public void handle(MouseEvent mouseEvent) {
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
          StageManager.MAIN_STAGE.changeScreen(Screen.MAIN_CONTACTS);
        }
      }
    });

    settingsLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {

      @Override
      public void handle(MouseEvent mouseEvent) {
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
          StageManager.MAIN_STAGE.changeScreen(Screen.MAIN_SETTINGS);
        }
      }
    });

    helpLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {

      @Override
      public void handle(MouseEvent mouseEvent) {
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
          StageManager.MAIN_STAGE.changeScreen(Screen.MAIN_HELP);
        }
      }
    });

    helpLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {

      @Override
      public void handle(MouseEvent mouseEvent) {
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
          StageManager.MAIN_STAGE.changeScreen(Screen.MAIN_WALLET);
        }
      }
    });

  }

  @Override
  public void initAwesome() {

    // Title

    // Sidebar
    // TODO Convert this to a wallet event listener
    updateBalance("20999999.12345678");

    // Top icons
    AwesomeDecorator.applyIcon(homeLabel, AwesomeIcon.HOME, ContentDisplay.LEFT);
    AwesomeDecorator.applyIcon(contactsLabel, AwesomeIcon.USER, ContentDisplay.LEFT);
    AwesomeDecorator.applyIcon(settingsLabel, AwesomeIcon.GEAR, ContentDisplay.LEFT);
    AwesomeDecorator.applyIcon(helpLabel, AwesomeIcon.QUESTION_CIRCLE, ContentDisplay.LEFT);

    // Wallets
    AwesomeDecorator.applyIcon(transactionsLabel, AwesomeIcon.BITCOIN, ContentDisplay.LEFT);
  }

  private void updateBalance(String amount) {

    String amountFormatted = Formats.formatCurrency(amount);

    if (BitcoinSymbol.FONT_AWESOME_ICON.equals(Stages.getConfiguration().getBitcoinSymbol())) {

      Locale locale = Stages.getConfiguration().getLocale();

    }

    AwesomeDecorator.applyIcon(balanceLHSLabel, AwesomeIcon.BITCOIN, ContentDisplay.LEFT, "32px");
    balanceLHSLabel.setText("20,999,999.12");
    AwesomeDecorator.applyIcon(balanceRHSLabel, AwesomeIcon.BITCOIN, ContentDisplay.RIGHT, "32px");
    balanceRHSLabel.setText("345678");

  }

  public AnchorPane getDetailAnchorPane() {
    return detailAnchorPane;
  }
}
