package org.multibit.hd.ui.javafx.controllers.main;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.multibit.hd.ui.javafx.config.Configuration;
import org.multibit.hd.ui.javafx.controllers.MultiBitController;
import org.multibit.hd.ui.javafx.fonts.AwesomeDecorator;
import org.multibit.hd.ui.javafx.fonts.AwesomeIcon;
import org.multibit.hd.ui.javafx.i18n.BitcoinSymbol;
import org.multibit.hd.ui.javafx.i18n.Formats;
import org.multibit.hd.ui.javafx.views.Screen;
import org.multibit.hd.ui.javafx.views.StageManager;
import org.multibit.hd.ui.javafx.views.Stages;

import java.math.BigDecimal;
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
  public AnchorPane detailAnchorPane;

  @FXML
  public Label walletLabel;


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

          Configuration configuration = Stages.getConfiguration();
          if (configuration.getLocale().equals(Locale.UK)) {
            configuration.setLocale(new Locale("RU"));
          } else {
            configuration.setLocale(Locale.UK);
          }
          // Update all the stages to the new locale
          Stages.build();
          StageManager.MAIN_STAGE.show();

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

    walletLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {

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
    updateBalance(new BigDecimal("20999999.12345678"));

    // Top icons
    AwesomeDecorator.applyIcon(homeLabel, AwesomeIcon.HOME, ContentDisplay.LEFT);
    AwesomeDecorator.applyIcon(contactsLabel, AwesomeIcon.USER, ContentDisplay.LEFT);
    AwesomeDecorator.applyIcon(settingsLabel, AwesomeIcon.GEAR, ContentDisplay.LEFT);
    AwesomeDecorator.applyIcon(helpLabel, AwesomeIcon.QUESTION_CIRCLE, ContentDisplay.LEFT);

    // Wallets
    AwesomeDecorator.applyIcon(walletLabel, AwesomeIcon.BITCOIN, ContentDisplay.LEFT);
  }

  private void updateBalance(BigDecimal amount) {

    Configuration configuration = Stages.getConfiguration();

    String[] balance = Formats.formatBitcoinBalance(amount);

    if (configuration.isCurrencySymbolPrefixed()) {

      if (BitcoinSymbol.FONT_AWESOME_ICON.equals(configuration.getBitcoinSymbol())) {
        AwesomeDecorator.applyIcon(balanceLHSLabel, AwesomeIcon.BITCOIN, ContentDisplay.LEFT, "32px");
      } else {
        balance[0] = configuration.getBitcoinSymbol().getSymbol() + balance[0];
      }

    } else {
      if (BitcoinSymbol.FONT_AWESOME_ICON.equals(configuration.getBitcoinSymbol())) {
        AwesomeDecorator.applyIcon(balanceRHSLabel, AwesomeIcon.BITCOIN, ContentDisplay.RIGHT, "32px");
      } else {
        balance[1] = configuration.getBitcoinSymbol().getSymbol() + balance[1];
      }
    }

    balanceLHSLabel.setText(balance[0]);
    balanceRHSLabel.setText(balance[1]);

  }

  public AnchorPane getDetailAnchorPane() {
    return detailAnchorPane;
  }
}
