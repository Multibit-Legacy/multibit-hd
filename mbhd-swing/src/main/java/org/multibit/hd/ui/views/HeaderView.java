package org.multibit.hd.ui.views;

import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.config.BitcoinConfiguration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.config.I18NConfiguration;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.events.BalanceChangeEvent;
import org.multibit.hd.ui.i18n.BitcoinSymbol;
import org.multibit.hd.ui.i18n.Formats;
import org.multibit.hd.ui.i18n.Languages;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

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

  private JLabel balanceLHSLabel;
  private JLabel balanceRHSLabel;
  private JLabel balanceRHSSymbolLabel;
  private JLabel exchangeLabel;

  private BalanceChangeEvent latestBalanceChangeEvent;

  private JLabel helpLabel;
  private JLabel settingsLabel;
  private JLabel signOutLabel;

  private final JPanel contentPanel;

  public HeaderView() {

    CoreServices.uiEventBus.register(this);

    MigLayout layout = new MigLayout("fillx");
    contentPanel = new JPanel(layout);

    // Create the balance labels
    JLabel[] balanceLabels = Labels.newBalanceLabels();
    balanceLHSLabel = balanceLabels[0];
    balanceRHSLabel = balanceLabels[1];
    balanceRHSSymbolLabel = balanceLabels[2];
    exchangeLabel = balanceLabels[3];

    contentPanel.add(balanceLHSLabel, "baseline grow");
    contentPanel.add(balanceRHSLabel, "gap 0");
    contentPanel.add(balanceRHSSymbolLabel, "gap 0");
    contentPanel.add(exchangeLabel, "gap unrelated");
    contentPanel.add(new JLabel(), "push");

    // Add the right hand controls
    helpLabel = Labels.newHelpLabel();
    settingsLabel = Labels.newSettingsLabel();
    signOutLabel = Labels.newSignOutLabel();

    contentPanel.add(helpLabel, "gap unrelated");
    contentPanel.add(settingsLabel, "gap unrelated");
    contentPanel.add(signOutLabel, "gap unrelated");

    // Bind event handlers
    bindBalanceClickEvent();

  }

  /**
   * @return The content panel for this View
   */
  public JPanel getContentPanel() {
    return contentPanel;
  }

  /**
   * Configure a mouse click event to change the presentation format
   */
  private void bindBalanceClickEvent() {

    MouseListener balanceMouseListener = new MouseAdapter() {

      @Override
      public void mouseClicked(MouseEvent e) {

        // Change the unit
        BitcoinSymbol currentSymbol = BitcoinSymbol.valueOf(Configurations
          .currentConfiguration
          .getBitcoinConfiguration()
          .getBitcoinSymbol()
        );

        Configurations
          .currentConfiguration
          .getBitcoinConfiguration()
          .setBitcoinSymbol(currentSymbol.next().name());

        // Refresh the UI using the last balance event
        handleBalanceChange();
      }

    };

    balanceLHSLabel.addMouseListener(balanceMouseListener);
    balanceRHSLabel.addMouseListener(balanceMouseListener);
    balanceRHSSymbolLabel.addMouseListener(balanceMouseListener);

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

    balanceLHSLabel.setText(balance[0]);
    balanceRHSLabel.setText(balance[1]);

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
      AwesomeDecorator.applyIcon(AwesomeIcon.BITCOIN, balanceLHSLabel);
      AwesomeDecorator.removeIcon(balanceRHSSymbolLabel);
      balanceRHSSymbolLabel.setText("");
    } else {
      // Add symbol to LHS, remove from elsewhere
      balance[0] = symbol.getSymbol() + " " + balance[0];
      AwesomeDecorator.removeIcon(balanceLHSLabel);
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
      AwesomeDecorator.applyIcon(AwesomeIcon.BITCOIN, balanceRHSSymbolLabel);
      AwesomeDecorator.removeIcon(balanceLHSLabel);
      balanceRHSSymbolLabel.setText("");
    } else {
      // Add symbol to RHS, remove from elsewhere
      balanceRHSSymbolLabel.setText(symbol.getSymbol());
      AwesomeDecorator.removeIcon(balanceLHSLabel);
      AwesomeDecorator.removeIcon(balanceRHSSymbolLabel);
    }

  }


}
