package org.multibit.hd.ui.views;

import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.config.BitcoinConfiguration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.config.I18NConfiguration;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.events.BalanceChangeEvent;
import org.multibit.hd.ui.events.ShowAlertEvent;
import org.multibit.hd.ui.events.ViewEvents;
import org.multibit.hd.ui.i18n.BitcoinSymbol;
import org.multibit.hd.ui.i18n.Formats;
import org.multibit.hd.ui.i18n.Languages;
import org.multibit.hd.ui.views.alerts.Alerts;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;

import javax.swing.*;
import java.awt.*;

/**
 * <p>View to provide the following to application:</p>
 * <ul>
 * <li>Provision of components and layout for the balance display</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class HeaderView {

  private JLabel primaryBalanceLabel;
  private JLabel secondaryBalanceLabel;
  private JLabel trailingSymbolLabel;
  private JLabel exchangeLabel;

  private BalanceChangeEvent latestBalanceChangeEvent;

  private final JPanel contentPanel;

  private final JPanel alertPanel;

  public HeaderView() {

    CoreServices.uiEventBus.register(this);

    contentPanel = new JPanel(new MigLayout(
      "", // Layout
      "[][][][][]", // Columns
      "[]10[shrink]" // Rows
    ));

    // Create the alert panel
    CardLayout cardLayout = new CardLayout();
    alertPanel = new JPanel(cardLayout);
    alertPanel.setVisible(false);

    // Create the balance labels
    JLabel[] balanceLabels = Labels.newBalanceLabels();
    primaryBalanceLabel = balanceLabels[0];
    secondaryBalanceLabel = balanceLabels[1];
    trailingSymbolLabel = balanceLabels[2];
    exchangeLabel = balanceLabels[3];

    contentPanel.add(primaryBalanceLabel, "shrink,baseline");
    contentPanel.add(secondaryBalanceLabel, "shrink,gap 0");
    contentPanel.add(trailingSymbolLabel, "shrink,gap 0");
    contentPanel.add(exchangeLabel, "shrink,gap 0");
    contentPanel.add(new JLabel(), "push,wrap"); // Provides a flexible column

    contentPanel.add(alertPanel, "grow,span 5");

  }

  /**
   * @return The content panel for this View
   */
  public JPanel getContentPanel() {
    return contentPanel;
  }

  /**
   * <p>Handles the representation of the balance based on the current configuration</p>
   *
   * @param event The balance change event
   */
  @Subscribe
  public void onBalanceChangeEvent(BalanceChangeEvent event) {

    // Keep track of the latest balance
    this.latestBalanceChangeEvent = event;

    // Handle the update
    handleBalanceChange();
  }

  /**
   * <p>Handles the representation of the balance based on the current configuration</p>
   *
   * @param event The balance change event
   */
  @Subscribe
  public void onShowAlertEvent(ShowAlertEvent event) {

    ViewEvents.fireSystemStatusEvent(event.getSeverity());

    alertPanel.add(Alerts.newBitcoinNetworkAlert(event.getMessage()).getContentPanel());

    alertPanel.setVisible(true);

  }

  /**
   * <p>Reflect the current balance on the UI</p>
   */
  private void handleBalanceChange() {

    BitcoinConfiguration bitcoinConfiguration = Configurations.currentConfiguration.getBitcoinConfiguration();
    I18NConfiguration i18nConfiguration = Configurations.currentConfiguration.getI18NConfiguration();

    String[] balance = Formats.formatBitcoinBalance(latestBalanceChangeEvent.getBtcBalance().getAmount());
    String localBalance = Formats.formatLocalBalance(latestBalanceChangeEvent.getLocalBalance().getAmount());

    BitcoinSymbol symbol = BitcoinSymbol.valueOf(bitcoinConfiguration.getBitcoinSymbol());

    if (i18nConfiguration.isCurrencySymbolPrefixed()) {
      handlePrefixedSymbol(balance, symbol);
    } else {
      handleSuffixSymbol(symbol);
    }

    primaryBalanceLabel.setText(balance[0]);
    secondaryBalanceLabel.setText(balance[1]);

    // TODO Add this to resource bundles
    String exchangeText = "~ ${0} ({1})";
    exchangeLabel.setText(
      Languages.safeText(
        exchangeText,
        localBalance,
        latestBalanceChangeEvent.getRateProvider()
      ));
  }


  /**
   * <p>Place currency symbol before the number</p>
   *
   * @param symbol The symbol to use
   */
  private void handlePrefixedSymbol(String[] balance, BitcoinSymbol symbol) {

    // Place currency symbol before the number
    if (BitcoinSymbol.ICON.equals(symbol)) {
      // Add icon to LHS, remove from elsewhere
      AwesomeDecorator.applyIcon(AwesomeIcon.BITCOIN, primaryBalanceLabel, true);
      AwesomeDecorator.removeIcon(trailingSymbolLabel);
      trailingSymbolLabel.setText("");
    } else {
      // Add symbol to LHS, remove from elsewhere
      balance[0] = symbol.getSymbol() + " " + balance[0];
      AwesomeDecorator.removeIcon(primaryBalanceLabel);
    }

  }

  /**
   * <p>Place currency symbol after the number</p>
   *
   * @param symbol The symbol to use
   */
  private void handleSuffixSymbol(BitcoinSymbol symbol) {

    if (BitcoinSymbol.ICON.equals(symbol)) {
      // Add icon to RHS, remove from elsewhere
      AwesomeDecorator.applyIcon(AwesomeIcon.BITCOIN, trailingSymbolLabel, true);
      AwesomeDecorator.removeIcon(primaryBalanceLabel);
      trailingSymbolLabel.setText("");
    } else {
      // Add symbol to RHS, remove from elsewhere
      trailingSymbolLabel.setText(symbol.getSymbol());
      AwesomeDecorator.removeIcon(primaryBalanceLabel);
      AwesomeDecorator.removeIcon(trailingSymbolLabel);
    }

  }

}
