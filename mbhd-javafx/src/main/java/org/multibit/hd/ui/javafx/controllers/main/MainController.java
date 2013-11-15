package org.multibit.hd.ui.javafx.controllers.main;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.multibit.hd.ui.javafx.config.BitcoinConfiguration;
import org.multibit.hd.ui.javafx.config.Configurations;
import org.multibit.hd.ui.javafx.config.I18NConfiguration;
import org.multibit.hd.ui.javafx.controllers.MultiBitController;
import org.multibit.hd.ui.javafx.fonts.AwesomeDecorator;
import org.multibit.hd.ui.javafx.fonts.AwesomeIcon;
import org.multibit.hd.ui.javafx.i18n.BitcoinSymbol;
import org.multibit.hd.ui.javafx.i18n.Formats;
import org.multibit.hd.ui.javafx.views.Screen;
import org.multibit.hd.ui.javafx.views.StageManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

public class MainController extends MultiBitController {

  private static final Logger log = LoggerFactory.getLogger(MainController.class);

  @FXML
  public Label balanceLHSLabel;

  @FXML
  public Label balanceRHSLabel;

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

  @FXML
  public TreeView<String> walletTreeView;

  @Override
  public void initClickEvents() {

    helpLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {

      @Override
      public void handle(MouseEvent mouseEvent) {
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {

          StageManager.MAIN_STAGE.changeScreen(Screen.MAIN_HELP);

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

    TreeItem<String> root = new TreeItem<>("");
    root.setExpanded(false);

    TreeItem<String> wallet = new TreeItem<>("Wallet");
    wallet.setExpanded(true);
    wallet.getChildren().add(new TreeItem<>("Account 1"));
    wallet.getChildren().add(new TreeItem<>("Account 2"));
    wallet.getChildren().add(new TreeItem<>("All Contacts"));
    wallet.getChildren().add(new TreeItem<>("All Transactions"));
    wallet.getChildren().add(new TreeItem<>("All Messages"));

    root.getChildren().add(wallet);

    TreeItem<String> trezor1 = new TreeItem<>("Trezor 1");
    trezor1.setExpanded(true);
    trezor1.getChildren().add(new TreeItem<>("Account 1"));
    trezor1.getChildren().add(new TreeItem<>("Account 2"));
    trezor1.getChildren().add(new TreeItem<>("All Contacts"));
    trezor1.getChildren().add(new TreeItem<>("All Transactions"));
    trezor1.getChildren().add(new TreeItem<>("All Messages"));

    root.getChildren().add(trezor1);

    TreeItem<String> trezor2 = new TreeItem<>("Trezor 2");
    trezor2.setExpanded(true);
    trezor2.getChildren().add(new TreeItem<>("Account 1"));
    trezor2.getChildren().add(new TreeItem<>("Account 2"));
    trezor2.getChildren().add(new TreeItem<>("All Contacts"));
    trezor2.getChildren().add(new TreeItem<>("All Transactions"));
    trezor2.getChildren().add(new TreeItem<>("All Messages"));

    root.getChildren().add(trezor2);

    walletTreeView.setShowRoot(false);
    walletTreeView.setRoot(root);

  }

  @Override
  public void initAwesome() {

    // Title

    // Sidebar
    // TODO Convert this to a wallet event listener
    updateBalance(new BigDecimal("20999999.12345678"));

    // Top icons
    AwesomeDecorator.applyIcon(settingsLabel, AwesomeIcon.GEAR, ContentDisplay.LEFT);
    AwesomeDecorator.applyIcon(helpLabel, AwesomeIcon.QUESTION_CIRCLE, ContentDisplay.LEFT);

    // Wallets
    AwesomeDecorator.applyIcon(walletTreeView.getRoot().getChildren().get(0), AwesomeIcon.BITCOIN);
    AwesomeDecorator.applyIcon(walletTreeView.getRoot().getChildren().get(0).getChildren().get(0), AwesomeIcon.BOOK);
    AwesomeDecorator.applyIcon(walletTreeView.getRoot().getChildren().get(0).getChildren().get(1), AwesomeIcon.BOOK);
    AwesomeDecorator.applyIcon(walletTreeView.getRoot().getChildren().get(0).getChildren().get(2), AwesomeIcon.USER);
    AwesomeDecorator.applyIcon(walletTreeView.getRoot().getChildren().get(0).getChildren().get(3), AwesomeIcon.LIST, "18px");
    AwesomeDecorator.applyIcon(walletTreeView.getRoot().getChildren().get(0).getChildren().get(4), AwesomeIcon.ENVELOPE);

    AwesomeDecorator.applyIcon(walletTreeView.getRoot().getChildren().get(1), AwesomeIcon.SHIELD);
    AwesomeDecorator.applyIcon(walletTreeView.getRoot().getChildren().get(1).getChildren().get(0), AwesomeIcon.BOOK);
    AwesomeDecorator.applyIcon(walletTreeView.getRoot().getChildren().get(1).getChildren().get(1), AwesomeIcon.BOOK);
    AwesomeDecorator.applyIcon(walletTreeView.getRoot().getChildren().get(1).getChildren().get(2), AwesomeIcon.USER);
    AwesomeDecorator.applyIcon(walletTreeView.getRoot().getChildren().get(1).getChildren().get(3), AwesomeIcon.LIST, "18px");
    AwesomeDecorator.applyIcon(walletTreeView.getRoot().getChildren().get(1).getChildren().get(4), AwesomeIcon.ENVELOPE);

    AwesomeDecorator.applyIcon(walletTreeView.getRoot().getChildren().get(2), AwesomeIcon.SHIELD);
    AwesomeDecorator.applyIcon(walletTreeView.getRoot().getChildren().get(2).getChildren().get(0), AwesomeIcon.BOOK);
    AwesomeDecorator.applyIcon(walletTreeView.getRoot().getChildren().get(2).getChildren().get(1), AwesomeIcon.BOOK);
    AwesomeDecorator.applyIcon(walletTreeView.getRoot().getChildren().get(2).getChildren().get(2), AwesomeIcon.USER);
    AwesomeDecorator.applyIcon(walletTreeView.getRoot().getChildren().get(2).getChildren().get(3), AwesomeIcon.LIST, "18px");
    AwesomeDecorator.applyIcon(walletTreeView.getRoot().getChildren().get(2).getChildren().get(4), AwesomeIcon.ENVELOPE);
  }

  private void updateBalance(BigDecimal amount) {

    BitcoinConfiguration bitcoinConfiguration = Configurations.currentConfiguration.getBitcoinConfiguration();
    I18NConfiguration i18nConfiguration = Configurations.currentConfiguration.getI18NConfiguration();

    String[] balance = Formats.formatBitcoinBalance(amount);

    if (i18nConfiguration.isCurrencySymbolPrefixed()) {

      // Place currency symbol before the number
      if (BitcoinSymbol.ICON.equals(bitcoinConfiguration.getBitcoinSymbol())) {
        // Add icon to LHS, remove from elsewhere
        AwesomeDecorator.applyIcon(balanceLHSLabel, AwesomeIcon.BITCOIN, ContentDisplay.LEFT, "32px");
        AwesomeDecorator.removeIcon(balanceRHSSymbolLabel);
        balanceRHSSymbolLabel.setText("");
      } else {
        // Add symbol to LHS, remove from elsewhere
        balance[0] = bitcoinConfiguration.getBitcoinSymbol().getSymbol() + " " + balance[0];
        AwesomeDecorator.removeIcon(balanceLHSLabel);
      }

    } else {

      // Place currency symbol after the number
      if (BitcoinSymbol.ICON.equals(bitcoinConfiguration.getBitcoinSymbol())) {
        // Add icon to RHS, remove from elsewhere
        AwesomeDecorator.applyIcon(balanceRHSSymbolLabel, AwesomeIcon.BITCOIN, ContentDisplay.LEFT, "32px");
        AwesomeDecorator.removeIcon(balanceLHSLabel);
        balanceRHSSymbolLabel.setText("");
      } else {
        // Add symbol to RHS, remove from elsewhere
        balanceRHSSymbolLabel.setText(bitcoinConfiguration.getBitcoinSymbol().getSymbol());
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
