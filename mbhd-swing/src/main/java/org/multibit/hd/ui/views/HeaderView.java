package org.multibit.hd.ui.views;

import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.i18n.MessageKey;
import org.multibit.hd.core.config.BitcoinConfiguration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.config.I18NConfiguration;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.core.utils.BitcoinSymbol;
import org.multibit.hd.core.utils.CurrencyUtils;
import org.multibit.hd.ui.events.controller.ControllerEvents;
import org.multibit.hd.ui.events.controller.RemoveAlertEvent;
import org.multibit.hd.ui.events.view.AlertAddedEvent;
import org.multibit.hd.ui.events.view.BalanceChangedEvent;
import org.multibit.hd.ui.events.view.LocaleChangedEvent;
import org.multibit.hd.ui.i18n.Formats;
import org.multibit.hd.ui.i18n.Languages;
import org.multibit.hd.ui.models.AlertModel;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.display_amount.DisplayAmountStyle;
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

  private Optional<BalanceChangedEvent> latestBalanceChangedEvent = Optional.absent();

  private final JPanel contentPanel;
  private final JPanel alertPanel;
  private final JPanel balancePanel;

  public HeaderView() {

    CoreServices.uiEventBus.register(this);

    // Create the content panel
    contentPanel = Panels.newPanel(new MigLayout(
      "fillx,insets 10 10 0 10,hidemode 3", // Layout insets ensure border is tight to sidebar
      "[]", // Columns
      "[][shrink]" // Rows
    ));

    // Create the balance panel - forcing a LTR layout to ensure correct placement of labels
    balancePanel = Panels.newPanel(new MigLayout(
      "fill,insets 0,ltr", // Layout
      "[][][][][]", // Columns
      "[]10[shrink]" // Rows
    ));

    // Create the alert panel
    alertPanel = Panels.newPanel(new MigLayout(
      "fillx,insets 5", // Layout insets define the padding for the alert
      "[grow][][]", // Columns
      "[]" // Rows
    ));

    // Start off in hiding
    alertPanel.setVisible(false);

    // Apply the theme
    contentPanel.setBackground(Themes.currentTheme.headerPanelBackground());
    balancePanel.setBackground(Themes.currentTheme.headerPanelBackground());

    // Create the balance labels
    JLabel[] balanceLabels = Labels.newBalanceLabels(DisplayAmountStyle.HEADER);
    primaryBalanceLabel = balanceLabels[0];
    secondaryBalanceLabel = balanceLabels[1];
    trailingSymbolLabel = balanceLabels[2];
    exchangeLabel = balanceLabels[3];

    contentPanel.add(balancePanel, "growx,wrap");
    contentPanel.add(alertPanel, "growx,aligny top,push");

    onLocaleChangedEvent(null);
  }

  /**
   * @return The content panel for this View
   */
  public JPanel getContentPanel() {
    return contentPanel;
  }

  /**
   * <p>Handles the representation of the header when a locale change occurs</p>
   *
   * @param event The balance change event
   */
  @Subscribe
  public void onLocaleChangedEvent(LocaleChangedEvent event) {

    populateBalancePanel();
    populateAlertPanel();

  }

  /**
   * <p>Handles the representation of the balance based on the current configuration</p>
   *
   * @param event The balance change event
   */
  @Subscribe
  public void onBalanceChangedEvent(BalanceChangedEvent event) {

    // Keep track of the latest balance
    latestBalanceChangedEvent = Optional.of(event);

    // Handle the update
    handleBalanceChange();
  }

  /**
   * <p>Handles the presentation of a new alert</p>
   *
   * @param event The show alert event
   */
  @Subscribe
  public void onAlertAddedEvent(AlertAddedEvent event) {

    AlertModel alertModel = event.getAlertModel();

    // Update the text according to the model
    alertMessageLabel.setText(alertModel.getLocalisedMessage());
    alertRemainingLabel.setText(alertModel.getRemainingText());


    switch (alertModel.getSeverity()) {
      case RED:
        PanelDecorator.applyDangerTheme(alertPanel);
        break;
      case AMBER:
        PanelDecorator.applyWarningTheme(alertPanel);
        break;
      case GREEN:
        PanelDecorator.applySuccessTheme(alertPanel);
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

  private void populateAlertPanel() {

    alertPanel.removeAll();

    alertMessageLabel = Labels.newBlankLabel();
    alertRemainingLabel = Labels.newBlankLabel();

    JLabel closeLabel = Labels.newPanelCloseLabel(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        ControllerEvents.fireRemoveAlertEvent();
      }
    });

    // Determine how to add them back into the panel
    if (Languages.isLeftToRight()) {
      alertPanel.add(alertMessageLabel, "push");
      alertPanel.add(alertRemainingLabel, "shrink,right");
      alertPanel.add(closeLabel);
    } else {
      alertPanel.add(closeLabel);
      alertPanel.add(alertRemainingLabel, "shrink,left");
      alertPanel.add(alertMessageLabel, "push");
    }

  }

  private void populateBalancePanel() {

    balancePanel.removeAll();

    // Determine how to add them back into the panel
    if (Languages.isLeftToRight()) {
      balancePanel.add(primaryBalanceLabel, "left,shrink,baseline");
      balancePanel.add(secondaryBalanceLabel, "left,shrink,gap 0");
      balancePanel.add(trailingSymbolLabel, "left,shrink,gap 0");
      balancePanel.add(exchangeLabel, "left,shrink,gap 0");
      balancePanel.add(new JLabel(), "push,wrap"); // Provides a flexible column
    } else {

      balancePanel.add(exchangeLabel, "right,shrink,gap 0");
      balancePanel.add(primaryBalanceLabel, "right,shrink,baseline");
      balancePanel.add(secondaryBalanceLabel, "right,shrink,gap 0");
      balancePanel.add(trailingSymbolLabel, "right,shrink,gap 0");
      balancePanel.add(new JLabel(), "push,wrap"); // Provides a flexible column
    }

    if (latestBalanceChangedEvent.isPresent()) {
      handleBalanceChange();
    }
  }

  /**
   * <p>Reflect the current balance on the UI</p>
   *
   * TODO Consider replacing this with the DisplayAmount component
   */
  private void handleBalanceChange() {

    BitcoinConfiguration bitcoinConfiguration = Configurations.currentConfiguration.getBitcoinConfiguration();
    I18NConfiguration i18nConfiguration = Configurations.currentConfiguration.getI18NConfiguration();

    String[] balance = Formats.formatSatoshisAsSymbolic(latestBalanceChangedEvent.get().getSatoshis());
    String localBalance = Formats.formatLocalAmount(latestBalanceChangedEvent.get().getLocalBalance());

    BitcoinSymbol symbol = BitcoinSymbol.of(bitcoinConfiguration.getBitcoinSymbol());

    if (i18nConfiguration.isCurrencySymbolLeading()) {
      handleLeadingSymbol(balance, symbol);
    } else {
      handleTrailingSymbol(symbol);
    }

    primaryBalanceLabel.setText(balance[0]);
    secondaryBalanceLabel.setText(balance[1]);

    String localCurrencySymbol = CurrencyUtils.currentSymbol();

    exchangeLabel.setText(
      Languages.safeText(
        MessageKey.EXCHANGE_FIAT_RATE,
        "~ "+localCurrencySymbol,
        localBalance,
        latestBalanceChangedEvent.get().getRateProvider()
      ));
  }


  /**
   * <p>Place currency symbol before the number</p>
   * TODO Consider replacing this with the DisplayAmount component
   *
   * @param symbol The symbol to use
   */
  private void handleLeadingSymbol(String[] balance, BitcoinSymbol symbol) {

    // Placement is primary, secondary, trailing, exchange (reading LTR)

    if (BitcoinSymbol.ICON.equals(symbol)) {

      // Icon leads primary balance but decorator will automatically swap which is undesired
      if (Languages.isLeftToRight()) {
        AwesomeDecorator.applyIcon(AwesomeIcon.BITCOIN, primaryBalanceLabel, true, (int) MultiBitUI.BALANCE_HEADER_LARGE_FONT_SIZE);
      } else {
        AwesomeDecorator.applyIcon(AwesomeIcon.BITCOIN, primaryBalanceLabel, false, (int) MultiBitUI.BALANCE_HEADER_LARGE_FONT_SIZE);
      }
      AwesomeDecorator.removeIcon(trailingSymbolLabel);
      trailingSymbolLabel.setText("");

    } else {

      // Symbol leads primary balance
      balance[0] = symbol.getSymbol() + " " + balance[0];
      AwesomeDecorator.removeIcon(primaryBalanceLabel);
      AwesomeDecorator.removeIcon(trailingSymbolLabel);

    }

  }

  /**
   * <p>Place currency symbol after the number</p>
   * TODO Consider replacing this with the DisplayAmount component
   *
   * @param symbol The symbol to use
   */
  private void handleTrailingSymbol(BitcoinSymbol symbol) {

    if (BitcoinSymbol.ICON.equals(symbol)) {

      // Icon trails secondary balance
      AwesomeDecorator.applyIcon(AwesomeIcon.BITCOIN, trailingSymbolLabel, true, (int) MultiBitUI.BALANCE_HEADER_LARGE_FONT_SIZE);
      AwesomeDecorator.removeIcon(primaryBalanceLabel);
      trailingSymbolLabel.setText("");

    } else {

      // Symbol trails secondary balance
      trailingSymbolLabel.setText(symbol.getSymbol());
      AwesomeDecorator.removeIcon(primaryBalanceLabel);
      AwesomeDecorator.removeIcon(trailingSymbolLabel);

    }


  }

}
