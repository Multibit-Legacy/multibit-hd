package org.multibit.hd.ui.views;

import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.api.MessageKey;
import org.multibit.hd.core.config.BitcoinConfiguration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.config.I18NConfiguration;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.events.controller.ControllerEvents;
import org.multibit.hd.ui.events.controller.RemoveAlertEvent;
import org.multibit.hd.ui.events.view.AlertChangedEvent;
import org.multibit.hd.ui.events.view.BalanceChangedEvent;
import org.multibit.hd.ui.i18n.BitcoinSymbol;
import org.multibit.hd.ui.i18n.Formats;
import org.multibit.hd.ui.i18n.Languages;
import org.multibit.hd.ui.models.AlertModel;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.PanelDecorator;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
  private JLabel alertMessageLabel;
  private JLabel alertRemainingLabel;

  private BalanceChangedEvent latestBalanceChangedEvent;

  private final JPanel contentPanel;

  private final JPanel alertPanel;

  public HeaderView() {

    CoreServices.uiEventBus.register(this);

    contentPanel = Panels.newPanel(new MigLayout(
      "insets 15 8,hidemode 1", // Layout
      "[][][][][]", // Columns
      "[]10[shrink]" // Rows
    ));

    // Apply the theme
    contentPanel.setBackground(Themes.currentTheme.headerPanelBackground());

    // Create the alert panel
    alertPanel = createAlertPanel();

    // Start off in hiding
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

  private JPanel createAlertPanel() {

    JPanel panel = Panels.newPanel(new MigLayout(
      "fill,ins 5,hidemode 3",
      "[grow][][]", // Columns
      "[]" // Rows
    ));

    alertMessageLabel = new JLabel();
    panel.add(alertMessageLabel, "push");

    alertRemainingLabel = new JLabel();
    panel.add(alertRemainingLabel, "shrink,right");

    JLabel closeLabel = Labels.newPanelCloseLabel(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        ControllerEvents.fireRemoveAlertEvent();
      }
    });

    panel.add(closeLabel);

    return panel;
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
  public void onBalanceChangedEvent(BalanceChangedEvent event) {

    // Keep track of the latest balance
    this.latestBalanceChangedEvent = event;

    // Handle the update
    handleBalanceChange();
  }

  /**
   * <p>Handles the presentation of a new alert</p>
   *
   * @param event The show alert event
   */
  @Subscribe
  public void onAlertChangedEvent(AlertChangedEvent event) {

    AlertModel alertModel = event.getAlertModel();

    // Update the text according to the model
    alertMessageLabel.setText(alertModel.getLocalisedMessage());
    alertRemainingLabel.setText(alertModel.getRemainingText());


    switch (alertModel.getSeverity()) {
      case RED:
        PanelDecorator.applyDanger(alertPanel);
        break;
      case AMBER:
        PanelDecorator.applyWarning(alertPanel);
        break;
      case GREEN:
        PanelDecorator.applySuccess(alertPanel);
        break;
      default:
        throw new IllegalStateException("Unknown severity: " + alertModel.getSeverity().name());
    }

    alertPanel.setVisible(true);

  }

  /**
   * <p>Remove any existing alert</p>
   *
   * @param event The remove alert event
   */
  @Subscribe
  public void onAlertRemovedEvent(RemoveAlertEvent event) {

    // Hide the alert panel
    alertPanel.setVisible(false);

  }

  /**
   * <p>Reflect the current balance on the UI</p>
   */
  private void handleBalanceChange() {

    BitcoinConfiguration bitcoinConfiguration = Configurations.currentConfiguration.getBitcoinConfiguration();
    I18NConfiguration i18nConfiguration = Configurations.currentConfiguration.getI18NConfiguration();

    String[] balance = Formats.formatBitcoinBalance(latestBalanceChangedEvent.getBtcBalance().getAmount());
    String localBalance = Formats.formatLocalBalance(latestBalanceChangedEvent.getLocalBalance().getAmount());

    BitcoinSymbol symbol = BitcoinSymbol.valueOf(bitcoinConfiguration.getBitcoinSymbol());

    if (i18nConfiguration.isCurrencySymbolPrefixed()) {
      handlePrefixedSymbol(balance, symbol);
    } else {
      handleSuffixSymbol(symbol);
    }

    primaryBalanceLabel.setText(balance[0]);
    secondaryBalanceLabel.setText(balance[1]);

    exchangeLabel.setText(
      Languages.safeText(
        MessageKey.EXCHANGE_FIAT_RATE,
        localBalance,
        latestBalanceChangedEvent.getRateProvider()
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
      AwesomeDecorator.applyIcon(AwesomeIcon.BITCOIN, primaryBalanceLabel, true, (int) Labels.BALANCE_LARGE_FONT_SIZE);
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
      AwesomeDecorator.applyIcon(AwesomeIcon.BITCOIN, trailingSymbolLabel, true, (int) Labels.BALANCE_LARGE_FONT_SIZE);
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
