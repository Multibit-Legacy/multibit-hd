package org.multibit.hd.ui.views;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.bitcoinj.core.Coin;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.FiatPayment;
import org.multibit.hd.core.services.WalletService;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.events.controller.ControllerEvents;
import org.multibit.hd.ui.events.view.AlertAddedEvent;
import org.multibit.hd.ui.events.view.AlertRemovedEvent;
import org.multibit.hd.ui.events.view.BalanceChangedEvent;
import org.multibit.hd.ui.events.view.ViewChangedEvent;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.models.AlertModel;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.display_amount.DisplayAmountModel;
import org.multibit.hd.ui.views.components.display_amount.DisplayAmountStyle;
import org.multibit.hd.ui.views.components.display_amount.DisplayAmountView;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.themes.NimbusDecorator;
import org.multibit.hd.ui.views.themes.Themes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;

/**
 * <p>View to provide the following to application:</p>
 * <ul>
 * <li>Provision of components and layout for the balance display</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class HeaderView extends AbstractView {
  private static final Logger log = LoggerFactory.getLogger(HeaderView.class);

  private JLabel plusUncomfirmedLabel;
  private final ModelAndView<DisplayAmountModel, DisplayAmountView> availableBalanceDisplayMaV;
  private final ModelAndView<DisplayAmountModel, DisplayAmountView> unconfirmedDisplayMaV;

  private JLabel alertMessageLabel;
  private JLabel alertRemainingLabel;

  private JButton alertButton;
  private JButton closeButton;

  private final JPanel contentPanel;
  private final JPanel alertPanel;

  public HeaderView() {

    super();

    contentPanel = Panels.newPanel(
            new MigLayout(
                    Panels.migLayout("fillx,insets 10 10 5 10,hidemode 3"), // Layout insets ensure border is tight to sidebar
                    "[][]10[]", // Columns
                    "[][shrink]" // Rows
            ));

    // Create the alert panel
    alertPanel = Panels.newPanel(
            new MigLayout(
                    Panels.migXLayout(),
                    "[][][][]", // Columns
                    "[]" // Rows
            ));

    // Start off in hiding
    alertPanel.setVisible(false);

    // Apply the theme
    contentPanel.setBackground(Themes.currentTheme.headerPanelBackground());
    contentPanel.setOpaque(true);

    // Create the balance display and unconfirmed amount not displaying it initially
    availableBalanceDisplayMaV = Components.newDisplayAmountMaV(DisplayAmountStyle.HEADER, true, "header.balance");
    unconfirmedDisplayMaV = Components.newDisplayAmountMaV(DisplayAmountStyle.HEADER_SMALL, true, "header.unconfirmed");

    plusUncomfirmedLabel = Labels.newPlusUnconfirmed();

    availableBalanceDisplayMaV.getView().setVisible(false);
    unconfirmedDisplayMaV.getView().setVisible(false);
    plusUncomfirmedLabel.setVisible(false);
    plusUncomfirmedLabel.setBorder(BorderFactory.createEmptyBorder(0,0,8,0));

    // Provide a fixed height to avoid an annoying "slide down" during unlock
    contentPanel.add(availableBalanceDisplayMaV.getView().newComponentPanel(), "growx,push,hmin 50, aligny bottom");
    contentPanel.add(plusUncomfirmedLabel, "shrink, aligny bottom");
    JPanel unconfirmedViewPanel = unconfirmedDisplayMaV.getView().newComponentPanel();
    unconfirmedViewPanel.setBorder(BorderFactory.createEmptyBorder(0,0,4,0));

    contentPanel.add(unconfirmedViewPanel, "growx,push, aligny bottom, wrap");
    contentPanel.add(alertPanel, "growx,aligny top,span 3, push");

    populateAlertPanel();

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
  public void onBalanceChangedEvent(final BalanceChangedEvent event) {

    // Handle the update
    availableBalanceDisplayMaV.getModel().setLocalAmount(event.getLocalBalance());
    availableBalanceDisplayMaV.getModel().setCoinAmount(event.getCoinBalance());
    availableBalanceDisplayMaV.getModel().setRateProvider(Optional.<String>absent());
    availableBalanceDisplayMaV.getModel().setLocalAmountVisible(event.getLocalBalance() != null);

    // Do not set the visibility on available balance here, use the ViewChangedEvent

    availableBalanceDisplayMaV.getView().updateView(Configurations.currentConfiguration);

    // If the unconfirmed is different from the estimated then show the unconfirmed
    if (event.getCoinBalance().compareTo(event.getCoinWithUnconfirmedBalance()) != 0) {
      Coin unconfirmedCoin = event.getCoinWithUnconfirmedBalance().subtract(event.getCoinBalance());

      FiatPayment unconfirmedFiat = WalletService.calculateFiatPaymentEquivalent(unconfirmedCoin);

      log.trace("Unconfirmed bitcoin. Coin:{}, Fiat:{}", unconfirmedCoin, unconfirmedFiat);
      boolean hasFiat = unconfirmedFiat.getAmount().isPresent();
      unconfirmedDisplayMaV.getModel().setLocalAmountVisible(hasFiat);
      if (hasFiat) {
        unconfirmedDisplayMaV.getModel().setLocalAmount(unconfirmedFiat.getAmount().get());
      }
      unconfirmedDisplayMaV.getModel().setCoinAmount(unconfirmedCoin);
      unconfirmedDisplayMaV.getView().updateViewFromModel();

      // As long as the main header is visible, show the unconfirmed
      if (availableBalanceDisplayMaV.getView().isVisible()) {
        boolean showHeader = Configurations.currentConfiguration.getAppearance().isShowBalance();
        plusUncomfirmedLabel.setVisible(showHeader);
        unconfirmedDisplayMaV.getView().setVisible(showHeader);
      }
    } else {
      // Unconfirmed and estimated is the same - switch off the unconfirmed
      unconfirmedDisplayMaV.getModel().setCoinAmount(Coin.ZERO);
      unconfirmedDisplayMaV.getModel().setLocalAmount(BigDecimal.ZERO);
      unconfirmedDisplayMaV.getView().updateViewFromModel();
      plusUncomfirmedLabel.setVisible(false);
      unconfirmedDisplayMaV.getView().setVisible(false);
    }

    unconfirmedDisplayMaV.getView().updateView(Configurations.currentConfiguration);
  }

  /**
   * <p>Handles the presentation of a new alert</p>
   *
   * @param event The show alert event
   */
  @Subscribe
  public void onAlertAddedEvent(final AlertAddedEvent event) {

    Preconditions.checkNotNull(event, "'event' must be present");

    final AlertModel alertModel = event.getAlertModel();

    Preconditions.checkNotNull(alertModel, "'alertModel' must be present");

    log.debug("Received 'alert added event': {}", event.getAlertModel());

    // Update the text according to the model
    alertMessageLabel.setText(alertModel.getLocalisedMessage());
    alertRemainingLabel.setText(alertModel.getRemainingText());

    if (alertModel.getButton().isPresent()) {

      JButton button = alertModel.getButton().get();
      alertButton.setAction(button.getAction());
      alertButton.setText(button.getText());
      alertButton.setIcon(button.getIcon());
      alertButton.setName(button.getName());
      alertButton.setToolTipText(button.getToolTipText());

      alertButton.setVisible(true);
    } else {
      alertButton.setVisible(false);
    }

    // Don't play sounds here since this will be called each time an alert is dismissed
    switch (alertModel.getSeverity()) {
      case RED:
        PanelDecorator.applyDangerTheme(alertPanel);
        NimbusDecorator.applyThemeColor(Themes.currentTheme.dangerAlertBackground(), alertButton);
        NimbusDecorator.applyThemeColor(Themes.currentTheme.dangerAlertBackground(), closeButton);
        break;
      case AMBER:
        PanelDecorator.applyWarningTheme(alertPanel);
        NimbusDecorator.applyThemeColor(Themes.currentTheme.warningAlertBackground(), alertButton);
        NimbusDecorator.applyThemeColor(Themes.currentTheme.warningAlertBackground(), closeButton);
        break;
      case GREEN:
        PanelDecorator.applySuccessTheme(alertPanel);
        NimbusDecorator.applyThemeColor(Themes.currentTheme.successAlertBackground(), alertButton);
        NimbusDecorator.applyThemeColor(Themes.currentTheme.successAlertBackground(), closeButton);
        break;
      case PINK:
        PanelDecorator.applyPendingTheme(alertPanel);
        NimbusDecorator.applyThemeColor(Themes.currentTheme.pendingAlertBackground(), alertButton);
        NimbusDecorator.applyThemeColor(Themes.currentTheme.pendingAlertBackground(), closeButton);
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
  public void onAlertRemovedEvent(final AlertRemovedEvent event) {
    // Hide the alert panel and clear the label
    alertPanel.setVisible(false);
    alertMessageLabel.setText("");
  }

  /**
   * <p>Called when the view should be should be changed</p>
   *
   * @param event The view changed event
   */
  @Subscribe
  public void onViewChangedEvent(final ViewChangedEvent event) {
    if (event.getViewKey().equals(ViewKey.HEADER)) {
      log.trace("Saw a ViewChangedEvent {}", event);

      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          log.trace("Header now has visibility: {} ", event.isVisible());
          availableBalanceDisplayMaV.getView().setVisible(event.isVisible());
          if (alertMessageLabel.getText().length() != 0 && event.isVisible()) {
            alertPanel.setVisible(event.isVisible());
          }

          availableBalanceDisplayMaV.getView().updateView(Configurations.currentConfiguration);
          if (unconfirmedDisplayMaV.getModel().getCoinAmount().compareTo(Coin.ZERO) != 0 && event.isVisible()) {
            unconfirmedDisplayMaV.getView().setVisible(true);
            plusUncomfirmedLabel.setVisible(true);
          } else {
            unconfirmedDisplayMaV.getView().setVisible(false);
            plusUncomfirmedLabel.setVisible(false);
          }
        }
      });

    }
  }

  /**
   * <p>Populate the alert panel in preparation for any alerts</p>
   */
  private void populateAlertPanel() {
    Preconditions.checkState(SwingUtilities.isEventDispatchThread(), "Must be in the EDT. Check MainController.");

    alertPanel.removeAll();

    alertMessageLabel = Labels.newBlankLabel();
    alertMessageLabel.setName("alert_message_label");

    alertRemainingLabel = Labels.newBlankLabel();
    alertRemainingLabel.setName("alert_remaining_label");

    // Placeholder button that gets overwritten
    alertButton = new JButton();
    alertButton.setName("alert_button");
    alertButton.setVisible(false);

    closeButton = Buttons.newPanelCloseButton(getCloseAlertAction());

    // Determine how to add them back into the panel
    if (Languages.isLeftToRight()) {
      alertPanel.add(alertMessageLabel, "shrink,left," + MultiBitUI.ALERT_MESSAGE_MAX_WIDTH_MIG);
      alertPanel.add(alertRemainingLabel, "push,right");
      alertPanel.add(alertButton, "push,right");
      alertPanel.add(closeButton);
    } else {
      alertPanel.add(closeButton);
      alertPanel.add(alertButton, "shrink,left");
      alertPanel.add(alertRemainingLabel, "shrink,left");
      alertPanel.add(alertMessageLabel, "shrink,right," + MultiBitUI.ALERT_MESSAGE_MAX_WIDTH_MIG);
    }

  }

  /**
   * @return A new action for closing the alert panel
   */
  private Action getCloseAlertAction() {

    return new AbstractAction() {

      @Override
      public void actionPerformed(ActionEvent e) {
        ControllerEvents.fireRemoveAlertEvent();
      }
    };
  }
}
