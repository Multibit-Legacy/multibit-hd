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

  @FXML
  public Label balanceRHSSymbolLabel;

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

          Configuration configuration = Stages.getConfiguration();

          BitcoinSymbol symbol = configuration.getBitcoinSymbol();

          // Get the next ordinal
          int ordinal = symbol.ordinal() + 1;
          if (ordinal == BitcoinSymbol.values().length) {
            ordinal = 0;
          }
          // Toggle the placement
          if (ordinal == 0) {
            configuration.getI18NConfiguration().setCurrencySymbolPrefixed(
              !configuration.getI18NConfiguration().isCurrencySymbolPrefixed()
            );
          }

          BitcoinSymbol[] symbols = BitcoinSymbol.class.getEnumConstants();
          configuration.setBitcoinSymbol(symbols[ordinal]);

          // Update all the stages to the new locale
          updateBalance(new BigDecimal("20999999.12345678"));

        }
      }
    });

    settingsLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {

      @Override
      public void handle(MouseEvent mouseEvent) {
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {

          Configuration configuration = Stages.getConfiguration();
          if (configuration.getLocale().equals(Locale.UK)) {
            configuration.getI18NConfiguration().setLocale(new Locale("RU"));
          } else {
            configuration.getI18NConfiguration().setLocale(Locale.UK);
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

    if (configuration.getI18NConfiguration().isCurrencySymbolPrefixed()) {

      // Place currency symbol before the number
      if (BitcoinSymbol.ICON.equals(configuration.getBitcoinSymbol())) {
        // Add icon to LHS, remove from elsewhere
        AwesomeDecorator.applyIcon(balanceLHSLabel, AwesomeIcon.BITCOIN, ContentDisplay.LEFT, "32px");
        AwesomeDecorator.removeIcon(balanceRHSSymbolLabel);
        balanceRHSSymbolLabel.setText("");
      } else {
        // Add symbol to LHS, remove from elsewhere
        balance[0] = configuration.getBitcoinSymbol().getSymbol() + " " + balance[0];
        AwesomeDecorator.removeIcon(balanceLHSLabel);
      }

    } else {

      // Place currency symbol after the number
      if (BitcoinSymbol.ICON.equals(configuration.getBitcoinSymbol())) {
        // Add icon to RHS, remove from elsewhere
        AwesomeDecorator.applyIcon(balanceRHSSymbolLabel, AwesomeIcon.BITCOIN, ContentDisplay.LEFT, "32px");
        AwesomeDecorator.removeIcon(balanceLHSLabel);
        balanceRHSSymbolLabel.setText("");
      } else {
        // Add symbol to RHS, remove from elsewhere
        balanceRHSSymbolLabel.setText(configuration.getBitcoinSymbol().getSymbol());
        AwesomeDecorator.removeIcon(balanceLHSLabel);
        AwesomeDecorator.removeIcon(balanceRHSSymbolLabel);
      }
    }

    balanceLHSLabel.setText(balance[0]);
    balanceRHSLabel.setText(balance[1]);

  }

  public AnchorPane getDetailAnchorPane() {
    return detailAnchorPane;
  }
}
